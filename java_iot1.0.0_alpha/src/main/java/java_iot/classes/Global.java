package java_iot.classes;

import java.util.Map;

/* Classe représentant les données globales
 * 
/* 
 * @author PAPA-PATSOUMOUDOU Matthias
 */

public class Global {
    private String lastUpdateTime;
    private Map<String, Object> lastYearData;
    private Map<String, Object> lastMonthData;
    private Map<String, Object> lastDayData;
    private Map<String, Object> currentPower;
    private String measuredBy;

    /* Getters */
    public Map<String, Object> getCurrentPower() {
        return currentPower;
    }

    public Map<String, Object> getLastDayData() {
        return lastDayData;
    }

    public Map<String, Object> getLastMonthData() {
        return lastMonthData;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public Map<String, Object> getLastYearData() {
        return lastYearData;
    }

    public String getMeasuredBy() {
        return measuredBy;
    }

    /* Setters */

    public void setLastMonthData(Map<String, Object> lastMonthData) {
        this.lastMonthData = lastMonthData;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public void setLastYearData(Map<String, Object> lastYearData) {
        this.lastYearData = lastYearData;
    }

    public void setMeasuredBy(String measuredBy) {
        this.measuredBy = measuredBy;
    }

    public void setCurrentPower(Map<String, Object> currentPower) {
        this.currentPower = currentPower;
    }

    public void setLastDayData(Map<String, Object> lastDayData) {
        this.lastDayData = lastDayData;
    }
}