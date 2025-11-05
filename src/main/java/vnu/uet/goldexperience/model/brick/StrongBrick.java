package vnu.uet.goldexperience.model.brick;

import javafx.scene.canvas.GraphicsContext;
import vnu.uet.goldexperience.core.Constants;
import vnu.uet.goldexperience.effect.brick.RegenerateEffect;
import vnu.uet.goldexperience.effect.brick.ReviveEffect;
import vnu.uet.goldexperience.manager.AssetsManager;


public class StrongBrick extends MediumBrick {
    private double regenerationTimer;
    private boolean isRegenerating;
    private final RegenerateEffect regenerateEffect;
    private final ReviveEffect reviveEffect;
    public StrongBrick(double x, double y, double width, double height) {
        super(x, y, width, height);
        image = AssetsManager.bricks.get(5);
        this.hitPoints = Math.random() < 0.8 ? 4 : 6;
        this.regenerateEffect = new RegenerateEffect(this);
        this.reviveEffect = new ReviveEffect(this);
    }

    @Override
    public void takeHit() {
        hitPoints--;

        regenerationTimer = 0;
        isRegenerating = false;
        regenerateEffect.end();
        if (isDestroyed() && !playingBreakEffect && !playingExplosion) {
            triggerDestroyEffect();
            notifyDestroyed();
        } else if (!isDestroyed()) {
            triggerFlashEffect();
            notifyDestroyed();
            if (hitPoints <= Constants.REGENERATION_THRESHOLD) {
                image = AssetsManager.bricks.get(4);
                isRegenerating = true;
                regenerateEffect.trigger();
            }
        }
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        if (isRegenerating) {
            regenerateEffect.update(deltaTime);
        }
        reviveEffect.update(deltaTime);
        if (isRegenerating && hitPoints <= Constants.REGENERATION_THRESHOLD && !isDestroyed()) {
            regenerationTimer += deltaTime;

            if (regenerationTimer >= Constants.REGENERATION_TIME) {
                hitPoints++;
                regenerationTimer = 0;
                if (hitPoints == Constants.MAX_HIT_POINTS) {
                    isRegenerating = false;
                    image = AssetsManager.bricks.get(5);
                    regenerateEffect.end();
                    reviveEffect.trigger();
                }
                triggerFlashEffect();
            }
        }
    }
    @Override
    public void render(GraphicsContext gc) {
        super.render(gc);
        if (isRegenerating && regenerateEffect != null) {
            regenerateEffect.render(gc);
        }
        reviveEffect.render(gc);
    }
}
