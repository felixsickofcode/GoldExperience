package vnu.uet.goldexperience.model;

import javafx.scene.canvas.GraphicsContext;
import vnu.uet.goldexperience.effect.ExplosionEffect;
import vnu.uet.goldexperience.manager.AssetsManager;
import vnu.uet.goldexperience.manager.SpriteManager;

public class ExplodeBrick extends Brick {
    private final SpriteManager spriteLoader;

    public ExplodeBrick(double x, double y, double width, double height) {
        super(x, y, width, height);
        image = AssetsManager.bricks.get(2);
        spriteLoader = new SpriteManager(6, 0, 20);
        effectType = "Explosion";
    }

    @Override
    public void takeHit() {
        hitPoints--;
        if (isDestroyed()) {
            triggerDestroyEffect();
            notifyDestroyed();
        }
    }

    @Override
    protected void triggerDestroyEffect() {
        if (playingExplosion) {
            return;
        }
        explosionEffect = new ExplosionEffect(this);
        explosionEffect.trigger();
        playingExplosion = true;
    }

    @Override
    public void update(double deltaTime) {
        spriteLoader.update(deltaTime);
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
            int frame = spriteLoader.getCurrentFrame();
            int row = spriteLoader.getRow();

            double srcX = frame * width;
            double srcY = row * height;

            gc.drawImage(
                    image,
                    srcX, srcY, width, height,
                    x, y, width, height
            );
        }
        if (playingExplosion && explosionEffect != null) {
            explosionEffect.render(gc);
        }
    }
}