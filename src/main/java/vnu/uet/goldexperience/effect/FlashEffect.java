package vnu.uet.goldexperience.effect;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import vnu.uet.goldexperience.model.Brick;

public class FlashEffect {
    private Brick brick;
    private double duration;
    private double elapsed;
    private double interval;
    private int flashCount;
    private boolean isFinished;
    private Color flashColor;

    public FlashEffect(Brick brick) {
        this.brick = brick;
        this.duration = 0.2;
        this.interval = 0.08;
        this.elapsed = 0;
        this.flashCount = 0;
        this.isFinished = false;
        this.flashColor = Color.rgb(255, 255, 255, 0.85);
    }

    public void update(double deltaTime) {
        if (isFinished) return;

        elapsed += deltaTime;

        // Đếm số lần nhấp nháy
        int newFlashCount = (int)(elapsed / interval);
        if (newFlashCount > flashCount) {
            flashCount = newFlashCount;
        }

        // Kết thúc hiệu ứng
        if (elapsed >= duration) {
            isFinished = true;
        }
    }

    public void render(GraphicsContext gc) {
        if (isFinished || brick == null) return;

        boolean shouldFlash = (flashCount % 2 == 0);

        if (shouldFlash) {
            // Tính alpha giảm dần nhưng vẫn mạnh
            double progress = elapsed / duration;
            double alpha = 1.0 - (progress * 0.5);

            // Tính biên độ xung (pulse) - dao động từ 0 -> maxPulse
            double pulsePhase = (elapsed / interval) * Math.PI * 2;
            double pulseAmount = Math.abs(Math.sin(pulsePhase)) * 8;

            double x = brick.getX();
            double y = brick.getY();
            double width = brick.getWidth();
            double height = brick.getHeight();

            gc.save();

            // Vẽ hào quang xung quanh gạch (nhiều lớp)
            for (int i = 3; i >= 1; i--) {
                double offset = pulseAmount * i / 3.0;
                double layerAlpha = alpha * (1.0 - i / 4.0);

                gc.setGlobalAlpha(layerAlpha);
                gc.setFill(Color.rgb(255, 255, 100));
                gc.fillRect(
                        x - offset,
                        y - offset,
                        width + offset * 2,
                        height + offset * 2
                );
            }

            // Vẽ overlay trắng lên gạch
            gc.setGlobalAlpha(alpha * 0.7);
            gc.setFill(flashColor);
            gc.fillRect(x, y, width, height);

            // Vẽ viền sáng xung quanh
            gc.setGlobalAlpha(alpha);
            gc.setLineWidth(3 + pulseAmount * 0.3);
            gc.setStroke(Color.rgb(255, 255, 255, alpha));
            gc.strokeRect(x, y, width, height);

            gc.restore();
        }
    }

    public boolean isFinished() {
        return isFinished;
    }

    public Brick getBrick() {
        return brick;
    }

    public void setFlashColor(Color color) {
        this.flashColor = color;
    }
}