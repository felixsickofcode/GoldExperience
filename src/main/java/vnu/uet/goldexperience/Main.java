package vnu.uet.goldexperience;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import vnu.uet.goldexperience.controller.*;
import vnu.uet.goldexperience.manager.AssetsManager;
import vnu.uet.goldexperience.manager.SceneManager;
import vnu.uet.goldexperience.manager.SoundManager;

import java.awt.event.KeyEvent;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Font.loadFont(getClass().getResourceAsStream("/font/cyber22.ttf"), 24);
        Font font22 = Font.loadFont(getClass().getResourceAsStream("/font/cyber22.ttf"), 24);
        Font.loadFont(getClass().getResourceAsStream("/font/cyber32.ttf"), 24);
        Font font = Font.loadFont(getClass().getResourceAsStream("/font/cyber32.ttf"), 24);
        Font technoFont = Font.loadFont(getClass().getResourceAsStream("/font/TechnoCharmDemoVersionRegular-ALaZm.otf"), 32);
        Font Xirod = Font.loadFont(getClass().getResourceAsStream("/font/Xirod.otf"), 32);

        System.out.println("Font loaded: " + font);
        System.out.println("Font name: " + font.getName());
        System.out.println("Font family: " + font.getFamily());
        System.out.println("Font loaded: " + font22);
        System.out.println("Font name: " + font22.getName());
        System.out.println("Font family: " + font22.getFamily());

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: black;");

        SceneManager sceneManager = new SceneManager(root);

        // Load assets
        AssetsManager.loadAssets();
        // load sounds
        SoundManager.loadSound();

        // Preload FXML
        sceneManager.PreloadScene("login", "/fxml/login.fxml");
        sceneManager.PreloadScene("menu", "/fxml/menu-view.fxml");
        sceneManager.PreloadScene("tutorial", "/fxml/tutorial-view.fxml");
        sceneManager.PreloadScene("game", "/fxml/game.fxml");
        sceneManager.PreloadScene("setting", "/fxml/setting.fxml");
        sceneManager.PreloadScene("chapter", "/fxml/chapter.fxml");
        sceneManager.PreloadScene("level", "/fxml/level.fxml");
        sceneManager.PreloadScene("shop", "/fxml/shop.fxml");

        FXMLLoader loginLoader = sceneManager.getScreens().get("login");
        LoginController loginController = loginLoader.getController();
        loginController.setSceneManager(sceneManager);

        FXMLLoader menuLoader = sceneManager.getScreens().get("menu");
        MenuController menuController = menuLoader.getController();
        menuController.setSceneManager(sceneManager);

        FXMLLoader tutorialLoader = sceneManager.getScreens().get("tutorial");
        TutorialController tutorialController = tutorialLoader.getController();
        tutorialController.setSceneManager(sceneManager);

        FXMLLoader settingLoader = sceneManager.getScreens().get("setting");
        SettingController settingController = settingLoader.getController();
        settingController.setSceneManager(sceneManager);

        FXMLLoader chapterLoader = sceneManager.getScreens().get("chapter");
        SelectChapterController chapterController = chapterLoader.getController();
        chapterController.setSceneManager(sceneManager);

        FXMLLoader levelLoader = sceneManager.getScreens().get("level");
        SelectLevelController levelController = levelLoader.getController();
        levelController.setSceneManager(sceneManager);

        FXMLLoader shopLoader = sceneManager.getScreens().get("shop");
        ShopUIController shopController = shopLoader.getController();
        shopController.setSceneManager(sceneManager);


        Scene scene = new Scene(root, 1280, 720);
        scene.getStylesheets().add(getClass().getResource("/css/menu.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/css/login.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/css/chapter-select.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/css/level-select.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/css/shop.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/css/setting.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/css/tutorial.css").toExternalForm());


        stage.setScene(scene);
        stage.setTitle("Gold Experience");
        stage.show();

        sceneManager.switchTo("menu");
    }

    @Override
    public void stop() throws Exception {
        // dọn luồng trước khi tắt game
        SoundManager.shutdown();

        // này đóng giống hồi làm SDL2
        Platform.exit();
        System.exit(0);

        // đóng từ Application trên
        super.stop();
    }

    public static void main(String[] args) {
        launch();
    }
}