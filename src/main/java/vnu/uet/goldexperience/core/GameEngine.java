package vnu.uet.goldexperience.core;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
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

        levelManager.loadLevel(1);
        bricks = levelManager.getActiveBricks();
    }

    public void start() {

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
        if (input.isPressed(KeyCode.LEFT) || input.isPressed(KeyCode.J))
            paddle.moveLeft();
        else if (input.isPressed(KeyCode.RIGHT) || input.isPressed(KeyCode.L))
            paddle.moveRight();
        else
            paddle.stop();

        if (input.isPressed(KeyCode.SPACE) && ball.isReset())
            ball.shoot();
    }

    private void update(double deltaTime) {
        paddle.update(deltaTime);
        ball.update(deltaTime);

        if (ball.isReset()) {
            ball.setX(paddle.getX() + paddle.getWidth() / 2 - ball.getWidth() / 2);
            ball.setY(paddle.getY() - ball.getHeight());
        }

        ball.bounceOffWithPaddle(paddle, deltaTime);

        for (Brick brick : bricks) {
            if (!brick.isDestroyed()) {
                if (ball.bounceOffWithBrick(brick, deltaTime)) {
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
    }

    private void render() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        paddle.render(gc);
        ball.render(gc);

        for (Brick brick : bricks) {
            brick.render(gc);
            brick.drawHitBox(gc, brick.getX(), brick.getY(), brick.getWidth(), brick.getHeight());
        }

        gc.setStroke(Color.RED);
        gc.setLineWidth(1);
        gc.strokeRect(paddle.getX() + paddle.getWidth() / 2,
                0, 1, canvas.getHeight());
        gc.setLineWidth(5);
        gc.strokeRect(0, 0, canvas.getWidth(), canvas.getHeight());

        ball.drawHitBox(gc, ball.getX(), ball.getY(), ball.getRadius() * 2, ball.getRadius() * 2);
        paddle.drawHitBox(gc, paddle.getX(), paddle.getY(), paddle.getWidth(), paddle.getHeight());
    }
}