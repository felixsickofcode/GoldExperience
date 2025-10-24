package vnu.uet.goldexperience.effect;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class PaddleFlashEffect {
    private double x, y, width, height;
    private double alpha = 0;
    private boolean active = false;

    public PaddleFlashEffect(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void trigger() {
        alpha = 1.0;
        active = true;
    }

    public void update(double deltaTime) {
        if (!active) return;

        alpha -= deltaTime * 3; // giảm độ sáng dần
        if (alpha <= 0) {
            alpha = 0;
            active = false;
        }
    }

    public void render(GraphicsContext gc) {
        if (!active || alpha <= 0) return;

        gc.setGlobalAlpha(alpha);
        gc.setFill(Color.WHITE);
        gc.fillRect(x, y, width, height);
        gc.setGlobalAlpha(1.0);
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
