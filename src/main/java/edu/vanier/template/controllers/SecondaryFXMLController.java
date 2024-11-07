package edu.vanier.template.controllers;

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
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

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
    Button heatButton;
    @FXML
    Button coolButton;
    @FXML
    Line lid;
    @FXML
    ComboBox<String> comboBox;

    ArrayList<Particle> allParticles = new ArrayList<>();
    ArrayList<Particle> listOfParticles = new ArrayList<>();
    ArrayList<Particle> firstListOfParticles = new ArrayList<>();
    ArrayList<Particle> secondListOfParticles = new ArrayList<>();
    ArrayList<Particle> thirdListOfParticles = new ArrayList<>();
    ArrayList<Particle> fourthListOfParticles = new ArrayList<>();
    private Thermometer thermometer;
    private PVnRT pvnrt;
    private PressureGauge pressureGauge;
    private double baseParticleVelocity = 3;
    boolean paused = false;
    private double totalParticleCount;
    private double maxPressure = 1000;
    public boolean lidPopped = false;
    private Color particleColor;

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
        setupTemperatureControls();
        initializeVolumeSlider();
        lidPopping();
        initializeComboBox();
    }

    private void setupTemperatureControls() {
        Timeline heatTimeline = new Timeline(new KeyFrame(Duration.millis(100), event -> adjustTemperature(1)));
        heatTimeline.setCycleCount(Animation.INDEFINITE);

        heatButton.setOnMousePressed(event -> heatTimeline.play());
        heatButton.setOnMouseReleased(event -> heatTimeline.stop());

        Timeline coolTimeline = new Timeline(new KeyFrame(Duration.millis(100), event -> adjustTemperature(-1)));
        coolTimeline.setCycleCount(Animation.INDEFINITE);

        coolButton.setOnMousePressed(event -> coolTimeline.play());
        coolButton.setOnMouseReleased(event -> coolTimeline.stop());
    }

    private void updateParticlesWithTemperature(double newTemperature) {
        if (newTemperature <= 0) {
            for (Particle particle : allParticles) {
               particle.setVelocity(0);
                System.out.println(particle.getVelocity());
            }
        } else {
            for (Particle particle : allParticles) {
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

    private void addToQuadrants(){
        Timeline timeline = new Timeline();
        KeyFrame kf = new KeyFrame(
                Duration.millis(1),
                event -> {
                    addFirstQuadrant();
                    addToSecondThirdFourth(listOfParticles, secondListOfParticles);
                    addToSecondThirdFourth(listOfParticles, thirdListOfParticles);
                    addToSecondThirdFourth(listOfParticles, fourthListOfParticles);
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
            double particleVelocity = (baseParticleVelocity * calculateRMS(pvnrt.getTemperature())) / calculateRMS(300);
            Circle circle = new Circle(11, 11, 10, particleColor);
            Particle particle = new Particle(circle, particleVelocity, canvas, lid);
            particle.createTimeline();
            particle.play();
            canvas.getChildren().add(particle.getCircle());
            listOfParticles.add(particle);
            allParticles.add(particle);
            thermometer.updateThermometer();
        }
    }

    private void particleCollisionTimeline() {
        Timeline elasticCollisionTimeline = new Timeline();
        KeyFrame keyframe = new KeyFrame(
                Duration.millis(10),
                (event -> {
                    checkParticleParticleCollision(firstListOfParticles);
                    checkParticleParticleCollision(secondListOfParticles);
                    checkParticleParticleCollision(thirdListOfParticles);
                    checkParticleParticleCollision(fourthListOfParticles);
                    particleEscaped();
                    particleEscaped();
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
            firstListOfParticles.clear();
            secondListOfParticles.clear();
            thirdListOfParticles.clear();
            fourthListOfParticles.clear();
            totalParticleCount = 0;
            pvnrt.setMoles(0);
            updatePressure();
            pressureGauge.updateGauge();
            thermometer.updateThermometer();

            if (lidPopped){
                lid = makingLid();
                canvas.getChildren().add(lid);
                lidPopped = false;
                canvas.setBorder(new Border(new BorderStroke(null, null, null, null, BorderStrokeStyle.SOLID, null, null, null, new CornerRadii(10), null, null)));
            }
            volumeSlider.setValue(10);
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
                paused = true;
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
            if (firstListOfParticles.contains(allParticles.getLast())) firstListOfParticles.remove(allParticles.getLast());
            if (secondListOfParticles.contains(allParticles.getLast())) firstListOfParticles.remove(allParticles.getLast());
            if (thirdListOfParticles.contains(allParticles.getLast())) firstListOfParticles.remove(allParticles.getLast());
            if (fourthListOfParticles.contains(allParticles.getLast())) firstListOfParticles.remove(allParticles.getLast());
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
            }
        }
        else {
            pvnrt.setPressure(0);
            pvnrt.setMoles(0);
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
                remove1();
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
                    //targetListOfParticles.get(i).velocityY *= -1;
                    targetListOfParticles.get(j).velocityY *= -1;
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

    private void initializeVolumeSlider() {
        volumeSlider.valueProperty().addListener(((observable, oldValue, newValue) -> {
            canvas.setPrefWidth((volumeSlider.getValue()/volumeSlider.getMax()) * canvas.getMaxWidth());
            pvnrt.setVolume(volumeSlider.getValue());
            updatePressure();
            lid.setEndX(-250 + (430 * (volumeSlider.getValue()/10)) - 20);
        } ));
    }

    private double calculateRMS(double temp) {
        return Math.sqrt((3 * 8.314 * temp) / pvnrt.getMolarMass()) ;
    }

    private void lidPopping(){
        Timeline timeline = new Timeline();
        KeyFrame keyFrame = new KeyFrame(
                Duration.millis(1000),
                event -> {
                    if (pvnrt.getPressure() > maxPressure && lidPopped == false) {
                        lidPopped = true;
                        RotateTransition rotate = new RotateTransition(Duration.millis(1000), lid);
                        rotate.setByAngle(100);
                        rotate.setCycleCount(1);
                        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(1000), lid);
                        translateTransition.setToY(-1000);
                        translateTransition.setToX(1000);
                        translateTransition.setCycleCount(1);
                        ParallelTransition parallelTransition = new ParallelTransition(lid, rotate, translateTransition);
                        parallelTransition.setCycleCount(1);
                        parallelTransition.play();
                        parallelTransition.setOnFinished(event1 -> {
                            canvas.getChildren().remove(lid);
                        });
                        canvas.setBorder(new Border(new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, new CornerRadii(10), null, null)));
                    }
                }
        );
        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private Line makingLid(){
        Line line = new Line();
        line.setEndX(160);
        line.setLayoutX(260);
        line.setStartX(-240);
        line.setStrokeWidth(22);
        return line;
    }

    private void particleEscaped(){
        for (int i = 0; i < allParticles.size(); i++) {
            if (allParticles.get(i).getCircle().getCenterY() < 0){
                if (firstListOfParticles.contains(allParticles.get(i))) firstListOfParticles.remove(allParticles.get(i));
                if (secondListOfParticles.contains(allParticles.get(i))) firstListOfParticles.remove(allParticles.get(i));
                if (thirdListOfParticles.contains(allParticles.get(i))) firstListOfParticles.remove(allParticles.get(i));
                if (fourthListOfParticles.contains(allParticles.get(i))) firstListOfParticles.remove(allParticles.get(i));
                allParticles.remove(i);
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
        for (Particle particle : allParticles) {
            double particleVelocity = (baseParticleVelocity * calculateRMS(pvnrt.getTemperature())) / calculateRMS(300);
            particle.setVelocity(particleVelocity);
            setParticleColor(molarMass);
            particle.setCircleColor(particleColor);
        }

    }

    private void setParticleColor(double molarMass) {
        double hue = (270 * molarMass) / 0.2201;
        particleColor = Color.hsb(hue, 1, 1);
    }

    private void initializeComboBox() {
        comboBox.getItems().addAll("Oxygen", "Radon", "Hydrogen");
        comboBox.setValue("Oxygen");

        comboBox.setOnAction(event -> {
            switch (comboBox.getValue()) {
                case "Oxygen" -> changeMolarMass(0.0320);
                case "Radon" -> changeMolarMass(0.2201);
                case "Hydrogen" -> changeMolarMass(0.00202);
            }
        });
    }

}
