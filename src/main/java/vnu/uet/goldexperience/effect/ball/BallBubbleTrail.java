package vnu.uet.goldexperience.effect;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.Random;

public class BallBubbleTrail {
    private double x, y, vx, vy, life;
    private final double size;
    private final Color color;

    public BallBubbleTrail(double x, double y, double angle, double speed) {
        this.x = x;
        this.y = y;
        this.vx = Math.cos(angle) * speed;
        this.vy = Math.sin(angle) * speed;
        this.size = 2.5 + Math.random() * 3.5;
        this.life = 1.0;

        Random rand = new Random();
        int r = 140 + rand.nextInt(30);   // ít đỏ hơn
        int g = 210 + rand.nextInt(40);   // xanh lá sáng
        int b = 255;                      // xanh dương mạnh
        this.color = Color.rgb(r, g, b);
    }

    public void update(double deltaTime) {
        x += vx * deltaTime;
        y += vy * deltaTime;
        vx *= 0.9;
        vy = vy * 0.98 + 30 * deltaTime;  // trọng lực nhẹ
        life -= deltaTime * 1.5;
    }

    public boolean isDead() { return life <= 0; }
    public void render(GraphicsContext gc) {
        gc.setGlobalAlpha(life * 0.8);
        gc.setFill(color);

        // Bóng tròn chính
        gc.fillOval(x - size, y - size, size * 2, size * 2);

        // Viền sáng mờ — tạo hiệu ứng bong bóng
        gc.setGlobalAlpha(life * 0.25);
        gc.setFill(Color.rgb(255, 255, 255, 0.4));
        gc.fillOval(x - size * 1.4, y - size * 1.4, size * 2.8, size * 2.8);
    }
}
