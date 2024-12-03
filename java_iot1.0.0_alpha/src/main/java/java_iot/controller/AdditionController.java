package java_iot.controller;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Set;

import java_iot.view.MainSceneView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class AdditionController{

    private MainSceneController msc;
    private SettingsController sc;
    private static AdditionController instance;

    private AdditionController(){
        msc = MainSceneController.getInstance();
        sc = SettingsController.getInstance();
    }

    public void requestNewWindow(boolean mono, String callerButton){
        msc.requestNewAddition(mono, callerButton);
    }

    public HashMap<String, String> requestSettingsList(String data_name){
        return sc.requestSettings("Data treatment", false);
    }

    public String[][] getAllSettings(){
        return sc.requestAllAvailableFields();
    }

    public String getTopicNameFromIndex(int index){
        return sc.requestTopicNameFromIndex(index);
    }

    /**
     * Request the java_iot.controller.SettingsController for a setting field based on Index.
     * @param id : The id of the button containing the index of the field.
     * @return
     */
    public String requestSettingsFromId(int id){
        return sc.requestSettingsFromId(id);
    }

    public static AdditionController getInstance(){
        if (instance == null){
            instance = new AdditionController();
        }
        return instance;
    }
}
