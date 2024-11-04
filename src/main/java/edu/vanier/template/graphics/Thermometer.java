package edu.vanier.template.graphics;

import edu.vanier.template.calculations.PVnRT;
import eu.hansolo.medusa.Gauge;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class Thermometer {
    private PVnRT pvnrt;
    private Gauge thermometer;

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
        thermometer.setPrefSize(125, 125);
        thermometer.setMinSize(125, 125);
        thermometer.setMaxSize(125, 125);
        thermometer.setSkinType(Gauge.SkinType.MODERN);
        thermometer.setForegroundBaseColor(Color.WHITE);
        thermometer.setThresholdColor(Color.RED);
        thermometer.setThreshold(800);
        thermometer.setThresholdVisible(true);
        thermometer.setInteractive(true);
        thermometer.setOnButtonPressed(buttonEvent -> System.out.println("Thermometer button pressed."));
//        thermometer.setAnimated(true);
//        thermometer.setAnimationDuration(500);
    }

    public void updateThermometer() {
        thermometer.setValue(pvnrt.getTemperature());
    }

    public StackPane getThermometerPane() {
        return new StackPane(thermometer);
    }
}
