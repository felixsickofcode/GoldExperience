package vnu.uet.goldexperience.model;

import javafx.scene.canvas.GraphicsContext;
import vnu.uet.goldexperience.effect.ExplosionEffect;
import vnu.uet.goldexperience.manager.AssetsManager;
import vnu.uet.goldexperience.effect.ParticleEffect;


public class Brick extends GameObject {
    protected int hitPoints;
    protected ParticleEffect breakEffect;
    protected boolean playingBreakEffect;
    protected ExplosionEffect explosionEffect;
    protected boolean playingExplosion = false;
    protected String effectType;

    public Brick(double x, double y, double width, double height) {
        super(x, y, width, height);
        this.hitPoints = 1;
        this.image = AssetsManager.bricks.getFirst();
        this.effectType = "Particle";
    }

    public void takeHit() {
        hitPoints--;
        if (isDestroyed()) {
            triggerDestroyEffect();
        }
    }


    protected void triggerDestroyEffect() {
        if (playingBreakEffect || playingExplosion) {
            return;
        }
        breakEffect = new ParticleEffect(this);
        playingBreakEffect = true;
        effectType = "Particle";
        System.out.println("Tạo hiệu ứng vỡ tại: " + x + ", " + y);

    }

    public boolean isDestroyed() {
        return hitPoints <= 0;
    }

    public boolean canBeRemoved() {
        if (!isDestroyed()) {
            return false;
        }
        if ("Explosion".equals(effectType)) {
            return explosionEffect == null || explosionEffect.isFinished();
        } else if ("Particle".equals(effectType)) {
            return breakEffect == null || breakEffect.isFinished();
        }

        return true;
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

    public boolean isInExplosionRadius(ExplosionEffect explosion) {
        if (isDestroyed()) {
            return false;
        }

        double radius = explosion.getCurrentInnerRadius();
        if (radius <= 0) {
            return false;
        }

        double brickCenterX = x + width / 2;
        double brickCenterY = y + height / 2;

        double explosionX = explosion.getX();
        double explosionY = explosion.getY();
        double distance = Math.hypot(brickCenterX - explosionX, brickCenterY - explosionY);

        return distance <= radius;
    }

    /**
     * Chain reaction
     * Subclass override de quyet dinh no hay k
     */
    public void explodeByChainReaction() {
        if (!isDestroyed()) {
            hitPoints = 0;
            triggerDestroyEffect();
        }
    }

    public ExplosionEffect getExplosionEffect() {
        return explosionEffect;
    }
}