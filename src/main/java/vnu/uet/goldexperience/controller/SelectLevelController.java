package vnu.uet.goldexperience.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import vnu.uet.goldexperience.manager.GameSession;
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

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    public int getLevel() {
        return selectedLevel;
    }

    @FXML
    private void handleLevel1(ActionEvent event) {
        playLevel(1);
    }

    @FXML
    private void handleLevel2(ActionEvent event) {
        playLevel(2);
    }

    @FXML
    private void handleLevel3(ActionEvent event) {
        playLevel(3);
    }

    @FXML
    private void handleLevel4(ActionEvent event) {
        playLevel(4);
    }

    @FXML
    private void handleLevel5(ActionEvent event) {
        playLevel(5);
    }

    private void playLevel(int level) {
        System.out.println("Level " + level + " clicked");

        GameSession.getInstance().setLevel(level);

        System.out.println("Loading: " + GameSession.getInstance().getLevelFileName());

        if (sceneManager != null) {
            sceneManager.switchTo("game");
            GameController controller = (GameController) sceneManager.getController("game");
            controller.startGame();
            controller.setSceneManager(sceneManager);
        }
    }

    @FXML
    private void handleBackToMenu(ActionEvent event) {
        System.out.println("Back to menu clicked");
        selectedLevel = 0;
        if (sceneManager != null) {
            sceneManager.switchTo("menu");
        }
    }
}