package edu.vanier.gaslaw.graphics;

import eu.hansolo.medusa.Gauge;
import edu.vanier.gaslaw.calculations.PVnRT;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class PressureGauge {
    private Gauge pressureGauge;
    private PVnRT pvnrt;
    private boolean isKPa = false;

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
        pressureGauge.setPrefSize(175, 175);
        pressureGauge.setMinSize(175, 175);
        pressureGauge.setMaxSize(175, 175);
        pressureGauge.setSkinType(Gauge.SkinType.MODERN);
        pressureGauge.setForegroundBaseColor(Color.WHITE);
        pressureGauge.setThresholdColor(Color.RED);
        pressureGauge.setThreshold(170);
        pressureGauge.setThresholdVisible(true);
        pressureGauge.setInteractive(true);
        pressureGauge.setOnButtonPressed(buttonEvent -> System.out.println("Pressure Gauge button pressed."));
        pressureGauge.setOnButtonPressed(buttonEvent -> togglePressureGauge());
    }

    private void togglePressureGauge() {
        if (isKPa) {
            pressureGauge.setUnit("atm");
            pressureGauge.setMaxValue(200);
            pressureGauge.setValue(pvnrt.getPressure());
            pressureGauge.setMajorTickSpace(20);
            pressureGauge.setMinorTickSpace(5);
            pressureGauge.setForegroundBaseColor(Color.WHITE);
            pressureGauge.setThresholdColor(Color.RED);
            pressureGauge.setThreshold(170);
            pressureGauge.setThresholdVisible(true);
        } else {
            pressureGauge.setUnit("kPa");
            pressureGauge.setMaxValue(200 * 101.325);
            pressureGauge.setValue(pvnrt.getPressure() * 101.325);
            pressureGauge.setMajorTickSpace(20 * 101.325);
            pressureGauge.setMinorTickSpace(5 * 101.325);
            pressureGauge.setForegroundBaseColor(Color.WHITE);
            pressureGauge.setThresholdColor(Color.RED);
            pressureGauge.setThreshold(170 * 101.325);
            pressureGauge.setThresholdVisible(true);
        }
        isKPa = !isKPa;
    }

    public void updateGauge() {
        if (isKPa) {
            pressureGauge.setValue(pvnrt.getPressure() * 101.325);
        } else {
            pressureGauge.setValue(pvnrt.getPressure());
        }
    }

    public StackPane getGaugePane() {
        return new StackPane(pressureGauge);
    }
}
