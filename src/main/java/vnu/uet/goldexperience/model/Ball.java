package vnu.uet.goldexperience.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import vnu.uet.goldexperience.core.Constants;
import vnu.uet.goldexperience.manager.AssetsManager;

import java.util.ArrayList;
import java.util.List;

public class Ball extends MovableObject {
    private double speed = Constants.BALL_SPEED; // viết nnay
    private final double radius;
    private boolean reset = true;
    private long lastCollisionTime = 0;
    static final double minDy = 80;
    private final List<double[]> trail = new ArrayList<>();
    private final int maxTrail = 50;
    private double glowPulse = 0;

    public Ball(double x, double y, double radius) {
        super(x, y, radius * 2, radius * 2, 0, 0);
        this.radius = radius;
        this.image = AssetsManager.balls.getFirst();
    }

    public boolean isReset() {
        return reset;
    }

    public void shoot() {
        dx = 1;
        dy = -speed;
        reset = false;
    }

    public void reset(Paddle paddle) {
        reset = true;
        setX(paddle.getX() + paddle.getWidth() / 2 - radius); // đặt tâm chính giữa paddle
        setY(paddle.getY() - radius * 2);                   // đặt bóng ngay trên paddle
        dx = 0;
        dy = 0;
        trail.clear();
    }

    public void bounceOffWithPaddle(GameObject paddle) {
        long now = System.currentTimeMillis();
        if (paddle != null && checkCollision(paddle) && now - getLastCollisionTime() > 200) {

            double overlapTop = getCenterY() + radius - paddle.getY();
            double overlapBottom = paddle.getY() + paddle.getHeight() - (getCenterY() - radius);
            double overlapLeft = getCenterX() + radius - paddle.getX();
            double overlapRight = paddle.getX() + paddle.getWidth() - (getCenterX() - radius);

            double minOverlap = Math.min(Math.min(overlapLeft, overlapRight),
                    Math.min(overlapTop, overlapBottom));

            if (minOverlap == overlapTop && getDy() > 0) {
                double hit = (getCenterX() - (paddle.getX() + paddle.getWidth() / 2)) / (paddle.getWidth() / 2);
                double angle = hit * Math.toRadians(70);
                double s = Math.hypot(getDx(), getDy());
                setDx(s * Math.sin(angle));
                setDy(-Math.abs(s * Math.cos(angle)));
                setY(paddle.getY() - radius * 2 - 1);
            } else if (minOverlap == overlapLeft) {
                setX(paddle.getX() - 2 * radius - 2);
                setDx(-Math.abs(getDx() + 500));
                setDy(Math.max(getDy(), 220));
            } else if (minOverlap == overlapRight) {
                setX(paddle.getX() + paddle.getWidth() + 2);
                setDx(Math.abs(getDx()) + 500);
                setDy(Math.max(getDy(), 220));
            }

            increaseSpeedPercent(1.5);
            normalizeSpeed(Constants.BALL_MAX_SPEED);
            setLastCollisionTime(now);
        }
    }

    public boolean bounceOffWithBrick(GameObject brick) {
        if (brick == null || !checkCollision(brick)) {
            return false;
        }

        double overlapTop = getCenterY() + radius - brick.getY();
        double overlapBottom = brick.getY() + brick.getHeight() - (getCenterY() - radius);
        double overlapLeft = getCenterX() + radius - brick.getX();
        double overlapRight = brick.getX() + brick.getWidth() - (getCenterX() - radius);

        double minOverlap = Math.min(Math.min(overlapLeft, overlapRight), Math.min(overlapTop, overlapBottom));

        if (minOverlap == overlapLeft && getDx() > 0) {
//            System.out.println("LEFT");
            setDx(-Math.abs(getDx()));
            setX(brick.getX() - 2 * radius - 0.5);
            if (Math.abs(getDy()) < minDy) {
                setDy(getDy() >= 0 ? minDy : -minDy);
            }
        } else if (minOverlap == overlapRight && getDx() < 0) {
//            System.out.println("RIGHT");
            setDx(Math.abs(getDx()));
            setX(brick.getX() + brick.getWidth() + 0.5);
            if (Math.abs(getDy()) < minDy) {
                setDy(getDy() >= 0 ? minDy : -minDy);
            }
        } else if (minOverlap == overlapTop && getDy() > 0) {
//            System.out.println("TOP");
            setDy(-Math.abs(getDy()));
            setY(brick.getY() - 2 * radius - 0.5);
        } else if (minOverlap == overlapBottom && getDy() < 0) {
//            System.out.println("BOTTOM");
            setDy(Math.abs(getDy()));
            setY(brick.getY() + brick.getHeight() + 0.5);
        } else {
            return false;
        }

        increaseSpeedPercent(1.5);
        normalizeSpeed(Constants.BALL_MAX_SPEED);
        printSpeed();

        return true;
    }

