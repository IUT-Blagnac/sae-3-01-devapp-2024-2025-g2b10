package java_iot.classes;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javafx.application.Platform;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class dataLoader {

    public dataLoader() {
    }

    /**
     * Fonction qui charge les données depuis un fichier JSON
     * 
     * @param jsonFileURL URL du fichier JSON à charger
     * @return Data contenant les données chargées, ou null en cas d'erreur
     * @throws IOException Si le fichier JSON n'est pas trouvé
     * @author PAPA-PATSOUMOUDOU Matthias
     */

    public Data loadJsonData(URL jsonFileURL) {
        try {
            // Ouvre un flux pour lire le fichier JSON
            InputStream inputStream = jsonFileURL.openStream();
            // Vérifie si le flux est accessible ou égal à null
            if (inputStream == null) {
                System.out.println("Fichier JSON introuvable.");
                return null;
            }
            // Utilise Gson pour lire les données JSON
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            Map<String, Object> data = gson.fromJson(new InputStreamReader(inputStream), type);

            // Extrait "Global" si elle existe
            Global global = null;
            if (data.containsKey("Global")) {
                global = gson.fromJson(gson.toJson(data.get("Global")), Global.class);
            }
            // Extrait les informations des salles et capteurs
            Map<String, Room> rooms = new HashMap<>();
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                if (!entry.getKey().equals("Global")) {
                    String nom = entry.getKey();
                    List<Sensor> sensors = extractSensors(entry.getValue());
                    // Ajoute la salle si elle contient des capteurs
                    if (!sensors.isEmpty()) {
                        Room room = new Room(nom, sensors);
                        rooms.put(nom, room);
                    }
                }
            }
            // Retourne un objet `Data` contenant les informations globales et les salles
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
    public List<Sensor> extractSensors(Object value) {
        List<Sensor> sensors = new ArrayList<>();
        try {
            if (value instanceof Map) { // Vérifiez que `value` est bien une Map
                Map<String, Object> roomData = (Map<String, Object>) value;
                for (Map.Entry<String, Object> Group_Sensor : roomData.entrySet()) {
                    if (Group_Sensor.getValue() instanceof Map) {// Vérifiez que le groupe de capteurs est une Map
                        Map<String, Object> Data_sensor = (Map<String, Object>) Group_Sensor.getValue();
                        for (Map.Entry<String, Object> Sensors : Data_sensor.entrySet()) {
                            String key = Sensors.getKey(); // Type de capteur
                            Object val = Sensors.getValue();

                            if (val instanceof List) { // Vérifiez que c'est une liste (pour température, etc.)
                                List<Object> Details_sensor = (List<Object>) val;
                                if (Details_sensor.size() == 2) {
                                    Double values = (Double) Details_sensor.get(0);
                                    Boolean status = (Boolean) Details_sensor.get(1);
                                    String time = (String) Data_sensor.get("time");// Récupérez "time" si disponible

                                    Sensor sensor = new Sensor(key, values, status, time);
                                    sensors.add(sensor);
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
     * @param scriptURL   URL du script Python à exécuter.
     * @param jsonFileURL URL du fichier JSON à utiliser par le script Python
     * 
     */

    public void runPythonScript(URL scriptURL, URL jsonFileURL) {
        // Création d'un thread pour exécuter le script Python
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            try {
                // Conversion des URLs en chemins absolus
                String scriptPath = new java.io.File(scriptURL.toURI()).getAbsolutePath();
                String jsonFilePath = new java.io.File(jsonFileURL.toURI()).getAbsolutePath();

                // Configuration et lancement du processus Python
                ProcessBuilder pb = new ProcessBuilder("python", scriptPath, jsonFilePath);
                Process p = pb.start();

                // Lecture de la sortie standard
                BufferedReader bfr = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while ((line = bfr.readLine()) != null) {
                    System.out.println("Sortie: " + line);
                }

                // Lecture de la sortie d'erreur
                BufferedReader errorBfr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                while ((line = errorBfr.readLine()) != null) {
                    System.err.println("Erreur : " + line);
                }

                // Attente de la fin du processus
                p.waitFor();
            } catch (IOException | InterruptedException | java.net.URISyntaxException e) {
                e.printStackTrace();
                Platform.runLater(
                        // Affichage de l'erreur dans le thread
                        () -> System.out.println("Erreur lors de l'exécution du script Python : " + e.getMessage()));
            }
        });
    }

}