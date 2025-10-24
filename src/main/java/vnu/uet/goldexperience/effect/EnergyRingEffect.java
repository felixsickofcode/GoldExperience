package vnu.uet.goldexperience.effect;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class EnergyRingEffect {
    private final double x, y;
    private double radius;
    private double life;
    private final boolean isStrong;

    public EnergyRingEffect(double x, double y) {
        this(x, y, false);
    }

    public EnergyRingEffect(double x, double y, boolean isStrong) {
        this.x = x;
        this.y = y;
        this.radius = 10;
        this.life = 1.0;
        this.isStrong = isStrong;
    }

    public void update(double deltaTime) {
        radius += (isStrong ? 150 : 100) * deltaTime;
        life -= deltaTime * (isStrong ? 1.5 : 2.0);
    }

    public boolean isDead() { return life <= 0; }

    public void render(GraphicsContext gc) {
        gc.setGlobalAlpha(life * (isStrong ? 0.6 : 0.4));
        gc.setStroke(Color.CYAN);
        gc.setLineWidth(isStrong ? 5 : 3);
        gc.strokeOval(x - radius, y - radius, radius * 2, radius * 2);

        if (isStrong) {
            gc.setGlobalAlpha(life * 0.5);
            gc.setStroke(Color.WHITE);
            double innerRadius = radius * 0.7;
            gc.setLineWidth(2);
            gc.strokeOval(x - innerRadius, y - innerRadius, innerRadius * 2, innerRadius * 2);
        }
    }
}
