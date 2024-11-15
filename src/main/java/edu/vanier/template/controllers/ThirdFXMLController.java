package edu.vanier.template.controllers;

import edu.vanier.template.BalloonParticle;
import edu.vanier.template.MainApp;
import edu.vanier.template.Particle;
import edu.vanier.template.calculations.PVnRT;
import edu.vanier.template.graphics.PressureGauge;
import edu.vanier.template.graphics.Thermometer;
import javafx.animation.*;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Objects;

/**
 * FXML controller class for a secondary scene.
 * Contains all the FXML objects that will need behaviors
 * allParticle Arraylist used to keep all the particles
 * Pane is separated in 4 quadrants
 * Top Left is Quadrant 1, Top Right is Quadrant 2, Bottom Left is Quadrant 3, Bottom Right is Quadrant 4
 * Quadrants are used to be more effective when detecting collisions between Particles
 *
 */
public class ThirdFXMLController {

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
    Button pause;
    @FXML
    Button play;
    @FXML
    Button fastForward;
    @FXML
    Button heatButton;
    @FXML
    Button coolButton;
    @FXML
    ComboBox<String> comboBox;
    @FXML
    Label velocityLabel;
    @FXML
    Label volumeLabel;
    @FXML
    Circle circleCanvas;

    ArrayList<BalloonParticle> allParticles = new ArrayList<>();
    ArrayList<Particle> listOfParticles = new ArrayList<>();
    private Thermometer thermometer;
    private PVnRT pvnrt;
    private PressureGauge pressureGauge;
    private double baseParticleVelocity = 3;
    boolean paused = false;
    private double totalParticleCount;
    private double maxPressure = 1000;
    public boolean lidPopped = false;
    private Color particleColor;
    private double particleSize;
    private double randomNegative = 1;

    Image lidImage = new Image("/LidContainer.png");

    @FXML
    public void initialize() {
        logger.info("Initializing MainAppController...");
        btnSwitchScene.setOnAction(this::loadPrimaryScene);

        pvnrt = new PVnRT();
        pvnrt.setMoles(0);

        pressureGauge = new PressureGauge(pvnrt);
        thermometer = new Thermometer(pvnrt);
        gaugeVBox.getChildren().addAll(pressureGauge.getGaugePane(), thermometer.getThermometerPane());

        initialFunctions();
        circleCanvas = new Circle();
    }

    private void initialFunctions(){
        addParticlesButton();
        add10ParticlesButton();
        remove1Button();
        remove10Button();
        pauseFunction();
        playFunction();
        fastForwardFunction();
        resetButton();
        particleCollisionTimeline();
        setupTemperatureControls();
        initializeComboBox();
    }

    private void setupTemperatureControls() {
        DropShadow heatGlow = new DropShadow();
        heatGlow.setColor(Color.FIREBRICK);
        heatGlow.setRadius(25);

        DropShadow coolGlow = new DropShadow();
        coolGlow.setColor(Color.LIGHTBLUE);
        coolGlow.setRadius(25);

        Timeline heatTimeline = new Timeline(new KeyFrame(Duration.millis(100), event -> adjustTemperature(1)));
        heatTimeline.setCycleCount(Animation.INDEFINITE);

        heatButton.setOnMousePressed(event -> {
            heatTimeline.play();
            circleCanvas.setEffect(heatGlow);
        });
        heatButton.setOnMouseReleased(event -> {
            heatTimeline.stop();
            circleCanvas.setEffect(null);
        });

        Timeline coolTimeline = new Timeline(new KeyFrame(Duration.millis(100), event -> adjustTemperature(-1)));
        coolTimeline.setCycleCount(Animation.INDEFINITE);

        coolButton.setOnMousePressed(event -> {
            coolTimeline.play();
            circleCanvas.setEffect(coolGlow);
        });
        coolButton.setOnMouseReleased(event -> {
            coolTimeline.stop();
            circleCanvas.setEffect(null);
        });
    }

