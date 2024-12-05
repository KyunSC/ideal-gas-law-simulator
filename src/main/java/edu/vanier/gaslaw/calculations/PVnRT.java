package edu.vanier.gaslaw.calculations;

/**
 * A class that represents the Ideal Gas Law equation PV = nRT, providing methods to calculate pressure
 * and manage gas-related properties such as temperature, volume, moles, and molar mass.
 * In this version of the equation, pressure acts as the dependant variable
 * meaning its value is based on the other gas law properties.
 */
public class PVnRT {
    private double temperature;
    final private double rConstant;
    private double volume;
    private double pressure;
    private double moles;
    private double molarMass;

    /**
     * Default constructor that initializes the gas law properties
     * Default values:
     * Temperature: 300 K
     * R constant: 0.08206 L*atm/mol*K
     * Volume: 10 L
     * Moles: 0 mol
     * Pressure: Calculated based on other properties
     * Molar mass: 0.0320 g/mol (oxygen)
     */
    public PVnRT() {
        this.temperature = 300; //K
        this.rConstant = 0.08206; // L*atm/mol*K
        this.volume = 10; //L
        this.moles = 0; //mol
        this.pressure = calculatePressure(); //atm
        this.molarMass = 0.0320; //default molar mass for oxygen
    }

    /**
     * Calculates pressure using the ideal gas law equation
     */
    public double calculatePressure() {
        pressure = (moles * rConstant * temperature) / volume;
        return pressure;
    }

    /**
     * Gets the current temperature.
     *
     * @return the temperature in Kelvin (K)
     */
    public double getTemperature() {
        return temperature;
    }

    /**
     * Sets the temperature. If moles are 0 then the temperature is also set to 0.
     * Recalculates the pressure based on the temperature
     * @param temperature Temperature in Kelvin (K).
     */
    public void setTemperature(double temperature) {
        if (moles > 0) {
            this.temperature = temperature;
            calculatePressure();
        } else {
            System.out.println("Temperature cannot be changed when moles are zero.");
        }
    }

    /**
     * Gets the current volume.
     *
     * @return the volume in liters (L)
     */
    public double getVolume() {
        return volume;
    }

    /**
     * Sets the volume of the gas. Updates the pressure based on the new volume.
     *
     * @param volume the volume in liters (L)
     */
    public void setVolume(double volume) {
        this.volume = volume;
        calculatePressure();
    }

    /**
     * Gets the current pressure.
     *
     * @return the pressure in atmospheres (atm)
     */
    public double getPressure() {
        return pressure;
    }

    /**
     * Sets the pressure. This method does not recalculate other properties.
     *
     * @param pressure the new pressure in atmospheres (atm)
     */
    public void setPressure(double pressure){this.pressure = pressure;}

    /**
     * Gets the current amount of substance in moles.
     *
     * @return the moles of gas (mol)
     */
    public double getMoles() {
        return moles;
    }

    /**
     * Sets the amount of moles of gas. If the moles are set to 0 then temperature is subsequently set to 0.
     * When moles are initially added defaults temperature to 300.
     * Recalculates the pressure based on the amount moles.
     * @param moles the moles of gas (mol)
     */
    public void setMoles(double moles) {
        if (moles == 0) {
            this.temperature = 0;
        } else if (this.moles == 0 && moles > 0) {
            this.temperature = 300;
        }
        this.moles = moles;
        calculatePressure();
    }

    /**
     * Gets the molar mass of the gas.
     *
     * @return the molar mass in kg/mol
     */
    public double getMolarMass() {
        return molarMass;
    }

    /**
     * Sets the molar mass of the gas.
     *
     * @param molarMass the molar mass in kg/mol
     */
    public void setMolarMass(double molarMass) {
        this.molarMass = molarMass;
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
