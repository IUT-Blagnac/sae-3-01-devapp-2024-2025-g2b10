package java_iot;

import java.io.IOException;
import java.util.Map;

import java_iot.classes.Data;
import java_iot.classes.Room;
import java_iot.classes.dataLoader;
import java_iot.view.AdditionView;
import java_iot.view.AlertView;
import java_iot.view.MainSceneView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class App extends Application {

    private Pane rootPane;
    private Stage overlayStage;
    private Stage primaryStage;
    private Stage alertStage;

    public App() {

    }

    @Override
    public void start(Stage primaryStage) {

        this.primaryStage = primaryStage;
        this.rootPane = new Pane();

        primaryStage.setMinWidth(1120);
        primaryStage.setMinHeight(630);
        primaryStage.setResizable(false);

        //scene.getStylesheets().add(lireeApp.class.getResource("style.css").toExternalForm());
        loadMainScreen();

        Scene scene = new Scene(rootPane);
        scene.getStylesheets().add(getClass().getResource("ressources/css/button_styles.css").toExternalForm());

        primaryStage.setTitle("Java IoT");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public void closeOverlay() {
        if (overlayStage != null) {
            overlayStage.close();
        }
    }

    public void closeAlert() {
        if (alertStage != null) {
            alertStage.close();
        }
    }

    public void loadMainScreen() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("view/MainScreen.fxml"));

            Pane vueListe = loader.load();
            this.rootPane = vueListe;

            MainSceneView ctrl = loader.getController();
            ctrl.getController().setApp(this);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ressource FXML non disponible : MainScreen");
            System.exit(1);
        }
    }

    public void showAdditionMenu(boolean mono, String callerButton) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("view/Addition.fxml"));

            overlayStage = new Stage();
            overlayStage.initModality(Modality.WINDOW_MODAL);
            overlayStage.initOwner(this.primaryStage);

            Pane vueListe = loader.load();
            this.rootPane = vueListe;

            Scene scene = new Scene(vueListe);

            AdditionView ctrl = loader.getController();
            ctrl.setApp(this);
            ctrl.setCallerId(callerButton);
            ctrl.setMonoDialogue(mono);
            ctrl.start();

            overlayStage.setScene(scene);
            overlayStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ressource FXML non disponible : Addition");
            System.exit(1);
        }
    }

    public void showAlertPopup(boolean mono) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("view/AlertPopup.fxml"));

            alertStage = new Stage();
            alertStage.initModality(Modality.WINDOW_MODAL);
            alertStage.initOwner(this.primaryStage);

            Pane vueListe = loader.load();
            this.rootPane = vueListe;

            Scene scene = new Scene(vueListe);

            AlertView ctrl = loader.getController();
            ctrl.setApp(this);

            alertStage.setScene(scene);
            alertStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ressource FXML non disponible : AlertPopup");
            System.exit(1);
        }
    }

    public static void main2(String[] args) {
        Application.launch(args);
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
}
