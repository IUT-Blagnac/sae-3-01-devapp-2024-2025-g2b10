package java_iot.classes;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javafx.application.Platform;
import javafx.scene.chart.LineChart;


import javafx.application.Platform;
import javafx.scene.chart.LineChart;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class dataLoader {

    public dataLoader() {
    }

    /**
     * Fonction qui charge les données depuis un fichier JSON
     * 
     * @param jsonFilePath Le chemin du fichier JSON à charger
     * @return Data contenant les données chargées, ou null en cas d'erreur
     * @throws IOException Si le fichier JSON n'est pas trouvé
     * @author PAPA-PATSOUMOUDOU Matthias
     */

    public Data loadJsonData(String jsonFilePath) {
        try {

            // Chargement du fichier JSON depuis les ressources.
            InputStream inputStream = getClass().getResourceAsStream("/java_iot/ressources/data_collecting/data.json");
            if (inputStream == null) {
                System.out.println("Fichier JSON introuvable.");
            } else {
                System.out.println("Fichier JSON trouvé !");
            }

            // Conversion du JSON en Map via Gson
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            Map<String, Object> data = gson.fromJson(new InputStreamReader(inputStream), type);
            System.out.println("Contenu du fichier JSON : " + data);
            // Extraction des données globales
            Global global = null;
            if (data.containsKey("Global")) {
                global = gson.fromJson(gson.toJson(data.get("Global")), Global.class);
            }

            Map<String, Room> rooms = new HashMap<>();
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                if (!entry.getKey().equals("Global")) {
                    String nom = entry.getKey(); // Nom de la pièce
                    List<Sensor> sensors = extractSensors(entry.getValue()); // Extraction des capteurs
                    if (!sensors.isEmpty()) {
                        Room room = new Room(nom, sensors);
                        rooms.put(nom, room);
                    }
                }
            }
            // Retourne un Data contenant les données globales et les salles
            return new Data(global, rooms);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Retourne null si une erreur s'est produite.
    }

    /**
     * Extrait les capteurs d'un objet donné
     * 
     * @param value L'objet contenant les informations des capteurs
     * @return Une liste de capteurs extraits
     */
    public List<Sensor> extractSensors(Object value) {
        List<Sensor> sensors = new ArrayList<>();
        try {
            // Si l'objet est un Map, on traite les données salle par salle.
            if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> roomData = (Map<String, Object>) value;
                for (Map.Entry<String, Object> Group_Sensor : roomData.entrySet()) {
                    if (Group_Sensor.getValue() instanceof Map) {
                        Map<String, Object> Data_sensor = (Map<String, Object>) Group_Sensor.getValue();
                        for (Map.Entry<String, Object> Sensors : Data_sensor.entrySet()) {
                            String key = Sensors.getKey(); // Type de capteur
                            Object val = Sensors.getValue();

                            if (val instanceof List) {
                                List<Object> Details_sensor = (List<Object>) val;
                                // Extraction des détails du capteur : valeur, statut, et temps
                                if (Details_sensor.size() == 2) {
                                    Double values = (Double) Details_sensor.get(0); // Valeur réelle du capteur
                                    Boolean status = (Boolean) Details_sensor.get(1); // Statut du capteur (vrai ou
                                                                                      // faux)
                                    String time = (String) Data_sensor.get("time"); // Temps du capteur

                                    // Création de l'objet Sensor avec ces informations
                                    Sensor sensor = new Sensor(key, values, status, time);
                                    sensors.add(sensor);

                                    // Affichage du capteur extrait
                                    System.out.println("Capteur extrait : " + sensor);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'extraction des capteurs : " + e.getMessage());
        }
        return sensors;
    }

    /**
     * * Méthode qui exécute un script Python
     *
     * @param scriptPath   Le chemin du script Python à exécuter.
     * @param jsonFilePath Le chemin du fichier JSON à utiliser par le script Python
     * 
     */

    public void runPythonScript(String scriptPath, String jsonFilePath) {

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.submit(() -> {
            try {

                // Configuration du processus pour exécuter le script avec arguments
                ProcessBuilder pb = new ProcessBuilder("python", scriptPath, jsonFilePath);
                Process p = pb.start();

                // Lecture des messages de sortie
                BufferedReader bfr = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line = "";
                while ((line = bfr.readLine()) != null) {
                    System.out.println("Sortie: " + line);
                }
                // Lecture des messages d'erreur
                BufferedReader errorBfr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                while ((line = errorBfr.readLine()) != null) {
                    System.err.println("Erreur : " + line);
                }

                // Attente de la fin de l'exécution
                p.waitFor();

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                // Mise à jour de l'interface utilisateur en cas d'exception
                Platform.runLater(() -> {

                    System.out.println("Erreur lors de l'exécution du script Python : " + e.getMessage());
                });
            }
        });
    }

}