package vnu.uet.goldexperience.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.Cursor;

import vnu.uet.goldexperience.core.GameEngine;
import vnu.uet.goldexperience.model.Paddle;

public class GameController {
    @FXML private StackPane root;
    @FXML private Canvas canvas;
    private GameEngine engine;

    @FXML
    public void initialize() {
        engine = new GameEngine(canvas);
        Platform.runLater(this::setupInputHandlers);
    }

    private void setupInputHandlers() {
        canvas.setCursor(Cursor.NONE);
        canvas.setFocusTraversable(true);
        Paddle paddle = engine.getPaddle();

        canvas.setOnMouseMoved(e -> {
            double newX = e.getX() - paddle.getWidth() / 2;
            if (newX < 0) newX = 0;
            if (newX + paddle.getWidth() > canvas.getWidth()) {
                newX = canvas.getWidth() - paddle.getWidth();
            }
            paddle.setX(newX);
        });

        canvas.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.LEFT) {
                engine.movePaddleLeft();
            } else if (e.getCode() == KeyCode.RIGHT) {
                engine.movePaddleRight();
            } else if (e.getCode() == KeyCode.SPACE) {
                engine.shootBall();
            }
        });

        canvas.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.RIGHT) {
                engine.stopPaddle();
            }
        });

        canvas.requestFocus();
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