package java_iot.controller;

import java_iot.App;
import java_iot.view.AlertView;

public class AlertController {

    private App app;

    private AlertView av;
    private MainSceneController msc;

    public AlertController(MainSceneController _msc) {
        this.msc = _msc;

    }

    public void setAlertView(AlertView _av) {
        this.av = _av;
    }

    public void requestNewWindow(boolean mono) {
        msc.requestNewAlert(mono);
    }
}
