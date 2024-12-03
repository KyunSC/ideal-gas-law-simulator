package edu.vanier.gaslaw.controllers;

import edu.vanier.gaslaw.MainApp;
import edu.vanier.gaslaw.Particle;
import edu.vanier.gaslaw.calculations.PVnRT;
import edu.vanier.gaslaw.graphics.PressureGauge;
import edu.vanier.gaslaw.graphics.Thermometer;
import javafx.animation.*;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * FXML controller class for a secondary scene. Contains all the FXML objects
 * that will need behaviors allParticle Arraylist used to keep all the particles
 * Pane is separated in 4 quadrants Top Left is Quadrant 1, Top Right is
 * Quadrant 2, Bottom Left is Quadrant 3, Bottom Right is Quadrant 4 Quadrants
 * are used to be more effective when detecting collisions between Particles
 *
 */
public class IdealGasFXMLController {

    private final static Logger logger = LoggerFactory.getLogger(IdealGasFXMLController.class);

    @FXML
    Button btnSwitchScene;
    @FXML
    Button add;
    @FXML
    Button add10;
    @FXML
    Pane animationPane;
    @FXML
    BorderPane borderPane;
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
    ImageView lid;
    @FXML
    ComboBox<String> comboBox;
    @FXML
    Label velocityLabel;
    @FXML
    Button lidButton;
    @FXML
    Label volumeLabel;
    @FXML
    ImageView horizontalSlider;

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
    private double particleSize;

    Image lidImage = new Image("/LidContainer.png");
    Rectangle cover;

    @FXML
    public void initialize() {
        logger.info("Initializing MainAppController...");
        btnSwitchScene.setOnAction(this::loadPrimaryScene);

        pvnrt = new PVnRT();
        pvnrt.setMoles(0);

        pressureGauge = new PressureGauge(pvnrt);
        thermometer = new Thermometer(pvnrt);
        gaugeVBox.getChildren().addAll(pressureGauge.getGaugePane(), thermometer.getThermometerPane());

        lid.setImage(lidImage);
        animationPane.getChildren().add(lid);
        initialFunctions();
    }

    private void initialFunctions() {
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
        returnLid();
        update();
        windowResize();
    }

