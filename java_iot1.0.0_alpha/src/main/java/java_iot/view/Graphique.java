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

public class Graphique {
    private static Graphique instance;
    private MainSceneController msc;
    private String datajson;

    private Graphique(MainSceneController pfmsc) {
        msc = pfmsc;
    }

    /**
     * simple getter
     * 
     * @return the data
     */
    public String getData() {
        return datajson;
    }

    /**
     * simple setter
     * 
     * @param pfdatajson
     */
    public void setData(String pfdatajson) {
        this.datajson = pfdatajson;
    }

    /**
     * Returns the existing instance of the SettingsView or creates one.
     * 
     * @param MainSceneView pfmsc : the Main Scene Controller
     * @author GIARD--PELLAT Jules
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

    public void showRoom(String roomName) throws URISyntaxException {
        Pane roomPane = msc.getMainSceneView().roomPane; // Assume roomPane exists and is initialized
        VBox container = (VBox) roomPane.getChildren().get(0);
    
        // Remove any existing charts in the container
        container.getChildren().removeIf(node -> node instanceof BarChart); // Or other chart types
    
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
    

    private Map<String, Double> fetchRoomData(String roomName) throws URISyntaxException {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(App.class.getResource("ressources\\data_collecting\\data.json").toURI());
        try {
            JsonNode rootNode = objectMapper.readTree(file);
            JsonNode roomNode = rootNode.get(roomName);
            if (roomNode != null) {
                Map<String, Double> roomData = new HashMap<>();
                roomNode.fields().forEachRemaining(entry -> {
                    
                        roomData.put(entry.getKey(), entry.getValue().asDouble());
                    
                    System.out.println("here");
                    System.out.println(entry.getKey().toString());
                    System.out.println(entry.getValue().toString());
                });
                return roomData;
            } else {
                System.err.println("Room not found: " + roomName);
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

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
    
        // Assuming the relevant data is under the global data, specifically for energy metrics
        if (globalData.containsKey("Panel")) {
            Map<String, Double> panelData = (Map<String, Double>) globalData.get("Panel");
    
            // Add only the currentPower to the series for now
            if (panelData.containsKey("currentPower")) {
                series.getData().add(new XYChart.Data<>("Current Power", panelData.get("currentPower")));
                System.out.println(panelData.get("currentPower"));
                System.out.println("heeeeere");
            }
            System.out.println("heeeeeeeeeeeeeeeere");
    
            // You can add the other data later by uncommenting the following lines:
            /*
            if (panelData.containsKey("lifeTimeData")) {
                series.getData().add(new XYChart.Data<>("Lifetime Energy", panelData.get("lifeTimeData")));
            }
            if (panelData.containsKey("lastYearData")) {
                series.getData().add(new XYChart.Data<>("Last Year Energy", panelData.get("lastYearData")));
            }
            if (panelData.containsKey("lastMonthData")) {
                series.getData().add(new XYChart.Data<>("Last Month Energy", panelData.get("lastMonthData")));
            }
            if (panelData.containsKey("lastDayData")) {
                series.getData().add(new XYChart.Data<>("Last Day Energy", panelData.get("lastDayData")));
            }
            */
        }
    
        barChart.getData().add(series);
    
        // Add the chart to the panelPane
        panelPane.getChildren().add(barChart);
    }
    
    

    private BarChart<String, Number> createBarChart(String title, String xAxisLabel, String yAxisLabel) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel(xAxisLabel);

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(yAxisLabel);

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle(title);
        return barChart;
    }

}