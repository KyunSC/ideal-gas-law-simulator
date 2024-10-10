package edu.vanier.template.controllers;

import edu.vanier.template.MainApp;
import javafx.animation.*;
import javafx.event.Event;
import javafx.fxml.FXML;
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
    Pane canvas = new Pane();

    @FXML
    VBox vbox;

    Double velocityX = 3.0;
    Double velocityY = 2.0;

    Circle[] listOfParticles = new Circle[1000];
    int numberOfParticles = 0;


    @FXML
    public void initialize() {
        logger.info("Initializing MainAppController...");
        btnSwitchScene.setOnAction(this::loadPrimaryScene);
        canvas.setMinSize(500, 500);
        canvas.setMaxSize(500, 500);
        canvas.setBorder(new Border((new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT))));
        vbox.getChildren().add(canvas);
        add.setOnAction(event -> {
            Circle circle= new Circle(10,10,10,Color.RED);
            velocityX = 10.0;
            listOfParticles[numberOfParticles] = circle;
            KeyFrame kf = new KeyFrame(Duration.millis(1),(e -> moveCircle(circle)));
            Timeline timeline1 = new Timeline();
            timeline1.getKeyFrames().add(kf);
            timeline1.setCycleCount(Animation.INDEFINITE);
            timeline1.play();
            canvas.getChildren().add(listOfParticles[numberOfParticles++]);
        });

    }


    private void loadPrimaryScene(Event e) {

        MainApp.switchScene(MainApp.MAINAPP_LAYOUT, new MainAppFXMLController());
        logger.info("Loaded the primary scene...");
    }

    private void moveCircle(Circle circle) {

        for (int i = 0; i < numberOfParticles; i++) {
            if (listOfParticles[i].getCenterX() <= 9){
                velocityX = -velocityX;
            }
            else if (listOfParticles[i].getCenterX() >= 490) {
                velocityX = -velocityX;
            }
            if (listOfParticles[i].getCenterY() <= 9){
                velocityY = -velocityY;
            }
            else if (listOfParticles[i].getCenterY() >= 490){
                velocityY = -velocityY;
            }
            listOfParticles[i].setCenterX(listOfParticles[i].getCenterX() + velocityX);
            listOfParticles[i].setCenterY(listOfParticles[i].getCenterY() + velocityY);
        }


        System.out.println(circle.getCenterX());
    }
}
