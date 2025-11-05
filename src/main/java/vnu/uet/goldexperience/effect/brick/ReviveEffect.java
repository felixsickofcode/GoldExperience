package vnu.uet.goldexperience.effect.brick;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import vnu.uet.goldexperience.model.Brick;

public class ReviveEffect {
    private final Brick brick;
    private double elapsedTime;
    private double duration;
    private double[] starX;
    private double[] starY;
    private double[] starAlpha;
    private int starCount = 7;
    private boolean isActive;

    public ReviveEffect(Brick brick) {
        this.brick = brick;
        this.duration = 1.0;
        this.isActive = false;

        starX = new double[starCount];
        starY = new double[starCount];
        starAlpha = new double[starCount];
    }

    public void trigger() {
        this.isActive = true;
        this.elapsedTime = 0;


        for (int i = 0; i < starCount; i++) {
            double angle = Math.random() * Math.PI * 2;
            double distance = 10 + Math.random() * 5;
            starX[i] = Math.cos(angle) * distance;
            starY[i] = Math.sin(angle) * distance;
            starAlpha[i] = 1.0;
        }
    }

    public void end() {
        this.isActive = false;
    }

    public void update(double deltaTime) {
        if (!isActive) return;

        elapsedTime += deltaTime;
        double progress = elapsedTime / duration;


        if (progress >= 1.0) {
            end();
            return;
        }


        for (int i = 0; i < starCount; i++) {
            starX[i] *= 1.005;
            starY[i] *= 1.01;
            starAlpha[i] = 1.0 - progress;
        }
    }

    public void render(GraphicsContext gc) {
        if (!isActive) return;

        double centerX = brick.getX() + brick.getWidth() / 2;
        double centerY = brick.getY() + brick.getHeight() / 2;

        if (elapsedTime < 0.5) {
            gc.setGlobalAlpha(0.7 * (1 - elapsedTime / 0.3));
            gc.setFill(Color.WHITE);
            gc.fillOval(centerX - 30, centerY - 30, 60, 60);
        }


        for (int i = 0; i < starCount; i++) {
            if (starAlpha[i] > 0) {
                gc.setGlobalAlpha(starAlpha[i]);
                gc.setFill(Color.YELLOW);
                drawStar(gc, centerX + starX[i], centerY + starY[i]);
            }
        }

        gc.setGlobalAlpha(1.0);
    }

    private void drawStar(GraphicsContext gc, double x, double y) {
        double[] xPoints = new double[10];
        double[] yPoints = new double[10];

        for (int i = 0; i < 10; i++) {
            double angle = Math.PI * i / 5 - Math.PI / 2;
            double r = (i % 2 == 0) ? (double) 4 : (double) 4 / 2;
            xPoints[i] = x + r * Math.cos(angle);
            yPoints[i] = y + r * Math.sin(angle);
        }

        gc.fillPolygon(xPoints, yPoints, 10);
    }
}