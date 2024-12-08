package java_iot.view;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import java_iot.classes.Data;
import java_iot.classes.Data_sensors;
import java_iot.classes.Room;
import java_iot.classes.Sensor;
import java_iot.classes.dataLoader;
import java_iot.controller.MainSceneController;
import java_iot.controller.SettingsController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
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

		Rooms.setCellValueFactory(new PropertyValueFactory<>("roomName"));
		CO2.setCellValueFactory(new PropertyValueFactory<>("co2"));
		Humidity.setCellValueFactory(new PropertyValueFactory<>("humidity"));
		Temperature.setCellValueFactory(new PropertyValueFactory<>("temperature"));
		time.setCellValueFactory(new PropertyValueFactory<>("time"));
		System.out.println("Initialisation du contrôleur et des colonnes du TableView.");

		
		String[][] roomData = settingsController.requestAllAvailableFields("listened_rooms");
        String[] rooms = roomData[0];  // Assuming the first array contains room names
		roomComboBox.getItems().addAll(rooms);
		roomComboBox.setOnAction(event -> handleRoomSelection());

		Rooms.setCellValueFactory(new PropertyValueFactory<>("roomName"));
		CO2.setCellValueFactory(new PropertyValueFactory<>("co2"));
		Humidity.setCellValueFactory(new PropertyValueFactory<>("humidity"));
		Temperature.setCellValueFactory(new PropertyValueFactory<>("temperature"));
		System.out.println("Initialisation du contrôleur et des colonnes du TableView.");

	}

	public MainSceneController getController() {
		return msc;
	}

	private void handleRoomSelection() {
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

	/**
	 * Methode qui met à jour la TableView avec les données extraites d'un objet
	 * Data.
	 * Cette méthode parcourt les salles et leurs capteurs et extrait les valeurs
	 * (CO2, humidité, ...)
	 * 
	 * 
	 * @param data contenant les informations sur les salles et leurs capteurs.
	 */

	public void updateTableView(Data data) {
		ObservableList<Data_sensors> dataList = FXCollections.observableArrayList();

		// Parcourir les données et ajouter chaque capteur à la liste
		for (Map.Entry<String, Room> roomEntry : data.getRooms().entrySet()) {
			String roomName = roomEntry.getKey();
			Room room = roomEntry.getValue();

			// Ajouter les valeurs des capteurs dans Data_sensors
			Double co2 = null, humidity = null, temperature = null;
			String time = null;

			for (Sensor sensor : room.getSensors()) {
				if (sensor.getName().equals("co2")) {
					co2 = sensor.getValue();

				} else if (sensor.getName().equals("humidity")) {
					humidity = sensor.getValue();

				} else if (sensor.getName().equals("temperature")) {
					temperature = sensor.getValue();
					time = sensor.getTime();
				}
			}

			// Créer un objet Data_sensors et l'ajouter à la liste
			if (co2 != null || humidity != null || temperature != null || time != null) {
				dataList.add(new Data_sensors(roomName, co2, humidity, temperature, time));

			}
		}

		// Remplir la TableView avec les données
		tableV.setItems(dataList);
		System.out.println("Mise à jour de la TableView avec " + dataList.size() + " entrées.");
	}

	/**
	 * Méthode qui exécute le script Python pour générer les nouvelles données.
	 * 
	 * @param event L'événement déclenché par l'appui sur le bouton.
	 */

	@FXML
	public void InitialisationButton(ActionEvent event) {

		// Chemin du script Python et du fichier JSON
		String scriptPyPath = "java_iot1.0.0_alpha\\src\\main\\resources\\java_iot\\ressources\\data_collecting\\main.py";
		String jsonFilesPath = "java_iot1.0.0_alpha\\src\\main\\resources\\java_iot\\ressources\\data_collecting\\data.json";

		// Vérifie si les fichiers existent
		boolean scriptExistes = Files.exists(Paths.get(scriptPyPath));
		boolean jsonExistes = Files.exists(Paths.get(jsonFilesPath));

		if (scriptExistes && jsonExistes) {
			// Exécution du script Python et chargement des données JSON
			dataLoader loader = new dataLoader();
			loader.runPythonScript(scriptPyPath, jsonFilesPath);
			Data data = loader.loadJsonData(jsonFilesPath);
			if (data != null) {
				Platform.runLater(() -> {
					// Mise à jour de la TableView avec les données chargées
					updateTableView(data);
				});
			}
		} else {
			// Affiche les erreurs si les fichiers sont introuvables
			if (!scriptExistes) {
				System.err.println("Script Python introuvable : " + scriptPyPath);
			}
			if (!jsonExistes) {
				System.err.println("Fichier JSON introuvable : " + jsonFilesPath);
			}
		}
	}

}
