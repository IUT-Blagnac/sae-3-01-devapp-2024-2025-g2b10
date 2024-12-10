package java_iot.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;
import org.ini4j.Wini;

import java_iot.App;
import javafx.beans.Observable;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

/**
 * <p>Settings is a model class that allows R/W access to the config.ini file, typically the 
   reading of a specified or all settings, and the updating, deletion and creating of a requested
   settings.
 * <p>This class handles all the necessary verifications to ensure a proper configuration file, and 
   provides an adequate answer for the controller to relay.
   Located in resources/ressources/data_collecting/config.ini
 * <p>Settings should NOT be called by a view.
 * <p>Settings provide the data necessary for SettingsController to pass down.
 * <p>SettingsController notifies Settings of any update on the Settings interface
 * 
 * @author Alban-Moussa ESTIENNE
 * @see java_iot.view.SettingsController
 */
public class Settings {

	// ALL_TOPIC_LIST refers to the list of subscribable topics.
	private final String[] ALL_TOPIC_LIST = { "AM107/by-room/#", "Triphaso/by-room/#", "solaredge/blagnac/#" };
	// NAME_TO_TOPIC converts the button ID to its corresponding topic name.
	private final Map<String, Integer> NAME_TO_TOPIC = Map.of("am107Button", 0, "triphasoButton", 1, "solarDataButton",
			2);
	// ALL VALUES AVAILABLE
	private final String[] ALL_TRIPHASO_VALUES = { "Irms", "Vrms", "phase_angle", "negative_active_power_W",
			"negative_reactive_power_VAR",
			"positive_active_power_W", "positive_reactive_power_VAR", "sum_negative_active_energy_Wh",
			"sum_negative_reactive_energy_VARh",
			"sum_positive_active_energy_Wh", "sum_positive_reactive_energy_VARh", "positive_active_power_W",
			"positive_reactive_power_VAR",
			"sum_negative_active_energy_Wh", "sum_negative_reactive_energy_VARh", "sum_positive_active_energy_Wh",
			"sum_positive_reactive_energy_VARh" };

	private final String[] ALL_AM107_VALUES = { "temperature", "humidity", "activity", "co2", "tvoc", "illumination",
			"infrared", "infrared_and_visible", "pressure" };

	private final String[] ALL_SOLAREDGE_VALUES = { "lastUpdateTime", "lastUpdateTime", "lifeTimeData", "lastYearData",
			"lastMonthData", "lastDayData", "currentPower", "measuredBy" };

	private final String[] TOPICS_NAMES_ORDERED = {"AM107", "Triphaso", "Solaredge", "Rooms"};

	private final String[] ALL_ROOMS = {"B201","C001","B109","B002","Salle-conseil","B105","Foyer-etudiants-entrée","B111","B234","E006","B113","E209","E003","B217","B112","C002","E001","C102","E007","B203","E208","amphi1","E210","B103","E207","E101","E100","C006","hall-amphi","E102","E103","B110","hall-entrée-principale","B106","B001","E004","E106","Foyer-personnels","B202","Local-velo","C004"};
	// Some final colours codes

	private final String[][] ALL_VALUES = {ALL_AM107_VALUES, ALL_TRIPHASO_VALUES, ALL_SOLAREDGE_VALUES};
	private final String[][] ALL_ROOMS_VALUES = {ALL_ROOMS};

	private final String OK_COLOR_HEX = "#4d8e41";
	private final String ERROR_COLOR_HEX = "#902d2d";
	@SuppressWarnings("unused")
	private final String WARNING_COLOR_HEX = "#ab743a";

	private Map<String, Integer> alerts;
	private List<String> dtk;
	private List<String> listenedRooms;

	private ObservableMap<String, Integer> observable_alerts;
	private ObservableList<String> observable_dtk;
	private ObservableList<String> observables_rooms;
	private SimpleIntegerProperty observable_frequency;

