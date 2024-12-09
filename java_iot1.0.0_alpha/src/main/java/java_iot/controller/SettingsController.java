package java_iot.controller;

import java.util.HashMap;
import java.util.List;

import java_iot.model.Settings;
import java_iot.view.AdditionView;
import java_iot.view.SettingsView;
import javafx.beans.Observable;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

/**
 * <p>SettingsController is a <b>Singleton class</b> that link {@link java_iot.view.SettingsView} and {@link java_iot.model.Settings}
 * <p>This class is only responsible for the transmission of requests by View and the transmission of updates to View.
 * @author ESTIENNE Alban-Moussa
 */
public class SettingsController {

    private SettingsView sv;
    private Settings se;
    private AdditionController ac;
    private MainSceneController msc;
    private static SettingsController instance;
    
    /**
     * Singleton private controller.
     * This class initializes the listeners of settings arrays.
     */
    private SettingsController(){
        this.msc = MainSceneController.getInstance();
        this.se = new Settings();
        
        se.getAlertsObservable().addListener((MapChangeListener<String, Integer>) c ->{
            if (c.wasAdded()){
                int value = c.getMap().get(c.getKey());
                sv.updateContainer(0, se.getAlertsObservable(), c.getKey(), value);
            }else if (c.wasRemoved()){
                se.removeDataTreatmentElement("alerts", c.getKey());
                sv.removeWithId(0, c.getKey());
            }
        });

		se.getDtkObservable().addListener((ListChangeListener<String>) c ->{
			while (c.next()){
				if (c.wasAdded()){
					for (String key : c.getAddedSubList()){
						sv.updateContainer(1, se.getDtkObservable(), key);
					}
				}
				if (c.wasRemoved()){
					for (String key : c.getRemoved()){
						se.removeDataTreatmentElement("data_to_keep", key);
						sv.removeWithId(1, key);
					}
				}
			}
        });

		se.getRoomsObservable().addListener((ListChangeListener<String>) c ->{
			while (c.next()){
                if (c.wasAdded()){
				    for (String key : c.getAddedSubList()){
						sv.updateContainer(2, se.getRoomsObservable(), key);
					}
				}
                if (c.wasRemoved()){
                    for (String key : c.getRemoved()){
                        se.removeDataTreatmentElement("listened_rooms", key);
                        System.out.println("SENDING SIGNAL TO REMOVE : " + key);
                        sv.removeWithId(2, key);
                    }
                }
			}
        });

        se.getFrequencyObservable().addListener((observable, oldvalue, newvalue) -> {
            sv.changeFrequencyText(newvalue.toString());
        });
    }

    /**
     * Provides the current instance of SettingsController, and creates one if none has been set.
     * @return SettingsController : The singleton instance.
     */
    public static SettingsController getInstance(){
        if (instance == null){
            instance = new SettingsController();
        }
        return instance;
    }

    /**
     * Request a field name based on its position in the settings file.
     * @param id : The id of the button containing the index of the field.
     * @return
     */
    public String requestSettingsFromId(int id){
        return se.getSectionNameFromId(id);
    }

    /**
     * Request all available settings associated with a field.
     * @param field : The field to withdraw settings list from.
     * @return String[][] : The settings list.
     */
    public String[][] requestAllAvailableFields(String field){
        return se.getAllAvailableSettings(field);
    }

    /**
     * Sets the {@link java_iot.view.SettingsView} of the controller.
     * This has been seperated from the constructor in order to prevent infinite loops of
     * classes referencing each others.
     * @param _sv : The SettingsView
     */
    public void setSettingsView(SettingsView _sv){
        this.sv = _sv;
    }

    /**
     * Requests a list of rewriting of the connection settings.
     * @param informationFieldList : The list containing all the fields.
     */
    public void requestConnectionSettingInFile(List<TextField> informationFieldList){
        se.writeConnectionSettingInFile(informationFieldList);
    }

    /**
     * Request a change of the topics section based on the update of a button state
     * @param list : The list of togglebutton to write states from.
     */
    public void requestSaveTopicSettings(List<ToggleButton> list){
        se.saveToggleButtonTopicSettings(list);
    }

    /**
     * Requests a specific topic name based on its position in the config file.
     * @param index : THe position of the field.
     * @return
     */
    public String requestTopicNameFromIndex(int index){
        return se.getTopicNameFromIndex(index);
    }

    /**
     * Requests all topics name.
     * @return String[] : An array containing all the topics name.
     */
    public String[] requestTopicNameFromIndex(){
        return se.getTopicNameFromIndex();
    }

    /**
     * Requests a specific field from a category and its index.
     * @param category : The category name.
     * @param index : The position of the field name.
     * @return
     */
    public String requestFieldNameFromIndex(String category, int index){
        return se.getFieldFromIndex(category, index);
    }

    /**
     * Requests a deletion of a specific settings inside a list.
     * @param ol : The observable list containing the key
     * @param key : The key to remove.
     */
    public void requestDeletionOfSettings(Observable ol, String key){
        se.removeSettings(ol, key);
    }

    /**
     * Submit a request to get the specificed section of the settings file.
     * @param section :  The section to get
     * @param loadSettingsIntoMemory : Specify if the returned settings should also be loaded into memory for usage
     * @return HashMap<String, String> : The map of the values. Key is the name and value is its value.
     */
    public HashMap<String, String> requestSettings(String section, boolean loadSettingsIntoMemory){
        return se.loadSetting(section, loadSettingsIntoMemory);
    }

    /**
     * Submit a request for the controller to pass down to the model section to change a specified settings.
     * @param section : The section of the .INI file where the field is located.
     * @param name : The field name of the parameter
     * @param text : The new value to input.
     * @return boolean : If the change was successful or not.
     */
    public boolean requestSettingChange(String section, String name, String text, boolean addition){
        return se.changeSettingField(section, name, text, addition);
    }

    /**
     * Requests a new AdditionWindow to the {@link java_iot.App}.
     * @param mono : If the Pane should contain a value field.
     * @param callerButton : Who called for a new window.
     */
    public void requestNewWindow(boolean mono, String callerButton){
        AdditionController.getInstance().requestNewWindow(mono, callerButton);
    }
    
    /**
     * Requests a connection test to the MQTT server based on the information 
     * provided in the configuration file
     * @return String : The message formatted : returncode/colourcode/message of the response.
     */
    public String requestConnectionTest(){
        return se.testConnection();
    }
}
