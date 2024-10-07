package edu.vanier.template.controllers;

import edu.vanier.template.MainApp;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;
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
    Canvas canvas;

    @FXML
    public void initialize() {
        Stage stage = new Stage();
        Group root = new Group();
        logger.info("Initializing MainAppController...");
        btnSwitchScene.setOnAction(this::loadPrimaryScene);
        canvas = new Canvas(500, 500);
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        drawShapes(graphicsContext);
        root.getChildren().addAll(canvas);
        System.out.println("INIT SECONDARY");
        stage.setScene(new Scene(root, 500, 500));
        stage.show();
    }


    private void loadPrimaryScene(Event e) {
        MainApp.switchScene(MainApp.MAINAPP_LAYOUT, new MainAppFXMLController());
        logger.info("Loaded the primary scene...");
    }

    private void drawShapes(GraphicsContext graphicsContext){
        graphicsContext.setFill(Color.valueOf("#ff0000"));
        graphicsContext.fillRect(100, 100, 200, 200);

    }
}
