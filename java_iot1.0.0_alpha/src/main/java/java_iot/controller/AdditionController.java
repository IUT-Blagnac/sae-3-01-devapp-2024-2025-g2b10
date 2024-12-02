package java_iot.controller;

import java.net.URL;
import java.util.ResourceBundle;

import java_iot.view.MainSceneView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class AdditionController{

    private MainSceneController msc;

    public AdditionController(MainSceneController _msc){
        msc = _msc;
    }

    public void requestNewWindow(boolean mono){
        msc.requestNewAddition(mono);
    }
}