	public Settings() {
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

	public ObservableMap<String, Integer> getAlertsObservable() {
		return this.observable_alerts;
	}

	public ObservableList<String> getDtkObservable() {
		return this.observable_dtk;
	}

	public ObservableList<String> getRoomsObservable() {
		return this.observables_rooms;
	}

	public SimpleIntegerProperty getFrequencyObservable() {
		return this.observable_frequency;
	}

	 public String getTopicNameFromIndex(int index){
		return TOPICS_NAMES_ORDERED[index];
	}

	public String[] getTopicNameFromIndex(){
		return TOPICS_NAMES_ORDERED;
	}

	public String getSetting(String section, String field){
		try {
			Wini ini = new Wini(new FileInputStream("data_collecting/config.ini"));
			return ini.get(section, field);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	 /**
	  * <p>Returns a list of all the associated values with a provided field.
	  * <p>This method isn't as efficient as I had wished it to be, as I found
	    no other ways to distinguish rooms from sensors datas.
	  * @param field : The field name of the data.
	  * @return String[][] : All the possible options inside.
	  */
	 public String[][] getAllAvailableSettings(String field){
		if (field.equals("listened_rooms")){
			return this.ALL_ROOMS_VALUES;
		}else{
			return this.ALL_VALUES;
		}
	 }

	/**
	 * <p>Reads the raw ini file and withdraw the categories by string regex.
	 * <p>Provide the requested category based on its position in the configuration file.
	 * <p>The only purpose of this class is to prevent having to pass down full string of category names which
	   can cause issues when adding or modifying existing categories. The advantage of using indexes is that it
	   is versatile for further additions.

	 * <p><b>Footnote : The regexes and the match finders were 90% ChatGPT as I cannot comprehend how a regex work.
	 * @param index : The ID of the button linked to the position of the category.
	 * @return fieldName : The field name associated with the index provided.
	 * 
	 * @author ChatGPT 4o Mini, ESTIENNE Alban-Moussa
	 */
	 public String getSectionNameFromId(int numberIndex){
		try{
			String configString = Files.readString(Paths.get("data_collecting/config.ini"));

			/*
			 * Ok so you must probably wonder what the hell this is
			 * Well let me explain
			 * Since ini4j is wonderful and only gives a SET of sections, I CANNOT use an
			 * index to specify which
			 * section. So I need to manually go in the String of the file, filter anything
			 * in brackets, and
			 * THEN AND ONLY THEN can I see the order of the sections.
			 * Thanks.
			 */

			// This section is purely CHATGPT (thanks bud), sorry but regex are hell
			List<String> sections = new ArrayList<>();
			Pattern pattern = Pattern.compile("\\[([a-zA-Z0-9\\s]+)\\]");
			Matcher matcher = pattern.matcher(configString);

			// Find all matches and add them to the list
			while (matcher.find()) {
				sections.add(matcher.group(1)); // Group 1 contains the section name
			}

			return sections.get(numberIndex);
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * <p>Reads the raw ini file and withdraw the fields by string regex.
	 * <p>Provides the request field based on its <b>position</b> in the sub-sequent <b>provided category</b>
	   of the ini file
	 * <p>The only purpose of this class is to prevent having to pass down full string of field names which
	   can cause issues when adding or modifying existing fields. The advantage of using indexes is that it
	   is versatile for further additions.

	 * <p><b>Footnote : The regexes and the match finders were 90% ChatGPT as I cannot comprehend how a regex work.
	 * @param category : The category where to find the index.
	 * @param index : The ID of the button linked to the position of the field.
	 * @return fieldName : The field name associated with the index provided.
	 * 
	 * @author ChatGPT 4o Mini, ESTIENNE Alban-Moussa
	 */
	public String getFieldFromIndex(String category, int index){
		try{
			String config = Files.readString(Paths.get("data_collecting/config.ini"));

			Pattern categoryPattern = Pattern.compile("\\[([A-Za-z0-9\\s]+)\\]"); 
			Matcher categoryMatcher = categoryPattern.matcher(config);

			List<String> fields = new ArrayList<>();

			while (categoryMatcher.find()) {
				String categoryName = categoryMatcher.group(1);
				if (categoryName.equals(category)) {
					int startIndex = categoryMatcher.end();
					String categoryData = config.substring(startIndex);

					Pattern fieldPattern = Pattern.compile("^(?!\\[)([^=\\n]+)(?=\\s*=)", Pattern.MULTILINE);
					Matcher fieldMatcher = fieldPattern.matcher(categoryData);

					while (fieldMatcher.find()) {
						fields.add(fieldMatcher.group(1).trim());
					}
					if (fields.size() >= index + 1) {
						return fields.get(index);
					} else {
						return "";
					}
				}
			}
			return "";
		}catch (Exception e){
			return "";
		}
	}

	/**
	 * Saves the enabled topics in the .ini file
	 * Convert binary ON/OFF states of button into a string list of topic to be
	 * subscribed to
	 * 
	 * @param list : List of topic togglebuttons to be read
	 * @author Alban-Moussa ESTIENNE
	 */
	public void saveToggleButtonTopicSettings(List<ToggleButton> list){
		String constructedString = "";
		for (ToggleButton tb : list) {
			if (tb.isSelected()) {
				constructedString += ALL_TOPIC_LIST[NAME_TO_TOPIC.get(tb.getId())];
				constructedString += ",";
			}
		}

		// Gets rid of the last ,
		// Looks overly complicated but really it just crops the last char or nothing if
		// the string is empty
		constructedString = constructedString.substring(0, Math.max(0, constructedString.length() - 1));
		try {
			Wini ini = new Wini(new FileInputStream("data_collecting/config.ini"));
			ini.put("Topics", "topics", constructedString);
			ini.store(new File("data_collecting/config.ini"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Clears all the containers on request.
	 * This is more of a debugging option and shouldn't be used in a functional way.
	 */
	public void clearAll() {
		this.observable_alerts.clear();
		this.observable_dtk.clear();
		this.observables_rooms.clear();
	}

	/**
	 * <p>
	 * Test the connection to the MQTT server based on the provided information on
	 * the .ini file
	 * <p>
	 * Returns a formatted string seperated by "/" with :
	 * <ul>
	 * <li>The first character being the answer, 0 is all correct, 1 is an error.
	 * <li>The second string being the colour code to display, allows for
	 * customizable messages in colour
	 * <li>The last is the string content of the message.
	 * </ul>
	 * <p>
	 * The first parameter should only be used for debugging purposes.
	 * 
	 * @return String : The status of the connection
	 * @author ESTIENNE Alban-Moussa
	 */
	public String testConnection() {
		try {
			Wini ini = new Wini(new File("data_collecting/config.ini"));
			String server = ini.get("Connection Infos", "host");
			String port = ini.get("Connection Infos", "port");
			System.out.println(server);
			System.out.println(port);
			String finalUrl = "tcp://" + server + ":" + port;
			String publisherId = UUID.randomUUID().toString();
			IMqttClient publisher = new MqttClient(finalUrl, publisherId);
			publisher.connect();
			publisher.disconnect();
			return "0/" + OK_COLOR_HEX + "/Connexion Réussie";
		} catch (IOException e) {
			return "1/" + ERROR_COLOR_HEX + "/Fichier de configuration introuvable";
		} catch (MqttException e) {
			return "1/" + ERROR_COLOR_HEX + "/La connexion au serveur MQTT a échoué : " + e.toString();
		}
	}

	/**
	 * Write parameters in file based on the input of the TextField.
	 * 
	 * @todo : Check input value and notifies user if data is wrong.
	 * @param fieldData : The list of TextField to retrieve text from
	 * @return boolean : The response of the writing attempt.
	 */
	public boolean writeConnectionSettingInFile(List<TextField> fieldData) {
		try {
			Wini ini = new Wini(new File("data_collecting/config.ini"));
			// Ok so I found no better way to iterate through the variable all while
			// modifying.
			// Since iniIterable is readOnly, i don't know. Maybe i'm sacrificing RAM for
			// simplicity
			Wini iniIterable = new Wini(new File("data_collecting/config.ini"));
			Iterator<String> iterator = iniIterable.get("Connection Infos").keySet().iterator();
			for (TextField tF : fieldData) {
				String value = tF.getText();
				String dataSection = iterator.next();
				ini.put("Connection Infos", dataSection, value);
			}
			ini.store(new File("data_collecting/config.ini"));
			return true;
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
	}

	@SuppressWarnings("unused")
	public boolean changeSettingField(String section, String data, String val, boolean addition){
		try{
			Wini ini = new Wini(new File("data_collecting/config.ini"));
			if (!addition){
				int frequency = Integer.parseInt(val);
				ini.put(section, data, val);
			}else{
				System.out.println("REQUEST RECEIVED.");
				String[] checker = val.split(":");
				// Alerts are formatted like : name:value. If the splitter provides 2 fields, it's an alert.
				if (checker.length == 2){
					int checkInt = Integer.parseInt(checker[1]);
				}
				String selection = ini.get(section, data);
				
				// Checks if the field already exists, if it does, replace it, if not, add it
				if (selection.contains(checker[0])){
					System.out.println("REROUTING PROCESS TO : REPLACE");
					System.out.println("REPLACING "+ checker[0] + " FIELD WITH VALUE : "+ checker[1]);
					selection = selection.replaceAll(checker[0]+":\\d+", val);
				}else{
					// Prevents the addition of the , if this is the first value
					if (!selection.isBlank()){
						selection += ", ";
					}
					selection += val;
				}
				ini.put(section, data, selection);
			}
			ini.store(new File("data_collecting/config.ini"));
			loadSetting("Data treatment", true);
			return true;
		}catch (NumberFormatException e){
			System.err.println("REJECTED REQUEST WITH REASON : MALFORMED NUMBER");
			return false;
		}catch (IOException e){
			System.err.println("REJECTED REQUEST WITH REASON : UNAVAILABLE RESOURCE");
			return false;
		}
	}

	/**
	 * Grabs the .ini line corresponding to the "Data Treament" section.
	 * 
	 * @param row
	 * @param element
	 */
	public void removeDataTreatmentElement(String row, String element) {
		System.out.println(element);
		System.out.println(row);
		try {
			Wini ini = new Wini(new File("data_collecting/config.ini"));
			String line = ini.get("Data treatment", row);
			List<String> attributes = Arrays.stream(line.split(", "))
					.filter(word -> !word.contains(element))
					.collect(Collectors.toList());
			line = String.join(", ", attributes);
			line = line.replace(", ,", ",");
			line = line.replaceAll("^,|,$", "").trim();
			System.out.println(line);
			ini.put("Data treatment", row, line);
			ini.store(new File("data_collecting/config.ini"));
		} catch (Exception e) {
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
		return settingListString.replaceAll("\\s+","");
	}

	/**
	 * Removes a setting in the provided list on request.
	 * @param ol : The list where to remove it. 
	 * @param key : The name of the field to remove.
	 */
	public void removeSettings(Observable ol, String key){
        if (ol instanceof ObservableList) {
            ((ObservableList) ol).remove(key);
        } else if (ol instanceof ObservableMap) {
            ((ObservableMap) ol).remove(key);
        }
	}

	/**
	 * <p>
	 * Loads the settings based on the requested parameter field
	 * <p>
	 * OPTION Connection Infos will provide the host, port and keepalive values
	 * <p>
	 * OPTION Topics will provide the topic list
	 * <p>
	 * OPTION Data treatment will provide the :
	 * <ul>
	 * <li>frequency at which datas are written
	 * <li>list of alerts
	 * <li>list of kept data
	 * <li>list of listened rooms
	 * </ul>
	 * 
	 * @param page_setting_name : The name of the field to retrieve
	 * @return A HashMap<String, String> of the setting name and its value.
	 * @author Alban-Moussa ESTIENNE
	 */
	public HashMap<String, String> loadSetting(String page_setting_name, boolean loadSettingsIntoMemory) {
		try {
			// Loads the ini file from the ressources folder
			Wini ini = new Wini(new File("data_collecting/config.ini"));
			HashMap<String, String> fieldSettings = new HashMap<>();

			switch (page_setting_name) {
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
					for (String s : seperatedTopicList) {
						fieldSettings.put(s, "1");
					}
					for (String s : ALL_TOPIC_LIST) {
						if (fieldSettings.get(s) == null) {
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

					System.err.println("PERMISSION TO LOAD INTO MEMORY HAS BEEN SET TO " + loadSettingsIntoMemory);

					if (!loadSettingsIntoMemory){
						return fieldSettings;
					}

					System.err.println("CODE LOADED INTO MEMORY");

					String rawAlerts = neutralize(fieldSettings.get("alerts"));
					String rawFrequency = neutralize(fieldSettings.get("step"));
					String rawDtk = neutralize(fieldSettings.get("data_to_keep"));
					String rawListenedRooms = neutralize(fieldSettings.get("listened_rooms"));
			
					System.out.println(rawAlerts);
					String[] rawAlertsTable = rawAlerts.split(",");

					

					for (String s : rawAlertsTable){
						String seperated[] = s.split(":");
						if (!s.isBlank()) observable_alerts.putIfAbsent(seperated[0], Integer.valueOf(seperated[1]));
					}

					for (String s : rawDtk.split(",")){
						s = s.trim();
						if (!observable_dtk.contains(s)){
							if (!s.isBlank()) observable_dtk.add(s);
						}
					}
					for (String s : rawListenedRooms.split(",")){
						s = s.trim();
						System.out.println("LOADING : " + s);
						if (!observables_rooms.contains(s)){
							if (!s.isBlank()) observables_rooms.add(s);
						}
					}
					observable_frequency.set(Integer.valueOf(rawFrequency));

					break;

			}
			return fieldSettings;

		} catch (IOException e) {
			System.out.println(e);
			return null;
		}
	}

}
