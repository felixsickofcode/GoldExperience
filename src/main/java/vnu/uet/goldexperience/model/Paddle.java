package vnu.uet.goldexperience.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Paddle extends MovableObject {

    private final double speed = 300;
    private int direction = 0; // -1 = left, 1 = right, 0 = stop

    public Paddle(double x, double y, double width, double height) {
        super(x, y, width, height, 0, 0);
        try {
            this.image = new Image(getClass().getResource("/images/paddle.png").toExternalForm());
        } catch (Exception e) {
            System.err.println("Paddle image not found, using fallback rectangle.");
            this.image = null;
        }
    }

    public void moveLeft()  { direction = -1; }
    public void moveRight() { direction = 1; }
    public void stop()      { direction = 0; }

    @Override
    public void update(double deltaTime) {
        dx = direction * speed;
        move(deltaTime);
    }

    @Override
    public void render(GraphicsContext gc) {
        if (image != null)
            gc.drawImage(image, x, y, width, height);
    }

    public double getSpeed() {
        return speed;
    }
}
