package vnu.uet.goldexperience.manager;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.animation.ParallelTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.AnchorPane;
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

    //transition effect for scene switching
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

    /**
     * Animate stage transition with fade and slide effect
     * @param centerStage The center stage AnchorPane to animate
     * @param movingLeft True if moving left, false if moving right
     * @param onComplete Callback to run after animation completes
     */
    public void animateStageTransition(AnchorPane centerStage, boolean movingLeft, Runnable onComplete) {
        // Create fade out animation
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), centerStage);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.3);

        // Create slide in animation
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(200), centerStage);
        if (movingLeft) {
            // Coming from right
            slideIn.setFromX(50);
        } else {
            // Coming from left
            slideIn.setFromX(-50);
        }
        slideIn.setToX(0);

        // Create fade in animation
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), centerStage);
        fadeIn.setFromValue(0.3);
        fadeIn.setToValue(1.0);

        // Combine fade out first
        fadeOut.setOnFinished(e -> {
            // Update the stage content
            if (onComplete != null) {
                onComplete.run();
            }

            // Then play fade in and slide together
            ParallelTransition fadeInSlide = new ParallelTransition(fadeIn, slideIn);
            fadeInSlide.play();
        });

        fadeOut.play();
    }

    /**
     * Animate stage transition with custom duration
     * @param centerStage The center stage AnchorPane to animate
     * @param movingLeft True if moving left, false if moving right
     * @param duration Animation duration in milliseconds
     * @param onComplete Callback to run after animation completes
     */
    public void animateStageTransition(AnchorPane centerStage, boolean movingLeft, double duration, Runnable onComplete) {
        Duration animDuration = Duration.millis(duration);

        // Create fade out animation
        FadeTransition fadeOut = new FadeTransition(animDuration, centerStage);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.3);

        // Create slide in animation
        TranslateTransition slideIn = new TranslateTransition(animDuration, centerStage);
        double slideDistance = movingLeft ? 50 : -50;
        slideIn.setFromX(slideDistance);
        slideIn.setToX(0);

        // Create fade in animation
        FadeTransition fadeIn = new FadeTransition(animDuration, centerStage);
        fadeIn.setFromValue(0.3);
        fadeIn.setToValue(1.0);

        // Combine animations
        fadeOut.setOnFinished(e -> {
            if (onComplete != null) {
                onComplete.run();
            }

            ParallelTransition fadeInSlide = new ParallelTransition(fadeIn, slideIn);
            fadeInSlide.play();
        });

        fadeOut.play();
    }

    /**
     * Simple fade transition for any node
     * @param node The node to fade
     * @param fadeIn True to fade in, false to fade out
     * @param duration Duration in milliseconds
     * @param onComplete Optional callback when animation completes
     */
    public void fadeTransition(Node node, boolean fadeIn, double duration, Runnable onComplete) {
        FadeTransition fade = new FadeTransition(Duration.millis(duration), node);
        fade.setFromValue(fadeIn ? 0.0 : 1.0);
        fade.setToValue(fadeIn ? 1.0 : 0.0);

        if (onComplete != null) {
            fade.setOnFinished(e -> onComplete.run());
        }

        fade.play();
    }

    /**
     * Slide transition for any node
     * @param node The node to slide
     * @param fromX Starting X position
     * @param toX Ending X position
     * @param duration Duration in milliseconds
     * @param onComplete Optional callback when animation completes
     */
    public void slideTransition(Node node, double fromX, double toX, double duration, Runnable onComplete) {
        TranslateTransition slide = new TranslateTransition(Duration.millis(duration), node);
        slide.setFromX(fromX);
        slide.setToX(toX);

        if (onComplete != null) {
            slide.setOnFinished(e -> onComplete.run());
        }

        slide.play();
    }

    public Object getController(String name) {
        return controllers.get(name);
    }
}