package vnu.uet.goldexperience.model;

import javafx.scene.canvas.GraphicsContext;
import vnu.uet.goldexperience.manager.AssetsManager;


public class Brick extends GameObject {
    protected int hitPoints;

    public Brick(double x, double y, double width, double height, int hitPoints) {
        super(x, y, width, height);
        this.hitPoints = hitPoints;
        this.image = AssetsManager.bricks.getFirst();
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
