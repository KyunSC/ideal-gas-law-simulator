package edu.vanier.template.graphics;

import eu.hansolo.medusa.Gauge;
import edu.vanier.template.calculations.PVnRT;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class PressureGauge {
    private Gauge pressureGauge;
    private PVnRT pvnrt;

    public PressureGauge(PVnRT pvnrt) {
        this.pvnrt = pvnrt;
        createGauge();
    }

    private void createGauge() {
        pressureGauge = new Gauge();
        pressureGauge.setTitle("Pressure");
        pressureGauge.setUnit("atm");
        pressureGauge.setMinValue(0);
        pressureGauge.setMaxValue(200);
        pressureGauge.setValue(pvnrt.getPressure());
        pressureGauge.setMajorTickSpace(20);
        pressureGauge.setMinorTickSpace(5);
        pressureGauge.setTickLabelDecimals(0);
        pressureGauge.setPrefSize(125, 125);
        pressureGauge.setMinSize(125, 125);
        pressureGauge.setMaxSize(125, 125);
        pressureGauge.setSkinType(Gauge.SkinType.MODERN);
        pressureGauge.setForegroundBaseColor(Color.WHITE);
        pressureGauge.setThresholdColor(Color.RED);
        pressureGauge.setThreshold(170);
        pressureGauge.setThresholdVisible(true);
        pressureGauge.setInteractive(true);
        pressureGauge.setOnButtonPressed(buttonEvent -> System.out.println("Pressure Gauge button pressed."));
//        pressureGauge.setAnimated(true);
//        pressureGauge.setAnimationDuration(500);
    }

    public void updateGauge() {
        pressureGauge.setValue(pvnrt.getPressure());
    }

    public StackPane getGaugePane() {
        return new StackPane(pressureGauge);
    }
}
