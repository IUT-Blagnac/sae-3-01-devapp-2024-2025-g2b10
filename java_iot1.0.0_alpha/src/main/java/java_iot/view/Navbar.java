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

	// References the MainSceneController that called this object. Any stored MSC is
	// definitive
	private MainSceneController msc;
	private static Navbar instance;

	// Legacy settings before MainSceneController was migrated to the same package
	// The buttons of MSC being set to protected, these are obsolete
	private Pane settingPane;
	private Pane dashPane;
	@SuppressWarnings("unused")
	private Pane roomPane;
	private Pane panelPane;
	private Pane navPane;

	/**
	 * Private constructor to initialize the singleton
	 * Nothing much to see here, sets the settingPane and graphPane to null, and
	 * await for input
	 */
	private Navbar(MainSceneController _msc) {
		settingPane = null;
		dashPane = null;
		roomPane = null;
		panelPane = null;
		msc = _msc;
	}

	/**
	 * Return the instance of Navbar existing, creates one if none
	 * 
	 * @param MainSceneController _msc : The one and only MSC that handles the app
	 */
	public static Navbar getInstance(MainSceneController _msc) {
		if (instance == null) {
			instance = new Navbar(_msc);
		}
		return instance;
	}

	/**
	 * Checks if all the required panes are set.
	 * 
	 * @return The state of the main panes
	 * @author ESTIENNE Alban-Moussa
	 */
	private boolean checkPanes() {
		if (settingPane == null) {
			return false;
		}
		if (dashPane == null) {
			return false;
		}
		if (roomPane == null) {
			return false;
		}
		if (panelPane == null) {
			return false;
		}
		return true;
	}

	/*
	 * Yeah, just sets the settingPane
	 * won't write that I wrote it, seems pointless
	 */
	public void setSettingPane(Pane sP) {
		settingPane = sP;
	}

	/**
	 * Same as above
	 * 
	 * @param Pane dP
	 */
	public void setDashPane(Pane dP) {
		dashPane = dP;
	}

	/**
	 * room pane setter
	 * 
	 * @param Pane rP
	 */
	public void setRoomPane(Pane rP) {
		roomPane = rP;
	}

	/**
	 * panel pane setter
	 * 
	 * @param Pane pP
	 */
	public void setPanelPane(Pane pP) {
		panelPane = pP;
	}

	/**
	 * Brings forth ByroomPopup, and disable the settingPane
	 * The check condition is legacy, as it should always provide TRUE
	 * 
	 * @author GIARD--PELLAT Jules
	 */
	public boolean showRoomPane() {
		boolean validity = checkPanes();
		if (validity) {
			dashPane.setVisible(false);
			panelPane.setVisible(false);
			roomPane.setVisible(true);
			settingPane.setVisible(false);
		}
		return validity;
	}

	/**
	 * Brings forth BypanelPopup, and disable the settingPane
	 * The check condition is legacy, as it should always provide TRUE
	 * 
	 * @author GIARD--PELLAT Jules
	 */
	public boolean showPanelPane() {
		boolean validity = checkPanes();
		if (validity) {
			dashPane.setVisible(false);
			panelPane.setVisible(true);
			roomPane.setVisible(false);
			settingPane.setVisible(false);
			Graphique.getInstance(msc).showPanel();
		}
		return validity;
	}

	/**
	 * Toggle the dashPane, and disable the settingPane
	 * The check condition is legacy, as it should always provide TRUE
	 * 
	 * @authors ESTIENNE Alban-Moussa GIARD--PELLAT Jules
	 */
	public boolean showDashPane() {
		boolean validity = checkPanes();
		if (validity) {
			dashPane.setVisible(true);
			panelPane.setVisible(false);
			roomPane.setVisible(false);
			settingPane.setVisible(false);
			Graphique.getInstance(msc).showDash();
		}
		return validity;
	}

	/**
	 * Toggle the settingPane, and disable the graphPane
	 * The check condition is legacy, as it should always provide TRUE
	 * 
	 * @author ESTIENNE Alban-Moussa
	 */
	public boolean showSettingPane() {
		boolean validity = checkPanes();
		if (validity) {
			dashPane.setVisible(false);
			panelPane.setVisible(false);
			roomPane.setVisible(false);
			settingPane.setVisible(true);
			SettingsView.getInstance(msc).showConnectionPage();
		}
		return validity;
	}
}