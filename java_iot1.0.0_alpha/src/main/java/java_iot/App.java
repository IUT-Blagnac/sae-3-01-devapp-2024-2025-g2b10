package java_iot;
	
import java.io.IOException;

import java_iot.controller.AdditionController;
import java_iot.view.AdditionView;
import java_iot.view.MainSceneView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;


public class App extends Application {
	
	private Pane rootPane;
	private Stage overlayStage;
	private Stage primaryStage;
	
	public App() {
		
	}
	
	@Override
	public void start(Stage primaryStage) {
		
		this.primaryStage = primaryStage;
		this.rootPane     = new Pane();

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

	public void closeOverlay(){
		if (overlayStage != null){
			overlayStage.close();
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

	public void showAdditionMenu(boolean mono){
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

			overlayStage.setScene(scene);
			overlayStage.show();
						
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Ressource FXML non disponible : Addition");
			System.exit(1);
		}	
	}

	public static void main2(String[] args) {
		Application.launch(args);
	}
}
