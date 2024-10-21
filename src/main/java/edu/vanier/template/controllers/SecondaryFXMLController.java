package edu.vanier.template.controllers;

import edu.vanier.template.MainApp;
import edu.vanier.template.Particle;
import edu.vanier.template.calculations.PVnRT;
import edu.vanier.template.graphics.PressureGauge;
import edu.vanier.template.graphics.Thermometer;
import javafx.animation.*;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * FXML controller class for a secondary scene.
 *
 * @author frostybee
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
    public Pane canvas = new Pane();
    @FXML
    VBox vbox;


    ArrayList<Particle> listOfParticles = new ArrayList<>();
    ArrayList<Particle> firstListOfParticles = new ArrayList<>();
    ArrayList<Particle> secondListOfParticles = new ArrayList<>();
    ArrayList<Particle> thirdListOfParticles = new ArrayList<>();
    ArrayList<Particle> fourthListOfParticles = new ArrayList<>();

    int numberOfParticles = 0;
    int firstNumberOfParticles = 0;
    int secondNumberOfParticles = 0;
    int thirdNumberOfParticles = 0;
    int fourthNumberOfParticles = 0;

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

        HBox hbox = new HBox();

        hbox.getChildren().addAll(canvas, pressureGauge.getGaugePane(), thermometer.getThermometerPane());

        hbox.setAlignment(Pos.CENTER);

        vbox.getChildren().add(hbox);

        initCanvas();
        addParticlesButton();
        add10ParticlesButton();
        particleCollisionTimeline();
    }

    private void addToQuadrants(){
        Timeline timeline = new Timeline();
        KeyFrame kf = new KeyFrame(
                Duration.millis(100),
                event -> {
                    addFirstQuadrant();
                    addSecondQuadrant();
                    addThirdQuadrant();
                    addFourthQuadrant();
                }
        );
    }

    private void addFirstQuadrant(){
        for (int i = 0; i < secondNumberOfParticles; i++) {
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

    private void addSecondQuadrant(){
        for (int i = 0; i < listOfParticles.size(); i++) {
            if (listOfParticles.get(i).getCircle().getCenterX() < canvas.getWidth()/2 && listOfParticles.get(i).getCircle().getCenterY() < canvas.getHeight() / 2){
                secondListOfParticles.add(listOfParticles.get(i));
                listOfParticles.remove(i);
                numberOfParticles--;
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

    private void addThirdQuadrant(){
        for (int i = 0; i < listOfParticles.size(); i++) {
            if (listOfParticles.get(i).getCircle().getCenterX() < canvas.getWidth()/2 && listOfParticles.get(i).getCircle().getCenterY() < canvas.getHeight() / 2){
                thirdListOfParticles.add(listOfParticles.get(i));
                listOfParticles.remove(i);
                numberOfParticles--;
            }
        }

        for (int i = 0; i < thirdListOfParticles.size(); i++) {
            if (secondListOfParticles.get(i).getCircle().getCenterX() < canvas.getWidth()/2 && secondListOfParticles.get(i).getCircle().getCenterY() < canvas.getHeight() / 2){
                secondListOfParticles.add(thirdListOfParticles.get(i));
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

    private void addFourthQuadrant(){
        for (int i = 0; i < listOfParticles.size(); i++) {
            if (listOfParticles.get(i).getCircle().getCenterX() < canvas.getWidth()/2 && listOfParticles.get(i).getCircle().getCenterY() < canvas.getHeight() / 2){
                fourthListOfParticles.add(listOfParticles.get(i));
                listOfParticles.remove(i);
                numberOfParticles--;
            }
        }

        for (int i = 0; i < thirdListOfParticles.size(); i++) {
            if (thirdListOfParticles.get(i).getCircle().getCenterX() < canvas.getWidth()/2 && thirdListOfParticles.get(i).getCircle().getCenterY() < canvas.getHeight() / 2){
                firstListOfParticles.add(thirdListOfParticles.get(i));
                thirdListOfParticles.remove(i);
            }
        }

        for (int i = 0; i < secondListOfParticles.size(); i++) {
            if (fourthListOfParticles.get(i).getCircle().getCenterX() < canvas.getWidth()/2 && fourthListOfParticles.get(i).getCircle().getCenterY() < canvas.getHeight() / 2){
                firstListOfParticles.add(fourthListOfParticles.get(i));
                fourthListOfParticles.remove(i);
            }
        }
    }

    private void add10ParticlesButton() {
        add10.setOnAction(event -> {
            for (int i = 0; i < 10; i++) {
                addParticle();
            }
            updatePressure();
        });
    }

    private void addParticle() {
        Circle circle = new Circle(11,11,10,Color.RED);
        velocityX = Math.random()*velocity;
        velocityY = Math.sqrt(Math.pow(velocity, 2) - Math.pow(velocityX, 2));

        listOfParticles[numberOfParticles] = new Particle(circle, velocityX,velocityY, canvas);
        listOfParticles[numberOfParticles].createTimeline();
        listOfParticles[numberOfParticles].play();
        canvas.getChildren().add(listOfParticles[numberOfParticles++].getCircle());

        thermometer.updateThermometer();
        updatePressure();

        listOfParticles.add(new Particle(circle, velocityX,velocityY, canvas));
        listOfParticles.get(numberOfParticles).createTimeline();
        listOfParticles.get(numberOfParticles).play();
        canvas.getChildren().add(listOfParticles.get(numberOfParticles++).getCircle());

        thermometer.updateThermometer();
        updatePressure();

    }

    private void particleCollisionTimeline() {
        Timeline elasticCollisionTimeline = new Timeline();
        KeyFrame keyframe = new KeyFrame(
                Duration.millis(100),
                (event -> {
                    checkParticleParticleCollision();
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

    public void checkParticleParticleCollision() {
        for (int i = 0; i < numberOfParticles; i++) {
            for (int j = (i+1); j < numberOfParticles ; j++) {
                double deltaX = Math.abs(listOfParticles[i].getCircle().getCenterX() - listOfParticles[j].getCircle().getCenterX());
                double deltaY = Math.abs(listOfParticles[i].getCircle().getCenterY() - listOfParticles[j].getCircle().getCenterY());
                if (deltaX <= listOfParticles[i].getCircle().getRadius() + listOfParticles[j].getCircle().getRadius()
                        && deltaY <= listOfParticles[i].getCircle().getRadius() + listOfParticles[j].getCircle().getRadius()
                        && listOfParticles[i].getCircle().getCenterX() > 20
                        && listOfParticles[i].getCircle().getCenterY() > 20
                        && listOfParticles[j].getCircle().getCenterX() > 20
                        && listOfParticles[j].getCircle().getCenterY() > 20
                ){
                    listOfParticles[i].velocityX *= -1;
                    listOfParticles[j].velocityX *= -1;
                    listOfParticles[i].velocityY *= -1;
                    listOfParticles[j].velocityY *= -1;
                }
            }
        }
    }

    private void loadPrimaryScene(Event e) {
        MainApp.switchScene(MainApp.MAINAPP_LAYOUT, new MainAppFXMLController());
        logger.info("Loaded the primary scene...");
    }

    private void initCanvas(){
        canvas.setMinSize(500, 500);
        canvas.setMaxSize(500, 500);
        canvas.setBorder(new Border((new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT))));
        vbox.getChildren().add(canvas);
    }

    private void updatePressure() {
        pvnrt.setMoles(numberOfParticles);
        pressureGauge.updateGauge();
    }
}
