package vnu.uet.goldexperience.model;

import javafx.scene.canvas.GraphicsContext;
import vnu.uet.goldexperience.core.Constants;
import vnu.uet.goldexperience.effect.ball.BallEffect;
import vnu.uet.goldexperience.manager.AssetsManager;
import vnu.uet.goldexperience.manager.GameSession;
import vnu.uet.goldexperience.model.brick.Brick;

public class Ball extends MovableObject {
    private final double radius;
    private boolean reset = true;
    private long lastCollisionTime = 0;
    static final double minDy = 80;
    private final BallEffect effect;
    private double speedScale = 1.0;

    private Paddle attachedPaddle = null;
    private double rollTimer = 0.0;

    public Ball(double x, double y, double radius) {
        super(x, y, radius * 2, radius * 2, 0, 0);
        this.radius = radius;
        this.image = AssetsManager.balls.getFirst();
        this.effect = new BallEffect();
    }

    public void refreshEffects() {
        if (effect != null) {
            effect.refreshActiveEffects();
            System.out.println("🔄 Ball effects refreshed");
        }
    }

    public void setReset(boolean reset) {
        this.reset = reset;
    }

    public boolean isReset() {
        return reset;
    }

    public void shoot() {
        if (attachedPaddle == null) {
            dx = speedScale;
            dy = -Constants.BALL_SPEED * speedScale;
        } else {
            double halfPaddleWidth = attachedPaddle.getWidth() / 2;
            double angleScaler =
                    (getCenterX() - (attachedPaddle.getX() + halfPaddleWidth)) / halfPaddleWidth;

            double shootAngle = angleScaler * Math.toRadians(Constants.MAX_SHOOT_ANGLE);

            setDx(Constants.BALL_SPEED * Math.sin(shootAngle) * speedScale);
            setDy(-Math.abs(Constants.BALL_SPEED * Math.cos(shootAngle) * speedScale));
        }

        reset = false;
        attachedPaddle = null;
    }
    public void setSpeedScale(double speedScale) {
        this.speedScale = speedScale;
    }
    public void applySpeedScale(double factor) {
        if (factor == 1.0) {
            return;
        }

        speedScale *= factor;

        if (!reset) {
            setDx(getDx() * factor);
            setDy(getDy() * factor);
            normalizeSpeed(Constants.BALL_MAX_SPEED);
        }
    }

    public double getSpeedScale() {
        return speedScale;
    }

    public void reset(Paddle paddle) {
        reset = true;

        this.attachedPaddle = paddle;
        this.rollTimer = 0.0;

        setX(paddle.getX() + paddle.getWidth() / 2 - radius);
        setY(paddle.getY() - radius * 2);
        dx = 0;
        dy = 0;
        effect.clear();
    }

    public boolean bounceOffWithPaddle(Paddle paddle) {
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
            paddle.onBallCollision(this);
            return true;
        }
        return false;
    }

    public boolean bounceOffWithBrick(Brick brick) {
        if (brick == null || brick.isDestroyed() || !checkCollision(brick)) {
            return false;
        }

        double overlapTop = getCenterY() + radius - brick.getY();
        double overlapBottom = brick.getY() + brick.getHeight() - (getCenterY() - radius);
        double overlapLeft = getCenterX() + radius - brick.getX();
        double overlapRight = brick.getX() + brick.getWidth() - (getCenterX() - radius);

        double minOverlap = Math.min(Math.min(overlapLeft, overlapRight), Math.min(overlapTop, overlapBottom));

        if (minOverlap == overlapLeft && getDx() > 0) {
            setDx(-Math.abs(getDx()));
            setX(brick.getX() - 2 * radius - 0.5);

            if (Math.abs(getDy()) < minDy) {
                setDy(getDy() >= 0 ? minDy : -minDy);
            }
        } else if (minOverlap == overlapRight && getDx() < 0) {
            setDx(Math.abs(getDx()));
            setX(brick.getX() + brick.getWidth() + 0.5);

            if (Math.abs(getDy()) < minDy) {
                setDy(getDy() >= 0 ? minDy : -minDy);
            }
        } else if (minOverlap == overlapTop && getDy() > 0) {
            setDy(-Math.abs(getDy()));
            setY(brick.getY() - 2 * radius - 0.5);
        } else if (minOverlap == overlapBottom && getDy() < 0) {
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
        // System.out.println("Ball speed: " + s);
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

            if (Math.abs(getDy()) < minDy) {
                setDy(getDy() >= 0 ? minDy : -minDy);
            }

            GameSession.getInstance().notifyBallHitWall(GameSession.HitSide.LEFT);
        }
        
        if (x + radius * 2 >= Constants.GAMEPLAYZONE_WIDTH) {
            setX(Constants.GAMEPLAYZONE_WIDTH - 2 * radius);
            setDx(-Math.abs(getDx()));
            if (Math.abs(getDy()) < minDy)
                setDy(getDy() >= 0 ? minDy : -minDy);
            GameSession.getInstance().notifyBallHitWall(GameSession.HitSide.RIGHT);
        }

        if (getY() <= 0) {
            setY(0);
            setDy(Math.abs(getDy()));
            GameSession.getInstance().notifyBallHitWall(GameSession.HitSide.TOP);
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
        } else {
            if (attachedPaddle != null) {
                double paddleCenterX = attachedPaddle.getX() + attachedPaddle.getWidth() / 2;
                double maxOffsetX = (attachedPaddle.getWidth() - this.width) / 2;

                rollTimer += dt * Constants.BALL_ROLLING_SPEED;
                double rollThreshold = Math.sin(rollTimer);

                setX(paddleCenterX - (this.width / 2) + rollThreshold * maxOffsetX);
                setY(attachedPaddle.getY() - this.height);
            }
        }

        effect.update(getCenterX(), getCenterY(), dt, width, height);
    }

    @Override
    public void render(GraphicsContext gc) {
        effect.render(gc);
        gc.setGlobalAlpha(1.0);

        if (image != null) {
            gc.drawImage(image, x, y, width, height);
        }
    }
}