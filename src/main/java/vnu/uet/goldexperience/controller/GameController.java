package vnu.uet.goldexperience.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import vnu.uet.goldexperience.core.Constants;
import vnu.uet.goldexperience.core.GameEngine;
import vnu.uet.goldexperience.core.GameState;
import vnu.uet.goldexperience.manager.*;
import vnu.uet.goldexperience.view.GameBackground;
import vnu.uet.goldexperience.core.ChapterTheme;
import vnu.uet.goldexperience.view.GameUIComponents;

public class GameController implements GameSession.GameSessionListener {
    @FXML
    private StackPane rootGamePane;
    @FXML
    private Canvas canvas;

    @FXML
    private Label scoreLabel;
    @FXML
    private HBox livesContainer;

    private GameEngine engine;
    private InputManager input;
    private GameBackground background;
    private PauseMenuManager pauseMenu;
    private TransitionManager transitionManager;
    private SceneManager sceneManager;
    private GameStateManager gameStateManager;
    private GameOverManager gameOverManager;
    private ChapterTheme currentTheme;

    @FXML
    public void initialize() {
        input = new InputManager();
        engine = new GameEngine(canvas, input);
        pauseMenu = engine.getPauseMenuManager();
        transitionManager = engine.getTransitionManager();
        gameStateManager = engine.getStateManager();
        gameOverManager = engine.getGameOverManager();

        GameSession.getInstance().addListener(this);
        engine.setCursorChangeListener(() -> Platform.runLater(this::updateCursor));

        engine.setUICallback(new GameEngine.GameUICallback() {
            @Override
            public void onScoreChanged(int score) {
                updateScore(score);
            }

            @Override
            public void onLivesChanged(int lives) {
                updateLives(lives);
            }
        });

        Platform.runLater(() -> {
            setupBackground();
            setupInputHandlers();
           
            updateScore(GameSession.getInstance().getScore());
        });
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
        if (engine != null) {
            engine.setSceneManager(sceneManager);
        }
    }

    private void setupBackground() {
        background = new GameBackground(Constants.GAMEPLAYZONE_WIDTH, Constants.GAMEPLAYZONE_HEIGHT, rootGamePane);
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
            gameOverManager.handleMouseInput(canvasX, canvasY, false);
        });

        rootGamePane.setOnMouseDragged(e -> {
            input.mouseMoved(e.getX()); 
        });

        rootGamePane.setOnMousePressed(e -> {
            input.mouseClicked();

            if (engine != null && engine.getStateManager() != null) {
                if (engine.getStateManager().is(GameState.PAUSED))
                    engine.getPauseMenuManager().handleMouseInput(e.getX(), e.getY(), true); 
                if (engine.getStateManager().is(GameState.GAME_OVER))
                    engine.getGameOverManager().handleMouseInput(e.getX(), e.getY(), true); 
            }
        });

        rootGamePane.setOnMouseReleased(e -> input.mouseReleased());
        rootGamePane.requestFocus();
        rootGamePane.setOnMouseEntered(e -> updateCursor());
    }

    private void updateCursor() {
        if (engine != null && engine.getStateManager() != null) {
            if (engine.getStateManager().is(GameState.PAUSED) || engine.getStateManager().is(GameState.GAME_OVER)) {
                rootGamePane.setCursor(Cursor.DEFAULT);
            } else {
                rootGamePane.setCursor(Cursor.NONE);
            }
        } else {
            rootGamePane.setCursor(Cursor.NONE);
        }
    }

    public void updateScore(int score) {
        if (scoreLabel != null) {
            Platform.runLater(() ->
                    GameUIComponents.updateScoreLabel(scoreLabel, score)
            );
        }
    }

    public void updateLives(int lives) {
        if (livesContainer != null) {
            Platform.runLater(() -> {
                if (GameSession.getInstance().hasRecentlyLostLife()) {
                    GameUIComponents.animateHPLoss(livesContainer, currentTheme);
                    GameSession.getInstance().clearRecentLifeFlag();
                } else {
                    GameUIComponents.updateHPContainer(livesContainer, lives, 3, currentTheme);
                }
            });
        }
    }

    private void updateTheme(int chapter) {
        if (background != null) {
            this.currentTheme = getThemeForChapter(chapter);

            background.setTheme(currentTheme);
            pauseMenu.setTheme(currentTheme);
            gameOverManager.setTheme(currentTheme);
            transitionManager.setTheme(currentTheme);

            GameUIComponents.applyTheme(scoreLabel, currentTheme);
            updateLives(GameSession.getInstance().getLives());
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