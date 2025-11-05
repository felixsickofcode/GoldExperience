package vnu.uet.goldexperience.model;

import vnu.uet.goldexperience.core.Constants;
import vnu.uet.goldexperience.manager.AssetsManager;


public class StrongBrick extends MediumBrick {
    private double regenerationTimer;
    private boolean isRegenerating;

    public StrongBrick(double x, double y, double width, double height) {
        super(x, y, width, height);
        image = AssetsManager.bricks.get(5);
        this.hitPoints = Math.random() < 0.8 ? 4 : 6;
    }

    @Override
    public void takeHit() {
        hitPoints--;

        regenerationTimer = 0;
        isRegenerating = false;

        if (isDestroyed() && !playingBreakEffect && !playingExplosion) {
            triggerDestroyEffect();
            notifyDestroyed();
        } else if (!isDestroyed()) {
            triggerFlashEffect();
            if (hitPoints <= Constants.REGENERATION_THRESHOLD) {
                image = AssetsManager.bricks.get(4);
                isRegenerating = true;
            }
        }
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);

        if (isRegenerating && hitPoints <= Constants.REGENERATION_THRESHOLD && !isDestroyed()) {
            regenerationTimer += deltaTime;

            if (regenerationTimer >= Constants.REGENERATION_TIME) {
                hitPoints++;
                regenerationTimer = 0;
                if (hitPoints == Constants.MAX_HIT_POINTS) {
                    isRegenerating = false;
                    image = AssetsManager.bricks.get(5);
                }
                triggerFlashEffect();
            }
        }
    }
}
