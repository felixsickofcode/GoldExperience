package vnu.uet.goldexperience.effect.paddle;

import javafx.scene.canvas.GraphicsContext;

public class PaddleEffect {
    private EnergyRingEffect energyRings;
    private final PaddleFlashEffect flash;
    private final PaddleShakeEffect shake;

    public PaddleEffect(double width, double height) {
        flash = new PaddleFlashEffect(0, 0, width, height);
        shake = new PaddleShakeEffect();
    }

    public void update(double x, double y, double deltaTime) {
        if (energyRings != null)
            energyRings.update(deltaTime);
        flash.setPosition(x, y);
        flash.update(deltaTime);
        shake.update(deltaTime);
    }

    public void onBallHit(double x, double y) {
        energyRings = new EnergyRingEffect(x, y, true);
        flash.trigger();
        shake.trigger(0.15, 10);
    }

    public void render(GraphicsContext gc) {
        gc.save();
        if (energyRings != null)
            energyRings.render(gc);
        shake.apply(gc);
        flash.render(gc);

        gc.restore();
    }
}
