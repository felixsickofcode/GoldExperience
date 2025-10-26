package vnu.uet.goldexperience.effect;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.GaussianBlur;
import vnu.uet.goldexperience.core.ChapterTheme;

public class BorderFlashEffect {

    private double pulse = 0.0;
    private final double FADE_RATE = 0.04;

    public BorderFlashEffect() {
    }

    public void trigger() {
        this.pulse = 1.0;
    }

    public void update() {
        if (this.pulse > 0) {
            this.pulse -= FADE_RATE;
            if (this.pulse < 0) {
                this.pulse = 0;
            }
        }
    }

    public boolean isActive() {
        return this.pulse > 0;
    }

    public void render(GraphicsContext gc, double width, double height) {
        if (!isActive()) {
            return;
        }

        gc.save();
        gc.setStroke(ChapterTheme.PURE_WHITE.deriveColor(0, 1, 1, this.pulse * 0.8));
        gc.setLineWidth(8);
        gc.setEffect(new GaussianBlur(this.pulse * 15));
        gc.strokeRect(2, 2, width - 4, height - 4);
        gc.restore();
    }
}