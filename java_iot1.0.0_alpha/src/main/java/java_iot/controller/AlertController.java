package java_iot.controller;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import java_iot.classes.Data;
import java_iot.classes.Sensor;
import java_iot.view.AlertView;

public class AlertController {

    private Data data; // Data of the sensors
    private static AlertController instance;
    private static AlertView av;
    private ScheduledExecutorService scheduler;

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

    public void setAlertView(AlertView _av) {
        av = _av;
    }

    public void setLoop() {
        if (av == null)
            return;

        scheduler = Executors.newSingleThreadScheduledExecutor();

        // Exécute `av.updateAlerts()` toutes les 5 secondes
        scheduler.scheduleAtFixedRate(() -> {
            av.updateAlerts();
        }, 0, SettingsController.getInstance().requestSetting("Data treatment", ), TimeUnit.SECONDS);
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