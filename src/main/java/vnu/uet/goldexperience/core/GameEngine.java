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

    private SceneManager sceneManager;
    private CursorChangeListener cursorChangeListener;

    private Paddle paddle;
    private Ball ball;
    private List<Brick> bricks;

    private AnimationTimer loop;
    private long lastTime = 0;

    public GameEngine(Canvas canvas, InputManager input) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.input = input;
        this.levelManager = new LevelManager();

        this.transitionManager = new TransitionManager(canvas.getWidth(), canvas.getHeight());
        this.pauseMenuManager = new PauseMenuManager(canvas, null); // SceneManager set later
        this.stateManager = new GameStateManager(transitionManager, pauseMenuManager);

        setupPauseMenuCallbacks();

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

        levelManager.loadLevel(levelNumber);
        bricks = levelManager.getActiveBricks();
    }

    public void reloadLevel() {
        loadCurrentLevel();
        ball.reset(paddle);
        paddle.reset();
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
            public void onMainMenu() {
                System.out.println("Main Menu clicked");
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

        if (stateManager.shouldAcceptGameplayInput()) {
            handleGameplayInput();
        }
    }

    public interface CursorChangeListener {
        void onCursorVisibilityChanged();
    }

    public void setCursorChangeListener(CursorChangeListener listener) {
        this.cursorChangeListener = listener;
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

        // Check collision (disable when transitioning and bricks outside screen)
        if (!transitionManager.shouldDisableCollision()) {
            for (Brick brick : bricks) {
                if (!brick.isDestroyed() && ball.bounceOffWithBrick(brick)) {
                    brick.takeHit();
                    break;
                }
            }
        }

        if (ball.getY() >= canvas.getHeight()) {
            ball.reset(paddle);
        }

        for (Brick brick : bricks) {
            brick.update(deltaTime);
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

        gc.restore();

        paddle.render(gc);
        ball.render(gc);

        if (stateManager.is(GameState.TRANSITIONING)) {
            transitionManager.render(gc);
        } else if (stateManager.is(GameState.PAUSED)) {
            pauseMenuManager.render(gc);
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
}