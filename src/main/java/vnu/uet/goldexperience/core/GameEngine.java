package vnu.uet.goldexperience.core;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import vnu.uet.goldexperience.effect.BorderFlashEffect;
import vnu.uet.goldexperience.effect.brick.ExplosionEffect;
import vnu.uet.goldexperience.manager.*;
import vnu.uet.goldexperience.model.*;
import vnu.uet.goldexperience.model.brick.*;
import vnu.uet.goldexperience.model.brick.Brick.BrickListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameEngine implements Brick.BrickListener {
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final InputManager input;
    private final LevelManager levelManager;

    private final GameStateManager stateManager;
    private final TransitionManager transitionManager;
    private final PauseMenuManager pauseMenuManager;
    private final GameOverManager gameOverManager;
    private final DialogueSystem dialogueSystem;

    private SceneManager sceneManager;
    private CursorChangeListener cursorChangeListener;
    private GameUICallback uiCallback;

    private Paddle paddle;
    private Ball ball;
    private List<Brick> bricks;

    private final List<PowerUp> fallingPowerUps = new ArrayList<>();
    private final List<Ball> balls = new ArrayList<>();
    private final List<Bullet> bullets = new ArrayList<>();
    private PowerUpManager powerUpManager;
    private int hitsSinceLastDrop = 0;

    private AnimationTimer loop;
    private long lastTime = 0;

    private GameSession.GameMode mode;
    private int comboCount = 0;

    private boolean levelCompleteSoundPlayed = false;
    private Set<Brick> soundForExplosionChains = new HashSet<>();

    public GameEngine(Canvas canvas, InputManager input) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.input = input;
        this.levelManager = new LevelManager();
        this.transitionManager = new TransitionManager(canvas.getWidth(), canvas.getHeight());
        this.pauseMenuManager = new PauseMenuManager(canvas, null);
        this.gameOverManager = new GameOverManager(canvas, null);
        this.stateManager = new GameStateManager(transitionManager, pauseMenuManager, gameOverManager);
        this.dialogueSystem = new DialogueSystem(canvas);
        this.mode = GameSession.getInstance().getMode();

        setupDialogueCallbacks();
        setupPauseMenuCallbacks();
        setupGameOverCallbacks();

        initObjects();
    }

    public void setSceneManager(SceneManager sceneManager) {this.sceneManager = sceneManager;}

    private void initObjects() {
        paddle = new Paddle(Constants.PADDLE_INIT_POSITION, canvas.getHeight() - 120,
                Constants.MEDIUM_PADDLE_WIDTH, Constants.PADDLE_HEIGHT);

        ball = new Ball(Constants.BALL_INIT_POSITION,
                paddle.getY() - Constants.NORMAL_BALL_SIZE, Constants.NORMAL_BALL_SIZE);
    }

    private void loadCurrentLevel() {
        int levelNumber = GameSession.getInstance().getLevelNumber();
        System.out.println("Loading level: " + levelNumber +
                " (Chapter " + GameSession.getInstance().getCurrentChapter() +
                ", Level " + GameSession.getInstance().getCurrentLevel() + ")");

        // Load trước
        levelManager.loadLevel(levelNumber);
        bricks = levelManager.getActiveBricks();

        // Rồi mới add listener
        if (bricks != null) {
            for (Brick brick : bricks) {
                brick.addListener(this);
            }
        }

        ball.reset(paddle);
        paddle.reset();
        GameSession.getInstance().resetLives();

        if (uiCallback != null) {
            uiCallback.onLivesChanged(GameSession.getInstance().getLives());
            uiCallback.onScoreChanged(GameSession.getInstance().getScore());
        }
        soundForExplosionChains.clear();
        levelCompleteSoundPlayed = false;


        levelManager.loadLevel(levelNumber);
        bricks = levelManager.getActiveBricks();

        // Rebuild game context and managers
        balls.clear();
        balls.add(ball);
        bullets.clear();
        fallingPowerUps.clear();
        powerUpManager = new

                PowerUpManager(new GameContext(balls, paddle, bullets, bricks));
        hitsSinceLastDrop = 0;
    }

    public void reloadLevel() {
        loadCurrentLevel();
        stateManager.setState(GameState.PLAYING);
    }

    private void setupPauseMenuCallbacks() {
        pauseMenuManager.setCallback(new PauseMenuManager.PauseMenuCallback() {
            @Override
            public void onResume() {
                System.out.println("Resume clicked");
                stateManager.setState(GameState.PLAYING);
                notifyCursorChange();
            }

            @Override
            public void onRestart() {
                System.out.println("Restart clicked");
                reloadLevel();
                notifyCursorChange();
            }

            @Override
            public void onBack() {
                AssetsManager.playClickSound();
                System.out.println("Back");
                if (sceneManager != null) {
                    end();
                    if ( mode.equals(GameSession.GameMode.STORY))
                        sceneManager.switchTo("level");
                    else
                        sceneManager.switchTo("menu");
                }
            }

            @Override
            public void onQuit() {
                AssetsManager.playClickSound();
                System.out.println("Quit clicked");
                javafx.application.Platform.exit();
            }
        });
    }

