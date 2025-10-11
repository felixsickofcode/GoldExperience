package vnu.uet.goldexperience.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import vnu.uet.goldexperience.core.Constants;

public class Paddle extends MovableObject {

    private final double speed = Constants.PADDLE_SPEED;
    private int direction = 0;

    public Paddle(double x, double y, double width, double height) {
        super(x, y, width, height, 0, 0);
        try {
            this.image = new Image(getClass().getResource("/images/paddle.png").toExternalForm());
        } catch (Exception e) {
            System.err.println("Paddle image not found.");
            this.image = null;
        }
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

    public double getSpeed() {
        return speed;
    }
}
