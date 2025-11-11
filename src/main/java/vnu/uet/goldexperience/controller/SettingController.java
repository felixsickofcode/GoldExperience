package vnu.uet.goldexperience.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import vnu.uet.goldexperience.manager.SceneManager;
import vnu.uet.goldexperience.manager.SoundManager;

public class SettingController {
    private SceneManager sceneManager;

    @FXML
    private Button btnTutorial;

    @FXML
    private Button btnExit;

    @FXML
    private Button btnQuitSetting;

    @FXML
    private Slider musicVolumeSlider;

    @FXML
    private Label musicVolumeLabel;

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @FXML
    public void initialize() {
        System.out.println("Setting initialized");

        // Add listener to update label when slider value changes
        musicVolumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            musicVolumeLabel.setText(String.format("%.0f%%", newVal.doubleValue()));
        });
    }

    @FXML
    private void handleMusicVolume(MouseEvent event) {
        double volume = musicVolumeSlider.getValue();
        System.out.println("Music volume set to: " + volume);
        // TODO: Apply music volume to SoundManager
        // SoundManager.setMusicVolume(volume / 100.0);
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