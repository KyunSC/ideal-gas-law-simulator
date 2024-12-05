package edu.vanier.gaslaw.controllers;

import edu.vanier.gaslaw.BalloonParticle;
import edu.vanier.gaslaw.MainApp;
import edu.vanier.gaslaw.calculations.PVnRT;
import edu.vanier.gaslaw.graphics.PressureGauge;
import edu.vanier.gaslaw.graphics.Thermometer;
import javafx.animation.*;
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
 * FXML controller class for a hot air balloon simulation scene.
 * Handles particle collisions background movement, temperature control, altitude changes,
 * and molar mass and particle velocity adjustments based on ideal gas laws.
 *
 */
public class BalloonFXMLController {

    private final static Logger logger = LoggerFactory.getLogger(IdealGasFXMLController.class);

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
    Circle circleCanvas;

    ArrayList<BalloonParticle> allParticles = new ArrayList<>();
    private Thermometer thermometer;
    private PVnRT pvnrt;
    private PressureGauge pressureGauge;
    private double baseParticleVelocity = 3.5;
    boolean paused = false;
    private double totalParticleCount;
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
    private Timeline altitudeTimeline;
    private double altitude = 0;
    private boolean isGroundPresent = true;
    private Label altitudeLabel;
    private boolean isSpawningGround = false;
    private boolean isRemovingGround = false;

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

    /**
     * Contains all the methods that must be initialized to run the simulation.
     */
    private void initialFunctions() {
        initBackground();
        addParticlesButton();
        add10ParticlesButton();
        remove1Button();
        remove10Button();
        pauseFunction();
        playFunction();
        nextFrameFunction();
        resetButton();
        particleCollisionTimeline();
        setupTemperatureControls();
        initializeComboBox();
        gradualTemperatureDecrease();
        altitude();
        updateLabels();
        for (int i = 0; i < 29; i++) addBalloonParticle();
    }

    /**
     * Updates the velocity and volume labels using a timeline animation.
     */
    private void updateLabels() {
        Timeline updateTimeline = new Timeline();
        KeyFrame keyFrame = new KeyFrame(Duration.millis(100), event -> {
            changeVelocityLabel();
            changeVolumeLabel();
        });
        updateTimeline.getKeyFrames().add(keyFrame);
        updateTimeline.setCycleCount(Animation.INDEFINITE);
        updateTimeline.play();
    }

