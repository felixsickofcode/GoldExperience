package vnu.uet.goldexperience.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;
import vnu.uet.goldexperience.core.Constants;
import vnu.uet.goldexperience.effect.PaddleEffect;
import vnu.uet.goldexperience.manager.AssetsManager;

public class Paddle extends MovableObject {
    private final double speed = Constants.PADDLE_SPEED;
    private int direction = 0;
    private PaddleEffect effect;
    private double targetX = -1;

    public Paddle(double x, double y, double width, double height) {
        super(x, y, width, height, 0, 0);
        this.image = AssetsManager.paddles.get(2);
        effect = new PaddleEffect(width, height);
    }

    public void extendPaddle() {
        if (getSize() >= Constants.MAX_PADDLE_SIZE) {
            return;
        }
        setSize(getSize() + 1);
    }

    public void narrowPaddle() {
        if (getSize() <= Constants.MIN_PADDLE_SIZE) {
            return;
        }
        setSize(getSize()-1);
    }


    public void moveLeft() {
        direction = -1;
        targetX = -1;
    }

    public void moveRight() {
        direction = 1;
        targetX = -1;
    }

    public void stop() {
        direction = 0;
    }

    public void setTargetX(double x) {
        if (x < 0) {
            this.targetX = Math.max(0,x);
        }
        else if (x > 576) {
                this.targetX = Math.min(576,x);
        } else {
            this.targetX = x;
        }
        this.direction = 0;
    }

    @Override
    public void update(double deltaTime) {
        if (targetX >= 0) {
            double diff = targetX - x;
            if (Math.abs(diff) > 0.5) {
                x += diff * Constants.MOUSE_LERP_SPEED;
            } else {
                x = targetX;
            }
        } else {
            dx = direction * speed;
            move(deltaTime);
        }
        effect.update(x, y, deltaTime);
        handlePaddleEdgeCollision();
    }

    public void handlePaddleEdgeCollision() {
        if (x < 0) setX(0);
        if (x + width > Constants.GAMEPLAYZONE_WIDTH)
            setX(Constants.GAMEPLAYZONE_WIDTH - width);
    }

    @Override
    public void render(GraphicsContext gc) {

        if (image != null)
            gc.drawImage(image, x, y, width, height);
        effect.render(gc);
    }

    public int getSize() {
        switch ((int) width) {
            case (int) Constants.TINY_PADDLE_WIDTH -> {
                return 0;
            }
            case (int) Constants.SMALL_PADDLE_WIDTH -> {
                return 1;
            }
            case (int) Constants.MEDIUM_PADDLE_WIDTH -> {
                return 2;
            }
            case (int) Constants.LARGE_PADDLE_WIDTH -> {
                return 3;
            }
            case (int) Constants.BIG_PADDLE_WIDTH -> {
                return 4;
            }
        }
        return 2;
    }

    public void setSize(int size) {
        double newWidth;
        switch (size) {
            case 0 -> newWidth = Constants.TINY_PADDLE_WIDTH;
            case 1 -> newWidth = Constants.SMALL_PADDLE_WIDTH;
            case 2 -> newWidth = Constants.MEDIUM_PADDLE_WIDTH;
            case 3 -> newWidth = Constants.LARGE_PADDLE_WIDTH;
            case 4 -> newWidth = Constants.BIG_PADDLE_WIDTH;
            default -> newWidth = Constants.MEDIUM_PADDLE_WIDTH;
        }
        effect = new PaddleEffect(newWidth, height);
        setWidth(newWidth);
        setImage(AssetsManager.paddles.get(size));
    }
    public void onBallCollision(Ball ball) {
        effect.onBallHit(ball.getX(), ball.getY());
    }
    public double getSpeed() {
        return speed;
    }


}