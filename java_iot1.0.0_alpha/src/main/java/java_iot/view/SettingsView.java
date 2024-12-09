package java_iot.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import java_iot.controller.AdditionController;
import java_iot.controller.MainSceneController;
import java_iot.controller.SettingsController;
import java_iot.classes.PaneCloner;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * SettingsView is working in conjunction with java_iot.model.Settings and
 * handles the graphical part of the settings interface. SettingsView stores a
 * reference to the main scene controller. SettingsView is a singleton as it
 * should prevent duplication in data reading and writing SettingsView should
 * ONLY be called within the view package, as Navbar needs to call a data
 * loading process to load the according settings without any further requests.
 *
 * @see java_iot.model.Settings
 * @author ESTIENNE Alban-Moussa
 */
public class SettingsView {

    private MainSceneController msc;
    private MainSceneView msv;
    private AdditionController ac;
    private SettingsController sc;
    private static SettingsView instance;

    private Pane connectionInfoPane;
    private Pane currentlyOpenedPane;
    private Pane topicsPane;
    private Pane treatmentPane;

    private List<Button> settingButtonList;
    private List<ToggleButton> toggleButtonList;
    private List<TextField> informationFieldList;
    private List<VBox> containersList;

    /**
     * Private constructor for the SettingsView singleton. Stores a single
     * instance of _msc that will be UNCHANGEABLE unless force-overwritten.
     *
     * @author ESTIENNE Alban-Moussa
     */
    private SettingsView(MainSceneController _msc) {

        settingButtonList = new ArrayList<>();
        toggleButtonList = new ArrayList<>();
        informationFieldList = new ArrayList<>();
        containersList = new ArrayList<>();

        msc = _msc;

        msv = msc.getMainSceneView();
        ac = AdditionController.getInstance();
        sc = SettingsController.getInstance();

        connectionInfoPane = msv.connectionPane;
        topicsPane = msv.topicPane;
        treatmentPane = msv.treatmentPane;

        /*
		 * Ok, so before you start to scream that this code is horrendous, let me explain
		 * Settings is supposed to be an extention of MainScene, the pane containing Settings
		 * is itself a "child" of MainScene, its properties cannot be accessed from Settings.
		 * Their attributes are protected, so only the View package can see it.
		 * Since these attributes were never meant to be their in the first place, and I
		 * didn't want to write thousands of getters/setters, putting them in protected
		 * was, according to me, a smart choice.
		 * 
		 */

		settingButtonList.add(msv.connectionButton);
		settingButtonList.add(msv.topicButton);
		settingButtonList.add(msv.treatmentButton);

		toggleButtonList.add(msv.am107Button);
		toggleButtonList.add(msv.triphasoButton);
		toggleButtonList.add(msv.solarDataButton);

		informationFieldList.add(msv.adressField);
		informationFieldList.add(msv.portField);
		informationFieldList.add(msv.kaField);

		containersList.add(msv.alertContainer);
		containersList.add(msv.keptValueContainer);
		containersList.add(msv.listenedRoomContainer);

		ChangeListener<Boolean> focusListener = new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (!newValue) {
					sc.requestConnectionSettingInFile(informationFieldList);
				}
			}
		};

		ChangeListener<Boolean> frequencyFocusListener = new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (!newValue) {
					boolean valid = sc.requestSettingChange("Data treatment", "step", msv.frequencyField.getText(), false);
					if (!valid){
						msv.frequencyField.setText("1");
					}
				}
			}
		};

		msv.adressField.focusedProperty().addListener(focusListener);
		msv.portField.focusedProperty().addListener(focusListener);
		msv.kaField.focusedProperty().addListener(focusListener);

		msv.frequencyField.focusedProperty().addListener(frequencyFocusListener);

	}

	/**
	 * <p>
	 * Generate a new pane based on the value and list being updated.
	 * <p>
	 * This method follows some strict rules that are defined inside the
	 * very conception of the interface. Rules are :
	 * <ul>
	 * <li><b>Element 0</b> is a Label and contains the <b>name</b> of the attribute
	 * <li><b>Element 1</b> is a TextField and contains the <b>value</b> of the
	 * attribute
	 * <li><b>Element 2</b> is a Button that allows interaction for <b>removal</b>.
	 * </ul>
	 * 
	 * @param container : The VBox containing the elements
	 * @param ob        : The ObservableList to remove values from
	 * @param key       : The name of the attribute
	 * @param value     : The value of the attribute
	 * @see #toggleConfirmation()
	 * @author ESTIENNE Alban-Moussa
	 */
	public void updateContainer(int containerIndex, ObservableMap<String, Integer> ob, String key, Integer value){
		VBox container = containersList.get(containerIndex);
		Pane clonedPane = PaneCloner.cloneSettingPane(msv.biComponentSettingPane);
		clonedPane.setId(key);
		container.getChildren().add(clonedPane);
		ObservableList<Node> elementList = clonedPane.getChildren();
        TextField keptField = (TextField) elementList.get(1);
		Node loadedElement = (Label) elementList.get(0);
		((Label) loadedElement).setText(key);
		loadedElement = (TextField) elementList.get(1);
		((TextField) loadedElement).setText(value.toString());

		ChangeListener<Boolean> frequencyFocusListener = new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (!newValue) {
					boolean valid = sc.requestSettingChange("Data treatment", "alerts", key + ":" + keptField.getText(), true);
					if (!valid){
						msv.frequencyField.setText("");
					}
				}
			}
		};

		((TextField) loadedElement).focusedProperty().addListener(frequencyFocusListener);

		loadedElement = (Button) elementList.get(2);
		((Button) loadedElement).setOnAction(event -> toggleConfirmation(event, ob, key));
	}

	/**
	 * <p>
	 * Generate a new pane based on the value and list being updated.
	 * <p>
	 * This method follows some strict rules that are defined inside the
	 * very conception of the interface. Rules are :
	 * <ul>
	 * <li><b>Element 0</b> is a Label and contains the <b>name</b> of the attribute
	 * <li><b>Element 1</b> is a Button that allows interaction for <b>removal</b>.
	 * </ul>
	 * 
	 * @param container : The VBox containing the elements
	 * @param ob        : The ObservableList to remove values from
	 * @param key       : The name of the attribute
	 * @param value     : The value of the attribute
	 * @see #toggleConfirmation()
	 * @author ESTIENNE Alban-Moussa
	 */
	public void updateContainer(int containersIndex, ObservableList<String> ol, String key){
		VBox container = containersList.get(containersIndex);
		Pane clonedPane = PaneCloner.cloneSettingPane(msv.monoComponentSettingPane);
		clonedPane.setId(key);
		container.getChildren().add(clonedPane);
		ObservableList<Node> elementList = clonedPane.getChildren();
		Node loadedElement = (Label) elementList.get(0);
		((Label) loadedElement).setText(key);
		loadedElement = (Button) elementList.get(1);
		((Button) loadedElement).setOnAction(event -> toggleConfirmation(event, ol, key));
	}

	public void removeWithId(int containersIndex, String key) {
		VBox containers = containersList.get(containersIndex);
		containers.getChildren().removeIf(n -> n.getId() == key);
	}


    /**
     * Returns the instance of the SettingsView, creates one if none exists.
     *
     * @param MainSceneView _msc : The Main Scene Controller
     * @author ESTIENNE Alban-Moussa
     */
    public static SettingsView getInstance(MainSceneController _msc) {
        if (instance == null) {
            instance = new SettingsView(_msc);
        }
        return instance;
    }

    /**
     * Switches the button style. This is used on the settings menu button so
     * that the user knows in which section he is. (Inverse the colours)
     *
     * @param Button : The Button to be switched
     * @author ESTIENNE Alban-Moussa
     */
    protected void changeButtonStyle(Button button) {
        settingButtonList.forEach((n) -> n.getStyleClass().clear());
        settingButtonList.forEach((n) -> n.getStyleClass().add(0, "unselected"));
        button.getStyleClass().set(0, "selected");
    }

    /*
	 * Switches the toggleButton style to match their value
	 * 
	 * @param ToggleButton button : The button to switch
	 * 
	 * @author ESTIENNE Alban-Moussa
	 * 
	 * Footnote : This one should be more efficient as you will not need
	 * to write a bunch of if/then to make it work, adding a button
	 * will just result in storing it in the array and plugging this function
	 * into the button (scroll down for example)
     */
    private void switchButton(ToggleButton button) {
        button.getStyleClass().clear();
        String status = button.isSelected() == true ? "on" : "off";
        button.getStyleClass().add(status);
        button.setText(status.toUpperCase());

        sc.requestSaveTopicSettings(toggleButtonList);
    }

    /*
	 * Toggles the connection page.
	 * It doesn't look pretty, i'm sorry, but since the interface is
	 * going to change very little, i just assumed it would be impactless
	 * One better way to do it would be to store all the tabs into a table
	 * and manually set all to visible false except the wanted pane.
	 * if it looks too unpractical in the future i'll change it
	 * 
	 * @author ESTIENNE Alban-Moussa
     */
    protected void showConnectionPage() {

        currentlyOpenedPane = connectionInfoPane;
        connectionInfoPane.setVisible(true);
        topicsPane.setVisible(false);
        treatmentPane.setVisible(false);

        changeButtonStyle(settingButtonList.get(0));
        HashMap<String, String> fieldDatas = sc.requestSettings("Connection Infos", false);
        msv.adressField.setText(fieldDatas.get("host"));
        msv.portField.setText(fieldDatas.get("port"));
        msv.kaField.setText(fieldDatas.get("keepalive"));
    }

    /**
     * Same as {@link #showConnectionPage()}
     */
    protected void showTopicPage() {

        currentlyOpenedPane = topicsPane;
        connectionInfoPane.setVisible(false);
        topicsPane.setVisible(true);
        treatmentPane.setVisible(false);

        changeButtonStyle(settingButtonList.get(1));
        HashMap<String, String> fieldDatas = sc.requestSettings("Topics", false);

        if (fieldDatas.get("AM107/by-room/#").equals("0")) {
            msv.am107Button.getStyleClass().clear();
            msv.am107Button.getStyleClass().add("off");
            msv.am107Button.setText("OFF");
            msv.am107Button.setSelected(false);
        }

        if (fieldDatas.get("Triphaso/by-room/#").equals("0")) {
            msv.triphasoButton.getStyleClass().clear();
            msv.triphasoButton.getStyleClass().add("off");
            msv.triphasoButton.setText("OFF");
            msv.triphasoButton.setSelected(false);
        }

        if (fieldDatas.get("solaredge/blagnac/#").equals("0")) {
            msv.solarDataButton.getStyleClass().clear();
            msv.solarDataButton.getStyleClass().add("off");
            msv.solarDataButton.setText("OFF");
            msv.solarDataButton.setSelected(false);
        }

    }

    public void changeFrequencyText(String s) {
        msv.frequencyField.setText(s);
    }

    protected void showTreatmentPage() {

        currentlyOpenedPane = treatmentPane;
        sc.setSettingsView(instance);

        connectionInfoPane.setVisible(false);
        topicsPane.setVisible(false);
        treatmentPane.setVisible(true);

        changeButtonStyle(settingButtonList.get(2));
        sc.requestSettings("Data treatment", true);

    }

    /**
     * Opens the dialogue to add an element to the Data Treatment .ini file
     * section.
     *
     * @author ESTIENNE Alban-Moussa
     */
    public void openAdditionDialogue(boolean mono, String buttonCaller) {
        sc.requestNewWindow(mono, buttonCaller);
    }

    private void toggleConfirmation(ActionEvent e, Observable ol, String key) {
        Alert a = new Alert(AlertType.CONFIRMATION);
        a.setContentText("Etes-vous certain de vouloir supprimer l'attribut " + key.toUpperCase() + "?");
        Optional<ButtonType> option = a.showAndWait();

        if (option.get().equals(ButtonType.OK)) {
            sc.requestDeletionOfSettings(ol, key);
        }

        e.consume();
    }

    protected void switchAM107() {
        switchButton(msv.am107Button);
    }

    protected void switchTriphaso() {
        switchButton(msv.triphasoButton);
    }

    protected void switchSolar() {
        switchButton(msv.solarDataButton);
    }

    protected void startConnectionTest() {
        String status = sc.requestConnectionTest();
        String[] splitedStatus = status.split("/");
        msv.connectionStateLabel.setStyle("-fx-text-fill: " + splitedStatus[1] + ";");
        msv.connectionStateLabel.setText(splitedStatus[2]);
    }

}
