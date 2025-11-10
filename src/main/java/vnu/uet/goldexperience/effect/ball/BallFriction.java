package vnu.uet.goldexperience.effect.ball;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class BallFriction {
    private double x, y;
    private double vx, vy;
    private double life;
    private final double maxLife;
    private final double length;
    private final double thickness;
    private double angle;

    public BallFriction(double x, double y, double vx, double vy) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.maxLife = 0.4 + Math.random() * 0.3; // 0.4 den 0.7
        this.life = maxLife;
        this.length = 15 + Math.random() * 25; // 15 den 40
        this.thickness = 1.5 + Math.random() * 1.5; // 1.5 den 3
        this.angle = Math.atan2(vy, vx);
    }

    public void update(double deltaTime) {
        double friction = 0.9;
        vx*=friction;
        vy*=friction;
        if (Math.abs(vx) > 0.1 || Math.abs(vy) > 0.1) {
            angle = Math.atan2(vy, vx);
        }


        x += vx * deltaTime;
        y += vy * deltaTime;


        life -= deltaTime;
    }

    public void render(GraphicsContext gc) {
        double alpha = Math.max(0, life / maxLife);

        double endX = x - Math.cos(angle) * length;
        double endY = y - Math.sin(angle) * length;

        gc.setGlobalAlpha(alpha * 0.7);

        // trang xanh
        gc.setStroke(Color.rgb(200, 230, 255, alpha * 0.8));
        gc.setLineWidth(thickness);
        gc.strokeLine(x, y, endX, endY);

        // trang
        gc.setStroke(Color.rgb(255, 255, 255, alpha * 0.5));
        gc.setLineWidth(thickness * 0.5);
        gc.strokeLine(x, y, endX, endY);

        gc.setGlobalAlpha(1.0);
    }

    public boolean isDead() {
        return life <= 0;
    }
}