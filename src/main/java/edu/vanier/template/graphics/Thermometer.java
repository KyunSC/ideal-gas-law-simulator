package edu.vanier.template.graphics;

import edu.vanier.template.calculations.PVnRT;
import eu.hansolo.medusa.Gauge;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

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
        thermometer.setTickLabelsVisible(true);
        thermometer.setTickMarkSectionsVisible(true);

        thermometer.setPrefSize(125, 125);
        thermometer.setMinSize(125, 125);
        thermometer.setMaxSize(125, 125);

        thermometer.zeroColorProperty();

    }

    public void updateThermometer() {
        thermometer.setValue(pvnrt.getTemperature());
    }

    public StackPane getThermometerPane() {
        return new StackPane(thermometer);
    }
}
