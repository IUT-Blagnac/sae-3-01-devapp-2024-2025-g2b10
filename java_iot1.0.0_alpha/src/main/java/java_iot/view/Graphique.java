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
                    "temperature": 21.5,
                    "humidity": 45.3,
                    "co2": 600,
                    "tvoc": 300
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

    }
}