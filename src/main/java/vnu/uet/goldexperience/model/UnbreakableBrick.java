package vnu.uet.goldexperience.model;

import javafx.scene.canvas.GraphicsContext;
import vnu.uet.goldexperience.effect.brick.DebrisEffect;
import vnu.uet.goldexperience.effect.brick.DestructionEffect;
import vnu.uet.goldexperience.manager.AssetsManager;
import vnu.uet.goldexperience.manager.SpriteManager;

public class UnbreakableBrick extends Brick {
    private final SpriteManager spriteLoader;
    private boolean hitAnimating = false;
    private DestructionEffect destructionEffect;


    private boolean playingDestructionEffect;

    public UnbreakableBrick(double x, double y, double width, double height) {
        super(x, y, width, height);
        image = AssetsManager.bricks.get(3);
        spriteLoader = new SpriteManager(6, 0, 10);
        playingDestructionEffect = false;
    }

    @Override
    public boolean canBeRemoved() {
        if ("Destruction".equals(effectType)) {
            return destructionEffect != null && destructionEffect.isFinished();
        }
        return false;
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

    public void destroy() {
        hitPoints = 0;
        triggerDestructionEffect();
    }

    private void triggerDestructionEffect() {
        if (!playingDestructionEffect) {
            this.effectType = "Destruction";
            this.destructionEffect = new DestructionEffect(this);
            playingDestructionEffect = true;
        }
    }

    public DestructionEffect getDestructionEffect() {
        return destructionEffect;
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
            if (spriteLoader.getCurrentFrame() == 0 && prevFrame == spriteLoader.getFrameCount() - 1) {
                hitAnimating = false;
            }
        }
        if (playingBreakEffect && breakEffect != null) {
            breakEffect.update(deltaTime);
            if (breakEffect.isFinished()) {
                playingBreakEffect = false;
            }
        }
        if (playingDestructionEffect && destructionEffect != null) {
            destructionEffect.update(deltaTime);
            if (destructionEffect.isFinished()) {
                playingDestructionEffect = false;
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
        if (playingBreakEffect && breakEffect != null) {
            breakEffect.render(gc);
        }
        if (playingDestructionEffect) {
            destructionEffect.render(gc);
        }
    }
}
