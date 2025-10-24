package vnu.uet.goldexperience.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import vnu.uet.goldexperience.core.GameEngine;
import vnu.uet.goldexperience.manager.InputManager;
import vnu.uet.goldexperience.manager.GameSession;
import vnu.uet.goldexperience.view.GameBackground;

public class GameController implements GameSession.GameSessionListener {
    @FXML
    private StackPane rootGamePane;
    @FXML
    private Canvas canvas;

    private GameEngine engine;
    private InputManager input;
    private GameBackground background;

    @FXML
    public void initialize() {
        input = new InputManager();
        engine = new GameEngine(canvas, input);

        GameSession.getInstance().addListener(this);

        Platform.runLater(() -> {
            setupBackground();
            setupInputHandlers();
        });
    }

    private void setupBackground() {
        background = new GameBackground(576, 720, rootGamePane);
        rootGamePane.getChildren().add(0, background.getCanvas());

        updateBackgroundTheme(GameSession.getInstance().getCurrentChapter());

        background.start();
    }

    private void setupInputHandlers() {
        rootGamePane.setFocusTraversable(true);
        rootGamePane.setOnKeyPressed(e -> input.keyPressed(e.getCode()));
        rootGamePane.setOnKeyReleased(e -> input.keyReleased(e.getCode()));
        rootGamePane.setOnMouseMoved(e -> input.mouseMoved(e.getX()));
        rootGamePane.setOnMouseDragged(e -> input.mouseMoved(e.getX()));
        rootGamePane.setOnMousePressed(e -> input.mouseClicked());
        rootGamePane.setOnMouseReleased(e -> input.mouseReleased());
        rootGamePane.requestFocus();
        rootGamePane.setOnMouseEntered(e -> rootGamePane.setCursor(Cursor.NONE));
    }

    private void updateBackgroundTheme(int chapter) {
        if (background != null) {
            GameBackground.ChapterTheme theme = getThemeForChapter(chapter);
            background.setTheme(theme);
            System.out.println("Background theme updated to: " + theme);
        }
    }

    private GameBackground.ChapterTheme getThemeForChapter(int chapter) {
        switch (chapter) {
            case 1: return GameBackground.ChapterTheme.CHAPTER_1_RUST;
            case 2: return GameBackground.ChapterTheme.CHAPTER_2_NEON;
            case 3: return GameBackground.ChapterTheme.CHAPTER_3_VERDANT;
            case 4: return GameBackground.ChapterTheme.CHAPTER_4_CATHEDRAL;
            case 5: return GameBackground.ChapterTheme.CHAPTER_5_NEXUS;
            default: return GameBackground.ChapterTheme.ORIGINAL;
        }
    }

    public void onChapterChanged(int newChapter) {
        System.out.println("GameController: Chapter changed to " + newChapter);
        Platform.runLater(() -> updateBackgroundTheme(newChapter));
    }

    @Override
    public void onLevelChanged(int newLevel) {
        System.out.println("GameController: Level changed to " + newLevel);
    }

    public GameEngine getEngine() {
        return engine;
    }

    public InputManager getInput() {
        return input;
    }

    public void startGame() {
        if (engine != null) {
            engine.start();
        }
    }

    public void endGame() {
        if (engine != null) {
            engine.end();
            input.clear();
        }
        if (background != null) {
            background.stop();
        }
    }

    public void cleanup() {
        GameSession.getInstance().removeListener(this);
        endGame();
    }
}