    private void updateParticlesWithTemperature(double newTemperature) {
        if (newTemperature <= 0) {
            for (BalloonParticle particle : allParticles) {
                particle.setVelocity(0);
            }
        } else {
            for (BalloonParticle particle : allParticles) {
                particle.setVelocity((baseParticleVelocity * calculateRMS(newTemperature)) / calculateRMS(300));
            }
        }
    }

    private void adjustTemperature(int tempChange) {
        int temperatureIncrement = (pvnrt.getTemperature() < 100) ? 1 : 10;

        double newTemperature = pvnrt.getTemperature() + tempChange * temperatureIncrement;

        newTemperature = Math.min(1000, Math.max(0, newTemperature));
        pvnrt.setTemperature(newTemperature);

        updateParticlesWithTemperature(newTemperature);
        updatePressure();

        thermometer.updateThermometer();
    }

    private void add10ParticlesButton() {
        add10.setOnAction(event -> {
            for (int i = 0; i < 10; i++) {
                addParticle();
            }
        });
    }

    private void addParticle() {
        if (pvnrt.getTemperature() == 0) pvnrt.setTemperature(300);
        if (!paused) {
            totalParticleCount++;
            updatePressure();
            setParticleColor(pvnrt.getMolarMass());
            setParticleSize(pvnrt.getMolarMass());
            double particleVelocity = (baseParticleVelocity * calculateRMS(pvnrt.getTemperature())) / calculateRMS(300);
            thermometer.updateThermometer();

            Circle circle2 = new Circle(canvas.getWidth()/2, canvas.getHeight()/2, particleSize, particleColor);
            BalloonParticle balloonParticle = new BalloonParticle(circle2, particleVelocity, canvas);
            balloonParticle.setVelocityY(balloonParticle.getVelocityY() * randomNegative);
            randomNegative = randomNegative * -1;
            System.out.println(balloonParticle.velocityY);
            System.out.println(randomNegative);
            balloonParticle.setVelocityX(balloonParticle.getVelocityX() * randomNegative);
            balloonParticle.createTimeline();
            balloonParticle.play();
            canvas.getChildren().add(balloonParticle.getCircle());
            allParticles.add(balloonParticle);
        }
    }

    private void particleCollisionTimeline() {
        Timeline elasticCollisionTimeline = new Timeline();
        KeyFrame keyframe = new KeyFrame(
                Duration.millis(0.1),
                (event -> {
                    checkParticleParticleCollision(allParticles);
                    particleEscaped();
                    changeVelocityLabel();
                    changeVolumeLabel();
                })
        );
        elasticCollisionTimeline.getKeyFrames().add(keyframe);
        elasticCollisionTimeline.setCycleCount(Animation.INDEFINITE);
        elasticCollisionTimeline.play();
    }

    private void addParticlesButton(){
        add.setOnAction(event -> {
            addParticle();
        });
    }

    private void resetButton(){
        reset.setOnAction(event -> {
            for (int i = 0; i < allParticles.size(); i++) canvas.getChildren().remove(allParticles.get(i).getCircle());
            allParticles.clear();
            totalParticleCount = 0;
            pvnrt.setMoles(0);
            updatePressure();
            pressureGauge.updateGauge();
            thermometer.updateThermometer();

            if (lidPopped){
                lidPopped = false;
                canvas.setBorder(new Border(new BorderStroke(Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, BorderStrokeStyle.SOLID, null, null, null, new CornerRadii(20), null, null)));
            }
        });
    }

    private void pauseFunction(){
        pause.setOnAction(event -> {
            paused = true;
            for (int i = 0; i < allParticles.size(); i++) {
                allParticles.get(i).pause();
            }
        });
    }

    private void playFunction(){
        play.setOnAction(event -> {
            paused = false;
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
                    for (BalloonParticle allParticle : allParticles) {
                        allParticle.play();
                    }
                }
        );
        timeline.setOnFinished(event1 -> {
            for (BalloonParticle allParticle : allParticles) {
                allParticle.play();
            }
            for (int i = 0; i < allParticles.size(); i++) {
                allParticles.get(i).pause();
                paused = true;
            }
        });
        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(10);
        fastForward.setOnAction(event -> {
            timeline.play();
        });
    }

