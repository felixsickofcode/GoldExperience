package vnu.uet.goldexperience.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import vnu.uet.goldexperience.core.GameEngine;
import vnu.uet.goldexperience.manager.InputManager;
import vnu.uet.goldexperience.manager.LevelManager;

public class GameController {
    @FXML
    private StackPane rootGamePane;
    @FXML
    private Canvas canvas;

    private GameEngine engine;
    private InputManager input;


    @FXML
    public void initialize() {
        input = new InputManager();
        engine = new GameEngine(canvas, input);

        Platform.runLater(() -> {
            rootGamePane.setFocusTraversable(true);

            rootGamePane.setOnKeyPressed(e -> input.keyPressed(e.getCode()));
            rootGamePane.setOnKeyReleased(e -> input.keyReleased(e.getCode()));

            rootGamePane.setOnMouseMoved(e -> input.mouseMoved(e.getX()));
            rootGamePane.setOnMouseDragged(e -> input.mouseMoved(e.getX()));
            rootGamePane.setOnMousePressed(e -> input.mouseClicked());
            rootGamePane.setOnMouseReleased(e -> input.mouseReleased());

            rootGamePane.requestFocus();
            rootGamePane.setOnMouseEntered(e -> rootGamePane.setCursor(Cursor.NONE));
        });
    }

    public GameEngine getEngine() {
        return engine;
    }

    public InputManager getInput() {
        return input;
    }

    public void startGame() {
        if (engine != null) engine.start();
    }

    public void endGame() {
        if (engine != null) {
            engine.end();
            input.clear();
        }
    }
}