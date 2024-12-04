package java_iot.view;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ResourceBundle;

import java_iot.App;
import java_iot.controller.AdditionController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;

public class AdditionView implements Initializable{

    
    @FXML
    protected Button closeButton;
    @FXML
    protected Button confirmButton;
    @FXML
    protected ListView<String> containerList;
    @FXML
    protected TextField valueField;
    @FXML
    protected Pane dialoguePane;
    
    private App app;
    private AdditionController ac;
    private String callerId;
    private boolean mono;

    private String section;
    private String name;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ac = AdditionController.getInstance();
    }

    public void setApp(App _app){
        app = _app;
    }

    public void start(){
        containerList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        section = "Data treatment";
        HashMap<String, String> settings = ac.requestSettingsList(section);
        
        String fieldName = ac.requestFieldFromIndex("Data treatment", Integer.valueOf(callerId)+1);
        name = fieldName;
        String settingValue = settings.get(fieldName);
        System.out.println("Field name : " + fieldName);
        System.out.println("Settings value : "+settingValue);
        System.err.println(settings);

        String[] seperatedSettings = settingValue.split(",");
        
        String[][] allAvailableSettings = ac.getAllSettings(fieldName);

        for (int i = 0; i < allAvailableSettings.length; i++){
            containerList.getItems().add(ac.getTopicNameFromIndex(i)); // Add category
            for (String s : allAvailableSettings[i]){
                System.out.println("CURRENTLY INPUTING : "+s);
                if (!Arrays.asList(seperatedSettings).stream().anyMatch(str -> str.contains(s))){
                    System.out.println("NOT FOUND");
                    containerList.getItems().add(s);
                }else{
                    System.out.println("WAS FOUND");
                }
            }
        }

        containerList.setCellFactory(param -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
            
                if (empty || item == null) {
                    setText(null);
                    setDisable(false);
                    setStyle("");
                } else {
                    setText(item);
                    if (isCategory(item)) {
                        setDisable(true);
                        setStyle("-fx-background-color: #acfffc; -fx-text-fill: white; -fx-font-weight: bold;");
                    } else {
                        setDisable(false);
                        setStyle("");
                    }
            
                    // Handle selection
                    if (getListView().getSelectionModel().isSelected(getIndex())) {
                        setStyle("-fx-background-color: -fx-accent; -fx-text-fill: white;");
                    }
                }
            }


        // Helper method to determine if the item is a category
        private boolean isCategory(String item) {
            // You can add logic here to determine if an item is a category
            // For example, if the item is one of the category names
            // Or you could simply check based on the index you add them in
            // Here we just assume categories contain a specific naming pattern
            return Arrays.asList(ac.getTopicNameFromIndex()).contains(item);
        }
        });
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

    @FXML
    private void confirm(){
        String selectedItem = containerList.getSelectionModel().getSelectedItem();
        System.out.println(selectedItem);
        if (selectedItem != null){
            if (mono){
                ac.requestSettingChange(section, name, selectedItem, true);
                app.closeOverlay();
            }else{
                if (valueField.getText().isBlank()){
                    Alert a = new Alert(AlertType.ERROR);
                    a.setContentText("Entrez un seuil d'alerte.");
                    a.showAndWait();
                    return;
                }
                String text = valueField.getText().replaceAll("[^A-Za-z0-9]", "");
                String processedItem = selectedItem + ":" + text;
                boolean result = ac.requestSettingChange(section, name, processedItem, true);
                if (!result){
                    Alert a = new Alert(AlertType.ERROR);
                    a.setContentText("Le seuil doit être un chiffre.");
                    a.showAndWait();
                    return;
                }
                app.closeOverlay();
            }
        }else{
            Alert a = new Alert(AlertType.ERROR);
            a.setContentText("Une valeur doit etre sélectionnée");
            a.showAndWait();
        }
    }
    
}
