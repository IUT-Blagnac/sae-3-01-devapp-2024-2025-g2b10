package java_iot.view;

import java.net.URL;
import java.util.ResourceBundle;

import java_iot.App;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class AdditionView implements Initializable{

    
    @FXML
    protected Button closeButton;
    @FXML
    protected Button confirmButton;
    @FXML
    protected VBox containerBox;
    @FXML
    protected TextField valueField;
    @FXML
    protected Pane dialoguePane;
    
    private App app;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setApp(App _app){
        app = _app;
    }

    public void setValueBoxDisable(boolean state){
        dialoguePane.setDisable(state);
    }

    @FXML
    private void close(){
        app.closeOverlay();
    }
    
}
