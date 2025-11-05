package vnu.uet.goldexperience.manager;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Quản lý và render UI hearts (mạng)
 */
public class LifeManager {

    private final double canvasWidth;
    private final double canvasHeight;

    private static final double HEART_SIZE = 30;
    private static final double HEART_SPACING = 10;
    private static final double MARGIN = 20;

    // Cyberpunk colors
    private static final Color NEON_PINK = Color.rgb(255, 0, 128);
    private static final Color DARK_GRAY = Color.rgb(50, 50, 50);

    public LifeManager(double canvasWidth, double canvasHeight) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
    }

    /**
     * Render hearts ở góc trên phải
     */
    public void render(GraphicsContext gc, int currentLives, int maxLives) {
        double startX = canvasWidth - MARGIN - (maxLives * (HEART_SIZE + HEART_SPACING));
        double y = MARGIN;

        for (int i = 0; i < maxLives; i++) {
            double x = startX + i * (HEART_SIZE + HEART_SPACING);

            if (i < currentLives) {
                // Filled heart - còn mạng
                drawHeart(gc, x, y, NEON_PINK, true);
            } else {
                // Empty heart - mất mạng
                drawHeart(gc, x, y, DARK_GRAY, false);
            }
        }
    }

    /**
     * Vẽ 1 trái tim
     */
    private void drawHeart(GraphicsContext gc, double x, double y, Color color, boolean filled) {
        gc.save();

        // Glow effect nếu filled
        if (filled) {
            for (int i = 3; i > 0; i--) {
                gc.setFill(color.deriveColor(0, 1, 1, 0.2 * i));
                drawHeartShape(gc, x - i, y - i, HEART_SIZE + i * 2, true);
            }
        }

        // Main heart
        if (filled) {
            gc.setFill(color);
            drawHeartShape(gc, x, y, HEART_SIZE, true);
        } else {
            gc.setStroke(color);
            gc.setLineWidth(2);
            drawHeartShape(gc, x, y, HEART_SIZE, false);
        }

        gc.restore();
    }

    /**
     * Vẽ hình trái tim
     */
    private void drawHeartShape(GraphicsContext gc, double x, double y, double size, boolean filled) {
        double width = size;
        double height = size;

        // Simplified heart shape using bezier curves
        gc.beginPath();
        gc.moveTo(x + width / 2, y + height);

        // Left side
        gc.bezierCurveTo(
                x, y + height * 0.7,
                x, y + height * 0.3,
                x + width / 2, y
        );

        // Right side
        gc.bezierCurveTo(
                x + width, y + height * 0.3,
                x + width, y + height * 0.7,
                x + width / 2, y + height
        );

        gc.closePath();

        if (filled) {
            gc.fill();
        } else {
            gc.stroke();
        }
    }
}