package vnu.uet.goldexperience.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;


public class Brick extends GameObject {
    protected int hitPoints;
    protected Image image;

    public Brick(double x, double y, double width, double height, int hitPoints) {
        super(x, y, width, height);
        this.hitPoints = hitPoints;
        this.image = new Image(getClass().getResource("/images/brick_1.png").toExternalForm());
    }

    public void takeHit() {
        hitPoints--;
    }

    public boolean isDestroyed() {
        return hitPoints <= 0;
    }

    @Override
    public void update(double deltaTime) {
        // bricks đứng yên
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!isDestroyed() && image != null) {
            gc.drawImage(image, x, y);
        }
    }
}
