package vnu.uet.goldexperience.effect;

import javafx.scene.canvas.GraphicsContext;
import java.util.Random;

public class PaddleShakeEffect {
    private double shakeTime = 0;
    private double shakeIntensity = 0;
    private final Random random = new Random();

    public void trigger(double duration, double intensity) {
        this.shakeTime = duration;
        this.shakeIntensity = intensity;
    }

    public void update(double deltaTime) {
        if (shakeTime > 0) {
            shakeTime -= deltaTime;
            if (shakeTime < 0) shakeTime = 0;
        }
    }

    public void apply(GraphicsContext gc) {
        if (shakeTime <= 0) return;

        double offsetX = (random.nextDouble() - 0.5) * shakeIntensity;
        double offsetY = (random.nextDouble() - 0.5) * shakeIntensity;
        gc.translate(offsetX, offsetY);
    }

    public boolean isActive() {
        return shakeTime > 0;
    }
}
