package vnu.uet.goldexperience.core;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import vnu.uet.goldexperience.manager.InputManager;
import vnu.uet.goldexperience.model.*;

import java.util.ArrayList;
import java.util.List;

public class GameEngine {
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final InputManager input;

    private Paddle paddle;
    private Ball ball;
    private final List<Brick> bricks = new ArrayList<>();

    private AnimationTimer loop;
    private long lastTime = 0;

    public GameEngine(Canvas canvas, InputManager input) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.input = input;
        initObjects();
    }

    private void initObjects() {
        paddle = new Paddle(Constants.PADDLE_INIT_POSITION, canvas.getHeight() - 120,
                Constants.NORMAL_PADDLE_WIDTH, Constants.NORMAL_PADDLE_HEIGHT);

        ball = new Ball(Constants.BALL_INIT_POSITION,
                paddle.getY() - Constants.NORMAL_BALL_SIZE, Constants.NORMAL_BALL_SIZE);

        int cols = 10, rows = 5;
        double brickW = Constants.NORMAL_BRICK_WIDTH, brickH = Constants.NORMAL_BRICK_HEIGHT;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (r == 0)
                    bricks.add(new UnbreakableBrick(50 + c * (brickW + 5), 50 + r * (brickH + 5), brickW, brickH));
                else
                    bricks.add(new Brick(50 + c * (brickW + 5), 50 + r * (brickH + 5), brickW, brickH, 1));
            }
        }
    }

    public void start() {

        loop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                System.out.println(1);
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
        //UPDATE PADDLE
        if (paddle != null) {
            paddle.update(deltaTime);
        }
        //UPDATE BALL
        if (ball != null) {
            if (!ball.isReset()) ball.update(deltaTime);

            if (ball.isReset()) {
                ball.setX(paddle.getX() + paddle.getWidth() / 2 - ball.getWidth() / 2);
                ball.setY(paddle.getY() - ball.getHeight());
            }

            ball.bounceOffWithPaddle(paddle, deltaTime);

            for (GameObject obj : bricks) {
                if (obj instanceof Brick brick && !brick.isDestroyed() && ball.checkCollision(brick)) {

                    if (ball.bounceOffWithBrick(brick, deltaTime)) {
                        brick.takeHit();
                        break;
                    }
                }
            }

            if (ball.getY() >= canvas.getHeight()) {
                ball.reset(paddle);
            }
        }
        //UPDATE BRICK
        for (GameObject obj : bricks) {
            obj.update(deltaTime);
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
        gc.setLineWidth(5);
        gc.strokeRect(0, 0, canvas.getWidth(), canvas.getHeight());

        ball.drawHitBox(gc, ball.getX(), ball.getY(), ball.getRadius() * 2, ball.getRadius() * 2);
        paddle.drawHitBox(gc, paddle.getX(), paddle.getY(), paddle.getWidth(), paddle.getHeight());
    }

}
