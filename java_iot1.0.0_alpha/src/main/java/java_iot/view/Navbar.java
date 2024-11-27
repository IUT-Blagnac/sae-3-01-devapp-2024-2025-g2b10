package java_iot.view;

import java.net.URL;
import java.util.ResourceBundle;

import java_iot.Main;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

class Navbar {

	private MainSceneController msc;
	private static Navbar instance;
	private Pane settingPane;
	private Pane graphPane;
	Pane navPane;

	private Navbar(MainSceneController _msc){
		settingPane = null;
		graphPane = null;
		msc = _msc;

	}

	public static Navbar getInstance(MainSceneController _msc){
		if (instance == null){
			instance = new Navbar(_msc);
		}
		return instance;
	}

	private boolean checkPanes(){
		if (settingPane == null){
			return false;
		}
		if (graphPane == null){
			return false;
		}
		return true;
	}

	public void setSettingPane(Pane sP){
		settingPane = sP;
	}

	public void setGraphPane(Pane gP){
		graphPane = gP;
	}

	public boolean showGraphPane(){
		boolean validity = checkPanes();
		if (validity){
			graphPane.setVisible(true);
			settingPane.setVisible(false);
		}
		return validity;
	}

	public boolean showSettingPane(){
		boolean validity = checkPanes();
		if (validity){
			graphPane.setVisible(false);
			settingPane.setVisible(true);
			SettingsView.getInstance(msc).showConnectionPage();
		}
		return validity;
	}
}
