package vnu.uet.goldexperience.model;

import javafx.scene.canvas.GraphicsContext;
import vnu.uet.goldexperience.core.Constants;
import vnu.uet.goldexperience.core.GameContext;

public abstract class PowerUp extends MovableObject {

    private final PowerUpType type;
    private final PowerUpEffect applyEffect;
    private final PowerUpRemoval removeEffect;

    public PowerUp(double x, double y, double width, double height, PowerUpType type) {
        super(x, y, width, height, 0, Constants.POWER_UP_DROP_SPEED);
        this.type = type;
        this.applyEffect = type.getApplyEffect();
        this.removeEffect = type.getRemoveEffect();
        this.image = type.getImage();
    }

    public void applyEffect(GameContext context) {
        applyEffect.apply(context);
    }

    public void removeEffect(GameContext context) {
        if (removeEffect != null)
            removeEffect.remove(context);
    }

    public PowerUpType getType() {
        return type;
    }

    @Override
    public void update(double deltaTime) {
        move(deltaTime);
    }

    @Override
    public void render(GraphicsContext gc) {
        if (image != null) {
            gc.drawImage(image, x, y, width, height);
        } else {
            gc.setFill(javafx.scene.paint.Color.LIMEGREEN);
            gc.fillOval(x, y, width, height);
        }
    }
}