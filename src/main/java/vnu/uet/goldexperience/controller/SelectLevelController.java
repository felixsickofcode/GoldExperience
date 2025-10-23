package vnu.uet.goldexperience.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import vnu.uet.goldexperience.manager.SceneManager;

public class SelectLevelController {
    private SceneManager sceneManager;
    private int selectedLevel = 0;

    @FXML
    private Button btnBack;

    @FXML
    private Button btnLevel1;

    @FXML
    private Button btnLevel2;

    @FXML
    private Button btnLevel3;

    @FXML
    private Button btnLevel4;

    @FXML
    private Button btnLevel5;

    // Set scene manager
    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    // Get selected level
    public int getLevel() {
        return selectedLevel;
    }

    @FXML
    public void initialize() {
        System.out.println("SelectLevel initialized");
    }

    // Handle Level 1 clicked
    @FXML
    private void handleLevel1(ActionEvent event) {
        System.out.println("Level 1 clicked");
        selectedLevel = 1;
        if (sceneManager != null) {
            sceneManager.switchTo("game");
            GameController controller = (GameController) sceneManager.getController("game");
            controller.startGame();
        }
    }

    // Handle Level 2 clicked
    @FXML
    private void handleLevel2(ActionEvent event) {
        System.out.println("Level 2 clicked");
        selectedLevel = 2;
        if (sceneManager != null) {
            sceneManager.switchTo("game");
            GameController controller = (GameController) sceneManager.getController("game");
            controller.startGame();
        }
    }

    // Handle Level 3 clicked
    @FXML
    private void handleLevel3(ActionEvent event) {
        System.out.println("Level 3 clicked");
        selectedLevel = 3;
        if (sceneManager != null) {
            sceneManager.switchTo("game");
            GameController controller = (GameController) sceneManager.getController("game");
            controller.startGame();
        }
    }

    // Handle Level 4 clicked
    @FXML
    private void handleLevel4(ActionEvent event) {
        System.out.println("Level 4 clicked");
        selectedLevel = 4;
        if (sceneManager != null) {
            sceneManager.switchTo("game");
            GameController controller = (GameController) sceneManager.getController("game");
            controller.startGame();
        }
    }

    // Handle Level 5 clicked
    @FXML
    private void handleLevel5(ActionEvent event) {
        System.out.println("Level 5 clicked");
        selectedLevel = 5;
        if (sceneManager != null) {
            sceneManager.switchTo("game");
            GameController controller = (GameController) sceneManager.getController("game");
            controller.startGame();
        }
    }

    // Handle back to menu
    @FXML
    private void handleBackToMenu(ActionEvent event) {
        System.out.println("Back to menu clicked");
        selectedLevel = 0;
        if (sceneManager != null) {
            sceneManager.switchTo("menu");
        }
    }
}