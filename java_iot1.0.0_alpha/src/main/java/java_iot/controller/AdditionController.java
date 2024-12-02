package java_iot.view;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class AdditionController implements Initializable{

    private MainSceneController msc;

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

    public AdditionController(MainSceneController _msc){
        msc = _msc;
    }

    public void requestNewWindow(boolean mono){
        msc.requestNewAddition(mono);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
    
}
