package java_iot.view;

import java_iot.controller.MainSceneController;
import javafx.scene.layout.Pane;

/**
 * <p>Navbar is a view class that coordinates the lateral navigation bar displayed pane and
 * the navigation between UI elements.
 * <p>Navbar is a singleton to avoid any duplication of object that could cause a desync
 * during the navigation.
 * <p>Navbar should ONLY be called by {@link java_iot.view.MainSceneView} (it is only visible to the view package anyways)
 * 
 * @author ESTIENNE Alban-Moussa
 */
class Navbar {

	// References the MainSceneController that called this object. Any stored MSC is definitive
	private MainSceneController msc;
	private static Navbar instance;

	// Legacy settings before MainSceneController was migrated to the same package
	// The buttons of MSC being set to protected, these are obsolete
	private Pane settingPane;
	private Pane graphPane;
	@SuppressWarnings("unused")
	private Pane navPane;

	/**
	 * Private constructor to initialize the singleton
	 * Nothing much to see here, sets the settingPane and graphPane to null, and await for input
	 */
	private Navbar(MainSceneController _msc){
		settingPane = null;
		graphPane = null;
		msc = _msc;

	}

	/**
	 * Return the instance of Navbar existing, creates one if none
	 * @parem MainSceneController _msc : The one and only MSC that handles the app
	 */
	public static Navbar getInstance(MainSceneController _msc){
		if (instance == null){
			instance = new Navbar(_msc);
		}
		return instance;
	}

	/**
	 * Checks if all the required panes are set.
	 * @return The state of the main panes
	 * @author ESTIENNE Alban-Moussa
	 */
	private boolean checkPanes(){
		if (settingPane == null){
			return false;
		}
		if (graphPane == null){
			return false;
		}
		return true;
	}

	/*
	 * Yeah, just sets the settingPane
	 * won't write that I wrote it, seems pointless
	 */
	public void setSettingPane(Pane sP){
		settingPane = sP;
	}

	/*
	 * Same as above
	 */
	public void setGraphPane(Pane gP){
		graphPane = gP;
	}

	/**
	 * Toggle the graphPane, and disable the settingPane
	 * The check condition is legacy, as it should always provide TRUE
	 * 
	 * @author ESTIENNE Alban-Moussa
	 */
	public boolean showGraphPane(){
		boolean validity = checkPanes();
		if (validity){
			graphPane.setVisible(true);
			settingPane.setVisible(false);
		}
		return validity;
	}

	/**
	 * Toggle the settingPane, and disable the graphPane
	 * The check condition is legacy, as it should always provide TRUE
	 * 
	 * @author ESTIENNE Alban-Moussa
	 */
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
