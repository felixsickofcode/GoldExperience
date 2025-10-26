package vnu.uet.goldexperience.effect;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.paint.Color;
import vnu.uet.goldexperience.core.ChapterTheme;
import vnu.uet.goldexperience.manager.GameSession;

public class BorderFlashEffect {

    private double pulseTop = 0.0;
    private double pulseLeft = 0.0;
    private double pulseRight = 0.0;

    private final double FADE_RATE = 0.05;
    private final double TRIGGER_STRENGTH = 1.8;

    public BorderFlashEffect() {
    }

    public void trigger(GameSession.HitSide side) {
        switch (side) {
            case TOP:
                this.pulseTop = TRIGGER_STRENGTH;
                break;
            case LEFT:
                this.pulseLeft = TRIGGER_STRENGTH;
                break;
            case RIGHT:
                this.pulseRight = TRIGGER_STRENGTH;
                break;
        }
    }

    private double fadePulse(double pulse) {
        if (pulse > 0) {
            pulse -= FADE_RATE;
            if (pulse < 0) pulse = 0;
        }
        return pulse;
    }

    public void update() {
        this.pulseTop = fadePulse(this.pulseTop);
        this.pulseLeft = fadePulse(this.pulseLeft);
        this.pulseRight = fadePulse(this.pulseRight);
    }

    public boolean isActive() {
        return this.pulseTop > 0 || this.pulseLeft > 0 || this.pulseRight > 0;
    }

    private void renderWallFlash(GraphicsContext gc, double pulse, double x1, double y1, double x2, double y2, ChapterTheme theme) {
        if (pulse <= 0) return;
        double opacityPulse = Math.min(1.0, pulse);

        Color flashColor;
        switch (theme) {
            case CHAPTER_1_RUST:
                flashColor = ChapterTheme.NEON_ORANGE;
                break;
            case CHAPTER_3_VERDANT:
                flashColor = ChapterTheme.NEON_GREEN;
                break;
            case CHAPTER_4_CATHEDRAL:
                flashColor = ChapterTheme.GOLD;
                break;
            case CHAPTER_5_NEXUS:
                flashColor = ChapterTheme.PURE_WHITE;
                break;
            case ORIGINAL:
            case CHAPTER_2_NEON:
            default:
                flashColor = ChapterTheme.NEON_CYAN;
                break;
        }

        gc.save();

        gc.setEffect(new GaussianBlur(pulse * 50));
        gc.setStroke(flashColor.deriveColor(0, 1, 1, opacityPulse * 0.7));
        gc.setLineWidth(20);
        gc.strokeLine(x1, y1, x2, y2);

        gc.setEffect(new GaussianBlur(pulse * 12));
        gc.setStroke(flashColor.deriveColor(0, 1, 1, opacityPulse * 0.9));
        gc.setLineWidth(5);
        gc.strokeLine(x1, y1, x2, y2);

        gc.restore();
    }

    public void render(GraphicsContext gc, double width, double height, ChapterTheme theme) {
        if (!isActive()) {
            return;
        }
        renderWallFlash(gc, this.pulseTop, 10, 10, width - 10, 10, theme);
        renderWallFlash(gc, this.pulseLeft, 10, 10, 10, height, theme);
        renderWallFlash(gc, this.pulseRight, width - 10, 10, width - 10, height, theme);
    }
}
