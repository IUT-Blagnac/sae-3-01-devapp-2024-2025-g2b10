package java_iot.view;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import java_iot.App;
import java_iot.controller.MainSceneController;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * The {@code Graphique} class provides functionalities for displaying and managing 
 * graphical data representations in a JavaFX application. It includes methods to 
 * show dashboards, display room-specific data, and visualize global panel metrics 
 * using bar charts. 
 * 
 * <p>It uses JSON data from a predefined resource file to extract and display metrics 
 * such as temperature, humidity, CO2 levels, and solar panel performance data.</p>
 * 
 * <p>This class implements a singleton design pattern to ensure only one instance is created.</p>
 * 
 * @version 1.0
 * @since 2023
 * @authors 
 *     Quentin Martinez
 *     Jules Giard-Pellat
 */
public class Graphique {
    private static Graphique instance;
    private MainSceneController msc;
    private String datajson;

    private Graphique(MainSceneController pfmsc) {
        msc = pfmsc;
    }

    /**
     * Retrieves the current JSON data stored in the object.
     * 
     * @return the JSON data as a string.
     */
    public String getData() {
        return datajson;
    }

    /**
     * Sets the JSON data for this object.
     * 
     * @param pfdatajson the JSON data as a string.
     */
    public void setData(String pfdatajson) {
        this.datajson = pfdatajson;
    }

    /**
     * Returns the existing instance of the SettingsView or creates one.
     * 
     * @param MainSceneView pfmsc : the Main Scene Controller
     * @author GIARD--PELLAT Jules
     * @return the singleton instance of {@code Graphique}.
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
     * Converts a string to a double.
     * 
     * @param str the string to convert.
     * @return the double value of the string, or 0.0 if the conversion fails.
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
     * Displays metrics for a specific room in a bar chart.
     * 
     * @param roomName the name of the room for which data is to be displayed.
     * @throws URISyntaxException if there is an issue with the data file's URI.
     */
    public void showRoom(String roomName) throws URISyntaxException {
        Pane roomPane = msc.getMainSceneView().roomPane; // Assume roomPane exists and is initialized
        VBox container = (VBox) roomPane.getChildren().get(0);
    
        // Remove any existing charts in the container
        container.getChildren().removeIf(node -> node instanceof BarChart);
    
        // Fetch room data as a Map
        Map<String, Double> roomData = fetchRoomData(roomName);
        if (roomData == null || roomData.isEmpty()) {
            System.err.println("No data available for room: " + roomName);
            return;
        }
    
        // Create a Bar Chart
        BarChart<String, Number> barChart = createBarChart(
                "Room Metrics for: " + roomName, "Metrics", "Values");
    
        // Create a series for room metrics
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Room Metrics");
    
        // Add data to the series
        roomData.forEach((key, value) -> series.getData().add(new XYChart.Data<>(key, value)));
    
        barChart.getData().add(series);
    
        // Add the chart to the container
        container.getChildren().add(barChart);
    }
    
    


