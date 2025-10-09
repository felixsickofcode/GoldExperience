package vnu.uet.goldexperience.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import vnu.uet.goldexperience.core.GameEngine;
import vnu.uet.goldexperience.model.*;

import java.util.List;

public class GameController {
    @FXML private StackPane root;
    @FXML private Canvas canvas;

    private GameEngine engine;
    private InputHandler input;

    @FXML
    public void initialize() {
        input = new InputHandler();
        engine = new GameEngine(canvas, input);

        // Khi giao diện hiển thị xong, setup input và start game
        Platform.runLater(() -> {
            canvas.setFocusTraversable(true);
            canvas.setOnKeyPressed(e -> input.keyPressed(e.getCode()));
            canvas.setOnKeyReleased(e -> input.keyReleased(e.getCode()));
            canvas.requestFocus();
            engine.start();
        });

    }
}

