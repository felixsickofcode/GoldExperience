package vnu.uet.goldexperience.model;

import javafx.scene.canvas.GraphicsContext;
import vnu.uet.goldexperience.core.Constants;
import vnu.uet.goldexperience.core.GameContext;

public abstract class PowerUp extends MovableObject {

    private final PowerUpType type;
    private final long duration;
    private final PowerUpEffect applyEffect;
    private final PowerUpRemoval removeEffect;

    public PowerUp(double x, double y, double width, double height, PowerUpType type) {
        super(x, y, width, height, 0, Constants.POWER_UP_DROP_SPEED);
        this.type = type;
        this.applyEffect = type.getApplyEffect();
        this.removeEffect = type.getRemoveEffect();
        this.image = type.getImage();
        this.duration = type.getDuration();
    }

    public void applyEffect(GameContext context) {
        applyEffect.apply(context);
    }

    public void removeEffect(GameContext context) {
        if (removeEffect != null)
            removeEffect.remove(context);
    }

    public boolean isPermanent() {
        return duration == 0;
    }

    public PowerUpType getType() {
        return type;
    }

    public long getDuration() {
        return duration;
    }

    @Override
    public void update(double deltaTime) {
        move(deltaTime);
    }

    @Override
    public void render(GraphicsContext gc) {
        if (image != null)
            gc.drawImage(image, x, y);
    }
}