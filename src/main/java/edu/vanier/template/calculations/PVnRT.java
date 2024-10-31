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
        this.temperature = 300; //K
        this.rConstant = 0.08206; // L*atm/mol*K
        this.volume = 10; //L
        this.moles = 0; //mol
        this.pressure = calculatePressure(); //atm
    }

    public double calculatePressure() {
        pressure = (moles * rConstant * temperature) / volume;
        return pressure;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        if (moles > 0) {
            this.temperature = temperature;
            calculatePressure();
        } else {
            System.out.println("Temperature cannot be changed when moles are zero.");
        }
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
        calculatePressure();
    }

    public double getPressure() {
        return pressure;
    }

    public double getMoles() {
        return moles;
    }

    public void setMoles(double moles) {
        if (moles == 0) {
            this.temperature = 0;
        } else if (this.moles == 0 && moles > 0) {
            // when moles are initially added defaults temperature to 300
            this.temperature = 300;
        }
        this.moles = moles;
        calculatePressure();
    }

    public String toString() {
        return "PVnRT{" +
                "temperature=" + temperature +
                ", rConstant=" + rConstant +
                ", volume=" + volume +
                ", pressure=" + pressure +
                ", moles=" + moles +
                '}';
    }
}
