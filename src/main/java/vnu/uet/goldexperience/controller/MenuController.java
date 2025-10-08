package vnu.uet.goldexperience.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import vnu.uet.goldexperience.SceneManager;

public class MenuController {
    private SceneManager sceneManager;

    @FXML
    private Button btnChooseStage;

    @FXML
    private Button btnTutorial;

    @FXML
    private Button btnBack;

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

    // When click on choose stage - Load game scene
    @FXML
    private void handleChooseStage(ActionEvent event) {
        System.out.println("Choose Stage clicked");
        if (sceneManager != null) {
            sceneManager.switchTo("game");
        }
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
    private void handleBack(ActionEvent event) {
        System.out.println("Back to menu clicked");
        if (sceneManager != null) {
            sceneManager.switchTo("menu");
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
