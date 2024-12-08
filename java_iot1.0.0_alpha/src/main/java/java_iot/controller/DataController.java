package java_iot.controller;

import java.net.URL;
import java.util.Map;

import java_iot.classes.Data;
import java_iot.classes.Data_sensors;
import java_iot.classes.Room;
import java_iot.classes.Sensor;
import java_iot.classes.dataLoader;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

public class DataController {

    private TableView<Data_sensors> tableV;

    // Constructeur
    public DataController(TableView<Data_sensors> tableView) {
        this.tableV = tableView;
    }

    /**
     * Met à jour la TableView avec les données extraites d'un objet Data.
     * 
     * @param data Les informations sur les salles et leurs capteurs.
     */
    public void updateTableView(Data data) {
        ObservableList<Data_sensors> dataList = FXCollections.observableArrayList();

        for (Map.Entry<String, Room> roomEntry : data.getRooms().entrySet()) {
            String roomName = roomEntry.getKey();
            Room room = roomEntry.getValue();
            // Initialisation des variables pour stocker les valeurs des capteurs
            Double co2 = null, humidity = null, temperature = null;
            String time = null;

            // Parcours les capteurs de la salle
            for (Sensor sensor : room.getSensors()) {
                // Si le capteur est un capteur de CO2, on récupère sa valeur
                if (sensor.getName().equals("co2")) {
                    co2 = sensor.getValue();
                    // Si le capteur est un capteur d' humidité, on récupère sa valeur
                } else if (sensor.getName().equals("humidity")) {
                    humidity = sensor.getValue();
                    // Si le capteur est un capteur de temperature, on récupère sa valeur
                } else if (sensor.getName().equals("temperature")) {
                    temperature = sensor.getValue();
                    time = sensor.getTime();
                }
            }

            // Si au moins une des valeurs est présente,on ajoute une nouvelle entrée dans
            // la liste des données à afficher dans la TableView.
            if (co2 != null || humidity != null || temperature != null || time != null) {
                dataList.add(new Data_sensors(roomName, co2, humidity, temperature, time));
            }
        }
        // Remplir la TableView avec les données collectées
        tableV.setItems(dataList);

    }

    /**
     * Exécute le script Python pour générer les nouvelles données et charge les
     * données JSON.
     * 
     * @param scriptPyURL Le chemin du script Python.
     * @param jsonFileURL Le chemin du fichier JSON.
     */
    public void runPythonScriptAndData(URL scriptPyURL, URL jsonFileURL) {
        if (scriptPyURL == null || jsonFileURL == null) {
            System.err.println("Erreur : Une des ressources n'est pas disponible.");
            return;
        } else {
            dataLoader loader = new dataLoader();
            loader.runPythonScript(scriptPyURL, jsonFileURL);
            Data data = loader.loadJsonData(jsonFileURL);
            if (data != null) {
                Platform.runLater(() -> {
                    updateTableView(data);
                });
            }
        }
    }

}
