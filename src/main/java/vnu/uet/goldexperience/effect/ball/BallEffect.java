package vnu.uet.goldexperience.effect.ball;

import javafx.scene.canvas.GraphicsContext;
import vnu.uet.goldexperience.core.Constants;
import vnu.uet.goldexperience.manager.GameDataManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BallEffect {
    private static BallEffect instance;
    private final List<BallBubbleTrail> bubbles;
    private final List<BallFriction> frictions;
    private final Random random;
    private final BallTrail ballTrail;
    private final BallGlow ballGlow;
    private double lastX, lastY;
    private double ballW, ballH;
    private double centerX, centerY;
    private double glowPulse = 0;
    private double frictionSpawnTimer = 0;
    private boolean trailEnabled = false;
    private boolean bubbleEnabled = false;
    private boolean frictionEnabled = false;
    private BallEffect() {
        this.random = new Random();
        this.ballGlow = new BallGlow();
        this.ballTrail = new BallTrail();
        this.bubbles = new ArrayList<>();
        this.frictions = new ArrayList<>();
        refreshActiveEffects();
    }
    public static BallEffect getInstance() {
        if(instance == null) {
            instance = new BallEffect();
        }
        return instance;
    }
    public void refreshActiveEffects() {
        List<String> selectedEffects = GameDataManager.getSelectedBallEffects();

        trailEnabled = selectedEffects.contains("ball_trail");
        bubbleEnabled = selectedEffects.contains("ball_bubble");
        frictionEnabled = selectedEffects.contains("ball_friction");

        System.out.println("🎨 [BallEffect] Active effects:");
        System.out.println("   Trail: " + trailEnabled);
        System.out.println("   Bubble: " + bubbleEnabled);
        System.out.println("   Friction: " + frictionEnabled);
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
            if (bubbleEnabled) {
                spawnBubble(x, y, vx, vy);
            }

            if (frictionEnabled) {
                frictionSpawnTimer += deltaTime;
                if (frictionSpawnTimer >= 0.012) {
                    spawnFrictionParticles(x, y, vx, vy, width);
                    frictionSpawnTimer = 0;
                }
            }
        }

        bubbles.removeIf(b -> {
            b.update(deltaTime);
            return b.isDead();
        });

        frictions.removeIf(f -> {
            f.update(deltaTime);
            return f.isDead();
        });

        lastX = x;
        lastY = y;
        if (trailEnabled) {
            ballTrail.update(x, y);
        }
        glowPulse = (Math.sin(System.nanoTime() * 1e-9 * 6) + 1) / 2;
    }

    public void update(double x, double y) {
        centerX = x;
        centerY = y;
    }

    public void clear() {
        bubbles.clear();
        frictions.clear();
        if (ballTrail != null) {
            ballTrail.getTrail().clear();
        }
    }

    private void spawnBubble(double x, double y, double vx, double vy) {
        double angle = Math.atan2(-vy, -vx) + (random.nextDouble() - 0.5) * 0.6;
        double speed = 40 + random.nextDouble() * 40;
        bubbles.add(new BallBubbleTrail(x, y, angle, speed));
    }

    private void spawnFrictionParticles(double x, double y, double vx, double vy, double width) {
        double angle = Math.atan2(vy, vx);
        int windCount = 3 + random.nextInt(3); // 3-5 vệt gió mỗi frame

        for (int i = 0; i < windCount; i++) {
            // cac line xung quanh bong có góc = spreadAngle
            double spreadAngle = angle + (random.nextDouble() - 0.5) * Math.PI * 1.2; // ±108 độ
            double distance = width * 0.5 + random.nextDouble() * width * 0.4; // 0.5-0.9 width

            double px = x + Math.cos(spreadAngle) * distance;
            double py = y + Math.sin(spreadAngle) * distance;

            //trong update se lien tuc giam toc do bay cua các vệt gió
            //nhung vẫn khởi tạo khác nhau de vx,vy của tất cả các object ko đồng nhất
            double windSpeed = 0.7 + random.nextDouble() * 0.3;
            double pvx = vx * windSpeed + (random.nextDouble() - 0.5) * 30;
            double pvy = vy * windSpeed + (random.nextDouble() - 0.5) * 30;

            frictions.add(new BallFriction(px, py, pvx, pvy));
        }
    }

    public void render(GraphicsContext gc) {
        if (frictionEnabled) {
            for (BallFriction f : frictions) {
                f.render(gc);
            }
        }

        if (bubbleEnabled) {
            for (BallBubbleTrail b : bubbles) {
                b.render(gc);
            }
        }
        if (trailEnabled && ballTrail != null) {
            ballTrail.render(gc, ballTrail.getTrail(), ballW, ballH);
        }
        ballGlow.render(gc, centerX, centerY, ballW / 2, glowPulse);
    }
}