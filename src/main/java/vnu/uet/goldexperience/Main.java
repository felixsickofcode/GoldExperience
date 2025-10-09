package vnu.uet.goldexperience;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import vnu.uet.goldexperience.controller.MenuController;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        StackPane root = new StackPane();
        SceneManager sceneManager = new SceneManager(root);

        // preload FXML
        sceneManager.PreloadScene("menu", "/fxml/menu-view.fxml");
        sceneManager.PreloadScene("tutorial", "/fxml/tutorial-view.fxml");
        sceneManager.PreloadScene("game", "/fxml/game.fxml");

        FXMLLoader menuLoader = sceneManager.screens.get("menu");
        MenuController menuController = menuLoader.getController();
        menuController.setSceneManager(sceneManager);

        FXMLLoader tutorialLoader = sceneManager.screens.get("tutorial");
        MenuController tutorialController = tutorialLoader.getController();
        tutorialController.setSceneManager(sceneManager);

        sceneManager.switchTo("menu");

        Scene scene = new Scene(root,1280,720);
        scene.getStylesheets().add(getClass().getResource("/fxml/style.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
