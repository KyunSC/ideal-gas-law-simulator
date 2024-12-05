package edu.vanier.gaslaw.controllers;

import edu.vanier.gaslaw.MainApp;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FXML controller class for the primary stage's scene.
 * Two buttons for both simulations
 */
public class MainAppFXMLController {

    private final static Logger logger = LoggerFactory.getLogger(MainAppFXMLController.class);

    @FXML
    Button btnClickMe;
    @FXML
    Button btnSwitchScene;

    /**
     *
     * Set on click for the balloon to switch scene
     * Set on click for the ideal gas law simulation to switch scene
     *
     */
    @FXML
    public void initialize() {
        logger.info("Initializing MainAppController...");
        btnClickMe.setOnAction(this::handleClickMe);
        btnSwitchScene.setOnAction(this::loadSecondaryScene);
    }

    /**
     *
     * @param e
     * Switches to the balloon
     */
    private void handleClickMe(Event e) {
        MainApp.switchScene(MainApp.THIRD_LAYOUT, new BalloonFXMLController());
        logger.info("Click me button has been pressed...");
    }

    /**
     *
     * @param e
     * Switches to the ideal gas law
     */
    private void loadSecondaryScene(Event e) {
        MainApp.switchScene(MainApp.SECONDARY_LAYOUT, new IdealGasFXMLController());
        logger.info("Loaded the secondary scene...");
    }
}
