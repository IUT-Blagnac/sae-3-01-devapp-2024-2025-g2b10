package java_iot.view;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import java_iot.App;
import java_iot.controller.AdditionController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Cell;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class AdditionView implements Initializable{

    
    @FXML
    protected Button closeButton;
    @FXML
    protected Button confirmButton;
    @FXML
    protected ListView containerList;
    @FXML
    protected TextField valueField;
    @FXML
    protected Pane dialoguePane;
    
    private App app;
    private AdditionController ac;
    private String callerId;
    private boolean mono;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ac = AdditionController.getInstance();
    }

    public void setApp(App _app){
        app = _app;
    }

    public void start(){
        HashMap<String, String> settings = ac.requestSettingsList("Data treatment");
        String fieldName = ac.requestSettingsFromId(Integer.valueOf(callerId));
        
        String settingValue = settings.get(fieldName);
        
        String[][] allAvailableSettings = ac.getAllSettings();

        for (int i = 0; i < allAvailableSettings.length; i++){
            containerList.getItems().add(ac.getTopicNameFromIndex(i)); // Add category
            containerList.lookup(".list-cell:nth-child(" + (containerList.getItems().size()) + ")").setStyle("-fx-background-color: black; -fx-text-fill: lightgray;");  // Apply style
            for (String s : allAvailableSettings[i]){
                containerList.getItems().add(s);
            }
        }
    }

    /**
     * Specifies if the called box is a mono or bi dialogue, and closes the value tab accordingly.
     * @param state : If dialogue is mono (true) or bi (false)
     */
    public void setMonoDialogue(boolean state){
        mono = state;
        dialoguePane.setDisable(state);
    }

    /**
     * Specifies who called this box to load its settings.
     * @param fieldName : The name of the caller.
     */
    public void setCallerId(String fieldName){
        callerId = fieldName;
    }

    @FXML
    private void close(){
        app.closeOverlay();
    }
    
}
