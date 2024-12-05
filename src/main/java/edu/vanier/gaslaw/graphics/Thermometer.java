package edu.vanier.gaslaw.graphics;

import edu.vanier.gaslaw.calculations.PVnRT;
import eu.hansolo.medusa.Gauge;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 * Thermometer class provides a visual representation of temperature using a gauge.
 * Using JavaFX library for gauges made by HanSolo: https://github.com/HanSolo/medusa?tab=readme-ov-file.
 * It allows toggling between Kelvin (K) and Celsius (°C)
 * units and updates the thermometer's value based on the temperature obtained from the PVnRT object.
 */
public class Thermometer {
    private PVnRT pvnrt;
    private Gauge thermometer;
    private boolean isCelsius = false;

    /**
     * Constructs a Thermometer using the provided PVnRT object.
     *
     * @param pvnrt The PVnRT object used to get the temperature value.
     */
    public Thermometer(PVnRT pvnrt) {
        this.pvnrt = pvnrt;
        createThermometer();
    }


    /**
     * Creates the thermometer and sets up its properties.
     * Enables the interactive button inside the gauge to be able to switch
     * units without implementing additional buttons for a cleaner ui.
     */
    private void createThermometer() {
        thermometer = new Gauge();
        thermometer.setTitle("Temperature");
        thermometer.setUnit("K");
        thermometer.setMinValue(0);
        thermometer.setMaxValue(1000);
        thermometer.setValue(pvnrt.getTemperature());
        thermometer.setMajorTickSpace(50);
        thermometer.setMinorTickSpace(10);
        thermometer.setTickLabelDecimals(0);
        thermometer.setPrefSize(175, 175);
        thermometer.setMinSize(175, 175);
        thermometer.setMaxSize(175, 175);
        thermometer.setSkinType(Gauge.SkinType.MODERN);
        thermometer.setForegroundBaseColor(Color.WHITE);
        thermometer.setThresholdColor(Color.RED);
        thermometer.setThreshold(800);
        thermometer.setThresholdVisible(true);
        thermometer.setInteractive(true);
        thermometer.setOnButtonPressed(buttonEvent -> System.out.println("Thermometer button pressed."));
        thermometer.setOnButtonPressed(buttonEvent -> toggleTemperatureGauge());
    }

    /**
     * Toggles the unit of the thermometer between Kelvin (K) and Celsius (°C).
     * Adjusts the thermometer's minimum and maximum values, tick spaces, and threshold.
     */
    private void toggleTemperatureGauge() {
        if (isCelsius) {
            thermometer.setUnit("K");
            thermometer.setMinValue(0);
            thermometer.setMaxValue(1000);
            thermometer.setValue(pvnrt.getTemperature());
            thermometer.setForegroundBaseColor(Color.WHITE);
            thermometer.setThresholdColor(Color.RED);
            thermometer.setThreshold(800);
            thermometer.setThresholdVisible(true);
        } else {
            thermometer.setUnit("°C");
            thermometer.setMinValue(-280); // Not -273.15 because it causes rendering issues
            thermometer.setMaxValue(1000 - 273.15);
            thermometer.setValue(pvnrt.getTemperature() - 273.15);
            thermometer.setForegroundBaseColor(Color.WHITE);
            thermometer.setThresholdColor(Color.RED);
            thermometer.setThreshold(800 - 273.15);
            thermometer.setThresholdVisible(true);
        }
        isCelsius = !isCelsius;
    }

    /**
     * Updates the thermometer's value based on the current temperature from the PVnRT object.
     * Converts the value to Celsius if the unit is Celsius.
     */
    public void updateThermometer() {
        if (isCelsius) {
            thermometer.setValue(pvnrt.getTemperature() - 273.15);
        } else {
            thermometer.setValue(pvnrt.getTemperature());
        }
    }

    /**
     * Returns a StackPane containing the thermometer.
     *
     * @return A StackPane with the thermometer.
     */
    public StackPane getThermometerPane() {
        return new StackPane(thermometer);
    }
}
