package java_iot.view;

import java.net.URL;
import java.util.ResourceBundle;

import java_iot.App;
import java_iot.controller.AlertController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class AlertView implements Initializable {

    @FXML
    protected VBox containerBox;
    @FXML
    protected Pane alertPane;

    private App app;
    private AlertController ac;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ac = AlertController.getInstance();
    }

    public void setApp(App _app) {
        app = _app;
    }

    public void start() {

    }

    /**
     * Met à jour la vue en fonction de la liste d'alertes actuelle.
     */
    public void updateView() {
        containerBox.getChildren().clear(); // Réinitialise la vue

        // Pour chaque alerte, crée un Pane et l'ajoute à la vue
        for (Alert alert : ac.getAlerts()) {
            alertPane = createAlertPane(alert);
            containerBox.getChildren().add(alertPane);
        }
    }

    /**
     * Crée dynamiquement un Pane pour afficher une alerte.
     *
     * @param alert L'alerte à afficher.
     * @return Un Pane configuré avec les informations de l'alerte.
     */
    private Pane createAlertPane(Alert alert) {
        Pane pane = new Pane();
        pane.setStyle("-fx-background-color: #FFCCCC; -fx-padding: 10; -fx-border-color: black;");

        Label label = new Label(alert.toString());
        label.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        pane.getChildren().add(label);

        return pane;
    }
}
