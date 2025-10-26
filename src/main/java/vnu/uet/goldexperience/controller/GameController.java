package vnu.uet.goldexperience.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import vnu.uet.goldexperience.core.GameEngine;
import vnu.uet.goldexperience.core.GameState;
import vnu.uet.goldexperience.manager.*;
import vnu.uet.goldexperience.view.GameBackground;
import vnu.uet.goldexperience.core.ChapterTheme;

import java.util.concurrent.TransferQueue;

public class GameController implements GameSession.GameSessionListener {
    @FXML
    private StackPane rootGamePane;
    @FXML
    private Canvas canvas;

    private GameEngine engine;
    private InputManager input;
    private GameBackground background;
    private PauseMenuManager pauseMenu;
    private TransitionManager transitionManager;
    private SceneManager sceneManager;
    private GameStateManager gameStateManager;

    @FXML
    public void initialize() {
        input = new InputManager();
        engine = new GameEngine(canvas, input);
        pauseMenu = engine.getPauseMenuManager();
        transitionManager = engine.getTransitionManager();
        gameStateManager = engine.getStateManager();

        GameSession.getInstance().addListener(this);
        engine.setCursorChangeListener(() -> Platform.runLater(this::updateCursor));

        Platform.runLater(() -> {
            setupBackground();
            setupInputHandlers();
        });
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
        if (engine != null) {
            engine.setSceneManager(sceneManager);
        }
    }
    private void setupBackground() {
        background = new GameBackground(576, 720, rootGamePane);
        rootGamePane.getChildren().add(0, background.getCanvas());
        updateTheme(GameSession.getInstance().getCurrentChapter());

        background.start();
    }

    private void setupInputHandlers() {
        rootGamePane.setFocusTraversable(true);
        rootGamePane.setOnKeyPressed(e -> input.keyPressed(e.getCode()));
        rootGamePane.setOnKeyReleased(e -> input.keyReleased(e.getCode()));

        canvas.setOnMouseMoved(e -> {
            double canvasX = e.getX();
            double canvasY = e.getY();
            input.mouseMoved(canvasX + canvas.getLayoutX());
            pauseMenu.handleMouseInput(canvasX, canvasY, false);
        });

        rootGamePane.setOnMouseDragged(e -> {
            input.mouseMoved(e.getX());
        });

        rootGamePane.setOnMousePressed(e -> {
            input.mouseClicked();

            if (engine != null && engine.getStateManager() != null) {
                engine.getPauseMenuManager().handleMouseInput(e.getX(), e.getY(), true);
            }
        });

        rootGamePane.setOnMouseReleased(e -> input.mouseReleased());

        rootGamePane.requestFocus();
        rootGamePane.setOnMouseEntered(e -> updateCursor());
    }

    private void updateCursor() {
        if (engine != null && engine.getStateManager() != null) {
            if (engine.getStateManager().is(GameState.PAUSED)) {
                rootGamePane.setCursor(Cursor.DEFAULT);
            } else {
                rootGamePane.setCursor(Cursor.NONE);
            }
        } else {
            rootGamePane.setCursor(Cursor.NONE);
        }
    }

    private void updateTheme(int chapter) {
        if (background != null) {
            ChapterTheme theme = getThemeForChapter(chapter);
            background.setTheme(theme);
            pauseMenu.setTheme(theme);
            transitionManager.setTheme(theme);
            System.out.println("Background theme updated to: " + theme);
        }
    }

    private ChapterTheme getThemeForChapter(int chapter) {
        switch (chapter) {
            case 1: return ChapterTheme.CHAPTER_1_RUST;
            case 2: return ChapterTheme.CHAPTER_2_NEON;
            case 3: return ChapterTheme.CHAPTER_3_VERDANT;
            case 4: return ChapterTheme.CHAPTER_4_CATHEDRAL;
            case 5: return ChapterTheme.CHAPTER_5_NEXUS;
            default: return ChapterTheme.ORIGINAL;
        }
    }

    public void onChapterChanged(int newChapter) {
        System.out.println("GameController: Chapter changed to " + newChapter);
        Platform.runLater(() -> updateTheme(newChapter));
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
            updateCursor();
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

    @Override
    public void onBallHitWall(GameSession.HitSide hitSide) {}
}