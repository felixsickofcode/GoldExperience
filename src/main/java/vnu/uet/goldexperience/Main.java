package vnu.uet.goldexperience;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import vnu.uet.goldexperience.controller.MenuController;
import vnu.uet.goldexperience.controller.SelectStageController;
import vnu.uet.goldexperience.manager.AssetsManager;
import vnu.uet.goldexperience.manager.SceneManager;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: black;");

        SceneManager sceneManager = new SceneManager(root);

        // Load assets
        AssetsManager.loadAssets();

        // Preload FXML
        sceneManager.PreloadScene("menu", "/fxml/menu-view.fxml");
        sceneManager.PreloadScene("tutorial", "/fxml/tutorial-view.fxml");
        sceneManager.PreloadScene("game", "/fxml/game.fxml");
        sceneManager.PreloadScene("setting", "/fxml/setting.fxml");
        sceneManager.PreloadScene("stage-select", "/fxml/stage-select.fxml");

        // Setup controllers
        FXMLLoader menuLoader = sceneManager.getScreens().get("menu");
        MenuController menuController = menuLoader.getController();
        menuController.setSceneManager(sceneManager);

        FXMLLoader tutorialLoader = sceneManager.getScreens().get("tutorial");
        MenuController tutorialController = tutorialLoader.getController();
        tutorialController.setSceneManager(sceneManager);

        FXMLLoader optionLoader = sceneManager.getScreens().get("setting");
        MenuController optionController = optionLoader.getController();
        optionController.setSceneManager(sceneManager);

        FXMLLoader stageSelectLoader = sceneManager.getScreens().get("stage-select");
        SelectStageController stageSelectController = stageSelectLoader.getController();
        stageSelectController.setSceneManager(sceneManager);

        sceneManager.switchTo("menu");

        Scene scene = new Scene(root, 1280, 720);
        scene.getStylesheets().add(getClass().getResource("/fxml/style.css").toExternalForm());

        stage.setScene(scene);
//        stage.setFullScreen(true);
//        stage.setFullScreenExitHint("Nhấn F11 để thoát toàn màn hình");
        stage.show();
        System.out.println("R:" + root.getWidth());
//        double screenWidth = Screen.getPrimary().getBounds().getWidth();
//        double screenHeight = Screen.getPrimary().getBounds().getHeight();
//        double scaleX = screenWidth / 1280.0;
//        double scaleY = screenHeight / 720.0;
//        double scale = Math.min(scaleX, scaleY);
//        root.setScaleX(scale);
//        root.setScaleY(scale);
    }

    public static void main(String[] args) {
        launch();
    }
}