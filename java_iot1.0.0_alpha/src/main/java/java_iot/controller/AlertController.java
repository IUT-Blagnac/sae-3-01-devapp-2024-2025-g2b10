package java_iot.controller;

import java.util.Map;

import java_iot.classes.Data;
import java_iot.classes.Sensor;

public class AlertController {

    private Data data; // Data of the sensors
    private static AlertController instance;

    private AlertController() {
    }

    /**
     * Provides the current instance of SettingsController, and creates one if
     * none has been set.
     *
     * @return SettingsController : The singleton instance.
     */
    public static AlertController getInstance() {
        if (instance == null) {
            instance = new AlertController();
        }
        return instance;
    }

    /*
     * Sets the data used to display the alerts
     */
    public void setData(Data data) {
        this.data = data;
    }

    /**
     * Checks the sensors in alert
     *
     * @return Map : all sensors in alert.
     */
    public Map<String, Sensor> getAlertingSensors() {
        if (data == null) {
            System.err.println("Les données ne sont pas initialisées !");
            return null; // No data
        }
        System.out.println(data.getAlertingSensors());
        return data.getAlertingSensors();
    }

}
