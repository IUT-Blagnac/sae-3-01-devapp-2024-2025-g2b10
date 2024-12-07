package java_iot.view;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import java_iot.App;
import java_iot.classes.Sensor;
import java_iot.controller.AlertController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AlertView implements Initializable {

    private App app;
    private AlertController ac;

    @FXML
    private VBox alertContainer; // Conteneur pour afficher les alertes dynamiquement
    @FXML
    private VBox alertList; // Conteneur pour afficher les alertes dynamiquement

    /**
     * Sets the app that called this window in other to request a closure.
     */
    public void setApp(App _app) {
        app = _app;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ac = AlertController.getInstance();
    }

    /**
     * Met à jour la liste des alertes dans le conteneur.
     *
     * @param alertingSensors Map des capteurs en alerte.
     */
    public void updateAlerts(Map<String, Sensor> alertingSensors) {
        if (alertList == null) {
            System.err.println("alertList n'est pas initialisé !");
            return;
        }

        alertList.getChildren().clear();

        if (alertingSensors.isEmpty()) {
            alertList.getChildren().add(new Label("Aucune alerte détectée."));
        } else {
            for (Map.Entry<String, Sensor> entry : alertingSensors.entrySet()) {
                Sensor sensor = entry.getValue();
                String roomName = entry.getKey();
                Double alertValue = sensor.getValue();

                String alertText = "Salle : " + roomName + "\nDonnée : " + sensor.getName() + "\nValeur : " + alertValue;

                // Charger l'image d'alerte
                Image alertImage = new Image("java_iot\\ressources\\images\\warningicon.png"); // Remplacez par le chemin réel
                ImageView alertImageView = new ImageView(alertImage);
                alertImageView.setFitHeight(40);  // Taille de l'icône
                alertImageView.setFitWidth(40);

                // Créer un HBox pour combiner l'image et le texte
                HBox hbox = new HBox(20); // Espacement entre l'image et le texte
                hbox.setAlignment(Pos.CENTER_LEFT); // Centrer le contenu verticalement

                // Créer le Label et lui appliquer un style rouge
                Label alertLabel = new Label(alertText);
                alertLabel.setStyle("-fx-text-fill: #e32222; -fx-font-weight: bold; -fx-font-size: 14px;");

                // Ajouter l'image et le texte dans le HBox
                hbox.getChildren().addAll(alertImageView, alertLabel);

                // Bouton avec HBox
                Button alertButton = new Button();
                alertButton.setPrefWidth(400.0);
                alertButton.setPrefHeight(100.0);
                alertButton.setWrapText(true);
                alertButton.setAlignment(Pos.CENTER);
                alertButton.setStyle(
                        "-fx-background-color: #f8d7da; "
                        + "-fx-border-color: #f5c6cb; "
                        + "-fx-border-radius: 8; "
                        + "-fx-background-radius: 8; "
                        + "-fx-font-size: 14px; "
                        + "-fx-padding: 20;"
                );

                // Ajouter le HBox comme contenu du bouton
                alertButton.setGraphic(hbox);

                // Ajouter le bouton au conteneur des alertes
                alertList.getChildren().add(alertButton);
            }
        }
    }

}
