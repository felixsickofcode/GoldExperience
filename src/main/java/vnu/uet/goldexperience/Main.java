package vnu.uet.goldexperience;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import vnu.uet.goldexperience.controller.MenuController;
import vnu.uet.goldexperience.manager.AssetsManager;
import vnu.uet.goldexperience.manager.SceneManager;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        StackPane root = new StackPane();
        SceneManager sceneManager = new SceneManager(root);
        //asset
        AssetsManager.loadAssets();

        // preload FXML
        sceneManager.preloadScene("menu", "/fxml/menu-view.fxml");
        sceneManager.preloadScene("tutorial", "/fxml/tutorial-view.fxml");
        sceneManager.preloadScene("game", "/fxml/game.fxml");


        FXMLLoader menuLoader = sceneManager.getScreens().get("menu");
        MenuController menuController = menuLoader.getController();
        menuController.setSceneManager(sceneManager);

        FXMLLoader tutorialLoader = sceneManager.getScreens().get("tutorial");
        MenuController tutorialController = tutorialLoader.getController();
        tutorialController.setSceneManager(sceneManager);

        sceneManager.switchTo("menu");

        Scene scene = new Scene(root, 1280, 720);
        scene.getStylesheets().add(getClass().getResource("/fxml/style.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
