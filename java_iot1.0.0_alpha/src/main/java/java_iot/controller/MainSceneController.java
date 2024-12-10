package java_iot.controller;

import java.util.Map;

import java_iot.App;
import java_iot.classes.Data;
import java_iot.classes.Sensor;
import java_iot.view.MainSceneView;
import javafx.scene.control.Alert;

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
    private static AlertController ac;

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


    public void requestAlertLoop(){
        AlertController ac = AlertController.getInstance();
        ac.setLoop();
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
