package java_iot.controller;

import java.util.HashMap;
import java.util.List;

import java_iot.model.Settings;
import java_iot.view.SettingsView;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

public class SettingsController {

    private SettingsView sv;
    private Settings se;
    private MainSceneController msc;
    private static SettingsController instance;
    
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
				for (String key : c.getAddedSubList()){
					if (c.wasAdded()){
						sv.updateContainer(2, se.getRoomsObservable(), key);
					}else if (c.wasRemoved()){
						se.removeDataTreatmentElement("listened_rooms", key);
						sv.removeWithId(2, key);
					}
				}
			}
        });

        se.getFrequencyObservable().addListener((observable, oldvalue, newvalue) -> {
            sv.changeFrequencyText(newvalue.toString());
        });
    }

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

    public String[][] requestAllAvailableFields(){
        return se.getAllAvailableSettings();
    }

    public void setSettingsView(SettingsView _sv){
        this.sv = _sv;
    }

    public void requestConnectionSettingInFile(List<TextField> informationFieldList){
        se.writeConnectionSettingInFile(informationFieldList);
    }

    public void requestSaveTopicSettings(List<ToggleButton> list){
        se.saveTopicSettings(list);
    }

    public String requestTopicNameFromIndex(int index){
        return se.getTopicNameFromIndex(index);
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
    public boolean requestSettingChange(String section, String name, String text){
        return se.changeSettingField(section, name, text);
    }
    
    public String requestConnectionTest(){
        return se.testConnection();
    }
    public void clearContainers(){
        
    }

    

}
