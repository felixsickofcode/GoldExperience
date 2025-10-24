package vnu.uet.goldexperience.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import vnu.uet.goldexperience.manager.SceneManager;

public class MenuController {
    private SceneManager sceneManager;

    @FXML
    private Button btnChooseStage;

    @FXML
    private Button btnTutorial;

    @FXML
    private Button btnMoveToSetting;

    @FXML
    private Button btnSound;
    private boolean soundOn = true;

    @FXML
    private Button btnBack;

    @FXML
    private Button btnStoryMode;

    @FXML
    private Button btn2PlayerMode;

    @FXML
    private Button btnExit;

    @FXML
    private Label titleLabel;

    // Set scene manager
    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @FXML
    public void initialize() {
        System.out.println("Menu initialized");
    }

    // HandleSound clicked
    @FXML
    private void handleSound(ActionEvent event) {
        System.out.println("Sound clicked");
        soundOn = !soundOn;
        if (soundOn) {
            btnSound.setText("Sound ON");
        }
        else {
            btnSound.setText("Sound OFF");
        }
    }

    // HandleSound clicked
    @FXML
    private void handleStoryMode(ActionEvent event) {
        System.out.println("Story Mode clicked");
        if (sceneManager != null) {
            sceneManager.switchTo("chapter");
        }
    }

    // HandleMoveToSetting clicked
    @FXML
    private void handleMoveToSetting(ActionEvent event) {
        System.out.println("Move To Setting clicked");
        if (sceneManager != null) {
            sceneManager.switchTo("setting");
        }
    }

    // HandleSound clicked
    @FXML
    private void handle2PlayerMode(ActionEvent event) {
        System.out.println("2 Player Mode clicked");
    }

    // When click on tutorial
    @FXML
    private void handleTutorial(ActionEvent event) {
        System.out.println("Tutorial clicked");
        if (sceneManager != null) {
            sceneManager.switchTo("tutorial");
        }
    }

    // Handle back to menu
    @FXML
    private void handleBackToMenu(ActionEvent event) {
        System.out.println("Back to menu clicked");
        if (sceneManager != null) {
            sceneManager.switchTo("menu");
        }
    }

    // Handle back to setting
    @FXML
    private void handleBackToSetting(ActionEvent event) {
        System.out.println("Back to setting clicked");
        if (sceneManager != null) {
            sceneManager.switchTo("setting");
        }
    }

    // When click on exit
    @FXML
    private void handleExit(ActionEvent event) {
        System.out.println("Exit clicked");
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }
}
