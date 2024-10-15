package edu.vanier.template.graphics;

import eu.hansolo.medusa.Gauge;
import edu.vanier.template.calculations.PVnRT;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class PressureGauge {
    private Gauge pressureGauge;
    private PVnRT pvnrt;

    // Constructor
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
        pressureGauge.setTickLabelsVisible(true);
        pressureGauge.setTickMarkSectionsVisible(true);

        pressureGauge.setPrefSize(125, 125);
        pressureGauge.setMinSize(125, 125);
        pressureGauge.setMaxSize(125, 125);
    }

    public void updateGauge() {
        pressureGauge.setValue(pvnrt.getPressure());
    }

    public StackPane getGaugePane() {
        return new StackPane(pressureGauge);
    }
}
