package java_iot.view;

import java.io.IOException;
import java.util.Map;

import java_iot.App;
import java_iot.controller.MainSceneController;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
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

        container.getChildren().removeIf(node -> node instanceof BarChart);

        // Ensure the roomPane isn't fully cleared
        if (roomPane.getChildren().size() > 1) {
            // Remove only the chart if it already exists
            roomPane.getChildren().removeIf(node -> node instanceof BarChart);
        }

        // Example JSON data for a room
        String roomDataJson = """
                {
                   "temperature": 18.9,
                   "humidity": 53.5,
                   "activity": 0,
                   "co2": 1700,
                   "tvoc": 120,
                   "illumination": 1,
                   "infrared": 1,
                   "infrared_and_visible": 1,
                   "pressure": 993.3
                 }
                               """;

        // Parse JSON (use your preferred library, e.g., Jackson or Gson)
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Double> data;
        try {
            data = mapper.readValue(roomDataJson, new TypeReference<>() {
            });
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
        barChart.setTitle("Visualisation de la salle");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Room Metrics");

        // Add data to the series
        data.forEach((key, value) -> series.getData().add(new XYChart.Data<>(key, value)));

        barChart.getData().add(series);

        // Add the chart to the room pane
        container.getChildren().add(barChart);

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