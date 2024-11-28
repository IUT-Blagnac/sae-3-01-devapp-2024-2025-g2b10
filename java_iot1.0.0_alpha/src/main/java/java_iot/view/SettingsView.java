package java_iot.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java_iot.Main;
import java_iot.model.PaneCloner;
import java_iot.model.Settings;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;

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
		Map<String, Integer> alerts = new HashMap<>();

		for (String s : rawAlertsTable){
			String seperated[] = s.split(":");
			alerts.put(seperated[0], Integer.valueOf(seperated[1]));
		}

		String[] dtk = rawDtk.split(",");
		String[] listenedRooms = rawListenedRooms.split(",");

		msc.frequencyField.setText(rawFrequency);
		
		/* Documentation on the pane elements.
		* 0 is a label. Contains the name of the alert data name
		* 1 is a textfield. Contains the value of the section
		* 2 is a button. The remove button
		*/ 
		for (String key : alerts.keySet()){
			msc.alertContainer.getChildren().clear();

			Pane clonedPane = PaneCloner.cloneSettingPane(msc.biComponentSettingPane);
			msc.alertContainer.getChildren().add(clonedPane);
			ObservableList<Node> elementList = clonedPane.getChildren();
			Node loadedElement = (Label) elementList.get(0);
			((Label) loadedElement).setText(key);
			loadedElement = (TextField) elementList.get(1);
			((TextField) loadedElement).setText(alerts.get(key).toString());
			loadedElement = (Button) elementList.get(2);
			// ((Button) loadedElement); do this later, plug it with the delete func (thaaaat gotta be written)
			// hehe oopsie :,)

			
		}
		

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
