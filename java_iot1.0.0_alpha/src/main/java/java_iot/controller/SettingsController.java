package java_iot.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import java_iot.Main;
import java_iot.model.Settings;
import java_iot.view.MainSceneView;
import java_iot.view.SettingsView;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

public class SettingsController {

    private SettingsView sv;
    private Settings se;
    private MainSceneController msc;
    
    public SettingsController(MainSceneController _msc){
        this.msc = _msc;
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

    public void setSettingsView(SettingsView _sv){
        this.sv = _sv;
    }

    public void requestConnectionSettingInFile(List<TextField> informationFieldList){
        se.writeConnectionSettingInFile(informationFieldList);
    }

    public void requestSaveTopicSettings(List<ToggleButton> list){
        se.saveTopicSettings(list);
    }

    public HashMap<String, String> requestSettings(String section){
        return se.loadSetting(section);
    }

    public boolean requestSettingChange(String section, String name, String text){
        return se.changeSettingField(section, name, text);
    }
    
    public String requestConnectionTest(){
        return se.testConnection();
    }
    public void clearContainers(){
        
    }

    

}
