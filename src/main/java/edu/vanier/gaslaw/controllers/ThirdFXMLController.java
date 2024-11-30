package edu.vanier.gaslaw.controllers;

import edu.vanier.gaslaw.BalloonParticle;
import edu.vanier.gaslaw.MainApp;
import edu.vanier.gaslaw.calculations.PVnRT;
import edu.vanier.gaslaw.graphics.PressureGauge;
import edu.vanier.gaslaw.graphics.Thermometer;
import javafx.animation.*;
import javafx.beans.binding.Bindings;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
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
    BorderPane borderPane;
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
    Label altitudeLabel;
    @FXML
    Circle circleCanvas;
    @FXML
    StackPane stackPane;

    ArrayList<BalloonParticle> allParticles = new ArrayList<>();
    private Thermometer thermometer;
    private PVnRT pvnrt;
    private PressureGauge pressureGauge;
    private double baseParticleVelocity = 3;
    boolean paused = false;
    private double totalParticleCount;
    public boolean lidPopped = false;
    private Color particleColor;
    private double particleSize;
    private ImageView backgroundImageView;
    private ImageView backgroundImageViewTop;
    private ImageView backgroundImageViewBottom;
    private ImageView flameImageView;
    private Rectangle groundRectangle;
    private double backgroundVelocity = 0.007;
    private Timeline decreaseTempTimeline;
    private Timeline backgroundTimeline;
    private double altitude = 0;
    private boolean isGroundPresent = true;

    @FXML
    public void initialize() {
        logger.info("Initializing MainAppController...");
        btnSwitchScene.setOnAction(this::loadPrimaryScene);

        pvnrt = new PVnRT();
        pvnrt.setMoles(0);
        pvnrt.setVolume(700);

        pressureGauge = new PressureGauge(pvnrt);
        thermometer = new Thermometer(pvnrt);
        gaugeVBox.getChildren().addAll(pressureGauge.getGaugePane(), thermometer.getThermometerPane());

        circleCanvas = new Circle(250, 250, 250, Color.LIGHTYELLOW);
        circleCanvas.setStroke(Color.GRAY);
        initialFunctions();
    }

    private void initialFunctions() {
        initBackground();
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
        gradualTemperatureDecrease();
        altitude();
        for (int i = 0; i < 29; i++) addBalloonParticle();
    }

    private void initBackground(){
        Image backgroundImage = new Image(Objects.requireNonNull
                (getClass().getResource("/seamless-clouds.jpg")).toExternalForm());
        Image basketImage = new Image(Objects.requireNonNull(getClass().getResource("/balloonbasket.png")).toExternalForm());
        ImageView basketImageView = new ImageView(basketImage);
        Image flame = new Image(Objects.requireNonNull(getClass().getResource("/flame-gif.gif")).toExternalForm());
        flameImageView = new ImageView(flame);

        basketImageView.setX(150);
        basketImageView.setY(480);
        basketImageView.setFitWidth(200);
        basketImageView.setFitHeight(300);

        flameImageView.setX(220);
        flameImageView.setY(480);
        flameImageView.setFitWidth(60);
        flameImageView.setFitHeight(60);

        groundRectangle = new Rectangle(1100, 200, Color.GREEN);
        groundRectangle.setLayoutY(750);
        groundRectangle.widthProperty().bind(borderPane.widthProperty().multiply(0.65));
        groundRectangle.layoutXProperty().bind(canvas.widthProperty().subtract(groundRectangle.widthProperty()).divide(2));

        backgroundImageView = new ImageView(backgroundImage);
        backgroundImageView.setFitHeight(1200);
        backgroundImageView.setLayoutY(-300);

        backgroundImageViewTop = new ImageView(backgroundImage);
        backgroundImageViewTop.setFitHeight(1200);
        backgroundImageViewTop.setLayoutY(-1500);

        backgroundImageViewBottom = new ImageView(backgroundImage);
        backgroundImageViewBottom.setFitHeight(1200);
        backgroundImageViewBottom.setLayoutY(900);

        backgroundImageView.fitWidthProperty().bind(borderPane.widthProperty().multiply(0.65));
        backgroundImageViewTop.fitWidthProperty().bind(borderPane.widthProperty().multiply(0.65));
        backgroundImageViewBottom.fitWidthProperty().bind(borderPane.widthProperty().multiply(0.65));
        backgroundImageView.layoutXProperty().bind(canvas.widthProperty().subtract(backgroundImageView.fitWidthProperty()).divide(2));
        backgroundImageViewTop.layoutXProperty().bind((canvas.widthProperty().subtract(backgroundImageViewTop.fitWidthProperty())).divide(2));
        backgroundImageViewBottom.layoutXProperty().bind(canvas.widthProperty().subtract(backgroundImageViewBottom.fitWidthProperty()).divide(2));

        Rectangle upperRectangleBorder = new Rectangle(0, -200, 1100, 150);
        upperRectangleBorder.setFill(Color.BLACK);
        Rectangle lowerRectangleBorder = new Rectangle(0, 0, 1100, 200);
        lowerRectangleBorder.setFill(Color.BLACK);

        //binds lower rectangle to the bottom of the borderPane, binds lower rectangle width to width of borderpane, binds center of lower rectangle to center of canvas
        lowerRectangleBorder.layoutYProperty().bind(borderPane.heightProperty().subtract(125));
        lowerRectangleBorder.widthProperty().bind(borderPane.widthProperty().multiply(0.66));
        lowerRectangleBorder.layoutXProperty().bind(canvas.widthProperty().subtract(lowerRectangleBorder.widthProperty()).divide(2));

        upperRectangleBorder.widthProperty().bind(borderPane.widthProperty().multiply(0.66));
        upperRectangleBorder.layoutXProperty().bind(canvas.widthProperty().subtract(upperRectangleBorder.widthProperty()).divide(2));

        canvas.getChildren().addAll(backgroundImageView, backgroundImageViewTop, backgroundImageViewBottom, basketImageView, groundRectangle, circleCanvas, upperRectangleBorder, lowerRectangleBorder);

        Timeline backgroundVelocityTimeline = new Timeline();
        KeyFrame kf = new KeyFrame(Duration.millis(0.1), event -> {
            calculatingBackgroundVelocity();
        });
        backgroundVelocityTimeline.getKeyFrames().add(kf);
        backgroundVelocityTimeline.setCycleCount(Animation.INDEFINITE);
        backgroundVelocityTimeline.play();

        backgroundTimeline = new Timeline();
        KeyFrame keyFrame = new KeyFrame(Duration.millis(0.1), event -> {
            moveBackground();
            System.out.println(altitude + "altitude");
        });
        backgroundTimeline.getKeyFrames().add(keyFrame);
        backgroundTimeline.setCycleCount(Animation.INDEFINITE);
        backgroundTimeline.play();
    }

    private void gradualTemperatureDecrease() {
        decreaseTempTimeline  = new Timeline();

        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), event -> {
            if (pvnrt.getTemperature() > 300) {
                pvnrt.setTemperature(pvnrt.getTemperature() - 1);
                updatePressure();
                updateParticlesWithTemperature(pvnrt.getTemperature());
                thermometer.updateThermometer();
            }
        });
        decreaseTempTimeline.getKeyFrames().add(keyFrame);
        decreaseTempTimeline.setCycleCount(Animation.INDEFINITE);
        decreaseTempTimeline.play();
    }

    private void calculatingBackgroundVelocity() {
        if (pvnrt.getTemperature() != 300) backgroundVelocity = (301.95 - pvnrt.getTemperature()) / 2000;
        else backgroundVelocity = 0.007;
        if (pvnrt.getTemperature() < 330) circleCanvas.setRadius(250 + ((pvnrt.getTemperature() - 300) / 1.5));
        for (BalloonParticle allParticle : allParticles) allParticle.setCircleCanvas(circleCanvas);
    }

    private void moveBackground(){
        if (backgroundImageViewBottom.getLayoutY() <= -300 || backgroundImageViewTop.getLayoutY() >= -300){
            backgroundImageView.setLayoutY(-300);
            backgroundImageViewTop.setLayoutY(-1500);
            backgroundImageViewBottom.setLayoutY(900);
        }

        backgroundImageView.setLayoutY(backgroundImageView.getLayoutY() - backgroundVelocity);
        backgroundImageViewTop.setLayoutY(backgroundImageViewTop.getLayoutY() - backgroundVelocity);
        backgroundImageViewBottom.setLayoutY(backgroundImageViewBottom.getLayoutY() - backgroundVelocity);
    }

    private void altitude() {
        Timeline altitudeTimeline = new Timeline();
        KeyFrame keyFrame = new KeyFrame(Duration.millis(1000), event -> {
            if (altitude > 0) {
                altitude = altitude - (backgroundVelocity * 100);
                if (altitude <= 0) {
                    altitudeLabel.setText("0.00 m");
                    altitude = 0;
                    if (!isGroundPresent){
                        spawnGround();
                        isGroundPresent = true;
                    }
                } else {
                    String altitudeString;
                    altitudeString = String.format("%.2f", altitude);
                    altitudeLabel.setText(altitudeString + " m");
                    backgroundTimeline.play();
                    if (isGroundPresent) {
                        removeGround();
                        isGroundPresent = false;
                    }
                }
            } else if (altitude == 0 && backgroundVelocity < 0){
                altitude = altitude - (backgroundVelocity * 100);
                String altitudeString;
                altitudeString = String.format("%.2f", altitude);
                altitudeLabel.setText(altitudeString + " m");
            } else if (altitude == 0) {
                backgroundTimeline.stop();
            }
        });
        altitudeTimeline.getKeyFrames().add(keyFrame);
        altitudeTimeline.setCycleCount(Animation.INDEFINITE);
        altitudeTimeline.play();
    }

    private void spawnGround() {
        groundRectangle.setLayoutY(900);
        Timeline groundTimeline = new Timeline();
        KeyFrame keyFrame = new KeyFrame(Duration.millis(50), event -> {
            groundRectangle.setLayoutY(groundRectangle.getLayoutY() - 5);
        });
        groundTimeline.getKeyFrames().add(keyFrame);
        groundTimeline.setCycleCount(30);
        groundTimeline.play();

    }

    private void removeGround() {
        groundRectangle.setLayoutY(750);
        Timeline groundTimeline = new Timeline();
        KeyFrame keyFrame = new KeyFrame(Duration.millis(50), event -> {
            groundRectangle.setLayoutY(groundRectangle.getLayoutY() + 5);
        });
        groundTimeline.getKeyFrames().add(keyFrame);
        groundTimeline.setCycleCount(30);
        groundTimeline.play();

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
            canvas.getChildren().add(flameImageView);
            decreaseTempTimeline.stop();
        });
        heatButton.setOnMouseReleased(event -> {
            heatTimeline.stop();
            circleCanvas.setEffect(null);
            canvas.getChildren().remove(flameImageView);
            decreaseTempTimeline.play();
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

    private void adjustTemperature(double tempChange) {
        double newTemperature = pvnrt.getTemperature() + tempChange;
        if (newTemperature <= 500.15 && newTemperature >= 300) {
            pvnrt.setTemperature(newTemperature);
            thermometer.updateThermometer();
            updateParticlesWithTemperature(newTemperature);
            updatePressure();
        }
    }

    private void add10ParticlesButton() {
        add10.setOnAction(event -> {
            for (int i = 0; i < 10; i++) {
                addBalloonParticle();
            }
        });
    }

    private void addBalloonParticle() {
        if (pvnrt.getTemperature() == 0) {
            pvnrt.setTemperature(300);
            updateParticlesWithTemperature(300);
        }
        if (!paused) {
            totalParticleCount++;
            updatePressure();
            setParticleColor(pvnrt.getMolarMass());
            setParticleSize(pvnrt.getMolarMass());
            double particleVelocity = (baseParticleVelocity * calculateRMS(pvnrt.getTemperature())) / calculateRMS(300);
            thermometer.updateThermometer();

            Circle circle = new Circle(canvas.getMaxWidth()/2, canvas.getMaxHeight()/2, particleSize, particleColor);
            InnerShadow innerShadow = new InnerShadow();
            innerShadow.setOffsetX(-4);
            innerShadow.setOffsetY(-4);
            innerShadow.setColor(Color.LIGHTBLUE);
            circle.setEffect(innerShadow);

            BalloonParticle balloonParticle = new BalloonParticle(circle, particleVelocity, canvas, circleCanvas);
            balloonParticle.createTimeline();
            balloonParticle.play();
            canvas.getChildren().add(balloonParticle.getCircle());
            allParticles.add(balloonParticle);
            System.out.println(balloonParticle.velocityX + " " + balloonParticle.velocityY);
        }
    }

    private void particleCollisionTimeline() {
        Timeline elasticCollisionTimeline = new Timeline();
        KeyFrame keyframe = new KeyFrame(
                Duration.millis(0.1),
                (event -> {
                    checkParticleParticleCollision(allParticles);
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
            addBalloonParticle();
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
        double hue = (270 * molarMass) / 0.03200;
        particleColor = Color.hsb(hue, 0.8, 1);
    }

    /**
     * //equation found by having the smallest size of particle to be 9 with molar mass of hydrogen 0.00202kg/mol and
     *         // Largest size of particle to be 15 with molar mass of radon 0.2201k/mol.
     * @param molarMass
     */
    private void setParticleSize(double molarMass) {
        particleSize = 27.52 * molarMass + 8.944;
    }

    private void initializeComboBox() {
        comboBox.setStyle("-fx-text-fill : white");
        comboBox.getItems().addAll("Oxygen", "Nitrogen", "Hydrogen", "Helium");
        comboBox.setValue("Oxygen");

        comboBox.setOnAction(event -> {
            switch (comboBox.getValue()) {
                case "Oxygen" -> changeMolarMass(0.0320);
                case "Helium" -> changeMolarMass(0.004001);
                case "Hydrogen" -> changeMolarMass(0.00202);
                case "Nitrogen" -> changeMolarMass(0.02502);
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
            volumeValue = String.format("%.2f", pvnrt.getVolume());
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
