package java_iot.controller;

import java_iot.App;
import java_iot.view.AlertView;
import javafx.scene.control.Alert;

public class AlertController {

    private App app;
    private AlertView av;
    private MainSceneController msc;
    private static AlertController instance;

    public AlertController() {
        this.msc = MainSceneController.getInstance();
    }

    public static AlertController getInstance() {
        if (instance == null) {
            instance = new AlertController();
        }
        return instance;
    }

    public void setAlertView(AlertView _av) {
        this.av = _av;
    }

    public void requestNewWindow(boolean mono) {
        msc.requestNewAlert(mono);
    }

    /**
     * Ajoute une nouvelle alerte à la liste et met à jour la vue.
     *
     * @param alert L'alerte à ajouter.
     */
    public void addAlert(Alert alert) {
        av.updateView(); // Mettre à jour la vue
    }

}