    /**
     * Function reused by the buttons remove 1 and 10
     * Checks if there are any particles available to delete
     * If so, it removes the last circle in the list
     * Checks for the deleted particle to delete from its quadrant
     * Then it removes it from the general list
     * Updates the particle count, temperature and pressure
     * If the general list is empty, then the all variables should be at 0
     */
    private void remove1(){
        if (!allParticles.isEmpty()) {
            canvas.getChildren().remove(allParticles.getLast().getCircle());
            allParticles.removeLast();
            totalParticleCount--;
            updatePressure();
            pressureGauge.updateGauge();
            if (allParticles.isEmpty()){
                pvnrt.setPressure(0);
                pvnrt.setMoles(0);
                pressureGauge.updateGauge();
                pvnrt.setTemperature(0);
                thermometer.updateThermometer();
                allParticles.clear();
            }
        }
        else {
            pvnrt.setPressure(0);
            pvnrt.setMoles(0);
            pressureGauge.updateGauge();
            pvnrt.setTemperature(0);
            thermometer.updateThermometer();
            allParticles.clear();
        }
    }


    /**
     * When the button is pressed, it plays an event containing the function remove1() once
     */
    private void remove1Button(){
        remove1.setOnAction(event -> {
            remove1();
        });
    }

    /**
     * When the button is pressed, it repeats the function remove1() 10 times,
     * Then, it updates the thermometer
     */
    private void remove10Button(){
        remove10.setOnAction(event -> {
            for (int i = 0; i < 10; i++) {
                remove1();
            }
            thermometer.updateThermometer();
        });
    }

    public void checkParticleParticleCollision(ArrayList<BalloonParticle> targetListOfParticles) {
        for (int i = 0; i < targetListOfParticles.size(); i++) {
            if (!targetListOfParticles.get(i).isCollisionDelay()){
                for (int j = (i+1); j < targetListOfParticles.size() ; j++) {
                    if (!targetListOfParticles.get(j).isCollisionDelay()){
                        if (targetListOfParticles.get(i).getCircle().getBoundsInParent().intersects(targetListOfParticles.get(j).getCircle().getBoundsInParent())){
                            if (targetListOfParticles.get(i).getCircle().getCenterX() < targetListOfParticles.get(j).getCircle().getCenterX()){
                                targetListOfParticles.get(i).velocityX = (Math.abs(targetListOfParticles.get(i).velocityX) * -1);
                                targetListOfParticles.get(j).velocityX = (Math.abs(targetListOfParticles.get(j).velocityX));
                            }
                            else {
                                targetListOfParticles.get(j).velocityX = (Math.abs(targetListOfParticles.get(j).velocityX) * -1);
                                targetListOfParticles.get(i).velocityX = (Math.abs(targetListOfParticles.get(i).velocityX));
                            }
                            if (targetListOfParticles.get(i).getCircle().getCenterY() < targetListOfParticles.get(j).getCircle().getCenterY()){
                                targetListOfParticles.get(i).velocityY = (Math.abs(targetListOfParticles.get(i).velocityY) * -1);
                                targetListOfParticles.get(j).velocityY = (Math.abs(targetListOfParticles.get(j).velocityY));
                            }
                            else {
                                targetListOfParticles.get(j).velocityY = (Math.abs(targetListOfParticles.get(j).velocityY) * -1);
                                targetListOfParticles.get(i).velocityY = (Math.abs(targetListOfParticles.get(i).velocityY));
                            }
                            if (!targetListOfParticles.get(i).isCollisionDelay()) {
                                targetListOfParticles.get(i).setCollisionDelay(true);
                                delayCollision(targetListOfParticles.get(i));
                            }
                            if (!targetListOfParticles.get(j).isCollisionDelay()) {
                                targetListOfParticles.get(j).setCollisionDelay(true);
                                delayCollision(targetListOfParticles.get(j));
                            }
                        }
                    }
                }
            }
        }
    }

