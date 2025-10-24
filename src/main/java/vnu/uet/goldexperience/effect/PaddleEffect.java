package vnu.uet.goldexperience.effect;

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PaddleEffect {
    private final double paddleWidth;
    private final double paddleHeight;

    private double lastX, lastY;
    private double velocityX, velocityY;
    private final List<BubbleEffect> particles;
    private final List<EnergyRingEffect> energyRings;
    private final PaddleFlashEffect flash;
    private final PaddleShakeEffect shake;
    private final Random random;

    private double energyRingTimer;

    private static final int MAX_TRAIL = 25;
    private static final double PARTICLE_SPAWN_THRESHOLD = 25.0;

    public PaddleEffect(double width, double height) {
        this.paddleWidth = width;
        this.paddleHeight = height;
        this.particles = new ArrayList<>();
        this.energyRings = new ArrayList<>();
        flash = new PaddleFlashEffect(0, 0, paddleWidth, paddleHeight);
        shake = new PaddleShakeEffect();

        this.random = new Random();
    }

    // ------------------------------
    // UPDATE
    // ------------------------------
    public void update(double x, double y, double deltaTime) {
        velocityX = (x - lastX) / deltaTime;
        velocityY = (y - lastY) / deltaTime;
        double speed = Math.sqrt(velocityX * velocityX + velocityY * velocityY);


        if (speed > PARTICLE_SPAWN_THRESHOLD) {
            spawnParticles(x, y, speed);
        }

        energyRingTimer += deltaTime;
        if (speed > PARTICLE_SPAWN_THRESHOLD * 0.5 && energyRingTimer > 0.15) {
            spawnEnergyRing(x, y);
            energyRingTimer = 0;
        }

        particles.removeIf(p -> {
            p.update(deltaTime);
            return p.isDead();
        });

        energyRings.removeIf(r -> {
            r.update(deltaTime);
            return r.isDead();
        });

        flash.setPosition(x, y);
        flash.update(deltaTime);
        shake.update(deltaTime);


        lastX = x;
        lastY = y;
    }


    // ------------------------------
    // SPAWN
    // ------------------------------
    private void spawnParticles(double x, double y, double speed) {
        int count = (int) (speed / 200) + 1;
        for (int i = 0; i < count; i++) {
            double px = x + random.nextDouble() * paddleWidth;
            double py = y + paddleHeight + random.nextDouble() * 5;
            double angle = Math.atan2(-velocityY, -velocityX) + (random.nextDouble() - 0.5) * 0.5;
            double particleSpeed = 30 + random.nextDouble() * 50;
            particles.add(new BubbleEffect(px, py, angle, particleSpeed));
        }
    }

    private void spawnEnergyRing(double x, double y) {
        energyRings.add(new EnergyRingEffect(
                x + paddleWidth / 2,
                y + paddleHeight / 2
        ));
    }

    public void onBallHit(double x, double y) {
        for (int i = 0; i < 15; i++) {
            double angle = Math.PI / 2 + (random.nextDouble() - 0.5) * Math.PI;
            double speed = 100 + random.nextDouble() * 100;
            particles.add(new BubbleEffect(x, y, angle, speed));
        }
        energyRings.add(new EnergyRingEffect(x, y, true));
        flash.trigger();
        shake.trigger(0.15, 10);

    }

    // ------------------------------
    // RENDER
    // ------------------------------
    public void render(GraphicsContext gc) {
        gc.save();
        for (EnergyRingEffect ring : energyRings) ring.render(gc);
        for (BubbleEffect p : particles) p.render(gc);
        shake.apply(gc);
        flash.render(gc);
        gc.restore();

        gc.restore();
    }
}
