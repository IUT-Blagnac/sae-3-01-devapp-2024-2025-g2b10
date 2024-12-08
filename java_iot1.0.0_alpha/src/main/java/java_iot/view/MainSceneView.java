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
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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
	protected Pane graphPane;
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

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		msc = MainSceneController.getInstance();
		msc.setMainSceneView(this);
		settings = SettingsView.getInstance(msc);

		navigationBar = Navbar.getInstance(msc);
		navigationBar.setSettingPane(settingPane);
		navigationBar.setGraphPane(graphPane);
		navigationBar.showGraphPane();

		keptValueContainer.getChildren().clear();
		alertContainer.getChildren().clear();
		listenedRoomContainer.getChildren().clear();

		Rooms.setCellValueFactory(new PropertyValueFactory<>("roomName"));
		CO2.setCellValueFactory(new PropertyValueFactory<>("co2"));
		Humidity.setCellValueFactory(new PropertyValueFactory<>("humidity"));
		Temperature.setCellValueFactory(new PropertyValueFactory<>("temperature"));
		System.out.println("Initialisation du contrôleur et des colonnes du TableView.");
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
		navigationBar.showGraphPane();
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

	public void updateTableView(Data data) {
		ObservableList<Data_sensors> dataList = FXCollections.observableArrayList();

		// Parcourir les données et ajouter chaque capteur à la liste
		for (Map.Entry<String, Room> roomEntry : data.getRooms().entrySet()) {
			String roomName = roomEntry.getKey();
			Room room = roomEntry.getValue();

			// Ajouter les valeurs des capteurs dans Data_sensors
			Double co2 = null, humidity = null, temperature = null;

			for (Sensor sensor : room.getSensors()) {
				if (sensor.getName().equals("co2")) {
					co2 = sensor.getValue();
				} else if (sensor.getName().equals("humidity")) {
					humidity = sensor.getValue();
				} else if (sensor.getName().equals("temperature")) {
					temperature = sensor.getValue();
				}
			}

			// Créer un objet Data_sensors et l'ajouter à la liste
			if (co2 != null || humidity != null || temperature != null) {
				dataList.add(new Data_sensors(roomName, co2, humidity, temperature));
			}
		}

		// Remplir la TableView avec les données
		tableV.setItems(dataList);

		// Vérification des éléments ajoutés à la liste
		System.out.println("Nombre d'éléments ajoutés à la table : " + dataList.size());
	}

	/**
	 * Méthode qui exécute le script Python pour générer les nouvelles données.
	 * 
	 * @param event L'événement déclenché par l'appui sur le bouton.
	 */

	@FXML
	public void InitialisationButton(ActionEvent event) {
		String scriptPyPath = "java_iot1.0.0_alpha\\src\\main\\resources\\java_iot\\ressources\\data_collecting\\main.py";
		String jsonFilesPath = "java_iot1.0.0_alpha\\src\\main\\resources\\java_iot\\ressources\\data_collecting\\data.json";

		boolean scriptExistes = Files.exists(Paths.get(scriptPyPath));
		boolean jsonExistes = Files.exists(Paths.get(jsonFilesPath));

		if (scriptExistes && jsonExistes) {
			dataLoader loader = new dataLoader();
			loader.runPythonScript(scriptPyPath, jsonFilesPath);
			Data data = loader.loadJsonData(jsonFilesPath);
			if (data != null) {
				Platform.runLater(() -> {
					updateTableView(data);
				});
			}
		} else {
			if (!scriptExistes) {
				System.err.println("Script Python introuvable : " + scriptPyPath);
			}
			if (!jsonExistes) {
				System.err.println("Fichier JSON introuvable : " + jsonFilesPath);
			}
		}
	}

}
