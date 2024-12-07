package java_iot.view;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import java_iot.App;
import java_iot.classes.Sensor;
import java_iot.controller.AlertController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class AlertView implements Initializable {

    private App app;
    private AlertController ac;

    @FXML
    private VBox alertContainer; // Conteneur pour afficher les alertes dynamiquement

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
        if (alertContainer == null) {
            System.err.println("alertContainer n'est pas initialisé !");
            return;
        }

        alertContainer.getChildren().clear(); // Nettoie l'affichage existant

        if (alertingSensors.isEmpty()) {
            alertContainer.getChildren().add(new Label("Aucune alerte détectée."));
        } else {
            for (Map.Entry<String, Sensor> entry : alertingSensors.entrySet()) {
                Sensor sensor = entry.getValue();  // Récupère le sensor de l'alerte
                String roomName = entry.getKey(); // Récupère le nom de la salle (clé de l'entrée)

                Double alertValue = sensor.getValue();  // Exemple de méthode pour obtenir la valeur mise en alerte

                // Crée le texte de l'alerte à afficher
                String alertText = "Salle: " + roomName + " - Capteur: " + sensor.getName() + " - Valeur: " + alertValue;

                // Crée un label avec ce texte et ajoute des styles
                Label alertLabel = new Label(alertText);
                alertLabel.setStyle("-fx-padding: 5; -fx-border-color: black; -fx-background-color: #FFCCCC;");
                alertContainer.getChildren().add(alertLabel); // Ajoute chaque alerte comme un label
            }
        }
    }

}
