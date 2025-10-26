package vnu.uet.goldexperience.core;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import vnu.uet.goldexperience.effect.ExplosionEffect;
import vnu.uet.goldexperience.manager.GameSession;
import vnu.uet.goldexperience.manager.InputManager;
import vnu.uet.goldexperience.manager.LevelManager;
import vnu.uet.goldexperience.model.*;

import java.util.ArrayList;
import java.util.List;

public class GameEngine {
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final InputManager input;
    private final LevelManager levelManager;

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
        initObjects();
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
    }

    public void start() {

        int levelNumber = GameSession.getInstance().getLevelNumber();
        loadCurrentLevel();

        bricks = levelManager.getActiveBricks();

        ball.reset(paddle);

        loop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastTime == 0) {
                    lastTime = now;
                    return;
                }
                double dt = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;

                if (isLevelComplete()) {
                    handleLevelComplete();
                }
                handleInput();
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
        paddle.update(deltaTime);
        ball.update(deltaTime);

        if (ball.isReset()) {
            ball.setX(paddle.getX() + paddle.getWidth() / 2 - ball.getWidth() / 2);
            ball.setY(paddle.getY() - ball.getHeight());
        }

        ball.bounceOffWithPaddle(paddle);

        for (Brick brick : bricks) {
            if (!brick.isDestroyed() && ball.bounceOffWithBrick(brick)) {
                brick.takeHit();
                break;
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
        paddle.render(gc);
        ball.render(gc);

        for (Brick brick : bricks) {
            brick.render(gc);
            //brick.drawHitBox(gc, brick.getX(), brick.getY(), brick.getWidth(), brick.getHeight());
        }

        gc.setStroke(Color.RED);
        gc.setLineWidth(1);
        //gc.strokeRect(paddle.getX() + paddle.getWidth() / 2, 0, 1, canvas.getHeight());
        gc.setLineWidth(5);
        //gc.strokeRect(0, 0, canvas.getWidth(), canvas.getHeight());
        //System.out.println("Số lượng gạch còn lại: " + bricks.size());
        //ball.drawHitBox(gc, ball.getX(), ball.getY(), ball.getRadius() * 2, ball.getRadius() * 2);
        // paddle.drawHitBox(gc, paddle.getX(), paddle.getY(), paddle.getWidth(), paddle.getHeight());
    }

    private void checkChainExplosions() {
        /**
         * list brick bi no (moi nhat dc them vao)
         */
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
            end();
            System.out.println("Level Complete!");
            boolean hasNext = GameSession.getInstance().nextLevel();
            if (hasNext) {
                start();
            } else {
                System.out.println("Game Complete! All levels finished!");
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
}