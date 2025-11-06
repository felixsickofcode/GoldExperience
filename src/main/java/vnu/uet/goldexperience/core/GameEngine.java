package vnu.uet.goldexperience.core;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import vnu.uet.goldexperience.effect.brick.ExplosionEffect;
import vnu.uet.goldexperience.manager.*;
import vnu.uet.goldexperience.model.*;

import java.util.ArrayList;
import java.util.List;

public class GameEngine {
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final InputManager input;
    private final LevelManager levelManager;

    private final GameStateManager stateManager;
    private final TransitionManager transitionManager;
    private final PauseMenuManager pauseMenuManager;
    private final GameOverManager gameOverManager;
    private final LifeManager lifeManager;

    private SceneManager sceneManager;
    private CursorChangeListener cursorChangeListener;
    private GameUICallback uiCallback;

    private Paddle paddle;
    private Ball ball;
    private List<Brick> bricks;

    // Power-ups and context
    private final List<PowerUp> fallingPowerUps = new ArrayList<>();
    private final List<Ball> balls = new ArrayList<>();
    private final List<Bullet> bullets = new ArrayList<>();
    private PowerUpManager powerUpManager;

    private AnimationTimer loop;
    private long lastTime = 0;

    public GameEngine(Canvas canvas, InputManager input) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.input = input;
        this.levelManager = new LevelManager();
        this.lifeManager = new LifeManager(canvas.getWidth(), canvas.getHeight());
        this.transitionManager = new TransitionManager(canvas.getWidth(), canvas.getHeight());
        this.pauseMenuManager = new PauseMenuManager(canvas, null);
        this.gameOverManager = new GameOverManager(canvas, null);
        this.stateManager = new GameStateManager(transitionManager, pauseMenuManager, gameOverManager);

        setupPauseMenuCallbacks();
        setupGameOverCallbacks();

        initObjects();
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

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
        ball.reset(paddle);
        paddle.reset();

        GameSession.getInstance().resetLives();

        if (uiCallback != null) {
            uiCallback.onLivesChanged(GameSession.getInstance().getLives());
            uiCallback.onScoreChanged(GameSession.getInstance().getScore());
        }

        levelManager.loadLevel(levelNumber);
        bricks = levelManager.getActiveBricks();

        // Rebuild game context and managers
        balls.clear();
        balls.add(ball);
        bullets.clear();
        fallingPowerUps.clear();
        powerUpManager = new PowerUpManager(new GameContext(balls, paddle, bullets, bricks));
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
            public void onLevelSelect() {
                System.out.println("Level Select");
                if (sceneManager != null) {
                    end();
                    sceneManager.switchTo("level");
                }
            }

            @Override
            public void onQuit() {
                System.out.println("Quit clicked");
                javafx.application.Platform.exit();
            }
        });
    }

    private void setupGameOverCallbacks() {
        gameOverManager.setCallback(new GameOverManager.GameOverCallback() {
            @Override
            public void onRetry() {
                System.out.println("Retry clicked");
                reloadLevel();
            }

            @Override
            public void onLevelSelect() {
                System.out.println("Level Select clicked");
                if (sceneManager != null) {
                    end();
                    sceneManager.switchTo("level");
                }
            }

            @Override
            public void onMainMenu() {
                System.out.println("Main Menu clicked");
                if (sceneManager != null) {
                    end();
                    sceneManager.switchTo("menu");
                }
            }
        });
    }

    public void start() {
        loadCurrentLevel();
        stateManager.reset();

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

        if (input.isActionActive(Action.SHOOT) && ball.isReset())
            ball.shoot();
    }

    private void update(double deltaTime) {
        if (stateManager.is(GameState.PAUSED)) {
            pauseMenuManager.update(deltaTime);
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
                stateManager.setState(GameState.PLAYING);
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
        paddle.update(deltaTime);
        ball.update(deltaTime);

        if (ball.isReset()) {
            ball.setX(paddle.getX() + paddle.getWidth() / 2 - ball.getWidth() / 2);
            ball.setY(paddle.getY() - ball.getHeight());
        }

        ball.bounceOffWithPaddle(paddle);

        if (!transitionManager.shouldDisableCollision()) {
            for (Brick brick : bricks) {
                if (!brick.isDestroyed() && ball.bounceOffWithBrick(brick)) {
                    boolean wasDestroyed = brick.isDestroyed();

                    brick.takeHit();
                    if (!wasDestroyed && brick.isDestroyed()) {
                        int points = 124;
                        GameSession.getInstance().addScore(points);

                        if (uiCallback != null) {
                            uiCallback.onScoreChanged(GameSession.getInstance().getScore());
                        }

                        // Spawn a falling power-up when a StrongBrick is destroyed
                        if (brick instanceof StrongBrick) {
                            double size = Constants.POWER_UP_ITEM_SIZE;
                            double spawnX = brick.getX() + brick.getWidth() / 2 - size / 2;
                            double spawnY = brick.getY() + brick.getHeight() / 2 - size / 2;
                            fallingPowerUps.add(new SimplePowerUp(spawnX, spawnY, PowerUpType.EXTEND));
                        }
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

    private void render() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.save();
        transitionManager.applySlideTransform(gc);

        for (Brick brick : bricks) {
            brick.render(gc);
        }

        for (PowerUp pu : fallingPowerUps) {
            pu.render(gc);
        }

        gc.restore();

        paddle.render(gc);
        ball.render(gc);

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
        for (Brick brick : bricks) {
            if (brick instanceof UnbreakableBrick) {
                ((UnbreakableBrick) brick).destroy();
            }
        }
        if (areAllEffectsFinished()) {
            System.out.println("Level Complete!");
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
}