    private void printSpeed() {
        double s = Math.hypot(getDx(), getDy());
        //System.out.println("Ball speed: " + s);
    }

    private void normalizeSpeed(double maxSpeed) {
        double s = Math.hypot(getDx(), getDy());
        if (s > maxSpeed) {
            setDx(getDx() / s * maxSpeed);
            setDy(getDy() / s * maxSpeed);
        }
    }

    private void increaseSpeedPercent(double percent) {
        double s = Math.hypot(getDx(), getDy());
        if (s > 0) {
            double newSpeed = s * (1 + percent / 100.0);
            setDx(getDx() / s * newSpeed);
            setDy(getDy() / s * newSpeed);
        }
    }

    public void handleBallEdgeCollision() {

        if (x <= 0) {
            setX(0);
            setDx(Math.abs(getDx()));
            if (Math.abs(getDy()) < minDy)
                setDy(getDy() >= 0 ? minDy : -minDy);
        }
        if (x + radius >= Constants.GAMEPLAYZONE_WIDTH) {
            setX(Constants.GAMEPLAYZONE_WIDTH - 2 * radius);
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

    public void setLastCollisionTime(long t) {
        lastCollisionTime = t;
    }

    public long getLastCollisionTime() {
        return lastCollisionTime;
    }

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
        if (!reset) {
            move(dt);
            handleBallEdgeCollision();
            trail.add(0, new double[]{getCenterX(), getCenterY()});
            if (trail.size() > maxTrail)
                trail.remove(trail.size() - 1);
        }
        glowPulse = (Math.sin(System.nanoTime() * 1e-9 * 6) + 1) / 2;
    }

    @Override
    public void render(GraphicsContext gc) {
        double cx = getCenterX();
        double cy = getCenterY();
        // trail
        for (int i = 0; i < trail.size(); i++) {
            double[] pos = trail.get(i);
            double t = (double) i / trail.size();
            if (Math.random() < t * 0.25) continue;
            double alpha = (1 - t * 0.8) * (0.4 + Math.random() * 0.2);
            double scale = 0.8 - t * 0.7;

            double w = width * scale;
            double h = height * scale;

            Color start = Color.web("#ffffff");
            Color end = Color.web("#66ffff");
            Color trailColor = start.interpolate(end, t);

            gc.setGlobalAlpha(alpha);
            gc.setFill(trailColor);
            gc.fillOval(pos[0] - w / 2, pos[1] - h / 2, w, h);
        }

        // glow
        double dynamicGlow = 0.4 + 0.3 * glowPulse;
        double glowSize = radius * 4;
        gc.setGlobalAlpha(dynamicGlow);

        // light layer
        gc.setFill(Color.web("#ffffff", dynamicGlow * 0.8));
        gc.fillOval(cx - glowSize * 0.6, cy - glowSize * 0.6, glowSize * 1.2, glowSize * 1.2);

        gc.setFill(Color.web("#99ffff", dynamicGlow * 0.5)); // xanh đậm
        gc.fillOval(cx - glowSize * 0.45, cy - glowSize * 0.45, glowSize * 0.9, glowSize * 0.9);

        gc.setFill(Color.web("#00ffff", dynamicGlow * 0.3)); // xanh nhạt
        gc.fillOval(cx - glowSize * 0.3, cy - glowSize * 0.3, glowSize * 0.6, glowSize * 0.6);


        // ball
        gc.setGlobalAlpha(1.0);
        if (image != null)
            gc.drawImage(image, x, y, width, height);

        // reset alpha
        gc.setGlobalAlpha(1.0);
    }


}