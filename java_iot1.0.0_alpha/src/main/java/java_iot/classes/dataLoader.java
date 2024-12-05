package java_iot.classes;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.*;

public class dataLoader {

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
                    String roomName = entry.getKey();
                    List<Sensor> sensors = extractSensors(entry.getValue());
                    if (!sensors.isEmpty()) {
                        Room room = new Room(roomName, sensors);
                        rooms.put(roomName, room);
                    }
                }
            }
            return new Data(global, rooms);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Sensor> extractSensors(Object value) {

        List<Sensor> sensors = new ArrayList<>();

        try {
            if (value instanceof Map) {
                Map<String, Object> roomData = (Map<String, Object>) value;

                for (Map.Entry<String, Object> sensorGroup : roomData.entrySet()) {
                    if (sensorGroup.getValue() instanceof Map) {
                        Map<String, Object> sensorData = (Map<String, Object>) sensorGroup.getValue();

                        for (Map.Entry<String, Object> sensorEntry : sensorData.entrySet()) {
                            String sensorKey = sensorEntry.getKey();
                            Object sensorValue = sensorEntry.getValue();

                            if (sensorKey.equals("temperature") ||
                                    sensorKey.equals("humidity") ||
                                    sensorKey.equals("co2")) {
                                if (sensorValue instanceof List) {
                                    List<Object> sensorDetails = (List<Object>) sensorValue;
                                    if (sensorDetails.size() == 2) {
                                        Double values = null;
                                        Boolean status = null;

                                        if (sensorDetails.get(0) instanceof Double) {
                                            values = ((Double) sensorDetails.get(0)).doubleValue();
                                        }

                                        if (sensorDetails.get(1) instanceof Boolean) {
                                            status = (Boolean) sensorDetails.get(1);
                                        }

                                        String time = null;
                                        if (sensorData.containsKey("time")
                                                && sensorData.get("time") instanceof String) {
                                            time = (String) sensorData.get("time");
                                        }

                                        Sensor sensor = new Sensor(sensorKey, values, status, time);
                                        sensors.add(sensor);
                                    }
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

    public void runPythonScript(String scriptPath, String jsonFilePath) {
        try {

            ProcessBuilder pb = new ProcessBuilder("python", scriptPath, jsonFilePath);
            Process process = pb.start();

            try (BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {

                String line;
                while ((line = stdInput.readLine()) != null) {
                    System.out.println("Sortie " + line);
                }

                while ((line = stdError.readLine()) != null) {
                    System.err.println("Erreur" + line);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Le script Python a échoué  : " + exitCode);
            } else {
                System.out.println("Script Python exécuté avec succès.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
