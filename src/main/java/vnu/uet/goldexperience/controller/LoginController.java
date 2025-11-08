package vnu.uet.goldexperience.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import vnu.uet.goldexperience.manager.SceneManager;
import vnu.uet.goldexperience.view.LoginUI;

public class LoginController {

    @FXML
    private StackPane loginContainer;

    private SceneManager sceneManager;
    private LoginUI loginUI;

    @FXML
    public void initialize() {
        // LoginUI will be set up after sceneManager is injected
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
        setupLoginUI();
    }

    private void setupLoginUI() {
        // Create LoginUI and embed it into the container
        loginUI = new LoginUI(null); // Pass null since we're not using Stage directly

        // Get the root pane from LoginUI and add to container
        loginContainer.getChildren().add(loginUI.getRootPane());

        // Start animation
        loginUI.startAnimation();

        // Set callback for successful login
        loginUI.setOnLoginSuccess((playerName) -> {
            System.out.println("Player logged in: " + playerName);
            loginUI.stopAnimation();
            // Switch to menu
            sceneManager.switchTo("menu");
        });
    }
}