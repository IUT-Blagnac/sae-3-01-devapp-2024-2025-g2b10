package java_iot;

import java.io.IOException;
import java.util.Map;

import java_iot.model.Data;
import java_iot.model.Room;
import java_iot.model.dataLoader;
import java_iot.view.MainSceneController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

public class App extends Application {

	private Pane rootPane;
	private Stage primaryStage;

	public App() {

	}

	@Override
	public void start(Stage primaryStage) {

		this.primaryStage = primaryStage;
		this.rootPane = new Pane();

		primaryStage.setMinWidth(1120);
		primaryStage.setMinHeight(630);
		primaryStage.setResizable(false);

		// scene.getStylesheets().add(lireeApp.class.getResource("style.css").toExternalForm());
		Data data = loadData();
		if (data == null) {
			System.out.println("Échec du chargement des données.");
			System.exit(1);
		} else {
			System.out.println("Données chargées avec succès : " + data.getRooms().size() + " salles trouvées.");
		}
		loadMainScreen();

		Scene scene = new Scene(rootPane);
		scene.getStylesheets().add(getClass().getResource("ressources/css/button_styles.css").toExternalForm());
		primaryStage.setTitle("Java IoT");
		primaryStage.setScene(scene);

		primaryStage.show();
		loadData();

	}

	public void loadMainScreen() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(App.class.getResource("view/MainScreen.fxml"));

			Pane vueListe = loader.load();
			this.rootPane = vueListe;

			MainSceneController ctrl = loader.getController();

		} catch (IOException e) {
			System.out.println("Ressource FXML non disponible : MainScreen");
			System.exit(1);
		}
	}

	private Data loadData() {

		dataLoader dataLoader = new dataLoader();
		Data data = dataLoader.loadJsonData("resources/java_iot/ressources/data_collecting/data.json");

		if (data != null) {
			System.out.println("Données Globales : " + data.getglobal());
			System.out.println("Salles : ");
			for (Map.Entry<String, Room> entry : data.getRooms().entrySet()) {
				System.out.println(entry.getKey() + ": " + entry.getValue());
			}
		} else {
			System.out.println("Erreur lors du chargement des données.");
		}
		return data;

	}

	public static void main2(String[] args) {
		Application.launch(args);
	}
}
