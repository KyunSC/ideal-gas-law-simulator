package edu.vanier.template.calculations;

public class PVnRT {
    private double temperature;
    private double rConstant;
    private double volume;
    private double pressure;
    private double moles;

    /**
     * default constructor
     */
    public PVnRT() {
        this.temperature = 298; //K
        this.rConstant = 0.08206; // L*atm/mol*K
        this.volume = 1; //L
        this.pressure = 1; //atm
        this.moles = 0; //mol
    }

    public void recalculatePressure() {
        pressure = (moles * rConstant * temperature) / volume;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
        recalculatePressure();
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
        recalculatePressure();
    }

    public double getPressure() {
        return pressure;
    }

    public double getMoles() {
        return moles;
    }

    public void setMoles(double moles) {
        this.moles = moles;
        recalculatePressure();
    }
}
