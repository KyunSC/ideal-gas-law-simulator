package edu.vanier.gaslaw.graphics;

import edu.vanier.gaslaw.calculations.PVnRT;
import eu.hansolo.medusa.Gauge;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class Thermometer {
    private PVnRT pvnrt;
    private Gauge thermometer;
    private boolean isCelsius = false;

    public Thermometer(PVnRT pvnrt) {
        this.pvnrt = pvnrt;
        createThermometer();
    }

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

    public void updateThermometer() {
        if (isCelsius) {
            thermometer.setValue(pvnrt.getTemperature() - 273.15);
        } else {
            thermometer.setValue(pvnrt.getTemperature());
        }
    }

    public StackPane getThermometerPane() {
        return new StackPane(thermometer);
    }
}
