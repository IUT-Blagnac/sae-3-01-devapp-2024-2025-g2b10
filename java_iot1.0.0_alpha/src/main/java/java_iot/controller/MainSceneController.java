package java_iot.controller;

import java_iot.App;
import java_iot.view.MainSceneView;

public class MainSceneController {

    private App app;
    private MainSceneView msv;

    public MainSceneController(MainSceneView _msv){
        msv = _msv;
    }

    public void setApp(App _app){
        app = _app;
    }

    public void requestNewAddition(boolean mono){
		app.showAdditionMenu(mono);
	}

    public MainSceneView getMainSceneView(){
        return msv;
    }
    
}
