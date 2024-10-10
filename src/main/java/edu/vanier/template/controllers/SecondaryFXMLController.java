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
    Pane canvas = new Pane();
    @FXML
    VBox vbox;


    Particle[] listOfParticles = new Particle[1000];
    int numberOfParticles = 0;


    @FXML
    public void initialize() {
        logger.info("Initializing MainAppController...");
        btnSwitchScene.setOnAction(this::loadPrimaryScene);
        initCanvas();
        addParticlesButton();
    }

    private void addParticlesButton(){
        add.setOnAction(event -> {
            Circle circle= new Circle(10,10,10,Color.RED);
            listOfParticles[numberOfParticles] = new Particle(circle, 2 ,1);
            KeyFrame kf = new KeyFrame(Duration.millis(100),(e -> moveCircle(circle)));
            Timeline timeline1 = new Timeline();
            timeline1.getKeyFrames().add(kf);
            timeline1.setCycleCount(Animation.INDEFINITE);
            timeline1.play();
            canvas.getChildren().add(listOfParticles[numberOfParticles++].getCircle());
        });
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

    private void moveCircle(Circle circle) {

        for (int i = 0; i < numberOfParticles; i++) {
            if (listOfParticles[i].getCircle().getCenterX() <= 9){
                listOfParticles[i].velocityX = -listOfParticles[i].velocityX;
            }
            else if (listOfParticles[i].getCircle().getCenterX() >= 490) {
                listOfParticles[i].velocityX = -listOfParticles[i].velocityX;
            }
            if (listOfParticles[i].getCircle().getCenterY() <= 9){
                listOfParticles[i].velocityY = -listOfParticles[i].velocityY;
            }
            else if (listOfParticles[i].getCircle().getCenterY() >= 490){
                listOfParticles[i].velocityY = -listOfParticles[i].velocityY;
            }
            listOfParticles[i].getCircle().setCenterX(listOfParticles[i].getCircle().getCenterX() + listOfParticles[i].velocityX);
            listOfParticles[i].getCircle().setCenterY(listOfParticles[i].getCircle().getCenterY() + listOfParticles[i].velocityY);
        }

    }
}
