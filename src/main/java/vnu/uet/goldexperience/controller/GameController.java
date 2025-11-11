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
import vnu.uet.goldexperience.view.LoadGameDialog;
import vnu.uet.goldexperience.view.ScoreboardPanel;
import vnu.uet.goldexperience.database.PlayerDatabase;
import vnu.uet.goldexperience.manager.GameSession.*;

public class GameController implements GameSessionListener {
    @FXML
    private StackPane rootGamePane;

    @FXML
    private Canvas canvas;

    @FXML
    private Label scoreLabel;

    @FXML
    private HBox livesContainer;

    @FXML
    private ScoreboardPanel scoreboardPanel;

    private GameEngine engine;
    private InputManager input;
    private GameBackground background;
    private PauseMenuManager pauseMenu;
    private TransitionManager transitionManager;
    private LoadGameDialog loadGameDialog;
    private GameOverManager gameOverManager;
    private DialogueSystem dialogueSystem;
    private ChapterTheme currentTheme;

    @FXML
    public void initialize() {
        input = new InputManager();
        engine = new GameEngine(canvas, input);
        pauseMenu = engine.getPauseMenuManager();
        transitionManager = engine.getTransitionManager();
        gameOverManager = engine.getGameOverManager();
        dialogueSystem = engine.getDialogueSystem();
        loadGameDialog = engine.getLoadGameDialog();

        gameOverManager.setScoreSaveCallback((playerName, score) -> {
            savePlayerScore(playerName, score);
        });

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
            setupScoreboard();
            updateScore(GameSession.getInstance().getScore());
        });
    }

    public void setSceneManager(SceneManager sceneManager) {
        if (engine != null) {
            engine.setSceneManager(sceneManager);
        }
    }

    private void setupBackground() {
        background = new GameBackground(Constants.GAMEPLAYZONE_WIDTH, Constants.GAMEPLAYZONE_HEIGHT, rootGamePane);
        rootGamePane.getChildren().add(0, background.getCanvas());
    }

    private void setupScoreboard() {
        if (scoreboardPanel != null && currentTheme != null) {
            scoreboardPanel.applyTheme(currentTheme);
            scoreboardPanel.updateScoreboard();
        }
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
            if (loadGameDialog != null) {
                loadGameDialog.handleMouseInput(canvasX, canvasY, false);
            }
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
                if (loadGameDialog != null && loadGameDialog.isVisible()) {
                    loadGameDialog.handleMouseInput(e.getX(), e.getY(), true);
                }
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
            if (GameSession.getInstance().getMode()
                    .equals(GameSession.GameMode.ENDLESS))
                this.currentTheme = ChapterTheme.ORIGINAL;

            background.setTheme(currentTheme);
            pauseMenu.setTheme(currentTheme);
            gameOverManager.setTheme(currentTheme);
            transitionManager.setTheme(currentTheme);
            dialogueSystem.setTheme(currentTheme);

            GameUIComponents.applyTheme(scoreLabel, currentTheme);
            updateLives(GameSession.getInstance().getLives());

            // Update scoreboard theme
            if (scoreboardPanel != null) {
                scoreboardPanel.applyTheme(currentTheme);
            }
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
        System.out.println("GC: Chapter changed to " + newChapter);
        Platform.runLater(() -> updateTheme(newChapter));
    }

    @Override
    public void onLevelChanged(int newLevel) {
        System.out.println("GC: Level changed to " + newLevel);
    }

    public GameEngine getEngine() {
        return engine;
    }

    public InputManager getInput() {
        return input;
    }

    public void startGame() {
        updateTheme(GameSession.getInstance().getCurrentChapter());
        background.start();
        System.out.println("startGame() - Mode: " + GameSession.getInstance().getMode());
        if (engine != null) {
            engine.refreshMode();
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

    /**
     * Save player score to database and update scoreboard
     * Call this when game ends or player achieves new high score
     */
    public void savePlayerScore(String playerName, int finalScore) {
        if (playerName == null || playerName.trim().isEmpty()) {
            System.err.println("Cannot save score: invalid player name");
            return;
        }

        PlayerDatabase db = PlayerDatabase.getInstance();

        try {
            if (db.playerExists(playerName)) {
                int currentScore = db.getPlayerScore(playerName);
                if (finalScore > currentScore) {
                    db.updateScore(playerName, finalScore);
                    System.out.println("✓ Updated " + playerName + "'s high score: " + currentScore + " → " + finalScore);
                } else {
                    System.out.println("Score " + finalScore + " not higher than current " + currentScore);
                }
            } else {
                db.addOrUpdatePlayer(playerName, finalScore);
                System.out.println("✓ Added new player: " + playerName + " with score " + finalScore);
            }

            // Refresh scoreboard immediately
            if (scoreboardPanel != null) {
                Platform.runLater(() -> scoreboardPanel.updateScoreboard());
            }
        } catch (Exception e) {
            System.err.println("Error saving player score: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void cleanup() {
        GameSession.getInstance().removeListener(this);

        if (scoreboardPanel != null) {
            scoreboardPanel.stop();
        }

        endGame();
    }

    @Override
    public void onBallHitWall(GameSession.HitSide hitSide) {}

}