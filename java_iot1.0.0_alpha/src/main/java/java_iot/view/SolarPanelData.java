package java_iot.view;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SolarPanelData {
    private double energy;
    private double lastMonthEnergy;
    private double lastYearEnergy;
    private double currentPower;
    private double lastDayEnergy;

    // Getters and Setters
    public double getEnergy() {
        return energy;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }

    public double getLastMonthEnergy() {
        return lastMonthEnergy;
    }

    public void setLastMonthEnergy(double lastMonthEnergy) {
        this.lastMonthEnergy = lastMonthEnergy;
    }

    public double getLastYearEnergy() {
        return lastYearEnergy;
    }

    public void setLastYearEnergy(double lastYearEnergy) {
        this.lastYearEnergy = lastYearEnergy;
    }

    public double getCurrentPower() {
        return currentPower;
    }

    public void setCurrentPower(double currentPower) {
        this.currentPower = currentPower;
    }

    public double getLastDayEnergy() {
        return lastDayEnergy;
    }

    public void setLastDayEnergy(double lastDayEnergy) {
        this.lastDayEnergy = lastDayEnergy;
    }
}
