package java_iot.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java_iot.Main;
import java_iot.model.Settings;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;

public class SettingsView {
    
	private MainSceneController msc;
	private static SettingsView instance;
    private Settings settingsAccesser;
	private Pane connectionInfoPane;
	private Pane topicsPane;
	private Pane treatmentPane;

	private List<Button> settingButtonList;
	private List<ToggleButton> toggleButtonList;

	private SettingsView(MainSceneController _msc){
		settingButtonList = new ArrayList<>();
		toggleButtonList = new ArrayList<>();
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

        settingsAccesser = new Settings();
		
	}

    
	public static SettingsView getInstance(MainSceneController _msc){
		if (instance == null){
			instance = new SettingsView(_msc);
		}
		return instance;
	}

    
	protected void changeButtonStyle(Button button){
		settingButtonList.forEach((n) -> n.getStyleClass().clear());
		settingButtonList.forEach((n) -> n.getStyleClass().add(0, "unselected"));
		button.getStyleClass().set(0, "selected");
	}

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

	private void switchButton(ToggleButton button){
		button.getStyleClass().clear();
		String status = button.isSelected() == true ? "on" : "off";
		button.getStyleClass().add(status);
		button.setText(status.toUpperCase());

		settingsAccesser.saveTopicSettings(toggleButtonList);
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



	protected void showTreatmentPage(){
		connectionInfoPane.setVisible(false);
		topicsPane.setVisible(false);
		treatmentPane.setVisible(true);

		changeButtonStyle(settingButtonList.get(2));
	}

}