    /**
     * Fetches the metrics data for a specified room from the JSON resource.
     * 
     * @param roomName the name of the room to fetch data for.
     * @return a map containing the room's metrics and their respective values.
     * @throws URISyntaxException if there is an issue with the data file's URI.
     */
    public Map<String, Double> fetchRoomData(String roomName) throws URISyntaxException {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(App.class.getResource("ressources/data_collecting/data.json").toURI()); // Adjust path as needed

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
     * Fetches global metrics data from the JSON resource.
     * 
     * <p>The method parses the "Global" node in the JSON and extracts relevant 
     * metrics, such as current power, lifetime energy, and energy consumption data.</p>
     * 
     * @return a map containing the global metrics with their respective values. 
     *         Nested JSON nodes are returned as {@code JsonNode} objects.
     * @throws URISyntaxException if there is an issue with the data file's URI.
     */
    private Map<String, Object> fetchGlobalData() throws URISyntaxException {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(App.class.getResource("ressources\\data_collecting\\data.json").toURI());
        try {
            JsonNode rootNode = objectMapper.readTree(file);
            JsonNode globalNode = rootNode.get("Global"); // Access the "Global" node
            if (globalNode != null) {
                Map<String, Object> globalData = new HashMap<>();
                globalNode.fields().forEachRemaining(entry -> {
                    JsonNode value = entry.getValue();
                    // Add different types of values (e.g., objects, primitives)
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
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Displays global solar panel metrics on a bar chart.
     * 
     * @throws URISyntaxException if there is an issue with the data file's URI.
     */
    public void showPanel() throws URISyntaxException {
        Pane panelPane = msc.getMainSceneView().panelPane;  // Assume panelPane exists and is initialized
    
        // Remove any existing BarChart from the panelPane
        panelPane.getChildren().removeIf(node -> node instanceof BarChart);
    
        // Fetch global data for the panel
        Map<String, Object> globalData = fetchGlobalData();
        if (globalData == null || globalData.isEmpty()) {
            System.err.println("No global data available for the panel");
            return;
        }
    
        // Create a Bar Chart for Solar Panel Data
        BarChart<String, Number> barChart = createBarChart("Solar Panel Data", "Metrics", "Energy / Power");
    
        // Create a series for solar panel metrics
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Energy Metrics");
    
        // Add only currentPower to the series
        if (globalData.containsKey("currentPower")) {
            JsonNode currentPowerNode = (JsonNode) globalData.get("currentPower");
            if (currentPowerNode != null && currentPowerNode.has("power")) {
                series.getData().add(new XYChart.Data<>("Current Power", currentPowerNode.get("power").asDouble()));
            }
        }
    

        if (globalData.containsKey("lifeTimeData")) {
            JsonNode lifeTimeDataNode = (JsonNode) globalData.get("lifeTimeData");
            if (lifeTimeDataNode != null && lifeTimeDataNode.has("energy")) {
                series.getData().add(new XYChart.Data<>("Lifetime Energy", lifeTimeDataNode.get("energy").asDouble()));
            }
        }
        if (globalData.containsKey("lastYearData")) {
            JsonNode lastYearDataNode = (JsonNode) globalData.get("lastYearData");
            if (lastYearDataNode != null && lastYearDataNode.has("energy")) {
                series.getData().add(new XYChart.Data<>("Last Year Energy", lastYearDataNode.get("energy").asDouble()));
            }
        }
        if (globalData.containsKey("lastMonthData")) {
            JsonNode lastMonthDataNode = (JsonNode) globalData.get("lastMonthData");
            if (lastMonthDataNode != null && lastMonthDataNode.has("energy")) {
                series.getData().add(new XYChart.Data<>("Last Month Energy", lastMonthDataNode.get("energy").asDouble()));
            }
        }
        if (globalData.containsKey("lastDayData")) {
            JsonNode lastDayDataNode = (JsonNode) globalData.get("lastDayData");
            if (lastDayDataNode != null && lastDayDataNode.has("energy")) {
                series.getData().add(new XYChart.Data<>("Last Day Energy", lastDayDataNode.get("energy").asDouble()));
            }
        }
        
    
        barChart.getData().add(series);
    
        // Add the chart to the panelPane
        panelPane.getChildren().add(barChart);
    }
    
    
    
    /**
     * Creates a bar chart with the specified title and axis labels.
     * 
     * @param title the title of the bar chart.
     * @param xAxisLabel the label for the X-axis.
     * @param yAxisLabel the label for the Y-axis.
     * @return the created bar chart.
     */
    private BarChart<String, Number> createBarChart(String title, String xAxisLabel, String yAxisLabel) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel(xAxisLabel);

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(yAxisLabel);

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle(title);
        return barChart;
    }


    /**
     * Extracts a specific metric value from a JSON string based on a provided key.
     * 
     * @param jsonString the JSON string containing the metric.
     * @param key the key for the metric to extract.
     * @return the extracted metric value as a double, or 0.0 if not found or invalid.
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
}