package java_iot.classes;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
            InputStream inputStream = getClass().getResourceAsStream("/java_iot/ressources/data_collecting/data.json");
            if (inputStream == null) {
                throw new IOException("Fichier JSON introuvable : " + jsonFilePath);
            }

            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            Map<String, Object> data = gson.fromJson(new InputStreamReader(inputStream), type);

            Global global = null;
            if (data.containsKey("Global")) {
                global = gson.fromJson(gson.toJson(data.get("Global")), Global.class);
            }

            Map<String, Room> rooms = new HashMap<>();
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                if (!entry.getKey().equals("Global")) {
                    String nom = entry.getKey();
                    List<Sensor> sensors = extractSensors(entry.getValue());
                    if (!sensors.isEmpty()) {
                        Room room = new Room(nom, sensors);
                        rooms.put(nom, room);
                    }
                }
            }
            return new Data(global, rooms);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Extrait les capteurs d'un objet donné
     * 
     * @param value L'objet contenant les informations des capteurs
     * @return Une liste de capteurs extraits
     */
    private List<Sensor> extractSensors(Object value) {
        List<Sensor> sensors = new ArrayList<>();
        try {
            if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> roomData = (Map<String, Object>) value;
                for (Map.Entry<String, Object> Group_Sensor : roomData.entrySet()) {
                    if (Group_Sensor.getValue() instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> Data_sensor = (Map<String, Object>) Group_Sensor.getValue();
                        for (Map.Entry<String, Object> Sensors : Data_sensor.entrySet()) {
                            String key = Sensors.getKey();
                            Object val = Sensors.getValue();

                            if (val instanceof List) {
                                @SuppressWarnings("unchecked")
                                List<Object> Details_sensor = (List<Object>) val;
                                if (Details_sensor.size() == 2) {
                                    Double values = (Double) Details_sensor.get(0);
                                    Boolean status = (Boolean) Details_sensor.get(1);
                                    String time = (String) Data_sensor.get("time");
                                    Sensor sensor = new Sensor(key, values, status, time);
                                    sensors.add(sensor);
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

                ProcessBuilder pb = new ProcessBuilder("python", scriptPath, jsonFilePath);
                Process p = pb.start();
                BufferedReader bfr = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line = "";
                while ((line = bfr.readLine()) != null) {
                    System.out.println("Sortie: " + line);
                }
                BufferedReader errorBfr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                while ((line = errorBfr.readLine()) != null) {
                    System.err.println("Erreur : " + line);
                }
                p.waitFor();

                int exitCode = p.waitFor();

                if (exitCode != 0) {

                    System.err.println("Le script Python a échoué : " + exitCode);
                    System.err.println("Erreur : " + errorBfr.toString());

                    Platform.runLater(() -> {

                        System.out.println("Erreur du script Python : " + errorBfr.toString());
                    });
                } else {

                    System.out.println("Script Python exécuté sans erreur.");

                }

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();

                Platform.runLater(() -> {

                    System.out.println("Erreur lors de l'exécution du script Python : " + e.getMessage());
                });
            }
        });
    }

}
