package edu.vanier.template.controllers;

import edu.vanier.template.MainApp;
import edu.vanier.template.Particle;
import javafx.animation.*;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public Pane canvas = new Pane();
    @FXML
    VBox vbox;


    Particle[] listOfParticles = new Particle[1000];
    int numberOfParticles = 0;

    double velocityX;
    double velocityY;
    double velocity = 5;

    @FXML
    public void initialize() {
        logger.info("Initializing MainAppController...");
        btnSwitchScene.setOnAction(this::loadPrimaryScene);
        initCanvas();
        addParticlesButton();
        particleCollisionTimeline();
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
        elasticCollisionTimeline.play();
    }

    private void addParticlesButton(){
        add.setOnAction(event -> {
            Circle circle = new Circle(10,10,10,Color.RED);
            velocityX = Math.random()*5;
            velocityY = Math.sqrt(Math.pow(velocity, 2) - Math.pow(velocityX, 2));
            System.out.println(velocityX);
            System.out.println(velocityY);

            listOfParticles[numberOfParticles] = new Particle(circle, velocityX,velocityY, canvas);
            listOfParticles[numberOfParticles].createTimeline();
            listOfParticles[numberOfParticles].play();
            canvas.getChildren().add(listOfParticles[numberOfParticles++].getCircle());
        });
    }

    private void checkParticleParticleCollision() {
        for (int i = 0; i < numberOfParticles; i++) {
            for (int j = (i+1); j <numberOfParticles ; j++) {
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


}
