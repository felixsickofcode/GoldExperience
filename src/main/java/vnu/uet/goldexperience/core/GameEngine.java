package vnu.uet.goldexperience.core;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import vnu.uet.goldexperience.controller.InputHandler;
import vnu.uet.goldexperience.model.*;

import java.util.ArrayList;
import java.util.List;

public class GameEngine {
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final InputHandler input;

    private Paddle paddle;
    private Ball ball;
    private final List<Brick> bricks = new ArrayList<>();

    private AnimationTimer loop;
    private long lastTime = 0;

    public GameEngine(Canvas canvas, InputHandler input) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.input = input;
        initObjects();
    }

    private void initObjects() {
        paddle = new Paddle(canvas.getWidth() / 2, canvas.getHeight() - 50, 100, 20);
        ball = new Ball(paddle.getX() + paddle.getWidth() / 2 - 10.5, paddle.getY() - 21, 10.5);
        paddle.setX(canvas.getWidth() / 2 - paddle.getWidth() / 2);
        int cols = 10, rows = 5;
        double brickW = 60, brickH = 20;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                bricks.add(new Brick(50 + c * (brickW + 5), 50 + r * (brickH + 5), brickW, brickH, 1));
            }
        }
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

    private void handleInput() {
        // Paddle direction
        if (input.isPressed(KeyCode.LEFT) || input.isPressed(KeyCode.J))
            paddle.moveLeft();
        else if (input.isPressed(KeyCode.RIGHT) || input.isPressed(KeyCode.L))
            paddle.moveRight();
        else
            paddle.stop();

        // Ball shoot
        if (input.isPressed(KeyCode.SPACE) && ball.isReset())
            ball.shoot();
    }

    private void update(double dt) {
        if (paddle != null) {
            paddle.update(dt);

            // giới hạn biên
            if (paddle.getX() < 0) paddle.setX(0);
            if (paddle.getX() + paddle.getWidth() > canvas.getWidth())
                paddle.setX(canvas.getWidth() - paddle.getWidth());
        }

        if (ball != null) {
            if (!ball.isReset()) ball.update(dt);

            if (ball.isReset()) {
                ball.setX(paddle.getX() + paddle.getWidth() / 2 - ball.getWidth() / 2);
                ball.setY(paddle.getY() - ball.getHeight());
            }

            // va chạm paddle
            if (paddle != null && ball.checkCollision(paddle))
                ball.setDy(-Math.abs(ball.getDy()));

            // va chạm brick
            for (GameObject obj : bricks) {
                if (obj instanceof Brick brick && !brick.isDestroyed() && ball.checkCollision(brick)) {
                    brick.takeHit();
                    ball.setDy(-ball.getDy());
                }
            }

            // va chạm biên
            double radius = ball.getWidth() / 2.0;
            if (ball.getX() - radius <= 0) {
                ball.setX(radius);
                ball.setDx(Math.abs(ball.getDx()));
            }
            if (ball.getX() + radius >= canvas.getWidth()) {
                ball.setX(canvas.getWidth() - radius);
                ball.setDx(-Math.abs(ball.getDx()));
            }
            if (ball.getY() - radius <= 0) {
                ball.setY(radius);
                ball.setDy(Math.abs(ball.getDy()));
            }
            if (ball.getY() + radius >= canvas.getHeight()) {
                ball.reset(paddle);
            }
        }

        // update bricks
        for (GameObject obj : bricks) {
            obj.update(dt);
        }
    }

    private void render() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        paddle.render(gc);
        ball.render(gc);
        for (Brick brick : bricks) {
            brick.render(gc);
        }
    }
}
