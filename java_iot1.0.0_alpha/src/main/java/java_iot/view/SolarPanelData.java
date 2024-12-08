package java_iot.view;

/**
 * POJO to avoid an error
 */
public class SolarPanelData {
    private Long energy;
    private Long lastYearEnergy;
    private Long lastMonthEnergy;
    private Long lastDayEnergy;
    private Double currentPower; // Power should be a double
    private String measuredBy;

    // Getters and Setters
    public Long getEnergy() { return energy; }
    public void setEnergy(Long energy) { this.energy = energy; }

    public Long getLastYearEnergy() { return lastYearEnergy; }
    public void setLastYearEnergy(Long lastYearEnergy) { this.lastYearEnergy = lastYearEnergy; }

    public Long getLastMonthEnergy() { return lastMonthEnergy; }
    public void setLastMonthEnergy(Long lastMonthEnergy) { this.lastMonthEnergy = lastMonthEnergy; }

    public Long getLastDayEnergy() { return lastDayEnergy; }
    public void setLastDayEnergy(Long lastDayEnergy) { this.lastDayEnergy = lastDayEnergy; }

    public Double getCurrentPower() { return currentPower; }
    public void setCurrentPower(Double currentPower) { this.currentPower = currentPower; }

}
