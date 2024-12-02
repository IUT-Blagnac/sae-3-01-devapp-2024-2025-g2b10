package java_iot.model;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;
import org.ini4j.Wini;

import java_iot.App;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

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

	private Map<String, Integer> alerts;
	private List<String> dtk;
	private List<String> listenedRooms;

	private ObservableMap<String, Integer> observable_alerts;
	private ObservableList<String> observable_dtk;
	private ObservableList<String> observables_rooms;
	private SimpleIntegerProperty observable_frequency;


	public Settings(){
		this.alerts = new HashMap<>();
		this.dtk = new ArrayList<>();
		this.listenedRooms = new ArrayList<>();
		
		this.observable_alerts = FXCollections.observableMap(alerts);
		this.observable_dtk = FXCollections.observableArrayList(dtk);
		this.observables_rooms = FXCollections.observableArrayList(listenedRooms);
		this.observable_frequency = new SimpleIntegerProperty(0);
	}

	/*
	 * SETTERS AND GETTERS
	 */

	 public ObservableMap<String, Integer> getAlertsObservable(){
		return this.observable_alerts;
	 }

	 public ObservableList<String> getDtkObservable(){
		return this.observable_dtk;
	 }

	 public ObservableList<String> getRoomsObservable(){
		return this.observables_rooms;
	 }
	 
	 public SimpleIntegerProperty getFrequencyObservable(){
		return this.observable_frequency;
	 }

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
	 * Clears all the containers on request
	 * @return
	 */
	public void clearAll(){
		this.observable_alerts.clear();
		this.observable_dtk.clear();
		this.observables_rooms.clear();
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
			String server = ini.get("Connection Infos", "host");
			String port = ini.get("Connection Infos", "port");
			System.out.println(server);
			System.out.println(port);
			String finalUrl = "tcp://" + server + ":" + port;
			String publisherId = UUID.randomUUID().toString();
			IMqttClient publisher = new MqttClient(finalUrl, publisherId);
			publisher.connect();
			publisher.disconnect();
			return "0/"+ OK_COLOR_HEX +"/Connexion Réussie";
		}catch (IOException e){
			return "1/" + ERROR_COLOR_HEX + "/Fichier de configuration introuvable";
		}catch (MqttException e){
			return "1/" + ERROR_COLOR_HEX + "/La connexion au serveur MQTT a échoué : " + e.toString();
		}
	}

	/**
	 * Write parameters in file based on the input of the TextField.
	 * @todo : Check input value and notifies user if data is wrong.
	 * @param fieldData : The list of TextField to retrieve text from
	 * @return boolean : The response of the writing attempt.
	 */
	public boolean writeConnectionSettingInFile(List<TextField> fieldData){
		try{
			Wini ini = new Wini(App.class.getResource("ressources/data_collecting/config.ini"));
			// Ok so I found no better way to iterate through the variable all while modifying.
			// Since iniIterable is readOnly, i don't know. Maybe i'm sacrificing RAM for simplicity
			Wini iniIterable = new Wini(App.class.getResource("ressources/data_collecting/config.ini"));
			Iterator<String> iterator = iniIterable.get("Connection Infos").keySet().iterator();
			for (TextField tF : fieldData){
				String value = tF.getText();
				String dataSection = iterator.next();
				ini.put("Connection Infos", dataSection, value);
			}
			ini.store(new File(App.class.getResource("ressources/data_collecting/config.ini").toURI()));
			return true;
		}catch (Exception e){
			System.out.println(e);
			return false;
		}
	}

	public boolean changeSettingField(String section, String data, String val){
		try{
			int frequency = Integer.parseInt(val);
			Wini ini = new Wini(App.class.getResource("ressources/data_collecting/config.ini"));
			ini.put(section, data, val);
			ini.store(new File(App.class.getResource("ressources/data_collecting/config.ini").toURI()));
			return true;
		}catch (NumberFormatException e){
			return false;
		}catch (IOException e){
			return false;
		}catch (URISyntaxException e){
			return false;
		}
	}

	/**
	 * Grabs the .ini line corresponding to the "Data Treament" section.
	 * @param row
	 * @param element
	 */
	public void removeDataTreatmentElement(String row, String element){
		System.out.println(element);
		System.out.println(row);
		try{
			Wini ini = new Wini(App.class.getResource("ressources/data_collecting/config.ini"));
			String line = ini.get("Data treatment", row);
			List<String> attributes = Arrays.stream(line.split(", "))
								.filter(word -> !word.contains(element))
								.collect(Collectors.toList());
			line = String.join(", ", attributes);
			line = line.replace(", ,", ",");
			line = line.replaceAll("^,|,$", "").trim();
			System.out.println(line);
			ini.put("Data treatment", row, line);
			ini.store(new File(App.class.getResource("ressources/data_collecting/config.ini").toURI()));
		}catch (Exception e){
			System.out.println(e);
		}
	}

	/**
	 * Neutralizes the white space that follows the comma in the .ini
	 * 
	 * @param settingListString : The string to neutralize
	 * @return String : The same string to be used with string.split()
	 */
	public String neutralize(String settingListString){
		return settingListString.replaceAll(",\\s+",",");
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
					// Essentially what this does is putting a 1 to all toggled topics
					// And a 0 to any topics that aren't inside this list.
					for (String s : seperatedTopicList){
						fieldSettings.put(s, "1");
					}
					for (String s : ALL_TOPIC_LIST){
						if (fieldSettings.get(s) == null){
							fieldSettings.put(s, "0");
						}
					}
					break;
				case "Data treatment":
					cInfo = ini.get(page_setting_name);
					String frequence = cInfo.get("step");
					fieldSettings.put("step", frequence);
					String alerts = cInfo.get("alerts").trim();
					fieldSettings.put("alerts", alerts);
					String data_to_keep = cInfo.get("data_to_keep").trim();
					fieldSettings.put("data_to_keep", data_to_keep);
					String listenedRooms = cInfo.get("listened_rooms").trim();
					fieldSettings.put("listened_rooms", listenedRooms);

					String rawAlerts = neutralize(fieldSettings.get("alerts"));
					String rawFrequency = neutralize(fieldSettings.get("step"));
					String rawDtk = neutralize(fieldSettings.get("data_to_keep"));
					String rawListenedRooms = neutralize(fieldSettings.get("listened_rooms"));
			
					String[] rawAlertsTable = rawAlerts.split(",");

					for (String s : rawAlertsTable){
						String seperated[] = s.split(":");
						observable_alerts.put(seperated[0], Integer.valueOf(seperated[1]));
					}

					observable_dtk.addAll(Arrays.asList(rawDtk.split(",")));
					observables_rooms.addAll(Arrays.asList(rawListenedRooms.split(",")));

					observable_frequency.set(Integer.valueOf(rawFrequency));

					break;

			}
			return fieldSettings;
			
		}catch (IOException e){
			System.out.println(e);
			return null;
		}
	}

}
