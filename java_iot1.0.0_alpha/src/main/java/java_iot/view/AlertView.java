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
    private VBox alertList; // Container to show the alerts dynamically

    /**
     * Sets the app that called this window in other to request a closure.
     */
    public void setApp(App _app) {
        app = _app;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ac = AlertController.getInstance();
        ac.setAlertView(this);
    }

    /**
     * Updates the list of alerts in the container
     *
     * @param alertingSensors Map of the sensors in alert
     */
    public void updateAlerts() {
        if (alertList == null) {
            System.err.println("alertList n'est pas initialisé !");
            return;
        }

        Map<String, Sensor> alertingSensors = ac.getAlertingSensors();

        alertList.getChildren().clear();

        if (alertingSensors.isEmpty()) {
            // If there is no alert
            alertList.getChildren().add(new Label("Aucune alerte détectée."));
        } else {
            for (Map.Entry<String, Sensor> entry : alertingSensors.entrySet()) {
                Sensor sensor = entry.getValue();
                String roomName = entry.getKey();
                Double alertValue = sensor.getValue();

                String alertText = "Salle : " + roomName + "\nDonnée : " + sensor.getName() + "\nValeur : "
                        + alertValue;

                Image alertImage = new Image(App.class.getResourceAsStream("ressources/images/warningicon.png"));
                ImageView alertImageView = new ImageView(alertImage);
                alertImageView.setFitHeight(40);
                alertImageView.setFitWidth(40);

                // HBox for the image and message of alert
                HBox hbox = new HBox(20);
                hbox.setAlignment(Pos.CENTER_LEFT);

                // Adding the text to the label
                Label alertLabel = new Label(alertText);
                alertLabel.setStyle("-fx-text-fill: #e32222; -fx-font-weight: bold; -fx-font-size: 14px;");

                // Adding the image and label to the HBox
                hbox.getChildren().addAll(alertImageView, alertLabel);

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
                                + "-fx-padding: 20;");

                // Add the HBox as a content of alertButton
                alertButton.setGraphic(hbox);

                // Add the alertButton to the alertList
                alertList.getChildren().add(alertButton);
            }
        }
    }

}