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

    /**
     *
     * This handles the switch scene button
     * Creates a variable pvnrt needed for the calculations
     * Creates a pressure gauge and thermometer and add them to the VBox
     * Sets the image for the lid and adds it to the animationPane
     * Then calls the initial functions
     *
     */
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

    /**
     *
     * Calls all the functions needed in this application to be initiated
     *
     */
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

    /**
     *
     * Timeline that refreshes every 100ms to update the volume, velocity and pressure
     * It plays indefinitely
     *
     */
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

    /**
     *
     * Sets up drop shadows for heating and cooling with their respective color with radius 25
     * Creates timelines that calls the function to adjust temperature for cooling and heating
     * Sets up on mouse click and hold for the heating and cooling buttons to play the heating and cooling timelines
     *
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

    /**
     *
     * @param newTemperature New temperature received as a parameter
     * Changes the particles according to the formulas
     *                       Sets to 0 when temp is 0
     *
     */
    private void updateParticlesWithTemperature(double newTemperature) {
        if (newTemperature <= 0) for (Particle particle : allParticles) particle.setVelocity(0);
        else for (Particle particle : allParticles) particle.setVelocity((baseParticleVelocity * calculateRMS(newTemperature)) / calculateRMS(300));
    }

    /**
     *
     * @param tempChange
     * Sets the increment depending on if the temperature is above or under 100
     * Puts a limit to the temperature to 1000
     * Sets and updates the temperature
     * Updates the particles and the thermometer
     *
     */
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
                    addToFirstSecondThirdFourth(firstListOfParticles);
                    addToFirstSecondThirdFourth(secondListOfParticles);
                    addToFirstSecondThirdFourth(thirdListOfParticles);
                    addToFirstSecondThirdFourth(fourthListOfParticles);
                }
        );
        timeline.getKeyFrames().add(kf);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    /**
     *
     * @param listOfParticles First, Second, Third and Fourth Lists
     * Try and catch for when the particle doesn't exist anymore
     * Checks if the particle is not in the same quadrant first, then
     * Adds particles to their respective quadrants using x and y limits
     *
     */
    private void addToFirstSecondThirdFourth(ArrayList<Particle> listOfParticles) {
        try{
            for (int i = 0; i < listOfParticles.size(); i++) {
                if (firstListOfParticles!=listOfParticles && listOfParticles.get(i).getCircle().getCenterX() < animationPane.getWidth() / 2 && listOfParticles.get(i).getCircle().getCenterY() < animationPane.getHeight() / 2) {
                    firstListOfParticles.add(listOfParticles.get(i));
                    listOfParticles.remove(listOfParticles.get(i));
                }
                if (secondListOfParticles!=listOfParticles && listOfParticles.get(i).getCircle().getCenterX() > animationPane.getWidth() / 2 && listOfParticles.get(i).getCircle().getCenterY() < animationPane.getHeight() / 2) {
                    secondListOfParticles.add(listOfParticles.get(i));
                    listOfParticles.remove(listOfParticles.get(i));
                }
                if (thirdListOfParticles!=listOfParticles && listOfParticles.get(i).getCircle().getCenterX() < animationPane.getWidth() / 2 && listOfParticles.get(i).getCircle().getCenterY() > animationPane.getHeight() / 2) {
                    thirdListOfParticles.add(listOfParticles.get(i));
                    listOfParticles.remove(listOfParticles.get(i));
                }
                if (fourthListOfParticles!=listOfParticles && listOfParticles.get(i).getCircle().getCenterX() > animationPane.getWidth() / 2 && listOfParticles.get(i).getCircle().getCenterY() > animationPane.getHeight() / 2) {
                    fourthListOfParticles.add(listOfParticles.get(i));
                    listOfParticles.remove(listOfParticles.get(i));
                }
            }
        }catch (Exception e) {System.out.println("Particle does not exist anymore");}
    }

    /**
     *
     * Set on action for the button +10 that calls the addParticle() method 10 times
     *
     */
    private void add10ParticlesButton() {
        add10.setOnAction(event -> {
                for (int i = 0; i < 10; i++) addParticle();
            }
        );
    }

    /**
     *
     * Checks if the simulation is paused or not, then
     * If there are no particles, the temperature is 0, so it will go from 0 to 300 if a particle is added
     * Adds 1 to the counter for total number of particles
     * Updates the pressure
     * Sets the particles size and color using its molar mass
     * Calculates the particles velocity using the formula
     * Creates a circle with an inner shadow
     * Creates a particle that gets the circle as a parameter
     * Also send the cover to the particle
     * Then make the timeline for it to move and play it
     * Add the circle inside the particle to the animationPanel
     * Add the particle to all particles and the first list
     * Updates the thermometer
     *
     */
    private void addParticle() {
        if (!paused) {
            if (pvnrt.getTemperature() == 0) {
                pvnrt.setTemperature(300);
                updateParticlesWithTemperature(300);
            }
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

    /**
     *
     * Checks for the collisions in each quadrant for better efficiency
     * Checks if the particle escaped the animation panel
     *
     */
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

    /**
     *
     * Set on click to call the addParticle() method once
     *
     */
    private void addParticlesButton() {
        add.setOnAction(event -> addParticle());
    }

    /**
     *
     * Set on action for the reset button
     * Clears the animationPanel
     * Clears all the lists
     * Sets particle and moles count to 0
     * Updates the pressure to 0
     * Updates the thermometer and pressure gauge
     * If the lid is popped, then add back the lid and remove the cover used to simulate a gap in the animationPanel
     * If so, then sets lidPopped to false
     *
     */
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

    /**
     *
     * Pauses the timelines for each particle
     * Sets paused to true
     *
     */
    private void pauseFunction() {
        pause.setOnAction(event -> {
            paused = true;
            for (Particle allParticle : allParticles) allParticle.pause();
        });
    }


    /**
     *
     * Plays the timelines for each particle
     * Sets paused to false
     *
     */
    private void playFunction() {
        play.setOnAction(event -> {
            paused = false;
            for (Particle allParticle : allParticles) allParticle.play();
        });
    }

    /**
     *
     * Creates a timeline for the fast-forward function
     * Plays the timelines for the particles for one millis
     * Then it pauses the timelines for the particles
     * Set on action to play the timeline when pressed
     *
     */
    private void fastForwardFunction() {
        Timeline timeline = new Timeline();
        KeyFrame keyFrame = new KeyFrame(
                Duration.millis(10),
                event1 -> {
                    for (Particle allParticle : allParticles) allParticle.play();
                }
        );
        timeline.setOnFinished(event1 -> {
            for (Particle allParticle : allParticles) allParticle.play();
            for (Particle allParticle : allParticles) {
                allParticle.pause();
                paused = true;
            }
        });
        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(1);
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

    /**
     *
     * @param targetListOfParticles first, second, third, fourth quadrant
     * Checks for the collision delay first, then
     * Using intersects to find if the particles are colliding
     * Then checks who are on top, bottom, left, right to change their velocities according going negative
     * Then sets a delay to the particles
     *
     */
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

    /**
     *
     * @param e
     * Loads primary scene using switchScene() method
     *
     */
    private void loadPrimaryScene(Event e) {
        MainApp.switchScene(MainApp.MAINAPP_LAYOUT, new MainAppFXMLController());
        logger.info("Loaded the primary scene...");
    }

    /**
     *
     * Check if there are particles, if not then moles = 0
     * Else it sets moles to the particle count
     * Then updates the pressure gauge
     *
     */
    private void updatePressure() {
        if (allParticles.isEmpty()) pvnrt.setMoles(totalParticleCount);
        else pvnrt.setMoles(totalParticleCount);
        pressureGauge.updateGauge();
    }

    /**
     *
     * Volume slider for the animationPane
     * Using event handler for mouse drag
     * Check if the mouse is at the right area first, then
     * sets the animation pane to the mouse position
     * Adjusts the lid to the pane size is it is too small
     *
     */
    private void initializeVolumeSlider() {
        animationPane.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getX() > animationPane.getWidth() - 100 && event.getY() > 100 && event.getX() > 200) {
                    animationPane.setMaxWidth(event.getX());
                    if (lid.getFitWidth() > animationPane.getWidth() - 25 && event.getX() < animationPane.getWidth()) {
                        lid.setFitWidth(animationPane.getWidth() - 25);
                        lid.setLayoutX((animationPane.getWidth() - lid.getFitWidth() - 25));
                    }
                }
            }
        });
    }

    /**
     *
     * @param temp
     * @return number using formula
     */
    private double calculateRMS(double temp) {
        return Math.sqrt((3 * 8.314 * temp) / pvnrt.getMolarMass());
    }

    /**
     *
     * Adds a rotate and a translate transition to a parrallel transition to create the lid animation
     * @return transition to be used for when the lid pops
     *
     */
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

    /**
     *
     * Gives proportions to the image view
     * @return lid as an imageview
     *
     */
    private ImageView makingLid() {
        ImageView imageView = new ImageView(lidImage);
        imageView.setFitWidth(animationPane.getWidth());
        imageView.setFitHeight(100);
        imageView.setLayoutY(-60);
        imageView.setPreserveRatio(false);
        return imageView;
    }

    /**
     *
     * Set on action for the +/- lid button
     * Checks if the lid is popped or not
     * If so then, remove the cover, then
     * make a lid
     * add the lid to the animationPane
     * then set the lid for each particle
     * then set lid popped to false
     *
     * If lid is not popped, then
     * check if the animationPane contains the lid and does not contain the cover
     * then make a new cover using the lid as a reference to make it seem like there is a gap in the animationPane
     * Then play the lid popping animation
     * Then remove the lid from the animationPane
     * Set the cover for each particle
     * Set lidPopped to true
     * Then remove the cover using a timeline after 400 millis
     *
     */
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

    /**
     *
     * Timeline that checks every second if the pressure is too high
     * If so then create the cover using the lids dimension
     * Then play the animation of the lid popping
     * Then remove the lid and add the cover to the animationPane
     * Cycle through this checker indefinitely
     *
     */
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

    /**
     *
     * Checks for all particles if the particle passed a certain threshold inside the animationPane
     * If it is then, remove it from its list
     * Then remove it from the animationPane
     * Then remove it from allParticles
     * Remove one from the particle count
     * Updates pressure and its gauge
     *
     */
    private void particleEscaped() {
        for (int i = 0; i < allParticles.size(); i++) {
            if (allParticles.get(i).getCircle().getCenterY() < -50 || allParticles.get(i).getCircle().getCenterX() < -10 || allParticles.get(i).getCircle().getCenterX() > animationPane.getWidth() + 100 && allParticles.get(i).getCircle().getCenterY() > animationPane.getMaxHeight()) {
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

    /**
     *
     * @param molarMass
     * Calculates the new velocity using the molar mass formula
     * Changes the velocity for each particle
     * Changes the color and size of the particle
     *
     */
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

    /**
     *
     * @param molarMass
     * Changes particle color using the molar mass by hue values
     *
     */
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

    /**
     *
     * ComboBox for every gas
     * When choosing the new gas, change the molar mass accordingly
     *
     */
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

    /**
     *
     * Changes velocity label by calculating the velocity using the temperature in pvnrt
     *
     */
    private void changeVelocityLabel() {
        String velocityValue;
        if (totalParticleCount == 0) velocityValue = "0.00";
        else velocityValue = String.format("%.2f", calculateRMS(pvnrt.getTemperature()));

        velocityLabel.setText(velocityValue + " m/s");
    }

    /**
     *
     * Changes the volume label by using the volume in pvnrt
     *
     */
    private void changeVolumeLabel() {
        String volumeValue;
        if (pvnrt.getVolume() == 0) volumeValue = "0.00";
        else volumeValue = String.format("%.2f", pvnrt.getVolume());;

        volumeLabel.setText(volumeValue + " L");
    }

    /**
     *
     * @param particle
     * Creates a timeline of a duration 100ms
     * When finished the particleDelay is false
     *
     */
    private void delayCollision(Particle particle) {
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(100)));
        timeline.setOnFinished(event -> particle.setCollisionDelay(false));
        timeline.setCycleCount(1);
        timeline.play();
    }

}
