package vnu.uet.goldexperience.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Ball extends MovableObject {
    private double speed = 300; // pixel/giây

    private double radius;
    private boolean reset = true;

    public Ball(double x, double y, double radius) {
        super(x, y, radius * 2, radius * 2, 0, 0);
        this.radius = radius;
        this.image = new Image(getClass().getResource("/images/bigball.png").toExternalForm());
    }

    public boolean isReset() {
        return reset;
    }

    public void shoot() {
        dx = (Math.random() * 2 * speed) - speed;
        dy = -speed;
        reset = false;
    }
    public void reset(Paddle paddle) {
        reset = true;
        setX(paddle.getX() + paddle.getWidth()/2 - radius); // đặt tâm chính giữa paddle
        setY(paddle.getY() - radius * 2);                   // đặt bóng ngay trên paddle
        dx = 0;
        dy = 0;

    }

    public boolean checkCollision(GameObject other) {
        return x + radius > other.x && x - radius < other.x + other.width &&
                y + radius > other.y && y - radius < other.y + other.height;
    }

    @Override
    public void update(double dt) {
        if (!reset) move(dt); // chỉ di chuyển khi đã shoot
    }

    @Override
    public void render(GraphicsContext gc) {
        if (image != null)
            gc.drawImage(image, x, y, width, height);
    }
}
