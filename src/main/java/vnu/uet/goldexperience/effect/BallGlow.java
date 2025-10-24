package vnu.uet.goldexperience.effect;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class BallGlow {
    public void render(GraphicsContext gc, double cx, double cy, double radius, double glowPulse) {
        double dynamicGlow = 0.4 + 0.3 * glowPulse;
        double glowSize = radius * 4;
        gc.setGlobalAlpha(dynamicGlow);

        gc.setFill(Color.web("#ffffff", dynamicGlow * 0.8));
        gc.fillOval(cx - glowSize * 0.6, cy - glowSize * 0.6, glowSize * 1.2, glowSize * 1.2);

        gc.setFill(Color.web("#99ffff", dynamicGlow * 0.5));
        gc.fillOval(cx - glowSize * 0.45, cy - glowSize * 0.45, glowSize * 0.9, glowSize * 0.9);

        gc.setFill(Color.web("#00ffff", dynamicGlow * 0.3));
        gc.fillOval(cx - glowSize * 0.3, cy - glowSize * 0.3, glowSize * 0.6, glowSize * 0.6);
    }
}
