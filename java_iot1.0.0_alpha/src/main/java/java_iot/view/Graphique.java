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

        // Remove any existing BarChart instances in the container
        container.getChildren().removeIf(node -> node instanceof BarChart);

        // Fetch room data as a Map
        Map<String, Double> roomData = fetchRoomData(roomName);
        if (roomData == null || roomData.isEmpty()) {
            System.out.println(roomData.toString());
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