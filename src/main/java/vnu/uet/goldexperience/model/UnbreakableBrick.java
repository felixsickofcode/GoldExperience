package vnu.uet.goldexperience.model;

import javafx.scene.canvas.GraphicsContext;
import vnu.uet.goldexperience.effect.DebrisEffect;
import vnu.uet.goldexperience.manager.AssetsManager;
import vnu.uet.goldexperience.manager.SpriteManager;

public class UnbreakableBrick extends Brick {
    private final SpriteManager spriteLoader;
    private boolean hitAnimating = false;

    public UnbreakableBrick(double x, double y, double width, double height) {
        super(x, y, width, height);
        image = AssetsManager.bricks.get(3);
        spriteLoader = new SpriteManager(6, 0, 10);
    }

    @Override
    public void takeHit() {
        if (!hitAnimating) {
            hitAnimating = true;
            spriteLoader.start();
        }
        triggerDestroyEffect();
    }
    @Override
    protected void triggerDestroyEffect() {
        if (!playingBreakEffect) {
            breakEffect = new DebrisEffect(this);
            playingBreakEffect = true;
        }
    }
    @Override
    public void explodeByChainReaction() {
        if (!isDestroyed()) {
            if (!hitAnimating) {
                hitAnimating = true;
                spriteLoader.start();
            }
            triggerDestroyEffect();
        }
    }



    @Override
    public void update(double deltaTime) {
        if (hitAnimating) {
            int prevFrame = spriteLoader.getCurrentFrame();
            spriteLoader.update(deltaTime);
            if (spriteLoader.getCurrentFrame() == 0&& prevFrame == spriteLoader.getFrameCount() - 1) {
                hitAnimating = false;
            }
        }
        if (playingBreakEffect && breakEffect != null) {
            breakEffect.update(deltaTime);
            if (breakEffect.isFinished()) {
                playingBreakEffect = false;
            }
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (image != null) {
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
        if (playingBreakEffect && breakEffect != null) {
            breakEffect.render(gc);
        }
    }
}
