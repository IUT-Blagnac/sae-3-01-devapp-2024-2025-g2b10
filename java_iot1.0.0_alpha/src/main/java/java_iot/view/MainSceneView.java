package java_iot.view;

import java.net.URL;
import java.util.ResourceBundle;

import java_iot.controller.MainSceneController;
import java_iot.controller.SettingsController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class MainSceneView implements Initializable {

	private Navbar navigationBar;
	private SettingsView settings;
	private MainSceneController msc;
	private SettingsController settingsController;

	@FXML
	public Button settingButton;

	@FXML
	public Button dashboardButton;
	@FXML
	protected Button roomButton;
	@FXML
	protected Button solarButton;
	@FXML
	protected Button initializeButton;
	@FXML
	protected Pane settingPane;
	@FXML
	protected Pane dashPane;
	@FXML
	protected Pane roomPane;
	@FXML
	protected Pane panelPane;
	@FXML
	protected Pane connectionPane;
	@FXML
	protected Pane topicPane;
	@FXML
	protected Pane treatmentPane;

	@FXML
	protected Button connectionButton;
	@FXML
	protected Button topicButton;
	@FXML
	protected Button treatmentButton;
	@FXML
	protected Button connectionTestButton;
	@FXML
	protected Button alertAdditionButton;
	@FXML
	protected Button dtkAdditionButton;
	@FXML
	protected Button roomsAdditionButton;
	@FXML
	protected ToggleButton am107Button;
	@FXML
	protected ToggleButton triphasoButton;
	@FXML
	protected ToggleButton solarDataButton;

	@FXML
	protected TextField adressField;
	@FXML
	protected TextField portField;
	@FXML
	protected TextField kaField;
	@FXML
	protected TextField frequencyField;

	@FXML
	protected VBox alertContainer;
	@FXML
	protected VBox keptValueContainer;
	@FXML
	protected VBox listenedRoomContainer;

	@FXML
	protected ScrollPane alertSp;
	@FXML
	protected ScrollPane keptValueSp;
	@FXML
	protected ScrollPane listenedRoomSp;

	@FXML
	protected Pane biComponentSettingPane;
	@FXML
	protected Pane monoComponentSettingPane;
	@FXML
	protected Label connectionStateLabel;

	@FXML
	protected ComboBox<String> roomComboBox;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		msc = MainSceneController.getInstance();
		msc.setMainSceneView(this);
		settings = SettingsView.getInstance(msc);
		settingsController = SettingsController.getInstance();

		navigationBar = Navbar.getInstance(msc);
		navigationBar.setSettingPane(settingPane);
		navigationBar.setDashPane(dashPane);
		navigationBar.setRoomPane(roomPane);
		navigationBar.setPanelPane(panelPane);
		navigationBar.showDashPane();

		keptValueContainer.getChildren().clear();
		alertContainer.getChildren().clear();
		listenedRoomContainer.getChildren().clear();

		String[][] roomData = settingsController.requestAllAvailableFields("listened_rooms");
        String[] rooms = roomData[0];  // Assuming the first array contains room names
		roomComboBox.getItems().addAll(rooms);
		roomComboBox.setOnAction(event -> handleRoomSelection());

	}

    private void handleRoomSelection() {
        String selectedRoom = roomComboBox.getValue();
        if (selectedRoom != null && !selectedRoom.isEmpty()) {
            Graphique.getInstance(msc).showRoom(selectedRoom); // Pass the selected room
        }
    }

	public MainSceneController getController() {
		return msc;
	}

	/*
	 * FXML FUNCTIONS
	 */
	@FXML
	public void toggleSettings() {
		navigationBar.showSettingPane();
	}

	@FXML
	public void testConnection() {
		settings.startConnectionTest();
	}

	@FXML
	public void toggleGraph() {
		navigationBar.showDashPane();
	}

	@FXML
	public void showRoom() {
		navigationBar.showRoomPane();
	}

	@FXML
	public void showPanel() {
		navigationBar.showPanelPane();
	}

	@FXML
	public void connectionButton() {
		settings.showConnectionPage();
	}

	@FXML
	public void topicButton() {
		settings.showTopicPage();
	}

	@FXML
	public void treatmentButton() {
		settings.showTreatmentPage();
	}

	@FXML
	public void switchAM107() {
		settings.switchAM107();
	}

	@FXML
	public void switchTriphaso() {
		settings.switchTriphaso();
	}

	@FXML
	public void switchSolar() {
		settings.switchSolar();
	}

	@FXML
	public void addValueBi(ActionEvent event) {
		// This line is just a reminder that false is bicomponent
		Button sourceButton = (Button) event.getSource();

		boolean mono = false;
		settings.openAdditionDialogue(mono, sourceButton.getId());
	}

	@FXML
	public void addValueMono(ActionEvent event) {
		// And here it is monocomponent
		Button sourceButton = (Button) event.getSource();

		boolean mono = true;
		settings.openAdditionDialogue(mono, sourceButton.getId());
	}

}
