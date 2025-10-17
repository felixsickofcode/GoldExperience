package vnu.uet.goldexperience.model;

import javafx.scene.canvas.GraphicsContext;
import vnu.uet.goldexperience.core.Constants;
import vnu.uet.goldexperience.manager.AssetsManager;


public class Paddle extends MovableObject {

    private final double speed = Constants.PADDLE_SPEED;
    private int direction = 0;

    public Paddle(double x, double y, double width, double height) {
        super(x, y, width, height, 0, 0);
        this.image = AssetsManager.paddles.get(2);
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
    }

    public void moveRight() {
        direction = 1;
    }

    public void stop() {
        direction = 0;
    }

    @Override
    public void update(double deltaTime) {
        dx = direction * speed;
        move(deltaTime);
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
            gc.drawImage(image, x, y - 5);
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
        setWidth(newWidth);
        setImage(AssetsManager.paddles.get(size));
    }

    public double getSpeed() {
        return speed;
    }
}