    private void loadPrimaryScene(Event e) {
        MainApp.switchScene(MainApp.MAINAPP_LAYOUT, new MainAppFXMLController());
        logger.info("Loaded the primary scene...");
    }

    private void updatePressure() {
        if (allParticles.isEmpty())pvnrt.setMoles(totalParticleCount);
        else pvnrt.setMoles(totalParticleCount);
        pressureGauge.updateGauge();
    }

    private double calculateRMS(double temp) {
        return Math.sqrt((3 * 8.314 * temp) / pvnrt.getMolarMass()) ;
    }

    private void particleEscaped(){
        for (int i = 0; i < allParticles.size(); i++) {
            if (allParticles.get(i).getCircle().getCenterY() < -50 || allParticles.get(i).getCircle().getCenterX() < -10 || allParticles.get(i).getCircle().getCenterX() > 600){
                canvas.getChildren().remove(allParticles.get(i).getCircle());
                allParticles.remove(allParticles.get(i));
                totalParticleCount--;
                updatePressure();
                pressureGauge.updateGauge();
            }
        }
    }

    private void changeMolarMass(double molarMass) {
        double initialMolarMass = pvnrt.getMolarMass();
        pvnrt.setMolarMass(molarMass);
        // Calculation based on RMS gas speed equation. Changes base particle velocity
        baseParticleVelocity = Math.sqrt((Math.pow(baseParticleVelocity, 2) * initialMolarMass) / molarMass);
        for (BalloonParticle particle : allParticles) {
            double particleVelocity = (baseParticleVelocity * calculateRMS(pvnrt.getTemperature())) / calculateRMS(300);
            particle.setVelocity(particleVelocity);
            setParticleColor(molarMass);
            particle.setCircleColor(particleColor);
            setParticleSize(molarMass);
            particle.setCircleSize(particleSize);
        }
    }

    private void setParticleColor(double molarMass) {
        double hue = (270 * molarMass) / 0.2201;
        particleColor = Color.hsb(hue, 1, 1);
    }

    /**
     * //equation found by having the smallest size of particle to be 9 with molar mass of hydrogen 0.00202kg/mol and
     *         // largest size fo particle to be 15 with molar mass of radon 0.2201k/mol.
     * @param molarMass
     */
    private void setParticleSize(double molarMass) {
        particleSize = 27.52 * molarMass + 8.944;
    }

    private void initializeComboBox() {
        comboBox.setStyle("-fx-text-fill : white");
        comboBox.getItems().addAll("Oxygen", "Radon", "Hydrogen", "Bromine");
        comboBox.setValue("Oxygen");

        comboBox.setOnAction(event -> {
            switch (comboBox.getValue()) {
                case "Oxygen" -> changeMolarMass(0.0320);
                case "Radon" -> changeMolarMass(0.2201);
                case "Hydrogen" -> changeMolarMass(0.00202);
                case "Bromine" -> changeMolarMass(0.0799);
            }
        });
    }

    private void changeVelocityLabel() {
        String velocityValue;
        if (totalParticleCount == 0) {
            velocityValue = "0.00";
        } else {
            velocityValue = String.format("%.2f", calculateRMS(pvnrt.getTemperature()));;
        }
        velocityLabel.setText(velocityValue + " m/s");
    }

    private void changeVolumeLabel() {
        String volumeValue;
        if (pvnrt.getVolume() == 0) {
            volumeValue = "0.00";
        } else {
            volumeValue = String.format("%.2f", pvnrt.getVolume());;
        }
        volumeLabel.setText(volumeValue + " L");
    }

    private void delayCollision(BalloonParticle particle){
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(1000)));
        timeline.setOnFinished(event -> {
            particle.setCollisionDelay(false);
        });
        timeline.setCycleCount(1);
        timeline.play();
    }

}
