package vnu.uet.goldexperience.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import vnu.uet.goldexperience.manager.AssetsManager;
import vnu.uet.goldexperience.manager.SceneManager;

public class TutorialController {
    private SceneManager sceneManager;

    @FXML
    private Button btnBack;

    @FXML
    private ImageView characterImage;

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @FXML
    public void initialize() {
        System.out.println("Tutorial initialized");
    }

    @FXML
    private void handleBackToSetting(ActionEvent event) {
        AssetsManager.playClickSound();
        System.out.println("Back to setting clicked");
        if (sceneManager != null) {
            sceneManager.switchTo("setting");
        }
    }
}