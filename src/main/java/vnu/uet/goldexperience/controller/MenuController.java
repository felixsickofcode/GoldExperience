package vnu.uet.goldexperience.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import vnu.uet.goldexperience.manager.SceneManager;
import vnu.uet.goldexperience.view.MenuEffect;

public class MenuController {
    private SceneManager sceneManager;

    @FXML
    private VBox menuContainer;

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

        // Replace standard buttons with animated buttons if VBox exists
        if (menuContainer != null) {
            replaceWithAnimatedButtons();
        }
    }

    private void replaceWithAnimatedButtons() {
        // Clear existing buttons
        menuContainer.getChildren().clear();

        // Create animated buttons
        MenuEffect animBtn1 = new MenuEffect("STORY MODE", 340, 70);
        MenuEffect animBtn2 = new MenuEffect("2 PLAYER MODE", 340, 70);
        MenuEffect animBtn3 = new MenuEffect("SETTINGS", 340, 70);

        // Set actions
        animBtn1.setOnAction(this::handleStoryMode);
        animBtn2.setOnAction(this::handle2PlayerMode);
        animBtn3.setOnAction(this::handleMoveToSetting);

        // Add to container with proper spacing (VBox already has spacing from FXML)
        menuContainer.getChildren().addAll(animBtn1, animBtn2, animBtn3);
    }

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

    // HandleStoryMode clicked
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

    // Handle2PlayerMode clicked
    @FXML
    private void handle2PlayerMode(ActionEvent event) {
        System.out.println("2 Player Mode clicked");
        if (sceneManager != null) {
            sceneManager.switchTo("game");
            GameController controller = (GameController) sceneManager.getController("game");
            controller.startGame();
            controller.setSceneManager(sceneManager);
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