package vnu.uet.goldexperience.model;

import javafx.scene.canvas.GraphicsContext;
import vnu.uet.goldexperience.effect.brick.FlashEffect;
import vnu.uet.goldexperience.manager.AssetsManager;

public class MediumBrick extends Brick {
    protected FlashEffect flashEffect;
    protected boolean playingFlashEffect;

    public MediumBrick(double x, double y, double width, double height) {
        super(x, y, width, height);
        this.hitPoints = Math.random() < 0.4 ? 2 : 3;
        this.image = AssetsManager.bricks.get(1);
        this.playingFlashEffect = false;
    }

    @Override
    public void takeHit() {
        hitPoints--;
        if (isDestroyed() && !playingBreakEffect && !playingExplosion) {
            triggerDestroyEffect();
            notifyDestroyed();
        } else if (!isDestroyed()) {
            triggerFlashEffect();
            notifyDestroyed();
        }
    }

    protected void triggerFlashEffect() {
        flashEffect = new FlashEffect(this);
        playingFlashEffect = true;
    }

    @Override
    public void update(double deltaTime) {
        // Update flash effect
        if (playingFlashEffect && flashEffect != null) {
            flashEffect.update(deltaTime);
            if (flashEffect.isFinished()) {
                playingFlashEffect = false;
            }
        }
        // Destroy effect
        if (playingBreakEffect && breakEffect != null) {
            breakEffect.update(deltaTime);
            if (breakEffect.isFinished()) {
                playingBreakEffect = false;
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
            if (playingFlashEffect && flashEffect != null) {
                flashEffect.render(gc);
            }
        }
        if (playingExplosion && explosionEffect != null) {
            explosionEffect.render(gc);
        }
        if (playingBreakEffect && breakEffect != null) {
            breakEffect.render(gc);
        }
    }
}