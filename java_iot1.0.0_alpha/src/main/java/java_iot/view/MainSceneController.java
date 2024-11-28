package java_iot.view;

import java.net.URL;
import java.util.ResourceBundle;

import java_iot.model.Settings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class MainSceneController implements Initializable{

	private Navbar navigationBar;
	private SettingsView settings;

	/*
	 * FXML VARIABLES
	 */

	@FXML
	private Button settingButton;
	@FXML
	private Button dashboardButton;
	@FXML
	private Button roomButton;
	@FXML
	private Button solarButton;
	@FXML
	private Button initializeButton;
	@FXML
	private Pane settingPane;
	@FXML
	private Pane graphPane;
	@FXML
	protected Pane connectionPane;
	@FXML
	protected Pane topicPane;
	@FXML
	protected Pane treatmentPane;

	@FXML
	protected Button connectionButton;
	@FXML
	protected Button topicButton;
	@FXML
	protected Button treatmentButton;
	@FXML
	protected Button connectionTestButton;
	@FXML
	protected ToggleButton am107Button;
	@FXML
	protected ToggleButton triphasoButton;
	@FXML
	protected ToggleButton solarDataButton;

	@FXML
	protected TextField adressField;
	@FXML
	protected TextField portField;
	@FXML
	protected TextField kaField;
	@FXML
	protected TextField frequencyField;

	@FXML
	protected VBox alertContainer;
	@FXML
	protected VBox keptValueContainer;
	@FXML
	protected VBox listenedRoomContainer;

	@FXML
	protected ScrollPane alertSp;
	@FXML
	protected ScrollPane keptValueSp;
	@FXML
	protected ScrollPane listenedRoomSp;

	@FXML
	protected Pane biComponentSettingPane;
	@FXML
	protected Pane monoComponentSettingPane;

	@FXML
	protected Label connectionStateLabel;



	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		navigationBar = Navbar.getInstance(this);
		navigationBar.setSettingPane(settingPane);
		navigationBar.setGraphPane(graphPane);

		settings = SettingsView.getInstance(this);

		navigationBar.showGraphPane();
	}

	/*
	 * FXML FUNCTIONS
	 */
	@FXML
	 private void toggleSettings(){
		navigationBar.showSettingPane();
	 }

	 @FXML
	 private void testConnection(){
		settings.startConnectionTest();
	 }

	 @FXML
	 private void toggleGraph(){
		navigationBar.showGraphPane();
	 }

	 @FXML
	 private void connectionButton(){
		settings.showConnectionPage();
	 }

	 @FXML
	 private void topicButton(){
		settings.showTopicPage();
	 }

	 @FXML
	 private void treatmentButton(){
		settings.showTreatmentPage();
	 }

	 @FXML
	 private void switchAM107(){
		settings.switchAM107();
	 }

	 @FXML
	 private void switchTriphaso(){
		settings.switchTriphaso();
	 }

	 @FXML
	 private void switchSolar(){
		settings.switchSolar();
	 }

}
