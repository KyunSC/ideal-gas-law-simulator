package edu.vanier.template.controllers;

import edu.vanier.template.MainApp;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

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


    @FXML
    public void initialize() {
        logger.info("Initializing MainAppController...");
        btnSwitchScene.setOnAction(this::loadPrimaryScene);
        Circle[] listOfParticles = new Circle[1000];

        add.setOnAction(event -> {
            Circle circle= new Circle(10,10,10,Color.RED);
            velocityX = 10.0;
            listOfParticles[0] = circle;


            KeyFrame kf = new KeyFrame(Duration.millis(1),(e -> moveCircle(circle)));
            Timeline timeline1 = new Timeline();
                timeline1.getKeyFrames().add(kf);
                timeline1.setCycleCount(Animation.INDEFINITE);
                timeline1.play();

            canvas.getChildren().add(circle);
            canvas.setMinSize(500, 500);
            canvas.setBorder(new Border((new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT))));
            vbox.getChildren().add(canvas);
        });

    }


    private void loadPrimaryScene(Event e) {
        MainApp.switchScene(MainApp.MAINAPP_LAYOUT, new MainAppFXMLController());
        logger.info("Loaded the primary scene...");
    }

    private void moveCircle(Circle circle) {
        if (circle.getCenterX() <= 9){
            velocityX = -velocityX;
        }
        else if (circle.getCenterX() >= 490) {
            velocityX = -velocityX;
        }
        if (circle.getCenterY() <= 9){
            velocityY = -velocityY;
        }
        else if (circle.getCenterY() >= 490){
            velocityY = -velocityY;
        }
        circle.setCenterX(circle.getCenterX() + velocityX);
        circle.setCenterY(circle.getCenterY() + velocityY);
        System.out.println(circle.getCenterX());
    }
}
