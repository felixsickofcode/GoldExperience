package vnu.uet.goldexperience.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import vnu.uet.goldexperience.manager.AssetsManager;
import vnu.uet.goldexperience.manager.SceneManager;
import vnu.uet.goldexperience.manager.SoundManager;

public class SettingController {
    private SceneManager sceneManager;

    @FXML
    private Button btnSound;
    private boolean soundOn = true;

    @FXML
    private Button btnTutorial;

    @FXML
    private Button btnExit;

    @FXML
    private Button btnQuitSetting;

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @FXML
    public void initialize() {
        System.out.println("Setting initialized");
    }

    @FXML
    private void handleSound(ActionEvent event) {
        SoundManager.playClickSound();
        System.out.println("Sound clicked");
        soundOn = !soundOn;
        if (soundOn) {
            btnSound.setText("Sound ON");
        } else {
            btnSound.setText("Sound OFF");
        }
    }

    @FXML
    private void handleTutorial(ActionEvent event) {
        SoundManager.playClickSound();
        System.out.println("Tutorial clicked");
        if (sceneManager != null) {
            sceneManager.switchTo("tutorial");
        }
    }

    @FXML
    private void handleExit(ActionEvent event) {
        SoundManager.playClickSound();
        System.out.println("Exit clicked");
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleBackToMenu(ActionEvent event) {
        SoundManager.playClickSound();
        System.out.println("Back to menu clicked");
        if (sceneManager != null) {
            sceneManager.switchTo("menu");
        }
    }
}