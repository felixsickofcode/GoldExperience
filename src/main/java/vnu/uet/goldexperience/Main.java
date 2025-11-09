package vnu.uet.goldexperience;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import vnu.uet.goldexperience.controller.LoginController;
import vnu.uet.goldexperience.controller.MenuController;
import vnu.uet.goldexperience.controller.SettingController;
import vnu.uet.goldexperience.controller.TutorialController;
import vnu.uet.goldexperience.controller.SelectChapterController;
import vnu.uet.goldexperience.controller.SelectLevelController;
import vnu.uet.goldexperience.manager.AssetsManager;
import vnu.uet.goldexperience.manager.SceneManager;

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

        // Preload FXML (including login)
        sceneManager.PreloadScene("login", "/fxml/login.fxml");
        sceneManager.PreloadScene("menu", "/fxml/menu-view.fxml");
        sceneManager.PreloadScene("tutorial", "/fxml/tutorial-view.fxml");
        sceneManager.PreloadScene("game", "/fxml/game.fxml");
        sceneManager.PreloadScene("setting", "/fxml/setting.fxml");
        sceneManager.PreloadScene("chapter", "/fxml/chapter.fxml");
        sceneManager.PreloadScene("level", "/fxml/level.fxml");
        //sceneManager.PreloadScene("shop", "/fxml/shop.fxml");

        // Setup login controller
        FXMLLoader loginLoader = sceneManager.getScreens().get("login");
        LoginController loginController = loginLoader.getController();
        loginController.setSceneManager(sceneManager);

        // Setup menu controller (MenuController - chỉ cho menu)
        FXMLLoader menuLoader = sceneManager.getScreens().get("menu");
        MenuController menuController = menuLoader.getController();
        menuController.setSceneManager(sceneManager);

        // Setup tutorial controller (TutorialController - riêng biệt)
        FXMLLoader tutorialLoader = sceneManager.getScreens().get("tutorial");
        TutorialController tutorialController = tutorialLoader.getController();
        tutorialController.setSceneManager(sceneManager);

        // Setup setting controller (SettingController - riêng biệt)
        FXMLLoader settingLoader = sceneManager.getScreens().get("setting");
        SettingController settingController = settingLoader.getController();
        settingController.setSceneManager(sceneManager);

        // Setup chapter controller
        FXMLLoader chapterLoader = sceneManager.getScreens().get("chapter");
        SelectChapterController chapterController = chapterLoader.getController();
        chapterController.setSceneManager(sceneManager);

        // Setup level controller
        FXMLLoader levelLoader = sceneManager.getScreens().get("level");
        SelectLevelController levelController = levelLoader.getController();
        levelController.setSceneManager(sceneManager);

        //FXMLLoader levelLoader = sceneManager.getScreens().get("shop");
        //ShopController shopController = shop.getController();
        //shopController.setSceneManager(sceneManager);


        Scene scene = new Scene(root, 1280, 720);
        scene.getStylesheets().add(getClass().getResource("/fxml/menu.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/fxml/style.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/fxml/login.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Gold Experience");
//        stage.setFullScreen(true);
//        stage.setFullScreenExitHint("Nhấn F11 để thoát toàn màn hình");
        stage.show();

        // Start with login screen
        sceneManager.switchTo("login");
        System.out.println("R:" + root.getWidth());
    }

    public static void main(String[] args) {
        launch();
    }
}