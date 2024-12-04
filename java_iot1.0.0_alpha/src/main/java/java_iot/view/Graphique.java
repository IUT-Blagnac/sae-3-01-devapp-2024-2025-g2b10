package java_iot.view;

import java_iot.controller.MainSceneController;

public class Graphique {
    private static Graphique instance;
    private MainSceneController msc;
    private Graphique(MainSceneController pfmsc) {
        msc=pfmsc;
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

    public void showDash(){

    }

    public void showRoom(){

    }

    public void showPanel(){
        
    }
}