package java_iot.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java_iot.controller.MainSceneController;
import javafx.application.Platform;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;

/**
 * La classe {@code Graphique} fournit des fonctionnalités pour afficher et
 * gérer
 * des représentations graphiques de données dans une application JavaFX.
 * Elle inclut des méthodes pour afficher des tableaux de bord, des données
 * spécifiques
 * à une pièce, et des métriques globales de panneaux solaires sous forme de
 * graphiques à barres.
 * 
 * <p>
 * Elle utilise des données JSON provenant d'un fichier de ressources pour
 * extraire et
 * afficher des métriques telles que la température, l'humidité, les niveaux de
 * CO2,
 * et les performances des panneaux solaires.
 * </p>
 * 
 * <p>
 * Cette classe implémente un design pattern Singleton pour garantir qu'une
 * seule
 * instance soit créée.
 * </p>
 * 
 * @version 1.0
 * @since 2023
 * @authors
 *          Quentin Martinez
 *          Jules Giard--Pellat
 */
public class Graphique {
    private static Graphique instance;
    private MainSceneController msc;
    private String datajson;

    private Graphique(MainSceneController pfmsc) {
        msc = pfmsc;
    }

    /**
     * Récupère les données JSON actuellement stockées dans l'objet.
     * 
     * @return les données JSON sous forme de chaîne de caractères.
     */
    public String getData() {
        return datajson;
    }

    /**
     * Définit les données JSON pour cet objet.
     * 
     * @param pfdatajson les données JSON sous forme de chaîne de caractères.
     */
    public void setData(String pfdatajson) {
        this.datajson = pfdatajson;
    }

    /**
     * Retourne l'instance existante de {@code Graphique} ou en crée une nouvelle si
     * elle n'existe pas.
     * 
     * @param pfmsc le contrôleur de la scène principale à associer à cette
     *              instance.
     * @return l'instance unique de {@code Graphique}.
     */
    public static Graphique getInstance(MainSceneController pfmsc) {
        if (instance == null) {
            instance = new Graphique(pfmsc);
        }
        return instance;
    }

    public void showDash() {
        // Je te laisse ça vide Quentin
    }

    /**
     * Convertit une chaîne en un nombre à virgule flottante (double).
     * 
     * @param str la chaîne à convertir.
     * @return la valeur double de la chaîne, ou 0.0 si la conversion échoue.
     */
    public double stringToDouble(String str) {
        try {
            return Double.parseDouble(str); // Try parsing the string into a double
        } catch (NumberFormatException e) {
            System.err.println("Invalid input: " + str); // Handle invalid input
            return 0.0; // Return a default value if parsing fails
        }
    }

    /**
     * Affiche les métriques pour une pièce spécifique dans un graphique à barres.
     * 
     * @param roomName le nom de la pièce pour laquelle les données doivent être
     *                 affichées.
     * @throws URISyntaxException si un problème survient avec l'URI du fichier de
     *                            données.
     */
// Remplacez BarChart par LineChart dans la méthode showRoom
public void showRoom(String roomName) throws URISyntaxException {
    Pane temproomPane = msc.getMainSceneView().tempPane;
    Pane humroomPane = msc.getMainSceneView().humPane;
    Pane co2roomPane = msc.getMainSceneView().co2Pane;

    // Supprimer tous les anciens graphiques
    temproomPane.getChildren().removeIf(node -> node instanceof LineChart);
    humroomPane.getChildren().removeIf(node -> node instanceof LineChart);
    co2roomPane.getChildren().removeIf(node -> node instanceof LineChart);

    // Récupérer les données de la salle
    Map<String, Double> roomData = fetchRoomData(roomName);
    if (roomData == null || roomData.isEmpty()) {
        System.err.println("No data available for room: " + roomName);
        return;
    }

    // Créer des LineCharts pour température, humidité et CO2
    LineChart<Number, Number> tempChart = createLineChart("Température de la salle: " + roomName);
    LineChart<Number, Number> humChart = createLineChart("Humidité de la salle: " + roomName);
    LineChart<Number, Number> co2Chart = createLineChart("CO2 de la salle: " + roomName);

    // Ajouter des données aux graphiques
    if (roomData.containsKey("temperature")) {
        XYChart.Series<Number, Number> tempSeries = new XYChart.Series<>();
        tempSeries.setName("Température");
        tempSeries.getData().add(new XYChart.Data<>(1, roomData.get("temperature")));
        tempChart.getData().add(tempSeries);
    }

    if (roomData.containsKey("humidity")) {
        XYChart.Series<Number, Number> humSeries = new XYChart.Series<>();
        humSeries.setName("Humidité");
        humSeries.getData().add(new XYChart.Data<>(1, roomData.get("humidity")));
        humChart.getData().add(humSeries);
    }

    if (roomData.containsKey("co2")) {
        XYChart.Series<Number, Number> co2Series = new XYChart.Series<>();
        co2Series.setName("CO2");
        co2Series.getData().add(new XYChart.Data<>(1, roomData.get("co2")));
        co2Chart.getData().add(co2Series);
    }

    // Centrer et ajouter les graphiques aux panneaux
    centerChart(tempChart, temproomPane);
    centerChart(humChart, humroomPane);
    centerChart(co2Chart, co2roomPane);
}

// Méthode pour créer un LineChart
private LineChart<Number, Number> createLineChart(String title) {
    NumberAxis xAxis = new NumberAxis();
    xAxis.setLabel("Temps");

    NumberAxis yAxis = new NumberAxis();
    yAxis.setLabel("Valeurs");

    LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
    lineChart.setTitle(title);
    return lineChart;
}

  