private void setupGameOverCallbacks() {
    gameOverManager.setCallback(new GameOverManager.GameOverCallback() {
        @Override
        public void onRetry() {
            AssetsManager.playClickSound();
            System.out.println("Retry clicked");
            reloadLevel();
        }

            @Override
            public void onMainMenu() {
                AssetsManager.playClickSound();
                System.out.println("Main Menu clicked");
                if (sceneManager != null) {
                    end();
                    sceneManager.switchTo("menu");
                }
            }

            @Override
            public void onQuit() {
                AssetsManager.playClickSound();
                System.out.println("Quit clicked");
                javafx.application.Platform.exit();
            }
        });
    }

    private void setupDialogueCallbacks() {
        dialogueSystem.setCallback(new DialogueSystem.DialogueCallback() {
            @Override
            public void onDialogueComplete() {
                System.out.println("Dialogue complete");
                if (stateManager.is(GameState.STORY)) {
                    if (dialogueSystem.isAfterLevelDialogue()) {
                        boolean hasNext = GameSession.getInstance().nextLevel();
                        if (hasNext) {
                            stateManager.setState(GameState.TRANSITIONING);
                        } else {
                            if (sceneManager != null) {
                                end();
                                sceneManager.switchTo("menu");
                            }
                        }
                    } else {
                        stateManager.setState(GameState.PLAYING);
                    }
                }
                notifyCursorChange();
            }
        });
    }

    public void start() {
        loadCurrentLevel();
        checkAndShowBeforeDialogue();

        loop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastTime == 0) {
                    lastTime = now;
                    return;
                }
                double dt = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;

                handleInput();
                input.update();
                update(dt);

                render();
            }
        };
        loop.start();
    }

    public void end() {
        if (loop != null) {
            loop.stop();
            loop = null;
        }
    }

    private void handleInput() {
        if (input.isActionJustPressed(Action.PAUSE)) {
            if (stateManager.is(GameState.PLAYING)) {
                stateManager.setState(GameState.PAUSED);
                notifyCursorChange();
            } else if (stateManager.is(GameState.PAUSED)) {
                stateManager.setState(GameState.PLAYING);
                notifyCursorChange();
            }
            return;
        }
        if (stateManager.is(GameState.STORY)) {
            dialogueSystem.handleInput(input);
            return;
        }
        if (stateManager.is(GameState.PAUSED)) {
            pauseMenuManager.handleKeyInput(input);
            return;
        }
        if (stateManager.is(GameState.GAME_OVER)) {
            gameOverManager.handleKeyInput(input);
            notifyCursorChange();
            return;
        }

        if (stateManager.shouldAcceptGameplayInput()) {
            handleGameplayInput();
        }
    }

    public interface CursorChangeListener {
        void onCursorVisibilityChanged();
    }

    //Observer
    public interface GameUICallback {
        void onScoreChanged(int score);
        void onLivesChanged(int lives);
    }

    public void setCursorChangeListener(CursorChangeListener listener) {
        this.cursorChangeListener = listener;
    }

    public void setUICallback(GameUICallback callback) {
        this.uiCallback = callback;
    }

    private void notifyCursorChange() {
        if (cursorChangeListener != null) {
            cursorChangeListener.onCursorVisibilityChanged();
        }
    }

    private void handleGameplayInput() {
        if (input.isMouseActive()) {
            double targetX = input.getMouseX() - Constants.GAME_OFFSET
                    - paddle.getWidth() / 2;
            paddle.setTargetX(targetX);
        } else {
            if (input.isActionActive(Action.MOVE_LEFT))
                paddle.moveLeft();
            else if (input.isActionActive(Action.MOVE_RIGHT))
                paddle.moveRight();
            else
                paddle.stop();
        }

    // Shoot all reset balls
        if (input.isActionActive(Action.SHOOT)) {
            for (Ball b : balls) {
                if (b.isReset()) {
                    b.shoot();
                }
            }
        }
    }
    private void update(double deltaTime) {
        if (stateManager.is(GameState.PAUSED)) {
            pauseMenuManager.update(deltaTime);
            return;
        }

        System.out.print(stateManager.getCurrentState());
        System.out.print(" ");
        System.out.println(mode);
        if (stateManager.is(GameState.STORY)) {
            dialogueSystem.update(deltaTime);
            return;
        }

        if (stateManager.is(GameState.GAME_OVER)) {
            gameOverManager.update(deltaTime);
            return;
        }

        if (stateManager.is(GameState.TRANSITIONING)) {
            if (transitionManager.update(deltaTime)) {
                ball.reset(paddle);
                paddle.reset();
                loadCurrentLevel();
            }

            if (!transitionManager.isActive()) {
                checkAndShowBeforeDialogue();
            }
        }

        if (stateManager.shouldUpdateGameplay()) {
            updateGameplay(deltaTime);
        }

        if (stateManager.is(GameState.PLAYING) && isLevelComplete()) {
            handleLevelComplete();
        }
    }

    private void updateGameplay(double deltaTime) {
        mode = GameSession.getInstance().getMode();

        paddle.update(deltaTime);
        for (Ball b : balls) {
            b.update(deltaTime);

            if (b.isReset()) {
                b.setX(paddle.getX() + paddle.getWidth() / 2 - b.getWidth() / 2);
                b.setY(paddle.getY() - b.getHeight());
            }


            if (ball.bounceOffWithPaddle(paddle)) {
                AssetsManager.playHitPaddleSound();
            }
        }

        // Check collisions for all balls
        if (!transitionManager.shouldDisableCollision()) {
            for (Ball b : balls) {
                if (b.isReset()) continue;
            }
            for (Brick brick : bricks) {
                if (!brick.isDestroyed() && ball.bounceOffWithBrick(brick)) {
                    boolean wasDestroyed = brick.isDestroyed();

                    brick.takeHit();

                    hitsSinceLastDrop++;
                    if (hitsSinceLastDrop >= Constants.POWER_UP_HIT_DROP_TEST_THRESHOLD) {
                        spawnRandomDrop();
                        hitsSinceLastDrop = 0;
                    }

                    if (!wasDestroyed && brick.isDestroyed()) {
                        int points = 124;
                        GameSession.getInstance().addScore(points);

                        if (uiCallback != null) {
                            uiCallback.onScoreChanged(GameSession.getInstance().getScore());
                        }

                        // Spawn a falling power-up when a StrongBrick is destroyed
                        if (brick instanceof MediumBrick) {
                            spawnRandomDrop();
                        }
                    }
                    if (brick instanceof ExplodeBrick) {
                        AssetsManager.playExplosionSound();
                    } else if (brick.getHitPoints() == 0 || brick instanceof UnbreakableBrick) {
                        AssetsManager.playBreakBrickSound();
                    } else {
                        AssetsManager.playHitBrickSound();
                    }

                    break;
                }
            }
        }

        if (ball.getY() >= canvas.getHeight() && stateManager.is(GameState.PLAYING)) {
            GameSession.getInstance().loseLife();
            if (uiCallback != null) {
                uiCallback.onLivesChanged(GameSession.getInstance().getLives());
            }

            if (GameSession.getInstance().stillAlive()) {
                ball.reset(paddle);
            } else {
                stateManager.setState(GameState.GAME_OVER);
            }
        }

        for (Brick brick : bricks) {
            brick.update(deltaTime);
        }

            // Update active falling power-ups
        if (!fallingPowerUps.isEmpty()) {
            List<PowerUp> collected = new ArrayList<>();
            for (PowerUp pu : fallingPowerUps) {
                pu.update(deltaTime);

                if (pu.getY() > canvas.getHeight()) {
                    collected.add(pu);
                    continue;
                }

                if (pu.getX() < paddle.getX() + paddle.getWidth() &&
                        pu.getX() + pu.getWidth() > paddle.getX() &&
                        pu.getY() < paddle.getY() + paddle.getHeight() &&
                        pu.getY() + pu.getHeight() > paddle.getY()) {

                    if (powerUpManager != null) {
                        powerUpManager.activatePowerUp(pu);
                    }
                    collected.add(pu);
                }
            }
            if (!collected.isEmpty()) {
                fallingPowerUps.removeAll(collected);
            }
        }


        if (powerUpManager != null) {
            powerUpManager.update();
        }

        checkChainExplosions();
        bricks.removeIf(Brick::canBeRemoved);
    }

    private void spawnRandomDrop() {
        double minX = 0;
        double maxX = Constants.GAMEPLAYZONE_WIDTH - Constants.POWER_UP_ITEM_WIDTH;
        double spawnX = minX + Math.random() * (maxX - minX);
        double spawnY = -Constants.POWER_UP_ITEM_HEIGHT;
        PowerUpType dropType = PowerUpType.randomDroppable();
        fallingPowerUps.add(new SimplePowerUp(spawnX, spawnY, dropType));
    }

    private void render() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.save();
        transitionManager.applySlideTransform(gc);
        for (PowerUp pu : fallingPowerUps) {
            pu.render(gc);
        }
        for (Ball b : balls) {
            b.render(gc);
        }
        for (Brick brick : bricks) {
            brick.render(gc);
        }



        gc.restore();

        paddle.render(gc);



        if (stateManager.is(GameState.STORY)) {
            dialogueSystem.render(gc);
        }
        if (stateManager.is(GameState.TRANSITIONING)) {
            transitionManager.render(gc);
        } else if (stateManager.is(GameState.PAUSED)) {
            pauseMenuManager.render(gc);
        } else if (stateManager.is(GameState.GAME_OVER)) {
            gameOverManager.render(gc);
        }
    }

    private void checkChainExplosions() {
        List<Brick> newlyExploded = new ArrayList<>();
        for (Brick brick : bricks) {
            ExplosionEffect effect = brick.getExplosionEffect();
            if (effect != null && effect.isActive()) {
                for (Brick otherBrick : bricks) {
                    if (otherBrick != brick
                            && !otherBrick.isDestroyed()
                            && otherBrick.isInExplosionRadius(effect)) {
                        newlyExploded.add(otherBrick);
                    }
                }
            }
        }
        for (Brick brick : newlyExploded) {
            brick.explodeByChainReaction();

            if (!soundForExplosionChains.contains(brick)) {
                AssetsManager.playExplosionSound();
                soundForExplosionChains.add(brick);
            }
        }
    }

    private boolean isLevelComplete() {
        for (Brick brick : bricks) {
            if ((!brick.isDestroyed() && !(brick instanceof UnbreakableBrick))
                    || (brick.getBreakEffect() != null && !(brick.getBreakEffect().isFinished()))
                    || (brick.getExplosionEffect() != null && brick.getExplosionEffect().isActive())) {
                return false;
            }
        }
        return true;
    }

    private void handleLevelComplete() {
        boolean hasUnbreakableBricks = false;
        for (Brick brick : bricks) {
            if (brick instanceof UnbreakableBrick) {
                ((UnbreakableBrick) brick).destroy();
                hasUnbreakableBricks = true;
            }
        }

        if (hasUnbreakableBricks && !levelCompleteSoundPlayed) {
            AssetsManager.playBreakBrickSound();
            levelCompleteSoundPlayed = true;
        }
        if (areAllEffectsFinished()) {
            System.out.println("Level Complete!");
            checkAndShowAfterDialogue();
            boolean hasNext = GameSession.getInstance().nextLevel();
            if (hasNext) {
                stateManager.setState(GameState.TRANSITIONING);
            } else {
                System.out.println("Game Complete! All levels finished!");
                stateManager.setState(GameState.VICTORY);
            }
        }
    }

    private boolean areAllEffectsFinished() {
        for (Brick brick : bricks) {
            if (brick instanceof UnbreakableBrick)
                if (((UnbreakableBrick) brick).getDestructionEffect() != null && !((UnbreakableBrick) brick).getDestructionEffect().isFinished()) {
                    return false;
                }
            if (brick.getExplosionEffect() != null && brick.getExplosionEffect().isActive()) {
                return false;
            }
            if (brick.getBreakEffect() != null && !brick.getBreakEffect().isFinished()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onBrickDestroyed(Brick brick) {
        int points = 125;
        GameSession.getInstance().addScore(points + comboCount);
        comboCount += 5;

        if (uiCallback != null) {
            uiCallback.onScoreChanged(GameSession.getInstance().getScore());
        }
    }

    private void checkAndShowBeforeDialogue() {
        if ( mode.equals(GameSession.GameMode.ENDLESS)) {
            stateManager.setState(GameState.PLAYING);
            return;
        }
        int levelNumber = GameSession.getInstance().getLevelNumber();
        if (Story.hasDialogue(levelNumber)) {
            Story.DialogueData dialogue = Story.getDialogue(levelNumber);
            dialogueSystem.show(dialogue);
            stateManager.setState(GameState.STORY);
            return;
        }
        stateManager.setState(GameState.PLAYING);
    }

    private void checkAndShowAfterDialogue() {
        if ( mode.equals(GameSession.GameMode.ENDLESS)) {
            stateManager.setState(GameState.PLAYING);
            return;
        }
        int currentLevelNumber = GameSession.getInstance().getLevelNumber();
        if (Story.hasAfterDialogue(currentLevelNumber)) {
            Story.DialogueData afterDialogue = Story.getAfterDialogue(currentLevelNumber);
            dialogueSystem.show(afterDialogue);
            stateManager.setState(GameState.STORY);
            return;
        }
    }

    public void refreshMode() {
        this.mode = GameSession.getInstance().getMode();
    }

    public GameStateManager getStateManager() {
        return stateManager;
    }

    public PauseMenuManager getPauseMenuManager() {
        return pauseMenuManager;
    }

    public TransitionManager getTransitionManager() {
        return transitionManager;
    }

    public GameOverManager getGameOverManager() {
        return gameOverManager;
    }

    public DialogueSystem getDialogueSystem() {
        return dialogueSystem;
    }
}