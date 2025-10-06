package vnu.uet.goldexperience.controller;

import javafx.animation.AnimationTimer;

import vnu.uet.goldexperience.model.Ball;
import vnu.uet.goldexperience.model.Paddle;
import vnu.uet.goldexperience.view.GameView;
import vnu.uet.goldexperience.core.Constants;

public class GameController {

    private Ball ball;
    private Paddle paddle;
    private GameView view;
    private InputHandler input;

    public Ball getBall() {
        return ball;
    }

    public Paddle getPaddle() {
        return paddle;
    }

    public GameController(GameView view, InputHandler input) {
        this.view = view;
        this.ball = new Ball(420, 300);
        this.paddle = new Paddle(420, 650);
        this.input = input;
    }

    public void start() {
        new AnimationTimer() {
            private long lastTime = System.nanoTime();

            @Override
            public void handle(long now) {
                double deltaTime = (now - lastTime) / 1e9;
                if (deltaTime < Constants.TIME_PER_FRAME / 1e9) return;

                update(deltaTime);
                render();

                lastTime = now;
            }
        }.start();
    }

    public void update(double deltaTime) {

        ball.update(deltaTime);
        checkCollision();

        if (input != null) {
            if (input.isLeftPressed()) {
                System.out.println("Left pressed");
                paddle.moveLeft(deltaTime);
            }
            if (input.isRightPressed())
            {
                System.out.println("Right pressed");
                paddle.moveRight(deltaTime);
            }
        }
        else System.out.println("Input null");
    }

    private void render() {
        view.render(ball, paddle);
    }

    private void checkCollision() {
        if (ball.getX() < 40 || ball.getX() + ball.size > Constants.GAME_WIDTH) {
            ball.bounceX();
        }

        if (ball.getY() < 0)
            ball.bounceY();

        if (ball.getY() + ball.size >= paddle.getY()
                && ball.getX() + ball.size >= paddle.getX()
                && ball.getX() <= paddle.getX() + paddle.getW()) {
            ball.bounceY();
        }
    }
}
