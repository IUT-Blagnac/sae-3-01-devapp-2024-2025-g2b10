package java_iot.model;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.ini4j.Ini;
import org.ini4j.Profile.Section;

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

public class Settings {

	private final String[] ALL_TOPIC_LIST = {"AM107/by-room/#", "Triphaso/by-room/#", "solaredge/blagnac/#"};
	private final Map<String, Integer> NAME_TO_TOPIC = Map.of("am107Button", 0, "triphasoButton", 1, "solarDataButton", 2);

	public void saveTopicSettings(List<ToggleButton> list){
		String constructedString = "";
		for (ToggleButton tb : list){
			if (tb.isSelected()){
				System.out.println("My iD is : " + tb.getId());
				constructedString += ALL_TOPIC_LIST[NAME_TO_TOPIC.get(tb.getId())];
				constructedString += ",";
			}
		}

		// Gets rid of the last ,
		constructedString = constructedString.substring(0, Math.max(0, constructedString.length() - 1));
		System.out.println(constructedString);
		try{
			Ini ini = new Ini(App.class.getResource("ressources/data_collecting/config.ini"));
			ini.put("Topics", "topics", constructedString);
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	public HashMap<String, String> loadSetting(String page_setting_name){	
		try{
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
