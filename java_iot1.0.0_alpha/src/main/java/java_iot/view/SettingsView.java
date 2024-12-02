package java_iot.view;

import java.beans.EventHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import java_iot.Main;
import java_iot.model.PaneCloner;
import java_iot.model.Settings;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * SettingsView is working in conjunction with java_iot.model.Settings and handles the 
 * graphical part of the settings interface.
 * SettingsView stores a reference to the main scene controller.
 * SettingsView is a singleton as it should prevent duplication in data reading and writing
 * SettingsView should ONLY be called within the view package, as Navbar needs to call a data loading
 * process to load the according settings without any further requests.
 * 
 * @see java_iot.model.Settings
 * @author ESTIENNE Alban-Moussa
 */
public class SettingsView {
    
	private MainSceneController msc;
	private static SettingsView instance;
    private Settings settingsAccesser;
	private Pane connectionInfoPane;
	private Pane topicsPane;
	private Pane treatmentPane;

	private List<Button> settingButtonList;
	private List<ToggleButton> toggleButtonList;
	private List<TextField> informationFieldList;

	private Map<String, Integer> alerts;
	private List<String> dtk;
	private List<String> listenedRooms;

	private ObservableMap<String, Integer> observable_alerts;
	private ObservableList<String> observable_dtk;
	private ObservableList<String> observables_rooms;

