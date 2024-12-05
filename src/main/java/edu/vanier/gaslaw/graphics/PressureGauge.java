package edu.vanier.gaslaw.graphics;

import eu.hansolo.medusa.Gauge;
import edu.vanier.gaslaw.calculations.PVnRT;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 * PressureGauge class provides a visual representation of pressure using a gauge.
 * Using JavaFX library for gauges made by HanSolo: https://github.com/HanSolo/medusa?tab=readme-ov-file.
 * It allows toggling between atmospheres (atm) and kilopascals (kPa)
 * units and updates the gauge value based on the pressure obtained from the PVnRT class.
 */

public class PressureGauge {
    private Gauge pressureGauge;
    private PVnRT pvnrt;
    private boolean isKPa = false;

    /**
     * Constructs a PressureGauge using the provided PVnRT object.
     *
     * @param pvnrt the PVnRT object used to get pressure value.
     */
    public PressureGauge(PVnRT pvnrt) {
        this.pvnrt = pvnrt;
        createGauge();
    }

    /**
     * Creates the gauge and sets up its properties.
     * Enables the interactive button inside the gauge to be able to switch
     * units without implementing additional buttons for a cleaner ui.
     */
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

    /**
     * Toggles the unit of the gauge between atmospheres (atm) and kilopascals (kPa).
     * Adjusts the gauge's maximum value, tick spaces, and threshold.
     */
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

    /**
     * Updates the gauge value based on the current pressure from the PVnRT object.
     * Converts the value to kPa if the unit is kPa.
     */
    public void updateGauge() {
        if (isKPa) {
            pressureGauge.setValue(pvnrt.getPressure() * 101.325);
        } else {
            pressureGauge.setValue(pvnrt.getPressure());
        }
    }

    /**
     * Returns a StackPane containing the pressure gauge.
     *
     * @return A StackPane with the pressure gauge.
     */
    public StackPane getGaugePane() {
        return new StackPane(pressureGauge);
    }
}
