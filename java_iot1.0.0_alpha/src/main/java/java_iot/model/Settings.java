package java_iot.model;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.WildcardType;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.ini4j.Ini;
import org.ini4j.Profile.Section;
import org.ini4j.Wini;

import java_iot.App;
import java_iot.Main;
import java_iot.view.MainSceneController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;

/**
 * Settings is a model class that allows R/W access to the config.ini file
 * Located in resources/ressources/data_collecting/config.ini
 * Settings should NOT be called by a controller.
 * Settings provide the data necessary for SettingsView to read
 * SettingsView notifies Settings of any update on the Settings interface
 * 
 * @author Alban-Moussa ESTIENNE
 * @see java/java_iot/view/SettingsView
 */
public class Settings {

	// ALL_TOPIC_LIST refers to the list of subscribable topics.
	private final String[] ALL_TOPIC_LIST = {"AM107/by-room/#", "Triphaso/by-room/#", "solaredge/blagnac/#"};
	// NAME_TO_TOPIC converts the button ID to its corresponding topic name.
	private final Map<String, Integer> NAME_TO_TOPIC = Map.of("am107Button", 0, "triphasoButton", 1, "solarDataButton", 2);

	/**
	 * Saves the enabled topics in the .ini file 
	 * Convert binary ON/OFF states of button into a string list of topic to be subscribed to
	 * 
	 * @param list : List of topic togglebuttons to be read
	 * @author Alban-Moussa ESTIENNE
	 */
	public void saveTopicSettings(List<ToggleButton> list){
		String constructedString = "";
		for (ToggleButton tb : list){
			if (tb.isSelected()){
				constructedString += ALL_TOPIC_LIST[NAME_TO_TOPIC.get(tb.getId())];
				constructedString += ",";
			}
		}

		// Gets rid of the last ,
		// Looks overly complicated but really it just crops the last char or nothing if the string is empty
		constructedString = constructedString.substring(0, Math.max(0, constructedString.length() - 1));
		System.out.println(constructedString);
		try{
			Wini ini = new Wini(App.class.getResource("ressources/data_collecting/config.ini"));
			ini.put("Topics", "topics", constructedString);
			ini.store(new File(App.class.getResource("ressources/data_collecting/config.ini").toURI()));
		} catch (URISyntaxException e){
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	/**
	 * <p>Loads the settings based on the requested parameter field<br>
	 * OPTION Connection Infos will provide the host, port and keepalive values<br>
	 * OPTION Topics will provide the topic list<br>
	 * OPTION Data treatment will provide the :<br>
	 *  - frequency at which datas are written<br>
	 *  - list of alerts<br>
	 *  - list of kept data<br>
	 *  - list of listened rooms</p>
	 * @param page_setting_name : The name of the field to retrieve
	 * @return A HashMap<String, String> of the setting name and its value.
	 * @author Alban-Moussa ESTIENNE
	 */
	public HashMap<String, String> loadSetting(String page_setting_name){	
		try{
			// Loads the ini file from the ressources folder
			Ini ini = new Ini(App.class.getResource("ressources/data_collecting/config.ini"));
			HashMap<String, String> fieldSettings = new HashMap<>();
			
			switch (page_setting_name){
				case "Connection Infos":
					Section cInfo = ini.get(page_setting_name);
					fieldSettings.put("host", cInfo.get("host"));
					fieldSettings.put("port", cInfo.get("port"));
					fieldSettings.put("keepalive", cInfo.get("keepalive"));
					break;
				case "Topics":
					cInfo = ini.get(page_setting_name);
					String topicList = cInfo.get("topics");
					String[] seperatedTopicList = topicList.split(",");
					for (String s : seperatedTopicList){
						fieldSettings.put(s, "1");
					}
					for (String s : ALL_TOPIC_LIST){
						if (fieldSettings.get(s) == null){
							fieldSettings.put(s, "0");
						}
					}
					break;
				

			}
			return fieldSettings;
			
		}catch (IOException e){
			System.out.println(e);
			return null;
		}
	}

}
