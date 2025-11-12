package vnu.uet.goldexperience.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import vnu.uet.goldexperience.manager.GameDataManager;
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
        double currentVolume = GameDataManager.getGlobalData().getVolume();
        musicVolumeSlider.setValue(currentVolume * 100);
        musicVolumeLabel.setText(String.format("%.0f%%", currentVolume * 100));
        // Add listener to update label when slider value changes
        musicVolumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double volume = newVal.doubleValue() / 100;
            musicVolumeLabel.setText(String.format("%.0f%%", newVal.doubleValue()));
            GameDataManager.setVolume(volume);
            SoundManager.updateAllVolumes();
        });
    }

    @FXML
    private void handleMusicVolume(MouseEvent event) {
//       da dung listener
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