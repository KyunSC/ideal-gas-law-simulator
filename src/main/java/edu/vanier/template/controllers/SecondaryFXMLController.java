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
    @FXML
    BorderPane borderPane;


    ArrayList<Particle> listOfParticles = new ArrayList<>();
    ArrayList<Particle> firstListOfParticles = new ArrayList<>();
    ArrayList<Particle> secondListOfParticles = new ArrayList<>();
    ArrayList<Particle> thirdListOfParticles = new ArrayList<>();
    ArrayList<Particle> fourthListOfParticles = new ArrayList<>();

    int totalNumberOfParticles = 0;

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
        addToQuadrants();

        BackgroundFill bf = new BackgroundFill(Color.BLACK, CornerRadii.EMPTY , Insets.EMPTY);
        Background bg = new Background(bf);
        borderPane.setBackground(bg);
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
                    System.out.println(System.currentTimeMillis());
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

    private void addSecondQuadrant(){
        for (int i = 0; i < listOfParticles.size(); i++) {
            if (listOfParticles.get(i).getCircle().getCenterX() < canvas.getWidth()/2 && listOfParticles.get(i).getCircle().getCenterY() < canvas.getHeight() / 2){
                secondListOfParticles.add(listOfParticles.get(i));
                listOfParticles.remove(i);
            }
        }

        for (int i = 0; i < thirdListOfParticles.size(); i++) {
            if (thirdListOfParticles.get(i).getCircle().getCenterX() < canvas.getWidth()/2 && thirdListOfParticles.get(i).getCircle().getCenterY() < canvas.getHeight() / 2){
                secondListOfParticles.add(thirdListOfParticles.get(i));
                thirdListOfParticles.remove(i);
            }
        }

        for (int i = 0; i < fourthListOfParticles.size(); i++) {
            if (fourthListOfParticles.get(i).getCircle().getCenterX() < canvas.getWidth()/2 && fourthListOfParticles.get(i).getCircle().getCenterY() < canvas.getHeight() / 2){
                secondListOfParticles.add(fourthListOfParticles.get(i));
                fourthListOfParticles.remove(i);
            }
        }
    }

    private void addThirdQuadrant(){
        for (int i = 0; i < listOfParticles.size(); i++) {
            if (listOfParticles.get(i).getCircle().getCenterX() < canvas.getWidth()/2 && listOfParticles.get(i).getCircle().getCenterY() < canvas.getHeight() / 2){
                thirdListOfParticles.add(listOfParticles.get(i));
                listOfParticles.remove(i);
            }
        }

        for (int i = 0; i < secondListOfParticles.size(); i++) {
            if (secondListOfParticles.get(i).getCircle().getCenterX() < canvas.getWidth()/2 && secondListOfParticles.get(i).getCircle().getCenterY() < canvas.getHeight() / 2){
                secondListOfParticles.add(secondListOfParticles.get(i));
                secondListOfParticles.remove(i);
            }
        }

        for (int i = 0; i < fourthListOfParticles.size(); i++) {
            if (fourthListOfParticles.get(i).getCircle().getCenterX() < canvas.getWidth()/2 && fourthListOfParticles.get(i).getCircle().getCenterY() < canvas.getHeight() / 2){
                thirdListOfParticles.add(fourthListOfParticles.get(i));
                fourthListOfParticles.remove(i);
            }
        }
    }

    private void addFourthQuadrant(){
        for (int i = 0; i < listOfParticles.size(); i++) {
            if (listOfParticles.get(i).getCircle().getCenterX() < canvas.getWidth()/2 && listOfParticles.get(i).getCircle().getCenterY() < canvas.getHeight() / 2){
                fourthListOfParticles.add(listOfParticles.get(i));
                listOfParticles.remove(i);
            }
        }

        for (int i = 0; i < thirdListOfParticles.size(); i++) {
            if (thirdListOfParticles.get(i).getCircle().getCenterX() < canvas.getWidth()/2 && thirdListOfParticles.get(i).getCircle().getCenterY() < canvas.getHeight() / 2){
                fourthListOfParticles.add(thirdListOfParticles.get(i));
                thirdListOfParticles.remove(i);
            }
        }

        for (int i = 0; i < secondListOfParticles.size(); i++) {
            if (secondListOfParticles.get(i).getCircle().getCenterX() < canvas.getWidth()/2 && secondListOfParticles.get(i).getCircle().getCenterY() < canvas.getHeight() / 2){
                fourthListOfParticles.add(secondListOfParticles.get(i));
                secondListOfParticles.remove(i);
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
        double lastTime = System.currentTimeMillis();
        Circle circle = new Circle(11,11,10,Color.RED);
        velocityX = Math.random()*velocity;
        velocityY = Math.sqrt(Math.pow(velocity, 2) - Math.pow(velocityX, 2));
        double currentTime = System.currentTimeMillis();
        Particle particle = new Particle(circle, velocityX, velocityY, canvas);
        particle.createTimeline();
        particle.play();
        canvas.getChildren().add(particle.getCircle());
        thermometer.updateThermometer();
        updatePressure();
        while (lastTime > currentTime - 30){
            currentTime = System.currentTimeMillis();
        }
        listOfParticles.add(particle);


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
        for (int i = 0; i < firstListOfParticles.size(); i++) {
            for (int j = (i+1); j < firstListOfParticles.size() ; j++) {
                double deltaX = Math.abs(firstListOfParticles.get(i).getCircle().getCenterX() - firstListOfParticles.get(j).getCircle().getCenterX());
                double deltaY = Math.abs(firstListOfParticles.get(i).getCircle().getCenterY() - firstListOfParticles.get(j).getCircle().getCenterY());
                if (deltaX <= firstListOfParticles.get(i).getCircle().getRadius() + firstListOfParticles.get(j).getCircle().getRadius()
                        && deltaY <= firstListOfParticles.get(i).getCircle().getRadius() + firstListOfParticles.get(j).getCircle().getRadius()
                        && firstListOfParticles.get(i).getCircle().getCenterX() > 20
                        && firstListOfParticles.get(i).getCircle().getCenterY() > 20
                        && firstListOfParticles.get(j).getCircle().getCenterX() > 20
                        && firstListOfParticles.get(j).getCircle().getCenterY() > 20
                ){
                    firstListOfParticles.get(i).velocityX *= -1;
                    firstListOfParticles.get(j).velocityX *= -1;
                    firstListOfParticles.get(i).velocityY *= -1;
                    firstListOfParticles.get(j).velocityY *= -1;
                }
            }
        }
        for (int i = 0; i < secondListOfParticles.size(); i++) {
            for (int j = (i+1); j < secondListOfParticles.size() ; j++) {
                double deltaX = Math.abs(secondListOfParticles.get(i).getCircle().getCenterX() - secondListOfParticles.get(j).getCircle().getCenterX());
                double deltaY = Math.abs(secondListOfParticles.get(i).getCircle().getCenterY() - secondListOfParticles.get(j).getCircle().getCenterY());
                if (deltaX <= secondListOfParticles.get(i).getCircle().getRadius() + secondListOfParticles.get(j).getCircle().getRadius()
                        && deltaY <= secondListOfParticles.get(i).getCircle().getRadius() + secondListOfParticles.get(j).getCircle().getRadius()
                        && secondListOfParticles.get(i).getCircle().getCenterX() > 20
                        && secondListOfParticles.get(i).getCircle().getCenterY() > 20
                        && secondListOfParticles.get(j).getCircle().getCenterX() > 20
                        && secondListOfParticles.get(j).getCircle().getCenterY() > 20
                ){
                    secondListOfParticles.get(i).velocityX *= -1;
                    secondListOfParticles.get(j).velocityX *= -1;
                    secondListOfParticles.get(i).velocityY *= -1;
                    secondListOfParticles.get(j).velocityY *= -1;
                }
            }
        }
        for (int i = 0; i < thirdListOfParticles.size(); i++) {
            for (int j = (i+1); j < thirdListOfParticles.size() ; j++) {
                double deltaX = Math.abs(thirdListOfParticles.get(i).getCircle().getCenterX() - thirdListOfParticles.get(j).getCircle().getCenterX());
                double deltaY = Math.abs(thirdListOfParticles.get(i).getCircle().getCenterY() - thirdListOfParticles.get(j).getCircle().getCenterY());
                if (deltaX <= thirdListOfParticles.get(i).getCircle().getRadius() + thirdListOfParticles.get(j).getCircle().getRadius()
                        && deltaY <= thirdListOfParticles.get(i).getCircle().getRadius() + thirdListOfParticles.get(j).getCircle().getRadius()
                        && thirdListOfParticles.get(i).getCircle().getCenterX() > 20
                        && thirdListOfParticles.get(i).getCircle().getCenterY() > 20
                        && thirdListOfParticles.get(j).getCircle().getCenterX() > 20
                        && thirdListOfParticles.get(j).getCircle().getCenterY() > 20
                ){
                    thirdListOfParticles.get(i).velocityX *= -1;
                    thirdListOfParticles.get(j).velocityX *= -1;
                    thirdListOfParticles.get(i).velocityY *= -1;
                    thirdListOfParticles.get(j).velocityY *= -1;
                }
            }
        }
        for (int i = 0; i < fourthListOfParticles.size(); i++) {
            for (int j = (i+1); j < fourthListOfParticles.size() ; j++) {
                double deltaX = Math.abs(fourthListOfParticles.get(i).getCircle().getCenterX() - fourthListOfParticles.get(j).getCircle().getCenterX());
                double deltaY = Math.abs(fourthListOfParticles.get(i).getCircle().getCenterY() - fourthListOfParticles.get(j).getCircle().getCenterY());
                if (deltaX <= fourthListOfParticles.get(i).getCircle().getRadius() + fourthListOfParticles.get(j).getCircle().getRadius()
                        && deltaY <= fourthListOfParticles.get(i).getCircle().getRadius() + fourthListOfParticles.get(j).getCircle().getRadius()
                        && fourthListOfParticles.get(i).getCircle().getCenterX() > 20
                        && fourthListOfParticles.get(i).getCircle().getCenterY() > 20
                        && fourthListOfParticles.get(j).getCircle().getCenterX() > 20
                        && fourthListOfParticles.get(j).getCircle().getCenterY() > 20
                ){
                    fourthListOfParticles.get(i).velocityX *= -1;
                    fourthListOfParticles.get(j).velocityX *= -1;
                    fourthListOfParticles.get(i).velocityY *= -1;
                    fourthListOfParticles.get(j).velocityY *= -1;
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
        canvas.setBorder(new Border((new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT))));
        vbox.getChildren().add(canvas);
    }

    private void updatePressure() {
        pvnrt.setMoles(totalNumberOfParticles);
        pressureGauge.updateGauge();
    }
}
