package vnu.uet.goldexperience.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import vnu.uet.goldexperience.core.GameEngine;
import vnu.uet.goldexperience.manager.InputManager;
import vnu.uet.goldexperience.manager.LevelManager;

public class GameController {
    @FXML private StackPane root;
    @FXML private Canvas canvas;

    private GameEngine engine;
    private InputManager input;

    @FXML
    public void initialize() {
        input = new InputManager();
        engine = new GameEngine(canvas, input);
        Platform.runLater(() -> {
            canvas.setFocusTraversable(true);
            canvas.setOnKeyPressed(e -> input.keyPressed(e.getCode()));
            canvas.setOnKeyReleased(e -> input.keyReleased(e.getCode()));
            canvas.requestFocus();

        });
    }

    public GameEngine getEngine() {
        return engine;
    }
    public void startGame() {
        if (engine != null) engine.start();
    }
    public void endGame() {
        if (engine != null) engine.end();
    }
}