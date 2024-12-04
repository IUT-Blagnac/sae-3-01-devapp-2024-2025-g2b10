package java_iot.controller;

import java_iot.App;
import java_iot.view.MainSceneView;

public class MainSceneController {

    private App app;
    private MainSceneView msv;
    private static MainSceneController instance;

    private MainSceneController() {

    }

    public void setMainSceneView(MainSceneView _msv) {
        msv = _msv;
    }

    public void setApp(App _app) {
        app = _app;
    }

    public void requestNewAddition(boolean mono, String callerButton) {
        app.showAdditionMenu(mono, callerButton);
    }

    public static MainSceneController getInstance() {
        if (instance == null) {
            instance = new MainSceneController();
        }
        return instance;
    }

    public MainSceneView getMainSceneView() {
        return msv;
    }

}
