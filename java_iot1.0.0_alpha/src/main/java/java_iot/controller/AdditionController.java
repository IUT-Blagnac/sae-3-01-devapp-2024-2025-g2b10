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

/**
 * <p>AdditionController is a class that links {@link java_iot.view.AdditionView} to all the different 
 * controllers it utilizes to run (Mainly {@link java_iot.controller.SettingsController}).
 * @author ESTIENNE Alban-Moussa
 */
public class AdditionController{

    private MainSceneController msc;
    private SettingsController sc;
    private static AdditionController instance;

    private AdditionController(){
        msc = MainSceneController.getInstance();
        sc = SettingsController.getInstance();
    }

    /**
     * Requests a new AdditionWindow to the {@link java_iot.App}.
     * @param mono : If the Pane should contain a value field.
     * @param callerButton : Who called for a new window.
     */
    public void requestNewWindow(boolean mono, String callerButton){
        msc.requestNewAddition(mono, callerButton);
    }

    /**
     * Requests the list of all Data Treatment settings without loading them in the memory (READONLY)
     * @param data_name : The fieldName of the data underneath Data treatment
     * @return HashMap<String, String> : The list of settings with the key being the name.
     */
    public HashMap<String, String> requestSettingsList(String data_name){
        return sc.requestSettings("Data treatment", false);
    }

    /**
     * Requests all settings associated with a field.
     * @param field : The field to withdraw values from.
     * @return String[][] the tab containing the settings seperated in categories
     */
    public String[][] getAllSettings(String field){
        return sc.requestAllAvailableFields(field);
    }

    /**
     * Gets the requested topic name associated with the provided index.
     * @param index : The index of the topic.
     * @return String : the name of the topic.
     */
    public String getTopicNameFromIndex(int index){
        return sc.requestTopicNameFromIndex(index);
    }

    /**
     * Requests all topics name.
     * @return String[] : An array containing all the topics name.
     */
    public String[] getTopicNameFromIndex(){
        return sc.requestTopicNameFromIndex();
    }

    /**
     * Requests a specific field from a category and its index.
     * @param category : The category name.
     * @param index : The position of the field name.
     * @return
     */
    public String requestFieldFromIndex(String category, int index){
        return sc.requestFieldNameFromIndex(category, index);
    }

    /**
     * Submit a request for the controller to pass down to the model section to change a specified settings.
     * @param section : The section of the .INI file where the field is located.
     * @param name : The field name of the parameter
     * @param text : The new value to input.
     * @return boolean : If the change was successful or not.
     */
    public boolean requestSettingChange(String section, String name, String text, boolean addition){
        return sc.requestSettingChange(section, name, text, addition);
    }

    /**
     * Request the java_iot.controller.SettingsController for a setting field based on Index.
     * @param id : The id of the button containing the index of the field.
     * @return
     */
    public String requestSettingsFromId(int id){
        return sc.requestSettingsFromId(id);
    }

    /**
     * Gets the singleton instance of AdditionController.
     * @return AdditionController : the instance.
     */
    public static AdditionController getInstance(){
        if (instance == null){
            instance = new AdditionController();
        }
        return instance;
    }
}