    /**
     * Récupère les données métriques d'une pièce spécifiée à partir de la ressource
     * JSON.
     * 
     * @param roomName le nom de la pièce pour laquelle récupérer les données.
     * @return une carte contenant les métriques de la pièce et leurs valeurs
     *         respectives.
     * @throws URISyntaxException si un problème survient avec l'URI du fichier de
     *                            données.
     */
    public Map<String, Double> fetchRoomData(String roomName) throws URISyntaxException {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(new File("data_collecting/data.json").toURI()); // Adjust path as needed

        try {
            JsonNode rootNode = objectMapper.readTree(file);
            JsonNode roomNode = rootNode.get(roomName);

            if (roomNode != null) {
                // Convert the roomNode to a JSON string
                String roomJson = roomNode.toString();

                // Extract metrics using regex
                Map<String, Double> roomMetrics = new HashMap<>();
                roomMetrics.put("temperature", extractMetric(roomJson, "temperature"));
                roomMetrics.put("humidity", extractMetric(roomJson, "humidity"));
                roomMetrics.put("co2", extractMetric(roomJson, "co2"));

                // Debug output
                roomMetrics.forEach((key, value) -> System.out.println(key + ": " + value));

                return roomMetrics;
            } else {
                System.err.println("Room not found: " + roomName);
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Récupère les données globales métriques à partir de la ressource JSON.
     * 
     * <p>
     * Cette méthode analyse le nœud "Global" dans le JSON et extrait les métriques
     * pertinentes,
     * telles que la puissance actuelle, l'énergie de vie et les données de
     * consommation d'énergie.
     * </p>
     * 
     * @return une carte contenant les métriques globales avec leurs valeurs.
     *         Les nœuds JSON imbriqués sont retournés sous forme d'objets
     *         {@code JsonNode}.
     * @throws URISyntaxException si un problème survient avec l'URI du fichier de
     *                            données.
     */
    private Map<String, Object> fetchGlobalData() throws URISyntaxException {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(new File("data_collecting/data.json").toURI());
        try {
            JsonNode rootNode = objectMapper.readTree(new FileInputStream(file));
            JsonNode globalNode = rootNode.get("Global"); // Access the "Global" node
            if (globalNode != null) {
                Map<String, Object> globalData = new HashMap<>();
                globalNode.fields().forEachRemaining(entry -> {
                    JsonNode value = entry.getValue();
                    // Add different types of valeurs (e.g., objects, primitives)
                    if (value.isObject()) {
                        globalData.put(entry.getKey(), value); // Add as a JsonNode for nested objects
                    } else if (value.isTextual()) {
                        globalData.put(entry.getKey(), value.asText());
                    } else if (value.isDouble() || value.isFloat()) {
                        globalData.put(entry.getKey(), value.asDouble());
                    } else if (value.isInt() || value.isLong()) {
                        globalData.put(entry.getKey(), value.asLong());
                    } else {
                        globalData.put(entry.getKey(), value.toString());
                    }

                    // Debugging
                    System.out.println("Key: " + entry.getKey());
                    System.out.println("Value: " + value.toString());
                });
                return globalData;
            } else {
                System.err.println("Global data not found.");
                return null;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Affiche les métriques globales des panneaux solaires sous forme de graphique
     * à barres.
     * 
     * @throws URISyntaxException si un problème survient avec l'URI du fichier de
     *                            données.
     */
    public void showPanel() throws URISyntaxException {
    Pane container = msc.getMainSceneView().graphDisplayPane;  // Assuming graphDisplayPane exists and is initialized
    Label day = msc.getMainSceneView().panelDay;
    Label month = msc.getMainSceneView().panelMonth;
    Label year = msc.getMainSceneView().panelYear;
    Label current = msc.getMainSceneView().panelCurrent;

    // Clear existing visual elements
    container.getChildren().clear();

    // Fetch global data for the panel
    Map<String, Object> globalData = fetchGlobalData();
    if (globalData == null || globalData.isEmpty()) {
        System.err.println("No global data available for the panel");
        return;
    }

    // Identify the most recent timestamp and its corresponding data
    String latestTimestamp = null;
    JsonNode latestData = null;
    for (Map.Entry<String, Object> entry : globalData.entrySet()) {
        // Check if the key is a valid timestamp (e.g., "2024-12-10 13:38:05")
        if (entry.getKey().matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
            // If it's the first timestamp or a newer timestamp, update the latest timestamp and data
            if (latestTimestamp == null || entry.getKey().compareTo(latestTimestamp) > 0) {
                latestTimestamp = entry.getKey();
                latestData = (JsonNode) entry.getValue();
            }
        }
    }

    if (latestTimestamp == null || latestData == null) {
        System.err.println("No valid timestamp found in global data");
        return;
    }

    // Extract energy data from the most recent entry (latestData)
    JsonNode lastYearDataNode = latestData.get("lastYearData");
    JsonNode lastMonthDataNode = latestData.get("lastMonthData");
    JsonNode lastDayDataNode = latestData.get("lastDayData");
    JsonNode CurrentDataNode = latestData.get("currentPower");

    // Set the energy values in the labels
    if (lastYearDataNode != null && lastYearDataNode.has("energy")) {
        year.setText("Année dernière : " + lastYearDataNode.get("energy").asText() + " kWh");
    }
    if (lastMonthDataNode != null && lastMonthDataNode.has("energy")) {
        month.setText("Mois dernier : " + lastMonthDataNode.get("energy").asText() + " kWh");
    }
    if (lastDayDataNode != null && lastDayDataNode.has("energy")) {
        day.setText("Journée : " + lastDayDataNode.get("energy").asText() + " kWh");
    }
    if (CurrentDataNode != null && CurrentDataNode.has("power")) {
        current.setText("Actuel : " + CurrentDataNode.get("power").asText() + " kWh");
    }

    // Create the LineChart for currentPower
    LineChart<Number, Number> powerChart = createPowerLineChart("Current Power Over Time");

    // Extract currentPower time-series data from the "Global" section
    List<Long> timestamps = new ArrayList<>();
    List<Double> powerData = new ArrayList<>();

    // Loop through the globalData to extract timestamps and power values for the LineChart
    for (Map.Entry<String, Object> entry : globalData.entrySet()) {
        if (entry.getKey().matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
            JsonNode timeNode = (JsonNode) entry.getValue();
            if (timeNode != null && timeNode.has("currentPower")) {
                JsonNode currentPowerNode = timeNode.get("currentPower");
                if (currentPowerNode != null && currentPowerNode.has("power")) {
                    double power = currentPowerNode.get("power").asDouble();
                    long timestamp = parseTimestamp(entry.getKey());  // Convert the timestamp string to milliseconds
                    
                    // Add the timestamp and power to their respective lists
                    timestamps.add(timestamp);
                    powerData.add(power);
                }
            }
        }
    }



    // Add the data to the power chart
    if (!timestamps.isEmpty() && !powerData.isEmpty()) {
        XYChart.Series<Number, Number> powerSeries = new XYChart.Series<>();
        powerSeries.setName("Current Power");

        for (int i = 0; i < timestamps.size(); i++) {
            long time = timestamps.get(i);
            double power = powerData.get(i);
            powerSeries.getData().add(new XYChart.Data<>(time, power));
        }

        powerChart.getData().add(powerSeries);
    }

    // Add the power chart to the container
    container.getChildren().add(powerChart);
}

private long parseTimestamp(String timestampStr) {
    // Parse the timestamp string (e.g., "2024-12-10 13:38:05") into milliseconds
    try {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = dateFormat.parse(timestampStr);
        return date.getTime();
    } catch (ParseException e) {
        e.printStackTrace();
        return 0L;  // Return 0 if parsing fails
    }
}

    /**
     * Crée un graphique à barres avec le titre et les étiquettes d'axe spécifiés.
     * 
     * @param title      le titre du graphique à barres.
     * @param xAxisLabel l'étiquette pour l'axe des X.
     * @param yAxisLabel l'étiquette pour l'axe des Y.
     * @return le graphique à barres créé.
     */
    private BarChart<String, Number> createBarChart(String title, String xAxisLabel, String yAxisLabel) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel(xAxisLabel);
        // xAxis.setStyle("-fx-padding: 0 5 0 5;"); // Reduce the padding around the
        // bars

        // Adjust the category gap to make bars thinner
        // xAxis.setCategoryGap(5); // Default is 10, reducing this will make bars
        // thinner

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(yAxisLabel);

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle(title);
        barChart.setPrefSize(10, 500); // Adjust the chart's preferred size

        // Apply CSS for any other custom styling
        try {
            barChart.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        } catch (NullPointerException e) {
            System.err.println("CSS file not found: /styles.css");
        }

        return barChart;
    }

    /**
     * Extrait une valeur spécifique d'une métrique d'une chaîne JSON en fonction de
     * la clé fournie.
     * 
     * @param jsonString la chaîne JSON contenant la métrique.
     * @param key        la clé pour la métrique à extraire.
     * @return la valeur extraite sous forme de double, ou 0.0 si la clé n'est pas
     *         trouvée ou invalide.
     */
    private double extractMetric(String jsonString, String key) {
        String regex = "\"" + key + "\":\\[(\\d+\\.?\\d*)"; // Match both integers and doubles
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(jsonString);

        if (matcher.find()) {
            String value = matcher.group(1);
            try {
                return Double.parseDouble(value); // Convert to double (works for both int and double)
            } catch (NumberFormatException e) {
                System.err.println("Failed to parse value for key '" + key + "': " + value);
            }
        } else {
            System.err.println("Metric '" + key + "' not found in the provided JSON.");
        }
        return 0.0; // Default value if metric is missing
    }

/**
 * Centers the chart within its parent pane.
 */
private void centerChart(LineChart<Number, Number> chart, Pane pane) {
    pane.getChildren().add(chart); // Ensure the chart is added only once
    chart.layoutXProperty().bind(pane.widthProperty().subtract(chart.widthProperty()).divide(2));
    chart.layoutYProperty().bind(pane.heightProperty().subtract(chart.heightProperty()).divide(2));
}

private LineChart<Number, Number> createPowerLineChart(String title) {
    NumberAxis xAxis = new NumberAxis();
    xAxis.setLabel("Time");

    // Format the X-axis labels (timestamps in milliseconds)
    xAxis.setTickLabelFormatter(new javafx.util.StringConverter<Number>() {
        private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        @Override
        public String toString(Number object) {
            // Convert milliseconds to a readable date format
            return dateFormat.format(new Date(object.longValue()));
        }

        @Override
        public Number fromString(String string) {
            return null; // We don't need to support this for this use case
        }
    });

    NumberAxis yAxis = new NumberAxis();
    yAxis.setLabel("Power (kW)");

    // Create the LineChart
    LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
    lineChart.setTitle(title);

    return lineChart;
}

private LineChart<Number, Number> createLineChart(String title, List<Long> times) {
    NumberAxis xAxis = new NumberAxis();
    xAxis.setLabel("Temps");

    // Calculate the range based on the timestamps
    long minTime = Collections.min(times);  // Get the minimum time (start time)
    long maxTime = Collections.max(times);  // Get the maximum time (end time)
    
    xAxis.setLowerBound(minTime);  // Set the lower bound of the time range
    xAxis.setUpperBound(maxTime);  // Set the upper bound of the time range

    // Optional: Set tick spacing based on the total range
    long range = maxTime - minTime;
    xAxis.setTickUnit(range / 10);  // Set the tick unit to divide the range into 10 parts (approx.)

    NumberAxis yAxis = new NumberAxis();
    yAxis.setLabel("Valeurs");

    LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
    lineChart.setTitle(title);

    return lineChart;
}


}