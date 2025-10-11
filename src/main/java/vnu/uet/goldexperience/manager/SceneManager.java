package vnu.uet.goldexperience.manager;

import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import javafx.scene.Node;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class SceneManager {
    private final StackPane root;

    public Map<String, FXMLLoader> getScreens() {
        return screens;
    }

    protected final Map<String, FXMLLoader> screens;
    private final Map<String, Object> controllers = new HashMap<>();


    //pull all screens
    public SceneManager(StackPane root) {
        this.root = root;
        screens = new HashMap<>();
    }

    //preload screens
    public void PreloadScene(String name, String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        loader.load();
        screens.put(name, loader);
        controllers.put(name, loader.getController());
    }

    //tranition effect
    public void switchTo(String name){
        FXMLLoader loader = screens.get(name);
        if (loader == null) {
            System.err.println("Scene not found: " + name);
            return;
        }

        Parent newScreen = loader.getRoot();
        if (root.getChildren().isEmpty()){
            root.getChildren().add(newScreen);
            return;
        }

        Node current = root.getChildren().get(0);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(400), current);
        fadeOut.setToValue(0);

        fadeOut.setOnFinished(e -> {
            root.getChildren().setAll(newScreen);
            newScreen.setOpacity(0);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(400), newScreen);
            fadeIn.setToValue(1);
            fadeIn.play();
        });
        fadeOut.play();
    }
    public Object getController(String name) {
        return controllers.get(name);
    }
}
