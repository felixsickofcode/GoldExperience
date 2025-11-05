package vnu.uet.goldexperience.model;

import javafx.scene.canvas.GraphicsContext;
import vnu.uet.goldexperience.effect.brick.ExplosionEffect;
import vnu.uet.goldexperience.effect.brick.DebrisEffect;

import java.util.ArrayList;
import java.util.List;


public abstract class Brick extends GameObject {


    protected int hitPoints;
    protected DebrisEffect breakEffect;
    protected boolean playingBreakEffect;
    protected ExplosionEffect explosionEffect;
    protected boolean playingExplosion = false;
    protected String effectType;

    private final List<BrickListener> listeners = new ArrayList<>();

    public Brick(double x, double y, double width, double height) {
        super(x, y, width, height);
        this.hitPoints = 1;
    }

    // OBSERVER
    public interface BrickListener {
        void onBrickDestroyed(Brick brick);
    }

    public void addListener(BrickListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(BrickListener listener) {
        listeners.remove(listener);
    }

    protected void notifyDestroyed() {
        for (BrickListener listener : listeners) {
            listener.onBrickDestroyed(this);
        }
    }

    public abstract void takeHit();
    
    protected void triggerDestroyEffect() {
        if (playingBreakEffect || playingExplosion) {
            return;
        }
        breakEffect = new DebrisEffect(this);
        playingBreakEffect = true;
        effectType = "Debris";
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
        } else if ("Debris".equals(effectType)) {
            return breakEffect == null || breakEffect.isFinished();
        }
        return true;
    }

    @Override
    public abstract void update(double deltaTime);

    @Override
    public abstract void render(GraphicsContext gc);

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
            notifyDestroyed();
        }
    }

    public ExplosionEffect getExplosionEffect() {
        return explosionEffect;
    }

    public DebrisEffect getBreakEffect() {
        return breakEffect;
    }
}