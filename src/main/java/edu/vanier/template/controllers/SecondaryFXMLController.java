package edu.vanier.template.controllers;

import edu.vanier.template.MainApp;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
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
        logger.info("Initializing MainAppController...");
        btnSwitchScene.setOnAction(this::loadPrimaryScene);
        canvas.setHeight(500);
        canvas.setWidth(500);
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        drawShapes(graphicsContext);
    }

    private void loadPrimaryScene(Event e) {
        MainApp.switchScene(MainApp.MAINAPP_LAYOUT, new MainAppFXMLController());
        logger.info("Loaded the primary scene...");
    }

    private void drawShapes(GraphicsContext graphicsContext){

    }
}
