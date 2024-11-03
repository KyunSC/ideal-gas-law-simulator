package edu.vanier.template.controllers;

import edu.vanier.template.MainApp;
import edu.vanier.template.Particle;
import edu.vanier.template.calculations.PVnRT;
import edu.vanier.template.graphics.PressureGauge;
import edu.vanier.template.graphics.Thermometer;
import javafx.animation.*;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.BoundingBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * FXML controller class for a secondary scene.
 * Contains all the FXML objects that will need behaviors
 * allParticle Arraylist used to keep all the particles
 * Pane is separated in 4 quadrants
 * Top Left is Quadrant 1, Top Right is Quadrant 2, Bottom Left is Quadrant 3, Bottom Right is Quadrant 4
 * Quadrants are used to be more effective when detecting collisions between Particles
 *
 */
public class SecondaryFXMLController {

    private final static Logger logger = LoggerFactory.getLogger(SecondaryFXMLController.class);

    @FXML
    Button btnSwitchScene;
    @FXML
    Button add;
    @FXML
    Button add10;
    @FXML
    Pane canvas;
    @FXML
    VBox gaugeVBox;
    @FXML
    Button remove1;
    @FXML
    Button remove10;
    @FXML
    Button reset;
    @FXML
    Slider volumeSlider;
    @FXML
    Button pause;
    @FXML
    Button play;
    @FXML
    Button fastForward;
    @FXML
    Slider temperatureSlider;

    ArrayList<Particle> allParticles = new ArrayList<>();
    ArrayList<Particle> listOfParticles = new ArrayList<>();
    ArrayList<Particle> firstListOfParticles = new ArrayList<>();
    ArrayList<Particle> secondListOfParticles = new ArrayList<>();
    ArrayList<Particle> thirdListOfParticles = new ArrayList<>();
    ArrayList<Particle> fourthListOfParticles = new ArrayList<>();

    double velocityX;
    double velocityY;
    double velocity = 5;

    private Thermometer thermometer;
    private PVnRT pvnrt;
    private PressureGauge pressureGauge;

    @FXML
    public void initialize() {
        logger.info("Initializing MainAppController...");
        btnSwitchScene.setOnAction(this::loadPrimaryScene);

        pvnrt = new PVnRT();
        pvnrt.setMoles(0);

        pressureGauge = new PressureGauge(pvnrt);
        thermometer = new Thermometer(pvnrt);
        gaugeVBox.getChildren().addAll(pressureGauge.getGaugePane(), thermometer.getThermometerPane());

        addParticlesButton();
        add10ParticlesButton();
        remove1Button();
        remove10Button();
        pauseFunction();
        playFunction();
        fastForwardFunction();
        resetButton();
        particleCollisionTimeline();
        addToQuadrants();
    }

