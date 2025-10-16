package vnu.uet.goldexperience.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import vnu.uet.goldexperience.core.Constants;

public class Ball extends MovableObject {
    private double speed = Constants.BALL_SPEED; // viết nnay
    private double radius;
    private boolean reset = true;
    private long lastCollisionTime = 0;

    public Ball(double x, double y, double radius) {
        super(x, y, radius * 2, radius * 2, 0, 0);
        this.radius = radius;
        this.image = new Image(getClass().getResource("/images/normalball.png").toExternalForm());
    }

    public boolean isReset() {
        return reset;
    }

    public void shoot() {
        dx = 1; //Math.random();
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

    public void bounceOffWithPaddle(GameObject other, double deltaTime)
    {
        long now = System.currentTimeMillis();
        if (other != null && checkCollision(other) && now - getLastCollisionTime() > 200) {

            double overlapTop = getCenterY() + radius - other.getY();
            double overlapBottom = other.getY() + other.getHeight() - (getCenterY() - radius);
            double overlapLeft = getCenterX() + radius - other.getX();
            double overlapRight = other.getX() + other.getWidth() - (getCenterX() - radius);

            double minOverlap = Math.min(Math.min(overlapLeft, overlapRight), Math.min(overlapTop, overlapBottom));

            if (minOverlap == overlapTop && getDy() > 0) {
                double hit = (getCenterX() - (other.getX() + other.getWidth() / 2)) / (other.getWidth() / 2);
                double angle = hit * Math.toRadians(70);
                double s = Math.hypot(getDx(), getDy());
                setDx(s * Math.sin(angle));
                setDy(-Math.abs(s * Math.cos(angle)));
                setY(other.getY() - radius * 2 - 1);
            }
            else if (minOverlap == overlapLeft) {
                setX(other.getX() - 2 * radius - 2);
                setDx(-Math.abs(getDx() + 500));
                setDy(Math.max(getDy(), 220));
            }
            else if (minOverlap == overlapRight) {
                setX(other.getX() + other.getWidth() + 2);
                setDx(Math.abs(getDx()) + 500);
                setDy(Math.max(getDy(), 220));
            }

            double s = Math.hypot(getDx(), getDy());
            if (s > Constants.BALL_SPEED) {
                setDx(getDx() / s * Constants.BALL_SPEED);
                setDy(getDy() / s * Constants.BALL_SPEED);
            }
            setLastCollisionTime(now);
        }
    }


    public boolean bounceOffWithBrick(GameObject other, double deltaTime) {
        if (other != null && checkCollision(other)) {

            double minDy = 80;
            double minDx = 150;

            double overlapTop = getCenterY() + radius - other.getY();
            double overlapBottom = other.getY() + other.getHeight() - (getCenterY() - radius);
            double overlapLeft = getCenterX() + radius - other.getX();
            double overlapRight = other.getX() + other.getWidth() - (getCenterX() - radius);

            double minOverlap = Math.min(Math.min(overlapLeft, overlapRight), Math.min(overlapTop, overlapBottom));

            if (minOverlap == overlapTop && getDy() > 0) {
                setDy(-Math.abs(getDy()));
                setY(other.getY() - 2 * radius - 0.5);
                System.out.println("TOP");
            }
            else if (minOverlap == overlapBottom && getDy() < 0) {
                setDy(Math.abs(getDy()));
                setY(other.getY() + other.getHeight() + 0.5);
                System.out.println("BOT");
            }
            else if (minOverlap == overlapLeft && getDx() > 0) {
                double newDx = -Math.abs(getDx());
                if (Math.abs(newDx) < 1e-3) newDx = -minDx;
                setDx(newDx);
                setX(other.getX() - 2 * radius - 0.5);
                if (Math.abs(getDy()) < minDy)
                    setDy(getDy() >= 0 ? minDy : -minDy);
                System.out.println("LEFT");
            }
            else if (minOverlap == overlapRight && getDx() < 0) {
                double newDx = Math.abs(getDx());
                if (Math.abs(newDx) < 1e-3) newDx = minDx;
                setDx(newDx);
                setX(other.getX() + other.getWidth() + 0.5);
                if (Math.abs(getDy()) < minDy)
                    setDy(getDy() >= 0 ? minDy : -minDy);
                System.out.println("RIGHT");
            }
            else {
                setDy(-getDy());
                System.out.println("FALLBACK BOUNCE");
            }

            normalizeSpeed(Constants.BALL_MAX_SPEED);

            return true;
        }
        return false;
    }

    private void normalizeSpeed(double maxSpeed) {
        double s = Math.hypot(getDx(), getDy());
        if (s > maxSpeed) {
            setDx(getDx() / s * maxSpeed);
            setDy(getDy() / s * maxSpeed);
        }
    }

    public void handleBallEdgeCollision() {
        double minDy = 80;
        if (x <= 0) {
            setX(0);
            setDx(Math.abs(getDx()));
            if (Math.abs(getDy()) < minDy)
                setDy(getDy() >= 0 ? minDy : -minDy);
        }
        if (x + getRadius() >= Constants.GAMEPLAYZONE_WIDTH) {
            setX(Constants.GAMEPLAYZONE_WIDTH - 2 * getRadius());
            setDx(-Math.abs(getDx()));
            if (Math.abs(getDy()) < minDy)
                setDy(getDy() >= 0 ? minDy : -minDy);
        }
        if (getY() <= 0) {
            setY(0);
            setDy(Math.abs(getDy()));
        }
    }

    public boolean checkCollision(GameObject other) {
        return getCenterX() + radius > other.x && getCenterX() - radius < other.x + other.width &&
                getCenterY() + radius > other.y && getCenterY() - radius < other.y + other.height;
    }

    public void setLastCollisionTime(long t) { lastCollisionTime = t; }
    public long getLastCollisionTime() { return lastCollisionTime; }

    public double getCenterX() {
        return x + radius;
    }

    public double getCenterY() {
        return y + radius;
    }

    public double getRadius() {
        return radius;
    }

    public void setCenterX(double cx) {
        x = cx - radius;
    }

    public void setCenterY(double cy) {
        y = cy - radius;
    }

    @Override
    public void update(double dt) {
        if (!reset) move(dt);
        handleBallEdgeCollision();
    }

    @Override
    public void render(GraphicsContext gc) {
        if (image != null)
            gc.drawImage(image, x, y);
    }
}