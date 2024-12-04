package java_iot.view;

import java.net.URL;
import java.util.ResourceBundle;

import java_iot.App;
import java_iot.controller.AlertController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
     * Ajoute une alerte à partir d'une pièce.
     *
     * @param room La pièce contenant les données d'alerte.
     *
     * public void addAlert(Data data) { if (data.isAlertRaised()) { Pane
     * alertPane = new Pane(); alertPane.setStyle("-fx-background-color:
     * #FFCCCC; -fx-padding: 10; -fx-border-color: black;");

     *      *Label label = new Label("Température anormale enregistrée dans la salle "
     * + room.getName()); alertPane.getChildren().add(label);

     *      *containerBox.getChildren().add(alertPane); // Ajouter l'alerte au
     * conteneur } }
     */
    /**
     * Charge toutes les alertes dynamiquement.
     *
     * @param rooms Liste des pièces à analyser.
     *
     * public void loadAlerts(List<Alert> alerts) {
     * containerBox.getChildren().clear(); // Réinitialiser la liste des alertes
     * for (Alert alerts : alert) { addAlert(alert); // Ajouter chaque alerte
     * active } }
     */
}
