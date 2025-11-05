package vnu.uet.goldexperience.model.brick;

import javafx.scene.canvas.GraphicsContext;
import vnu.uet.goldexperience.manager.AssetsManager;

public class NormalBrick extends Brick {
    public NormalBrick(double x, double y, double width, double height) {
        super(x, y, width, height);
        this.image = AssetsManager.bricks.getFirst();
        this.effectType = "Debris";
    }

    @Override
    public void takeHit() {
        hitPoints--;
        if (isDestroyed()) {
            triggerDestroyEffect();
        }
    }

    @Override
    public void update(double deltaTime) {
        if (playingBreakEffect && breakEffect != null) {
            breakEffect.update(deltaTime);
            if (breakEffect.isFinished()) {
                playingBreakEffect = false;
                breakEffect = null;
            }
        }
        if (playingExplosion && explosionEffect != null) {
            explosionEffect.update(deltaTime);
            if (explosionEffect.isFinished()) {
                playingExplosion = false;
                explosionEffect = null;
            }
        }
    }
    @Override
    public void render(GraphicsContext gc) {
        if (!isDestroyed() && image != null) {
            gc.drawImage(image, x, y);
        }
        if (playingExplosion && explosionEffect != null) {
            explosionEffect.render(gc);
        }
        if (playingBreakEffect && breakEffect != null) {
            breakEffect.render(gc);
        }
    }
}
