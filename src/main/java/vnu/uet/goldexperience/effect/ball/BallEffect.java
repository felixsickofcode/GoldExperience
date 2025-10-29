package vnu.uet.goldexperience.effect.ball;

import javafx.scene.canvas.GraphicsContext;
import vnu.uet.goldexperience.core.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BallEffect {
    private final List<BallBubbleTrail> bubbles;
    private Random random;
    private BallTrail ballTrail;
    private BallGlow ballGlow;
    private double lastX, lastY;
    private double ballW, ballH;
    private double centerX, centerY;
    private double glowPulse = 0;

    public BallEffect() {
        this.random = new Random();
        this.ballGlow = new BallGlow();
        this.ballTrail = new BallTrail();
        this.bubbles = new ArrayList<>();
    }


    public void update(double x, double y, double deltaTime, double width, double height) {
        double vx = (x - lastX) / deltaTime;
        double vy = (y - lastY) / deltaTime;
        double speed = Math.sqrt(vx * vx + vy * vy);
        ballW = width;
        ballH = height;
        centerX = x;
        centerY = y;

        if (speed > Constants.SPAWN_THRESHOLD) {
            spawnBubble(x, y, vx, vy);
        }


        bubbles.removeIf(b -> {
            b.update(deltaTime);
            return b.isDead();
        });

        lastX = x;
        lastY = y;
        ballTrail.update(x, y);
        glowPulse = (Math.sin(System.nanoTime() * 1e-9 * 6) + 1) / 2;
    }

    public void update(double x, double y) {
        centerX = x;
        centerY = y;
    }

    public void clear() {
        bubbles.clear();
        ballTrail.getTrail().clear();
    }

    private void spawnBubble(double x, double y, double vx, double vy) {
        double angle = Math.atan2(-vy, -vx) + (random.nextDouble() - 0.5) * 0.6;
        double speed = 40 + random.nextDouble() * 40;
        bubbles.add(new BallBubbleTrail(x, y, angle, speed));
    }

    public void render(GraphicsContext gc) {
        for (BallBubbleTrail b : bubbles) {
            b.render(gc);
        }
        ballTrail.render(gc, ballTrail.getTrail(), ballW, ballH);
        ballGlow.render(gc, centerX, centerY, ballW / 2, glowPulse);
    }
}
