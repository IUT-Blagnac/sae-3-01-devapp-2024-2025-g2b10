package java_iot.view;

import java.io.IOException;

import java_iot.App;
import java_iot.controller.MainSceneController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

public class Graphique {
    private static Graphique instance;
    private MainSceneController msc;

    private Graphique(MainSceneController pfmsc) {
        msc = pfmsc;
    }

    /**
     * Returns the existing instance of the SettingsView or creates one.
     * 
     * @param MainSceneView pfmsc : the Main Scene Controller
     * @author GIARD--PELLAT Jules
     */
    public static Graphique getInstance(MainSceneController pfmsc) {
        if (instance == null) {
            instance = new Graphique(pfmsc);
        }
        return instance;
    }

    public void showDash() {
        // Je te laisse ça vide Quentin
    }

    public void showRoom() {

    }

    public void showPanel() {

    }
}