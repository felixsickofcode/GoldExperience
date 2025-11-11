package vnu.uet.goldexperience.model;

import javafx.scene.canvas.GraphicsContext;
import vnu.uet.goldexperience.core.Constants;
import vnu.uet.goldexperience.manager.AssetsManager;

public class Bullet extends MovableObject {

    public Bullet(double x, double y, double width, double height, double dx, double dy) {
        super(x, y, width, height, dx, dy);

        if (!AssetsManager.bullets.isEmpty()) {
            this.image = AssetsManager.bullets.getFirst();
        }
    }

    public boolean isOffScreen() {
        return getY() + getHeight() < 0;
    }

    public boolean checkCollide(GameObject other) {
        return x < other.getX() + other.getWidth() &&
                x + width > other.getX() &&
                y < other.getY() + other.getHeight() &&
                y + height > other.getY();
    }

    @Override
    public void update(double deltaTime) {
        move(deltaTime);
    }

    @Override
    public void render(GraphicsContext gc) {
        if (image != null) {
            gc.drawImage(image, x, y);
        }
    }
}