    /**
     * Initializes the background elements of the simulation such as a sky background, basket and
     * flame for the air balloon and ground.
     * Also provides timeline for the scrolling background and for determining the velocity at which it scrolls.
     */
    private void initBackground(){
        Image backgroundCloudImage = new Image(Objects.requireNonNull(getClass().getResource("/seamless-clouds.jpg")).toExternalForm());
        Image basketImage = new Image(Objects.requireNonNull(getClass().getResource("/balloonbasket.png")).toExternalForm());
        ImageView basketImageView = new ImageView(basketImage);
        Image flame = new Image(Objects.requireNonNull(getClass().getResource("/flame-gif.gif")).toExternalForm());
        flameImageView = new ImageView(flame);
        groundRectangle = new Rectangle(1100, 300, Color.GREEN);
        Label altitudeTitleLabel = new Label();
        altitudeLabel = new Label();

        basketImageView.setX(150);
        basketImageView.setY(480);
        basketImageView.setFitWidth(200);
        basketImageView.setFitHeight(300);

        flameImageView.setX(220);
        flameImageView.setY(480);
        flameImageView.setFitWidth(60);
        flameImageView.setFitHeight(60);

        groundRectangle.setLayoutY(750);
        groundRectangle.widthProperty().bind(borderPane.widthProperty().multiply(0.65));
        groundRectangle.layoutXProperty().bind(canvas.widthProperty().subtract(groundRectangle.widthProperty()).divide(2));

        altitudeTitleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 23px;;");
        altitudeTitleLabel.setText("Altitude:");
        altitudeTitleLabel.setLayoutX(-150);
        altitudeTitleLabel.setLayoutY(-25);

        altitudeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 28px;;");
        altitudeLabel.setText("0.00 m");
        altitudeLabel.setLayoutX(-150);
        altitudeLabel.setLayoutY(0);

        backgroundImageView = new ImageView(backgroundCloudImage);
        backgroundImageView.setFitHeight(1200);
        backgroundImageView.setLayoutY(-300);

        backgroundImageViewTop = new ImageView(backgroundCloudImage);
        backgroundImageViewTop.setFitHeight(1200);
        backgroundImageViewTop.setLayoutY(-1500);

        backgroundImageViewBottom = new ImageView(backgroundCloudImage);
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

        lowerRectangleBorder.layoutYProperty().bind(borderPane.heightProperty().subtract(125));
        lowerRectangleBorder.widthProperty().bind(borderPane.widthProperty().multiply(0.66));
        lowerRectangleBorder.layoutXProperty().bind(canvas.widthProperty().subtract(lowerRectangleBorder.widthProperty()).divide(2));

        upperRectangleBorder.widthProperty().bind(borderPane.widthProperty().multiply(0.66));
        upperRectangleBorder.layoutXProperty().bind(canvas.widthProperty().subtract(upperRectangleBorder.widthProperty()).divide(2));

        canvas.getChildren().addAll(backgroundImageView, backgroundImageViewTop, backgroundImageViewBottom, basketImageView, groundRectangle, circleCanvas, upperRectangleBorder, lowerRectangleBorder, altitudeTitleLabel, altitudeLabel);

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
        });
        backgroundTimeline.getKeyFrames().add(keyFrame);
        backgroundTimeline.setCycleCount(Animation.INDEFINITE);
        backgroundTimeline.play();
    }

    /**
     * Periodically decreases the temperature of the simulation environment. If
     * the temperature is above 300K, this method reduces the temperature, updates
     * the pressure, adjusts particle velocities, and refreshes the thermometer.
     */
    private void gradualTemperatureDecrease() {
        decreaseTempTimeline  = new Timeline();

        KeyFrame keyFrame = new KeyFrame(Duration.millis(500), event -> {
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

    /**
     * Calculates the velocity of the background based on the temperature.
     * Updates the size of the balloon based on the temperature until a temperature of 330K is reached
     */
    private void calculatingBackgroundVelocity() {
        if (pvnrt.getTemperature() != 300) backgroundVelocity = (301.95 - pvnrt.getTemperature()) / 2000;
        else backgroundVelocity = 0.007;
        if (pvnrt.getTemperature() < 330) circleCanvas.setRadius(250 + ((pvnrt.getTemperature() - 300) / 1.5));
        for (BalloonParticle allParticle : allParticles) allParticle.setCircleCanvas(circleCanvas);
    }

    /**
     * Moves the background images do simulate the upward or downward movement of the balloon.
     * Resets the positions of background elements when they reach a certain layout y-value
     * to create a seamless scrolling effect.
     */

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

    /**
     * Updates the altitude of the balloon based on the
     * background velocity. Handles conditions for removing
     * the ground when altitude goes above 0 and spawning it back when the altitude returns to 0.
     */
    private void altitude() {
        altitudeTimeline = new Timeline();
        KeyFrame keyFrame = new KeyFrame(Duration.millis(100), event -> {
            if (altitude > 0) {
                altitude = altitude - (backgroundVelocity * 25);
                if (altitude < 0) {
                    altitude = 0;
                } else {
                    String altitudeString;
                    altitudeString = String.format("%.2f", altitude);
                    altitudeLabel.setText(altitudeString + " m");
                    backgroundTimeline.play();
                    if (isGroundPresent && !isSpawningGround) {
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
                altitudeLabel.setText("0.00 m");
                if (!isGroundPresent && !isRemovingGround){
                        spawnGround();
                        isGroundPresent = true;
                }
                backgroundTimeline.stop();
            }
        });
        altitudeTimeline.getKeyFrames().add(keyFrame);
        altitudeTimeline.setCycleCount(Animation.INDEFINITE);
        altitudeTimeline.play();
    }

    /**
     * Animates the ground appearing when the balloon reaches an altitude of 0.
     */
    private void spawnGround() {
        isSpawningGround = true;
            groundRectangle.setLayoutY(900);
            Timeline groundTimeline = new Timeline();
            KeyFrame keyFrame = new KeyFrame(Duration.millis(25), event -> {
                groundRectangle.setLayoutY(groundRectangle.getLayoutY() - 5);
            });
            groundTimeline.getKeyFrames().add(keyFrame);
            groundTimeline.setCycleCount(30);
            groundTimeline.play();
            groundTimeline.setOnFinished(event -> {
                isSpawningGround = false;
            });
    }

    /**
     * Animates the ground disappearing when the balloon reaches an altitude above 0.
     */
    private void removeGround() {
        isRemovingGround = true;
            groundRectangle.setLayoutY(750);
            Timeline groundTimeline = new Timeline();
            KeyFrame keyFrame = new KeyFrame(Duration.millis(50), event -> {
                groundRectangle.setLayoutY(groundRectangle.getLayoutY() + 5);
            });
            groundTimeline.getKeyFrames().add(keyFrame);
            groundTimeline.setCycleCount(30);
            groundTimeline.play();
            groundTimeline.setOnFinished(event -> {
                isRemovingGround = false;
            });
    }

    /**
     * Sets up temperature controls for heating and cooling,
     * including a visual drop shadow to show heating and cooling glow.
     */
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

    /**
     * Updates the velocity of particles based on the given temperature.
     *
     * @param newTemperature The new temperature in Kelvin.
     */
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

    /**
     * Adjusts the temperature by a specified change and updates gauges and particle velocity.
     *
     * @param tempChange The change in temperature in Kelvin.
     */
    private void adjustTemperature(double tempChange) {
        double newTemperature = pvnrt.getTemperature() + tempChange;
        if (newTemperature <= 473.15 && newTemperature >= 300) {
            pvnrt.setTemperature(newTemperature);
            thermometer.updateThermometer();
            updateParticlesWithTemperature(newTemperature);
            updatePressure();
        }
    }

    /**
     * Adds 10 balloon particles to the canvas when the associated button is pressed.
     */
    private void add10ParticlesButton() {
        add10.setOnAction(event -> {
            for (int i = 0; i < 10; i++) {
                addBalloonParticle();
            }
        });
    }

    /**
     * Adds 1 balloon particle to the canvas when the associated button is pressed.
     */
    private void addParticlesButton(){
        add.setOnAction(event -> {
            addBalloonParticle();
        });
    }

    /**
     * Adds a single balloon particle to the canvas.
     */
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

    /**
     * Resets the simulation, returning the balloon particle number to 29,
     * setting the temperature to 300, setting altitude to 0 and updating the gauges accordingly.
     */
    private void resetButton(){
        reset.setOnAction(event -> {
            if (!paused) {
                for (int i = 0; i < allParticles.size(); i++)
                    canvas.getChildren().remove(allParticles.get(i).getCircle());
                allParticles.clear();
                totalParticleCount = 0;

                for (int i = 0; i < 29; i++) addBalloonParticle();
                altitude = 0;
                pvnrt.setTemperature(300);
                updatePressure();
                pressureGauge.updateGauge();
                thermometer.updateThermometer();
            }
        });

    }

    /**
     * Pauses the simulation, pausing animations and timelines.
     */
    private void pauseFunction(){
        pause.setOnAction(event -> {
            paused = true;
            decreaseTempTimeline.pause();
            altitudeTimeline.pause();
            backgroundTimeline.pause();
            for (int i = 0; i < allParticles.size(); i++) {
                allParticles.get(i).pause();
            }
        });
    }

    /**
     * Pauses the simulation, playing animations and timelines.
     */
    private void playFunction(){
        play.setOnAction(event -> {
            paused = false;
            decreaseTempTimeline.play();
            altitudeTimeline.play();
            backgroundTimeline.play();
            for (int i = 0; i < allParticles.size(); i++) {
                allParticles.get(i).play();
            }
        });
    }

    /**
     * Pauses the simulation and subsequently plays the animation for an incredibly short duration
     * to get a "frame by frame" view of the simulation.
     */
    private void nextFrameFunction(){
        Timeline timeline = new Timeline();
        KeyFrame keyFrame = new KeyFrame(
                Duration.millis(1),
                event1 -> {
                    decreaseTempTimeline.play();
                    altitudeTimeline.play();
                    backgroundTimeline.play();
                    for (BalloonParticle allParticle : allParticles) {
                        allParticle.play();
                    }
                }
        );
        timeline.setOnFinished(event1 -> {
            decreaseTempTimeline.play();
            altitudeTimeline.play();
            backgroundTimeline.play();
            for (BalloonParticle allParticle : allParticles) {
                allParticle.play();
            }
            decreaseTempTimeline.pause();
            altitudeTimeline.pause();
            backgroundTimeline.pause();
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
     * Checks if there are any particles available to delete.
     * Will only remove particles if there is more than 29 balloon particles.
     */
    private void remove1(){
        if (allParticles.size() > 29 ) {
            canvas.getChildren().remove(allParticles.getLast().getCircle());
            allParticles.removeLast();
            totalParticleCount--;
            updatePressure();
            pressureGauge.updateGauge();
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

    /**
     * Creates a timeline that checks for particle-particle collisions
     */
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

    /**
     * Checks for collisions between particles and adjusts their velocities accordingly.
     *
     * @param targetListOfParticles The list of particles to check for collisions.
     */
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

    /**
     * Updates the pressure gauge based on the current number of particles
     */
    private void updatePressure() {
        if (allParticles.isEmpty())pvnrt.setMoles(totalParticleCount);
        else pvnrt.setMoles(totalParticleCount);
        pressureGauge.updateGauge();
    }

    /**
     * Calculates the root mean square (RMS) velocity based on temperature.
     * This value is used for the velocity of the particles.
     *
     * @param temp The temperature in Kelvin.
     * @return The RMS velocity in meters per second.
     */
    private double calculateRMS(double temp) {
        return Math.sqrt((3 * 8.314 * temp) / pvnrt.getMolarMass()) ;
    }

    /**
     * Changes the molar mass of the particles and updates their size, color and speed accordingly..
     * Calculation based on making two RMS gas speed equation of different molar mass equal to each other.
     * Changes base particle velocity
     *
     * @param molarMass The new molar mass in kilograms per mole.
     */
    private void changeMolarMass(double molarMass) {
        double initialMolarMass = pvnrt.getMolarMass();
        pvnrt.setMolarMass(molarMass);
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

    /**
     * Sets the color of the particle. Changes particle's hue based on its molar mass.
     * @param molarMass
     */
    private void setParticleColor(double molarMass) {
        double hue = (270 * molarMass) / 0.03200;
        particleColor = Color.hsb(hue, 0.8, 1);
    }

    /**
     * Sets the size of the particles based on their molar mass.
     * Equation found by having the smallest size of particle to be 9 with molar mass of hydrogen 0.00202kg/mol and
     * Largest size of particle to be 15 with molar mass of radon 0.2201k/mol.
     * @param molarMass
     */
    private void setParticleSize(double molarMass) {
        particleSize = 27.52 * molarMass + 8.944;
    }

    /**
     * Initializes the combo box for selecting gas types and updates particle molar mass accordingly.
     */
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

    /**
     * Updates the velocity label to display the current RMS velocity of the particles.
     */
    private void changeVelocityLabel() {
        String velocityValue;
        if (totalParticleCount == 0) {
            velocityValue = "0.00";
        } else {
            velocityValue = String.format("%.2f", calculateRMS(pvnrt.getTemperature()));;
        }
        velocityLabel.setText(velocityValue + " m/s");
    }

    /**
     * Updates the volume label to display the current volume of the system.
     */
    private void changeVolumeLabel() {
        String volumeValue;
        if (pvnrt.getVolume() == 0) {
            volumeValue = "0.00";
        } else {
            volumeValue = String.format("%.2f", pvnrt.getVolume());
        }
        volumeLabel.setText(volumeValue + " L");
    }

    /**
     * Introduces a delay between particle collisions to prevent particles from overlapping and bunching up.
     *
     * @param particle The particle to delay collisions for.
     */
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
