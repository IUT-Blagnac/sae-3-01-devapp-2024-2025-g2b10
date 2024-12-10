package java_iot.view;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

import java_iot.classes.Data_sensors;
import java_iot.controller.AlertController;
import java_iot.controller.DataController;
import java_iot.controller.MainSceneController;
import java_iot.controller.SettingsController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class MainSceneView implements Initializable {

	private Navbar navigationBar;
	private SettingsView settings;
	private MainSceneController msc;
	private SettingsController settingsController;
	private AlertController ac;

	@FXML
	public Button settingButton;
	@FXML
	protected ComboBox<String> roomComboBox;
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
	private TableView<Data_sensors> tableV;

	@FXML
	private TableColumn<Data_sensors, String> Rooms;

	@FXML
	private TableColumn<Data_sensors, Double> CO2;

	@FXML
	private TableColumn<Data_sensors, Double> Humidity;

	@FXML
	private TableColumn<Data_sensors, Double> Temperature;

	@FXML
	private TableColumn<Data_sensors, String> time;

	@FXML
	protected Label panelDay;

	@FXML
	protected Label panelMonth;

	@FXML
	protected Label panelYear;

	@FXML
	protected Pane graphDisplayPane;

	@FXML
	protected Pane tempPane;
	
	@FXML
	protected Pane humPane;

	@FXML
	protected Pane co2Pane;

	private DataController dataController;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		msc = MainSceneController.getInstance();
		msc.setMainSceneView(this);

		settings = SettingsView.getInstance(msc);
		settingsController = SettingsController.getInstance();
		ac = AlertController.getInstance();

		navigationBar = Navbar.getInstance(msc);
		navigationBar.setSettingPane(settingPane);
		navigationBar.setDashPane(dashPane);
		navigationBar.setRoomPane(roomPane);
		navigationBar.setPanelPane(panelPane);
		navigationBar.showDashPane();

		keptValueContainer.getChildren().clear();
		alertContainer.getChildren().clear();
		listenedRoomContainer.getChildren().clear();

		dataController = new DataController(tableV);

		Rooms.setCellValueFactory(new PropertyValueFactory<>("roomName"));
		CO2.setCellValueFactory(new PropertyValueFactory<>("co2"));
		Humidity.setCellValueFactory(new PropertyValueFactory<>("humidity"));
		Temperature.setCellValueFactory(new PropertyValueFactory<>("temperature"));
		time.setCellValueFactory(new PropertyValueFactory<>("time"));
		System.out.println("Initialisation du contrôleur et des colonnes du TableView.");

		String[][] roomData = settingsController.requestAllAvailableFields("listened_rooms");
		String[] rooms = roomData[0]; // Assuming the first array contains room names
		roomComboBox.getItems().addAll(rooms);
		roomComboBox.setOnAction(event -> {
			try {
				handleRoomSelection();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		Rooms.setCellValueFactory(new PropertyValueFactory<>("roomName"));
		CO2.setCellValueFactory(new PropertyValueFactory<>("co2"));
		Humidity.setCellValueFactory(new PropertyValueFactory<>("humidity"));
		Temperature.setCellValueFactory(new PropertyValueFactory<>("temperature"));
		time.setCellValueFactory(new PropertyValueFactory<>("time"));
		System.out.println("Initialisation du contrôleur et des colonnes du TableView.");

	}

	public MainSceneController getController() {
		return msc;
	}

	private void handleRoomSelection() throws URISyntaxException {
		String selectedRoom = roomComboBox.getValue();
		if (selectedRoom != null && !selectedRoom.isEmpty()) {
			Graphique.getInstance(msc).showRoom(selectedRoom); // Pass the selected room
		}
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
	public void showPanel() throws URISyntaxException {
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

	/**
	 * Gère l'événement de clic sur le bouton d'initialisation.
	 * Vérifie l'existence du script Python et du fichier JSON, exécute le script
	 * Python,
	 * charge les données JSON générées, puis met à jour la TableView.
	 *
	 * @param event L'événement déclenché par le clic sur le bouton.
	 */
	@FXML
	public void InitialisationButton(ActionEvent event) {
		try {

			String scriptPyPath = "data_collecting/main.py";
			String jsonFilePath = "data_collecting/data.json";


			URL scriptURL = new File(scriptPyPath).toURI().toURL();
			URL jsonURL = new File(jsonFilePath).toURI().toURL();

			dataController.runPythonScriptAndData(scriptURL, jsonURL);
		} catch (Exception e) {
			System.err.println("Erreur lors de l'initialisation : " + e.getMessage());
			e.printStackTrace();
		}
	}

}
