package java_iot.controller;

import java.util.Map;

import java_iot.classes.Data;
import java_iot.classes.Sensor;

public class AlertController {

    private Data data; // Données globales
    private static AlertController instance;

    private AlertController() {
    }

    public static AlertController getInstance() {
        if (instance == null) {
            instance = new AlertController();
        }
        return instance;
    }

    public void setData(Data data) {
        this.data = data;
    }

    /**
     * Vérifie les capteurs en alerte.
     *
     * @return Map des capteurs en alerte.
     */
    public Map<String, Sensor> getAlertingSensors() {
        if (data == null) {
            System.err.println("Les données ne sont pas initialisées !");
            return null; // Retourne une liste vide
        }
        System.out.println(data.getAlertingSensors());
        return data.getAlertingSensors();
    }

}
