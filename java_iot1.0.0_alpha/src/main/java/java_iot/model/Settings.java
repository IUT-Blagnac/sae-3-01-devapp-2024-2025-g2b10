package java_iot.model;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.WildcardType;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
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
import javafx.scene.control.TextField;
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
	// Some final colours codes
	private final String OK_COLOR_HEX = "#4d8e41";
	private final String ERROR_COLOR_HEX = "#902d2d";
	private final String WARNING_COLOR_HEX = "#ab743a";

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
	 * <p>Test the connection to the MQTT server based on the provided information on the .ini file
	 * <p>Returns a formatted string seperated by "/" with : 
	 * <ul>
	 * <li>The first character being the answer, 0 is all correct, 1 is an error.
	 * <li>The second string being the colour code to display, allows for customizable messages in colour
	 * <li>The last is the string content of the message.
	 * </ul>
	 * <p> The first parameter should only be used for debugging purposes.
	 * @return String : The status of the connection
	 * @author ESTIENNE Alban-Moussa
	 */
	public String testConnection(){
		try{
			Wini ini = new Wini(App.class.getResource("ressources/data_collecting/config.ini"));
			String server = ini.get("Connection infos", "host");
			String port = ini.get("Connection infos", "port");
			String finalUrl = "tcp://" + server + ":" + port;
			String publisherId = UUID.randomUUID().toString();
			IMqttClient publisher = new MqttClient(finalUrl, publisherId);
			return "0/"+ OK_COLOR_HEX +"/Connexion Réussie";
		}catch (IOException e){
			return "1/" + ERROR_COLOR_HEX + "/Fichier de configuration introuvable";
		}catch (MqttException e){
			return "1/" + ERROR_COLOR_HEX + "/La connexion au serveur MQTT a échoué : " + e.getMessage();
		}
	}

	/**
	 * Write parameters in file based on the input of the TextField.
	 * @TODO : Check input value and notifies user if data is wrong.
	 * @param fieldData : The list of TextField to retrieve text from
	 * @return boolean : The response of the writing attempt.
	 */
	public boolean writeSettingInFile(List<TextField> fieldData){
		try{
			Wini ini = new Wini(App.class.getResource("ressources/data_collecting/config.ini"));
			Iterator<Entry<String, String>> entryS = ini.get("Connection Infos").entrySet().iterator();
			for (TextField tF : fieldData){
				String dataSection = entryS.next().getKey();
				String value = tF.getText();
				System.out.println("text value is ! "+ value);
				System.out.println("Field name is : " + dataSection);
				//ini.put("Connection Infos", value, dataSection);
			}
			ini.store(new File(App.class.getResource("ressources/data_collecting/config.ini").toURI()));
			return true;
		}catch (Exception e){
			return false;
		}
	}

	/**
	 * <p>Loads the settings based on the requested parameter field
	 * <p>OPTION Connection Infos will provide the host, port and keepalive values
	 * <p>OPTION Topics will provide the topic list
	 * <p>OPTION Data treatment will provide the :
	 * <ul>
	 * <li>  frequency at which datas are written
	 * <li>  list of alerts
	 * <li>  list of kept data
	 * <li>  list of listened rooms
	 * </ul>
	 * @param page_setting_name : The name of the field to retrieve
	 * @return A HashMap<String, String> of the setting name and its value.
	 * @author Alban-Moussa ESTIENNE
	 */
	public HashMap<String, String> loadSetting(String page_setting_name){	
		try{
			// Loads the ini file from the ressources folder
			Ini ini = new Ini(App.class.getResource("ressources/data_collecting/config.ini"));
			System.out.println(App.class.getResource("ressources/data_collecting/config.ini"));
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