    /**
     *
     * Event handler for the lid that works at a certain x, y
     * Listeners to change the borderPane width and height
     * Adjusts the lid width accordingly
     * Puts the volume change icon on the pane at the middle of the pane on the right
     *
     */
    private void windowResize(){
        animationPane.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getX() < animationPane.getWidth() - 200 && event.getX() > 0 && event.getY() < 100) {
                    lid.setLayoutX((animationPane.getWidth() - lid.getFitWidth() - 25));
                    lid.setFitWidth(animationPane.getWidth() - event.getX());
                }
            }
        });
        borderPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            animationPane.setPrefWidth(newValue.doubleValue());
            animationPane.setMaxWidth(newValue.doubleValue());
            lid.setFitWidth(animationPane.getMaxWidth() - 405);
        });
        borderPane.heightProperty().addListener((observable, oldValue, newValue) -> {
            animationPane.setMaxHeight(newValue.doubleValue());
        });
        horizontalSlider.layoutXProperty().bind(animationPane.widthProperty().subtract(36));
        horizontalSlider.layoutYProperty().bind(animationPane.heightProperty().divide(2));
    }

    private void update() {
        Timeline updateTimeline = new Timeline();
        KeyFrame keyFrame = new KeyFrame(Duration.millis(100), event -> {
            changeVelocityLabel();
            changeVolumeLabel();
            pvnrt.setVolume(((animationPane.getWidth()) / 1540) * 10);
            updatePressure();
        });
        updateTimeline.getKeyFrames().add(keyFrame);
        updateTimeline.setCycleCount(Animation.INDEFINITE);
        updateTimeline.play();
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
            animationPane.setEffect(heatGlow);
        });
        heatButton.setOnMouseReleased(event -> {
            heatTimeline.stop();
            animationPane.setEffect(null);
        });

        Timeline coolTimeline = new Timeline(new KeyFrame(Duration.millis(100), event -> adjustTemperature(-1)));
        coolTimeline.setCycleCount(Animation.INDEFINITE);

        coolButton.setOnMousePressed(event -> {
            coolTimeline.play();
            animationPane.setEffect(coolGlow);
        });
        coolButton.setOnMouseReleased(event -> {
            coolTimeline.stop();
            animationPane.setEffect(null);
        });
    }

    private void updateParticlesWithTemperature(double newTemperature) {
        if (newTemperature <= 0) {
            for (Particle particle : allParticles) {
                particle.setVelocity(0);
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

    /**
     * Timeline that calls the addFirstQuadrant and the addSecondThirdFourth methods to make the particles change quadrants
     */
    private void addToQuadrants() {
        Timeline timeline = new Timeline();
        KeyFrame kf = new KeyFrame(
                Duration.millis(1),
                event -> {
                    addFirstQuadrant(secondListOfParticles);
                    addFirstQuadrant(thirdListOfParticles);
                    addFirstQuadrant(fourthListOfParticles);
                    addToSecondThirdFourth(listOfParticles);
                }
        );
        timeline.getKeyFrames().add(kf);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    /**
     *
     * @param targetListOfParticles, the initial list of particles from the beginning
     *
     *
     */
    private void addFirstQuadrant(ArrayList<Particle> targetListOfParticles) {
        for (int i = 0; i < targetListOfParticles.size(); i++) {
            if (targetListOfParticles.get(i).getCircle().getCenterX() < animationPane.getWidth() / 2 && targetListOfParticles.get(i).getCircle().getCenterY() < animationPane.getHeight() / 2) {
                firstListOfParticles.add(targetListOfParticles.get(i));
                targetListOfParticles.remove(targetListOfParticles.get(i));
            }
        }
    }

    private void addToSecondThirdFourth(ArrayList<Particle> listOfParticles) {
        for (int i = 0; i < listOfParticles.size(); i++) {
            if (listOfParticles.get(i).getCircle().getCenterX() > animationPane.getWidth() / 2 && listOfParticles.get(i).getCircle().getCenterY() < animationPane.getHeight() / 2) {
                secondListOfParticles.add(listOfParticles.get(i));
                listOfParticles.remove(listOfParticles.get(i));
            }
            if (listOfParticles.get(i).getCircle().getCenterX() < animationPane.getWidth() / 2 && listOfParticles.get(i).getCircle().getCenterY() > animationPane.getHeight() / 2) {
                thirdListOfParticles.add(listOfParticles.get(i));
                listOfParticles.remove(listOfParticles.get(i));
            }
            if (listOfParticles.get(i).getCircle().getCenterX() > animationPane.getWidth() / 2 && listOfParticles.get(i).getCircle().getCenterY() > animationPane.getHeight() / 2) {
                fourthListOfParticles.add(listOfParticles.get(i));
                listOfParticles.remove(listOfParticles.get(i));
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

            Circle circle = new Circle(20, 20, particleSize, particleColor);
            InnerShadow innerShadow = new InnerShadow();
            innerShadow.setOffsetX(-4);
            innerShadow.setOffsetY(-4);
            innerShadow.setColor(Color.BLACK);
            circle.setEffect(innerShadow);

            Particle particle = new Particle(circle, particleVelocity, animationPane, lid);
            if (animationPane.getChildren().contains(cover)) particle.setCover(cover);
            else particle.setCover(new Rectangle(0,0,0,0));
            particle.createTimeline();
            particle.play();
            animationPane.getChildren().add(particle.getCircle());
            firstListOfParticles.add(particle);
            allParticles.add(particle);
            thermometer.updateThermometer();
        }
    }

    private void particleCollisionTimeline() {
        Timeline elasticCollisionTimeline = new Timeline();
        KeyFrame keyframe = new KeyFrame(
                Duration.millis(0.1),
                (event -> {
                    checkParticleParticleCollision(firstListOfParticles);
                    checkParticleParticleCollision(secondListOfParticles);
                    checkParticleParticleCollision(thirdListOfParticles);
                    checkParticleParticleCollision(fourthListOfParticles);
                    particleEscaped();
                })
        );
        elasticCollisionTimeline.getKeyFrames().add(keyframe);
        elasticCollisionTimeline.setCycleCount(Animation.INDEFINITE);
        elasticCollisionTimeline.play();
    }

    private void addParticlesButton() {
        add.setOnAction(event -> {
            addParticle();
        });
    }

    private void resetButton() {
        reset.setOnAction(event -> {
            for (int i = 0; i < allParticles.size(); i++) animationPane.getChildren().remove(allParticles.get(i).getCircle());
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

            if (lidPopped) {
                lid = makingLid();
                animationPane.getChildren().add(lid);
                for (Particle allParticle : allParticles) allParticle.setLid(lid);
                lidPopped = false;
                animationPane.getChildren().remove(cover);
            }
        });
    }

    private void pauseFunction() {
        pause.setOnAction(event -> {
            paused = true;
            for (Particle allParticle : allParticles) allParticle.pause();
        });
    }

    private void playFunction() {
        play.setOnAction(event -> {
            paused = false;
            for (Particle allParticle : allParticles) allParticle.play();
        });
    }

    private void fastForwardFunction() {
        Timeline timeline = new Timeline();
        KeyFrame keyFrame = new KeyFrame(
                Duration.millis(1),
                event1 -> {
                    for (Particle allParticle : allParticles) allParticle.play();
                }
        );
        timeline.setOnFinished(event1 -> {
            for (Particle allParticle : allParticles) allParticle.play();
            for (int i = 0; i < allParticles.size(); i++) {
                allParticles.get(i).pause();
                paused = true;
            }
        });
        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(10);
        fastForward.setOnAction(event -> timeline.play());
    }

    /**
     * Function reused by the buttons remove 1 and 10 Checks if there are any
     * particles available to delete If so, it removes the last circle in the
     * list Checks for the deleted particle to delete from its quadrant Then it
     * removes it from the general list Updates the particle count, temperature
     * and pressure If the general list is empty, then the all variables should
     * be at 0
     */
    private void remove1() {
        if (!allParticles.isEmpty()) {
            animationPane.getChildren().remove(allParticles.getLast().getCircle());
            firstListOfParticles.remove(allParticles.getLast());
            secondListOfParticles.remove(allParticles.getLast());
            thirdListOfParticles.remove(allParticles.getLast());
            fourthListOfParticles.remove(allParticles.getLast());
            allParticles.removeLast();
            totalParticleCount--;
            updatePressure();
            pressureGauge.updateGauge();
            if (allParticles.isEmpty()) {
                pvnrt.setPressure(0);
                pvnrt.setMoles(0);
                pressureGauge.updateGauge();
                pvnrt.setTemperature(0);
                thermometer.updateThermometer();
                allParticles.clear();
                firstListOfParticles.clear();
                secondListOfParticles.clear();
                thirdListOfParticles.clear();
                fourthListOfParticles.clear();
            }
        } else {
            pvnrt.setPressure(0);
            pvnrt.setMoles(0);
            pressureGauge.updateGauge();
            pvnrt.setTemperature(0);
            thermometer.updateThermometer();
            allParticles.clear();
            firstListOfParticles.clear();
            secondListOfParticles.clear();
            thirdListOfParticles.clear();
            fourthListOfParticles.clear();
        }
    }

    /**
     * When the button is pressed, it plays an event containing the function
     * remove1() once
     */
    private void remove1Button() {
        remove1.setOnAction(event -> remove1());
    }

    /**
     * When the button is pressed, it repeats the function remove1() 10 times,
     * Then, it updates the thermometer
     */
    private void remove10Button() {
        remove10.setOnAction(event -> {
            for (int i = 0; i < 10; i++) remove1();
            thermometer.updateThermometer();
        });
    }

    public void checkParticleParticleCollision(ArrayList<Particle> targetListOfParticles) {
        for (int i = 0; i < targetListOfParticles.size(); i++) {
            if (!targetListOfParticles.get(i).isCollisionDelay()) {
                for (int j = (i + 1); j < targetListOfParticles.size(); j++) {
                    if (!targetListOfParticles.get(j).isCollisionDelay()) {
                        if (targetListOfParticles.get(i).getCircle().getBoundsInParent().intersects(targetListOfParticles.get(j).getCircle().getBoundsInParent())) {
                            if (targetListOfParticles.get(i).getCircle().getCenterX() < targetListOfParticles.get(j).getCircle().getCenterX()) {
                                targetListOfParticles.get(i).velocityX = (Math.abs(targetListOfParticles.get(i).velocityX) * -1);
                                targetListOfParticles.get(j).velocityX = (Math.abs(targetListOfParticles.get(j).velocityX));
                            } else {
                                targetListOfParticles.get(j).velocityX = (Math.abs(targetListOfParticles.get(j).velocityX) * -1);
                                targetListOfParticles.get(i).velocityX = (Math.abs(targetListOfParticles.get(i).velocityX));
                            }
                            if (targetListOfParticles.get(i).getCircle().getCenterY() < targetListOfParticles.get(j).getCircle().getCenterY()) {
                                targetListOfParticles.get(i).velocityY = (Math.abs(targetListOfParticles.get(i).velocityY) * -1);
                                targetListOfParticles.get(j).velocityY = (Math.abs(targetListOfParticles.get(j).velocityY));
                            } else {
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
        if (allParticles.isEmpty()) pvnrt.setMoles(totalParticleCount);
        else pvnrt.setMoles(totalParticleCount);
        pressureGauge.updateGauge();
    }

    private void initializeVolumeSlider() {
        animationPane.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getX() > 200 && event.getY() > 100) {
                    animationPane.setMaxWidth(event.getX());
                    if (lid.getFitWidth() > animationPane.getMinWidth() - 100 && event.getX() < animationPane.getWidth()) {
                        lid.setFitWidth(animationPane.getWidth() - 25);
                        lid.setLayoutX((animationPane.getWidth() - lid.getFitWidth() - 25));
                    }
                    if (lid.getFitWidth() < animationPane.getWidth() && event.getX() > animationPane.getMinWidth() + 100) {
                        lid.setFitWidth(animationPane.getWidth() - 25);
                        lid.setLayoutX((animationPane.getWidth() - lid.getFitWidth() - 25));
                    }
                }
            }
        });
    }

    private double calculateRMS(double temp) {
        return Math.sqrt((3 * 8.314 * temp) / pvnrt.getMolarMass());
    }

    private ParallelTransition getParallelTransition() {
        RotateTransition rotate = new RotateTransition(Duration.millis(1000), lid);
        rotate.setByAngle(100);
        rotate.setCycleCount(1);
        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(1000), lid);
        translateTransition.setToY(-1000);
        translateTransition.setToX(1000);
        translateTransition.setCycleCount(1);
        return new ParallelTransition(lid, rotate, translateTransition);
    }

    private ImageView makingLid() {
        ImageView imageView = new ImageView(lidImage);
        imageView.setFitWidth(1300);
        imageView.setFitHeight(100);
        imageView.setLayoutY(-60);
        imageView.setPreserveRatio(false);

        return imageView;
    }

    private void returnLid() {
        lidButton.setOnAction(event -> {
            if (lidPopped) {
                animationPane.getChildren().remove(cover);
                lid = makingLid();
                animationPane.getChildren().add(lid);
                for (Particle allParticle : allParticles) allParticle.setLid(lid);
                lidPopped = false;
            } else {
                if (animationPane.getChildren().contains(lid) && !animationPane.getChildren().contains(cover)) {
                    cover = new Rectangle(lid.getLayoutX() + 50, lid.getLayoutY() + 55, lid.getFitWidth() - 100, 10);
                    cover.setFill(Color.BLACK);
                    ParallelTransition parallelTransition = getParallelTransition();
                    parallelTransition.setCycleCount(1);
                    parallelTransition.play();
                    parallelTransition.setOnFinished(event1 -> {
                        animationPane.getChildren().remove(lid);
                        for (Particle allParticle : allParticles) allParticle.setCover(cover);
                        lidPopped = true;
                    });
                    Timeline coverTimeline = new Timeline(new KeyFrame(Duration.millis(400)));
                    coverTimeline.setOnFinished(event1 -> animationPane.getChildren().addFirst(cover));
                    coverTimeline.setCycleCount(1);
                    coverTimeline.play();
                }
            }
        });
    }

    private void lidPopping() {
        Timeline timeline = new Timeline();
        KeyFrame keyFrame = new KeyFrame(
                Duration.millis(1000),
                event -> {
                    if (pvnrt.getPressure() > maxPressure && !lidPopped) {
                        cover = new Rectangle(lid.getLayoutX() + 50, lid.getLayoutY() + 55, lid.getFitWidth() - 100, 10);
                        lidPopped = true;
                        ParallelTransition parallelTransition = getParallelTransition();
                        parallelTransition.setCycleCount(1);
                        parallelTransition.play();
                        parallelTransition.setOnFinished(event1 -> {
                            animationPane.getChildren().remove(lid);
                            animationPane.getChildren().add(cover);
                            for (Particle allParticle : allParticles) allParticle.setCover(cover);
                        });
                    }
                }
        );
        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void particleEscaped() {
        for (int i = 0; i < allParticles.size(); i++) {
            if (allParticles.get(i).getCircle().getCenterY() < -50 || allParticles.get(i).getCircle().getCenterX() < -10 || allParticles.get(i).getCircle().getCenterX() > animationPane.getWidth() + 100) {
                if (firstListOfParticles.contains(allParticles.get(i))) firstListOfParticles.remove(allParticles.get(i));
                if (secondListOfParticles.contains(allParticles.get(i))) firstListOfParticles.remove(allParticles.get(i));
                if (thirdListOfParticles.contains(allParticles.get(i))) firstListOfParticles.remove(allParticles.get(i));
                if (fourthListOfParticles.contains(allParticles.get(i))) firstListOfParticles.remove(allParticles.get(i));
                animationPane.getChildren().remove(allParticles.get(i).getCircle());
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
            setParticleSize(molarMass);
            particle.setCircleSize(particleSize);
        }

    }

    private void setParticleColor(double molarMass) {
        double hue = (270 * molarMass) / 0.2201;
        particleColor = Color.hsb(hue, 1, 1);
    }

    /**
     * //equation found by having the smallest size of particle to be 9 with
     * molar mass of hydrogen 0.00202kg/mol and // largest size fo particle to
     * be 15 with molar mass of radon 0.2201k/mol.
     *
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
                case "Oxygen" ->
                    changeMolarMass(0.0320);
                case "Radon" ->
                    changeMolarMass(0.2201);
                case "Hydrogen" ->
                    changeMolarMass(0.00202);
                case "Bromine" ->
                    changeMolarMass(0.0799);
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

    private void delayCollision(Particle particle) {
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(100)));
        timeline.setOnFinished(event -> particle.setCollisionDelay(false));
        timeline.setCycleCount(1);
        timeline.play();
    }

}