	/**
	 * Private constructor for the SettingsView singleton.
	 * Stores a single instance of _msc that will be UNCHANGEABLE unless force-overwritten.
	 * 
	 * @author ESTIENNE Alban-Moussa
	 */
	private SettingsView(MainSceneController _msc){
		settingButtonList = new ArrayList<>();
		toggleButtonList = new ArrayList<>();
		informationFieldList = new ArrayList<>();
		msc = _msc;

		connectionInfoPane = msc.connectionPane;
		topicsPane = msc.topicPane;
		treatmentPane = msc.treatmentPane;

		settingButtonList.add(msc.connectionButton);
		settingButtonList.add(msc.topicButton);
		settingButtonList.add(msc.treatmentButton);

		toggleButtonList.add(msc.am107Button);
		toggleButtonList.add(msc.triphasoButton);
		toggleButtonList.add(msc.solarDataButton);

		informationFieldList.add(msc.adressField);
		informationFieldList.add(msc.portField);
		informationFieldList.add(msc.kaField);

		alerts = new HashMap<>();
		dtk = new ArrayList<>();
		listenedRooms = new ArrayList<>();

		observable_alerts = FXCollections.observableMap(alerts);
		observable_dtk = FXCollections.observableArrayList(dtk);
		observables_rooms = FXCollections.observableArrayList(listenedRooms);

		observable_alerts.addListener((MapChangeListener<String, Integer>) c ->{
            if (c.wasAdded()){
				int value = c.getMap().get(c.getKey());
				updateContainer(msc.alertContainer, observable_alerts, c.getKey(), value);
			}else if (c.wasRemoved()){
				settingsAccesser.removeDataTreatmentElement("alerts", "co2");
				msc.alertContainer.getChildren().removeIf(n -> n.getId() == c.getKey());
			}
        });

		observable_dtk.addListener((ListChangeListener<String>) c ->{
			while (c.next()){
				if (c.wasAdded()){
					for (String key : c.getAddedSubList()){
						updateContainer(msc.keptValueContainer, observable_dtk, key);
					}
				}
				if (c.wasRemoved()){
					for (String key : c.getRemoved()){
						settingsAccesser.removeDataTreatmentElement("data_to_keep", key);
						msc.keptValueContainer.getChildren().removeIf(n -> n.getId() == key);
					}
				}
			}
        });

		observables_rooms.addListener((ListChangeListener<String>) c ->{
			while (c.next()){
				for (String key : c.getAddedSubList()){
					if (c.wasAdded()){
						updateContainer(msc.listenedRoomContainer, observables_rooms, key);
					}else if (c.wasRemoved()){
						settingsAccesser.removeDataTreatmentElement("listened_rooms", key);
						msc.listenedRoomContainer.getChildren().removeIf(n -> n.getId() == key);
					}
				}
			}
        });

        settingsAccesser = new Settings();

		ChangeListener<Boolean> focusListener = new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (!newValue) {
					settingsAccesser.writeConnectionSettingInFile(informationFieldList);
				}
			}
		};

		msc.adressField.focusedProperty().addListener(focusListener);
		msc.portField.focusedProperty().addListener(focusListener);
		msc.kaField.focusedProperty().addListener(focusListener);
		
	}

	/**
	 * <p>Generate a new pane based on the value and list being updated.
	 * <p>This method follows some strict rules that are defined inside the
	   very conception of the interface. Rules are :
	 * <ul>
	 * <li> <b>Element 0</b> is a Label and contains the <b>name</b> of the attribute
	 * <li> <b>Element 1</b> is a TextField and contains the <b>value</b> of the attribute
	 * <li> <b>Element 2</b> is a Button that allows interaction for <b>removal</b>.
	 * </ul>
	 * @param container : The VBox containing the elements
	 * @param ob : The ObservableList to remove values from
	 * @param key : The name of the attribute
	 * @param value : The value of the attribute
	 * @see #toggleConfirmation()
	 * @author ESTIENNE Alban-Moussa
	 */
	private void updateContainer(VBox container, ObservableMap ob, String key, Integer value){
		Pane clonedPane = PaneCloner.cloneSettingPane(msc.biComponentSettingPane);
		clonedPane.setId(key);
		container.getChildren().add(clonedPane);
		ObservableList<Node> elementList = clonedPane.getChildren();
		Node loadedElement = (Label) elementList.get(0);
		((Label) loadedElement).setText(key);
		loadedElement = (TextField) elementList.get(1);
		((TextField) loadedElement).setText(value.toString());
		loadedElement = (Button) elementList.get(2);
		((Button) loadedElement).setOnAction(event -> toggleConfirmation(event, ob, key));
	}

	/**
	 * <p>Generate a new pane based on the value and list being updated.
	 * <p>This method follows some strict rules that are defined inside the
	   very conception of the interface. Rules are :
	 * <ul>
	 * <li> <b>Element 0</b> is a Label and contains the <b>name</b> of the attribute
	 * <li> <b>Element 1</b> is a Button that allows interaction for <b>removal</b>.
	 * </ul>
	 * @param container : The VBox containing the elements
	 * @param ob : The ObservableList to remove values from
	 * @param key : The name of the attribute
	 * @param value : The value of the attribute
	 * @see #toggleConfirmation()
	 * @author ESTIENNE Alban-Moussa
	 */
	private void updateContainer(VBox container, ObservableList ol, String key){
		Pane clonedPane = PaneCloner.cloneSettingPane(msc.monoComponentSettingPane);
		clonedPane.setId(key);
		container.getChildren().add(clonedPane);
		ObservableList<Node> elementList = clonedPane.getChildren();
		Node loadedElement = (Label) elementList.get(0);
		((Label) loadedElement).setText(key);
		loadedElement = (Button) elementList.get(1);
		((Button) loadedElement).setOnAction(event -> toggleConfirmation(event, ol, key));
	}

    /**
	 * Returns the instance of the SettingsView, creates one if none exists.
	 * 
	 * @param MainSceneController _msc : The Main Scene Controller
	 * @author ESTIENNE Alban-Moussa
	 */
	public static SettingsView getInstance(MainSceneController _msc){
		if (instance == null){
			instance = new SettingsView(_msc);
		}
		return instance;
	}

    /**
	 * Switches the button style.
	 * This is used on the settings menu button so that the user knows
	 * in which section he is. (Inverse the colours)
	 * 
	 * @param Button : The Button to be switched
	 * @author ESTIENNE Alban-Moussa
	 */
	protected void changeButtonStyle(Button button){
		settingButtonList.forEach((n) -> n.getStyleClass().clear());
		settingButtonList.forEach((n) -> n.getStyleClass().add(0, "unselected"));
		button.getStyleClass().set(0, "selected");
	}

	/*
	 * Switches the toggleButton style to match their value
	 * 
	 * @param ToggleButton button : The button to switch
	 * @author ESTIENNE Alban-Moussa
	 * 
	 * Footnote : This one should be more efficient as you will not need
	 * to write a bunch of if/then to make it work, adding a button 
	 * will just result in storing it in the array and plugging this function
	 * into the button (scroll down for example)
	 */
	private void switchButton(ToggleButton button){
		button.getStyleClass().clear();
		String status = button.isSelected() == true ? "on" : "off";
		button.getStyleClass().add(status);
		button.setText(status.toUpperCase());

		settingsAccesser.saveTopicSettings(toggleButtonList);
	}

	/*
	 * Toggles the connection page.
	 * It doesn't look pretty, i'm sorry, but since the interface is 
	 * going to change very little, i just assumed it would be impactless
	 * One better way to do it would be to store all the tabs into a table
	 * and manually set all to visible false except the wanted pane.
	 * if it looks too unpractical in the future i'll change it 
	 * 
	 * @author ESTIENNE Alban-Moussa
	 */
	protected void showConnectionPage(){
		connectionInfoPane.setVisible(true);
		topicsPane.setVisible(false);
		treatmentPane.setVisible(false);

		changeButtonStyle(settingButtonList.get(0));
		HashMap<String, String> fieldDatas = settingsAccesser.loadSetting("Connection Infos");
        msc.adressField.setText(fieldDatas.get("host"));
        msc.portField.setText(fieldDatas.get("port"));
        msc.kaField.setText(fieldDatas.get("keepalive"));
	}
	

	/**
	 * Same as {@link #showConnectionPage()}
	 */
	protected void showTopicPage(){
		connectionInfoPane.setVisible(false);
		topicsPane.setVisible(true);
		treatmentPane.setVisible(false);

		changeButtonStyle(settingButtonList.get(1));
		HashMap<String, String> fieldDatas = settingsAccesser.loadSetting("Topics");

		if (fieldDatas.get("AM107/by-room/#").equals("0")){
			msc.am107Button.getStyleClass().clear();
			msc.am107Button.getStyleClass().add("off");
			msc.am107Button.setText("OFF");
			msc.am107Button.setSelected(false);
		}

		if (fieldDatas.get("Triphaso/by-room/#").equals("0")){
			msc.triphasoButton.getStyleClass().clear();
			msc.triphasoButton.getStyleClass().add("off");
			msc.triphasoButton.setText("OFF");
			msc.triphasoButton.setSelected(false);
		}

		if (fieldDatas.get("solaredge/blagnac/#").equals("0")){
			msc.solarDataButton.getStyleClass().clear();
			msc.solarDataButton.getStyleClass().add("off");
			msc.solarDataButton.setText("OFF");
			msc.solarDataButton.setSelected(false);
		}
        
	}

	protected void showTreatmentPage(){
		connectionInfoPane.setVisible(false);
		topicsPane.setVisible(false);
		treatmentPane.setVisible(true);

		changeButtonStyle(settingButtonList.get(2));
		HashMap<String, String> fieldDatas = settingsAccesser.loadSetting("Data treatment");

		String rawAlerts = settingsAccesser.neutralize(fieldDatas.get("alerts"));
		String rawFrequency = settingsAccesser.neutralize(fieldDatas.get("step"));
		String rawDtk = settingsAccesser.neutralize(fieldDatas.get("data_to_keep"));
		String rawListenedRooms = settingsAccesser.neutralize(fieldDatas.get("listened_rooms"));

		String[] rawAlertsTable = rawAlerts.split(",");
		
		observable_alerts.clear();
		observable_dtk.clear();
		observables_rooms.clear();

		for (String s : rawAlertsTable){
			String seperated[] = s.split(":");
			observable_alerts.put(seperated[0], Integer.valueOf(seperated[1]));
		}

		observable_dtk.addAll(Arrays.asList(rawDtk.split(",")));
		observables_rooms.addAll(Arrays.asList(rawListenedRooms.split(",")));

		

		msc.frequencyField.setText(rawFrequency);
		
	}

	/**
	 * Opens the dialogue to add an element to the Data Treatment .ini file section.
	 * @author ESTIENNE Alban-Moussa
	 */
	private void openAdditionDialogue(ActionEvent e){

	}

	private void toggleConfirmation(ActionEvent e, Observable ol, String key){
		Alert a = new Alert(AlertType.CONFIRMATION);
		a.setContentText("Etes-vous certain de vouloir supprimer l'attribut " + key.toUpperCase() + "?");
		Optional<ButtonType> option = a.showAndWait();

		if (option.get().equals(ButtonType.OK)){
			if (ol instanceof ObservableList){
				((ObservableList) ol).remove(key);
				System.out.println("Removing "+key);
			}else if (ol instanceof ObservableMap){
				((ObservableMap) ol).remove(key);
			}
		}

		e.consume();
	}

	protected void switchAM107(){
		switchButton(msc.am107Button);
	}

	protected void switchTriphaso(){
		switchButton(msc.triphasoButton);
	}

	protected void switchSolar(){
		switchButton(msc.solarDataButton);
	}

	protected void startConnectionTest(){
		String status = settingsAccesser.testConnection();
		String[] splitedStatus = status.split("/");
		msc.connectionStateLabel.setStyle("-fx-text-fill: " + splitedStatus[1] + ";");
		msc.connectionStateLabel.setText(splitedStatus[2]);
	}

}
