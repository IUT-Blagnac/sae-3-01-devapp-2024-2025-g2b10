package java_iot.view;

import java.io.File;
import java.io.IOException;
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


    public void showRoom(String roomName) {
        Pane roomPane = msc.getMainSceneView().roomPane; // Assume roomPane exists and is initialized
        VBox container = (VBox) roomPane.getChildren().get(0);
    
        // Remove any existing BarChart instances in the container
        container.getChildren().removeIf(node -> node instanceof BarChart);
    
        // Fetch room data (replace this with your dynamic data retrieval logic)
        String roomDataJson = fetchRoomData(roomName);
        if (roomDataJson == null) {
            System.err.println("No data available for room: " + roomName);
            return;
        }
    
        // Parse JSON into a map
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Double> data;
        try {
            data = mapper.readValue(roomDataJson, new TypeReference<>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    
        // Create a Bar Chart
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Metrics");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Values");
    
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Room Metrics for: " + roomName);
    
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Room Metrics");
    
        // Add data to the series
        data.forEach((key, value) -> series.getData().add(new XYChart.Data<>(key, value)));
    
        barChart.getData().add(series);
    
        // Add the chart to the container
        container.getChildren().add(barChart);
    }
    
    private String fetchRoomData(String roomName) {
        ObjectMapper objectMapper = new ObjectMapper();
        // File file = new File("../../../resources/java_iot/ressources/data_collecting/data.json");
        File file = new File("java_iot1.0.0_alpha\\src\\main\\resources\\java_iot\\ressources\\data_collecting\\data.json");
        try {
            // Parse the JSON file into a JsonNode
            System.out.println("b4");
            JsonNode rootNode = objectMapper.readTree(file);
            System.out.println("here");
    
            // Access the specific room node
            JsonNode roomNode = rootNode.get(roomName);
    
            if (roomNode != null) {
                // Access the temperature field
                JsonNode temperatureNode = roomNode.get("temperature"); // Replace "temperature" with the actual field name
                if (temperatureNode != null && temperatureNode.isDouble()) {
                    double temperatureValue = temperatureNode.asDouble();
                    return String.format("Temperature: %.2f", temperatureValue); // Format as string with two decimal places
                } else if (temperatureNode != null && temperatureNode.isTextual()) {
                    return "Temperature: " + temperatureNode.asText(); // If the field is already a string
                } else {
                    System.err.println("Temperature field is missing or not a valid double.");
                    return null;
                }
            } else {
                System.err.println("Room not found in data.json: " + roomName);
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    

    public void showPanel() {
        Pane panelPane = msc.getMainSceneView().panelPane;
        VBox container = (VBox) panelPane.getChildren().get(0);
    
        // Remove any existing BarChart from the panelPane
        container.getChildren().removeIf(node -> node instanceof BarChart);
    
        // Example static solar panel data
        String panelDataJson = """
                {
                    "lastUpdateTime": "2024-12-07 18:41:22",
                    "lifeTimeData": {"energy": 3469161},
                    "lastYearData": {"energy": 2988131},
                    "lastMonthData": {"energy": 53725},
                    "lastDayData": {"energy": 4987},
                    "currentPower": {"power": 0}
                }
        """;

    
        // Parse the JSON to SolarPanelData POJO
        ObjectMapper mapper = new ObjectMapper();
        SolarPanelData panelData;
        try {
            // Deserialize only the relevant fields
            panelData = mapper.readValue(panelDataJson, SolarPanelData.class);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("here");
            return;
        }
    
        // Create BarChart for Solar Panel Data
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Metrics");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Energy / Power");
    
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Solar Panel Data");
    
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Energy Metrics");
    
        // Add data to the chart
        series.getData().add(new XYChart.Data<>("Lifetime Energy", panelData.getEnergy()));
        series.getData().add(new XYChart.Data<>("Last Year Energy", panelData.getLastYearEnergy()));
        series.getData().add(new XYChart.Data<>("Last Month Energy", panelData.getLastMonthEnergy()));
        series.getData().add(new XYChart.Data<>("Last Day Energy", panelData.getLastDayEnergy()));
        series.getData().add(new XYChart.Data<>("Current Power", panelData.getCurrentPower()));
    
        barChart.getData().add(series);
    
        container.getChildren().add(barChart); // Add the chart to the VBox
    }
}