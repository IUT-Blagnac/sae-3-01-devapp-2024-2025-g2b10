package java_iot.view;

import java.util.Map;

import java_iot.classes.Data;
import java_iot.classes.Data_sensors;
import java_iot.classes.Room;
import java_iot.classes.Sensor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class TableV {

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
    public void initialize() {
        // Initialisation des colonnes
        Rooms.setCellValueFactory(new PropertyValueFactory<Data_sensors, String>("roomName"));
        CO2.setCellValueFactory(new PropertyValueFactory<Data_sensors, Double>("co2"));
        Humidity.setCellValueFactory(new PropertyValueFactory<Data_sensors, Double>("humidity"));
        Temperature.setCellValueFactory(new PropertyValueFactory<Data_sensors, Double>("temperature"));
    }

    private Double getSensorValue(Room room, String sensorType) {
        for (Sensor sensor : room.getSensors()) {
            if (sensor.getName().equals(sensorType)) {
                return sensor.getValue(); // Retourne la valeur du capteur si trouvé
            }
        }
        return null; // Retourne null si aucun capteur correspondant n'est trouvé
    }

    public void updateTableView(Data data) {
        ObservableList<Data_sensors> values = FXCollections.observableArrayList();

        for (Map.Entry<String, Room> entry : data.getRooms().entrySet()) {
            String roomName = entry.getKey();
            Room room = entry.getValue();

            // Récupérer les valeurs de capteurs pour chaque pièce
            Double co2 = getSensorValue(room, "co2");
            Double humidity = getSensorValue(room, "humidity");
            Double temperature = getSensorValue(room, "temperature");

            // Ajouter une ligne à la table
            values.add(new Data_sensors(roomName, co2, humidity, temperature));
        }

        // Mettre à jour le TableView avec les nouvelles données
        tableV.setItems(values);
    }
}
