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

/**
 * <p>AdditionView is a class that gets called when the user attempts to 
   create a new setting in the Data treatment tab of the app.
 * <p>This class only handles the reading and transmission of inputs of the user
   to its dedicated controller.
 * <p>This class <b>SHOULD ONLY REFERENCE</br> {@link java_iot.controller.AdditionController}
   and {@link java_iot.App} as they are needed to assure a good functioning of this class.
 * @author ESTIENNE Alban-Moussa
 */
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

    /**
     * Sets the app that called this window in other to request a closure. 
     */
    public void setApp(App _app){
        app = _app;
    }

    /**
     * <p>Sets the start of the addition window.
     * <p>The start of the application has been delayed to allow {@link java_iot.App} to provide its instance
       to this class, in order for {@link java_iot.view.AdditionView#confirm()} to initialize the closing process
       of the window.
     * <p>This function withdraw the callerButtonId to know which button was pressed, and requests the 
       setting list accordingly. For now, as only Data Treatment has been planed to use this view, the 
       only section of the parameter file that this view calls is "Data treatment", further development will
       require to store the calling category inside this view.
     * <p>This function utilizes ListView and a factory to display the cell of the field, or the category 
       accordingly to whether or not it is present in the list {@link java_iot.model.Settings} provided.
     */
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

        /*
         * The outer loops iterate through all the categories, and adds he corresponding name
         */
        for (int i = 0; i < allAvailableSettings.length; i++){
            containerList.getItems().add(ac.getTopicNameFromIndex(i)); // Add category
            // The inner loop iterate through all the settings and adds them under the category.
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

        /*
         * this factory is responsible for the style of the categories or regular fiels.
         */
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
                        setStyle("-fx-background-color: #3c3c3c; -fx-text-fill: white; -fx-font-weight: bold;");
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

    /**
     * <p>Initiate the confirmation process of the addition windows.
     * <p>The following situations will provide an error alert : 
     * <ul>
     * <li>If a mono dialogue : 
     * <ul>
     * <li>Nothing has been selected.
     * </ul>
     * <li>If a bi dialogue :
     * <ul>
     * <li>Nothing has been selected.
     * <li>No values have been entered.
     * <li>The entered value is malformed.
     * </ul>
     * </ul>
     * 
     */
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
