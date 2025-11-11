package vnu.uet.goldexperience.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import vnu.uet.goldexperience.manager.AssetsManager;
import vnu.uet.goldexperience.manager.GameSession;
import vnu.uet.goldexperience.manager.SceneManager;
import vnu.uet.goldexperience.manager.SoundManager;
import vnu.uet.goldexperience.view.MenuUI;

public class MenuController {
    private SceneManager sceneManager;
    private MenuUI menuUI;

    @FXML
    private StackPane menuContainer;

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
        setupMenuUI();
    }

    @FXML
    public void initialize() {
        System.out.println("Menu initialized");
    }

    private void setupMenuUI() {
        // Create MenuUI and embed it into the container
        menuUI = new MenuUI();

        // Get the root pane from MenuUI and add to container
        menuContainer.getChildren().add(menuUI.getRootPane());

        // Start animation
        menuUI.startAnimation();

        // Set callback for menu actions
        menuUI.setOnMenuAction((action) -> {
            System.out.println("Menu action: " + action);

            // Play click sound
            SoundManager.playClickSound();

            switch (action) {
                case "story":
                    handleStoryMode();
                    break;
                case "2player":
                    handle2PlayerMode();
                    break;
                case "shop":
                    handleShop();
                    break;
                case "settings":
                    handleSettings();
                    break;
            }
        });
    }

    private void handleStoryMode() {
        System.out.println("Story Mode clicked");
        if (sceneManager != null) {
            menuUI.stopAnimation();
            sceneManager.switchTo("chapter");
        }
    }

    private void handle2PlayerMode() {
        System.out.println("2 Player Mode clicked");
        GameSession.getInstance().setMode(GameSession.GameMode.ENDLESS);
        GameSession.getInstance().resetLives();
        GameSession session = GameSession.getInstance();
        session.setChapter(6);
        session.setLevel(6);
        System.out.println(session.getCurrentChapter() + "\n" + session.getCurrentLevel());
        if (sceneManager != null) {
            menuUI.stopAnimation();
            sceneManager.switchTo("game");
            GameController controller = (GameController) sceneManager.getController("game");
            controller.setSceneManager(sceneManager);
            controller.startGame();
        }
    }

    private void handleShop() {
        System.out.println("Shop clicked");
        if (sceneManager != null) {
            menuUI.stopAnimation();
            sceneManager.switchTo("shop");
        }
    }

    private void handleSettings() {
        System.out.println("Settings clicked");
        if (sceneManager != null) {
            menuUI.stopAnimation();
            sceneManager.switchTo("setting");
        }
    }

    // Called when returning to menu from other scenes
    public void onSceneShown() {
        if (menuUI != null) {
            menuUI.startAnimation();
        }
    }
}