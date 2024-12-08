package java_iot.controller;

import java.util.Map;

import java_iot.App;
import java_iot.classes.Data;
import java_iot.classes.Sensor;
import java_iot.view.MainSceneView;

/**
 * MainSceneController is the controller linked
 * {@link java_iot.view.MainSceneView} to the diverse controllers it utilizes to
 * run different panes.
 *
 * @author ESTIENNE Alban-Moussa
 */
public class MainSceneController {

    private App app;
    private MainSceneView msv;
    private static MainSceneController instance;

    private Data donnees;
    private int frequency;

    private MainSceneController() {
    }

    public Data getDonnees() {
        return donnees;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setMainSceneView(MainSceneView _msv) {
        msv = _msv;
    }

    public void setApp(App _app) {
        app = _app;
    }

    /**
     * Demande à afficher un pop-up des alertes.
     */
    public void requestAlertPopup() {
        AlertController alertController = AlertController.getInstance();
        Map<String, Sensor> alertingSensors = alertController.getAlertingSensors();

        if (!alertingSensors.isEmpty()) {
            app.showAlertPopup(alertingSensors);
        } else {
            System.out.println("Aucune alerte détectée.");
        }
    }

    /**
     * Requests a new Addition pane to the {@link java_iot.App}.
     *
     * @param mono : Whether or not the Pane should have a value field.
     * @param callerButton : Who called for a new window.
     */
    public void requestNewAddition(boolean mono, String callerButton) {
        app.showAdditionMenu(mono, callerButton);
    }

    public static MainSceneController getInstance() {
        if (instance == null) {
            instance = new MainSceneController();
        }
        return instance;
    }

    public MainSceneView getMainSceneView() {
        return msv;
    }

}
