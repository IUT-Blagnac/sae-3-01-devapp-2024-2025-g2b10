package java_iot.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import java_iot.App;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class AlertView implements Initializable {

    @FXML
    protected VBox containerBox;
    @FXML
    protected Pane alertPane;

    private List<Pane> alertList;

    private static AlertView instance;

    private App app;

    public AlertView() {
        alertList = new ArrayList<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setApp(App _app) {
        app = _app;
    }

    public static AlertView getInstance() {
        if (instance == null) {
            instance = new AlertView();
        }
        return instance;
    }

}