    private void addToQuadrants(){
        Timeline timeline = new Timeline();
        KeyFrame kf = new KeyFrame(
                Duration.millis(100),
                event -> {
                    addFirstQuadrant();
                    addToSecondThirdFourth(listOfParticles, secondListOfParticles);
                    addToSecondThirdFourth(listOfParticles, thirdListOfParticles);
                    addToSecondThirdFourth(listOfParticles, fourthListOfParticles);
                    changeVolume();
                }
        );
        timeline.getKeyFrames().add(kf);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void addFirstQuadrant(){
        for (int i = 0; i < secondListOfParticles.size(); i++) {
            if (secondListOfParticles.get(i).getCircle().getCenterX() < canvas.getWidth()/2 && secondListOfParticles.get(i).getCircle().getCenterY() < canvas.getHeight() / 2){
                firstListOfParticles.add(secondListOfParticles.get(i));
                secondListOfParticles.remove(i);
            }
        }
        for (int i = 0; i < thirdListOfParticles.size(); i++) {
            if (thirdListOfParticles.get(i).getCircle().getCenterX() < canvas.getWidth()/2 && thirdListOfParticles.get(i).getCircle().getCenterY() < canvas.getHeight() / 2){
                firstListOfParticles.add(thirdListOfParticles.get(i));
                thirdListOfParticles.remove(i);
            }
        }
        for (int i = 0; i < fourthListOfParticles.size(); i++) {
            if (fourthListOfParticles.get(i).getCircle().getCenterX() < canvas.getWidth()/2 && fourthListOfParticles.get(i).getCircle().getCenterY() < canvas.getHeight() / 2){
                firstListOfParticles.add(fourthListOfParticles.get(i));
                fourthListOfParticles.remove(i);
            }
        }
    }

    private void addToSecondThirdFourth(ArrayList<Particle> listOfParticles, ArrayList<Particle> targetList){
        for (int i = 0; i < listOfParticles.size(); i++) {
            if (listOfParticles.get(i).getCircle().getCenterX() < canvas.getWidth()/2 && listOfParticles.get(i).getCircle().getCenterY() < canvas.getHeight() / 2){
                targetList.add(listOfParticles.get(i));
                listOfParticles.remove(i);
            }
        }
    }
    /*public static void setTimeout(Runnable runnable, int delay){
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            }
            catch (Exception e){
                System.err.println(e);
            }
        }).start();
    }*/

    private void add10ParticlesButton() {
        add10.setOnAction(event -> {
            for (int i = 0; i < 10; i++) {
                updatePressure();
                addParticle();
            }
        });
    }

    private void addParticle() {
        Circle circle = new Circle(11,11,10,Color.RED);
        velocityX = Math.random()*velocity;
        velocityY = Math.sqrt(Math.pow(velocity, 2) - Math.pow(velocityX, 2));
        Particle particle = new Particle(circle, velocityX, velocityY, canvas);
        particle.createTimeline();
        particle.play();
        canvas.getChildren().add(particle.getCircle());
        listOfParticles.add(particle);
        allParticles.add(particle);
        thermometer.updateThermometer();
        updatePressure();
    }

    private void particleCollisionTimeline() {
        Timeline elasticCollisionTimeline = new Timeline();
        KeyFrame keyframe = new KeyFrame(
                Duration.millis(2000),
                (event -> {
                    checkParticleParticleCollision(firstListOfParticles);
                    checkParticleParticleCollision(secondListOfParticles);
                    checkParticleParticleCollision(thirdListOfParticles);
                    checkParticleParticleCollision(fourthListOfParticles);
                })
        );
        elasticCollisionTimeline.getKeyFrames().add(keyframe);
        elasticCollisionTimeline.setCycleCount(Animation.INDEFINITE);
        elasticCollisionTimeline.play();
    }

    private void addParticlesButton(){
        add.setOnAction(event -> {
            addParticle();
            updatePressure();
        });
    }

    private void resetButton(){
        reset.setOnAction(event -> {
            for (int i = 0; i < allParticles.size(); i++) canvas.getChildren().remove(allParticles.get(i).getCircle());
            allParticles.clear();
            updatePressure();
            pressureGauge.updateGauge();
            thermometer.updateThermometer();
        });
    }

    private void pauseFunction(){
        pause.setOnAction(event -> {
            for (int i = 0; i < allParticles.size(); i++) {
                allParticles.get(i).pause();
            }
        });
    }

    private void playFunction(){
        play.setOnAction(event -> {
            for (int i = 0; i < allParticles.size(); i++) {
                allParticles.get(i).play();
            }
        });
    }

    private void fastForwardFunction(){
        Timeline timeline = new Timeline();
        KeyFrame keyFrame = new KeyFrame(
                Duration.millis(1),
                event1 -> {
                    for (Particle allParticle : allParticles) {
                        allParticle.play();
                    }
                }
        );
        timeline.setOnFinished(event1 -> {
            for (Particle allParticle : allParticles) {
                allParticle.play();
            }
            for (int i = 0; i < allParticles.size(); i++) {
                allParticles.get(i).pause();
            }
        });
        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(10);
        fastForward.setOnAction(event -> {
            timeline.play();
        });
    }

    private void remove1(){
        if (!allParticles.isEmpty()) {
            canvas.getChildren().remove(allParticles.getLast().getCircle());
            allParticles.removeLast();
            updatePressure();
            pressureGauge.updateGauge();
        }
        else {
            updatePressure();
            pressureGauge.updateGauge();
            pvnrt.setTemperature(0);
            thermometer.updateThermometer();
        }
    }

    private void remove1Button(){
        remove1.setOnAction(event -> {
            remove1();
        });
    }

    private void remove10Button(){
        remove10.setOnAction(event -> {
            for (int i = 0; i < 10; i++) {
                if (!allParticles.isEmpty())remove1();
            }
            thermometer.updateThermometer();
        });
    }

    public void checkParticleParticleCollision(ArrayList<Particle> targetListOfParticles) {
        for (int i = 0; i < targetListOfParticles.size(); i++) {
            for (int j = (i+1); j < targetListOfParticles.size() ; j++) {
                if (targetListOfParticles.get(i).getCircle().getBoundsInParent().intersects(targetListOfParticles.get(j).getCircle().getBoundsInParent()) ){
                    targetListOfParticles.get(i).velocityX *= -1;
                    targetListOfParticles.get(j).velocityX *= -1;
                    targetListOfParticles.get(i).velocityY *= -1;
                    targetListOfParticles.get(j).velocityY *= -1;
                }
            }
        }
    }

    private void changeVolume(){
        canvas.setPrefWidth((volumeSlider.getValue()/100) * canvas.getMaxWidth());
    }

    private void changeVelocityByTemperature(){
        
    }

    private void loadPrimaryScene(Event e) {
        MainApp.switchScene(MainApp.MAINAPP_LAYOUT, new MainAppFXMLController());
        logger.info("Loaded the primary scene...");
    }

    private void updatePressure() {
        pvnrt.setMoles(allParticles.size());
        pressureGauge.updateGauge();
    }

}
