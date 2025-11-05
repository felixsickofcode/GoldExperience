package vnu.uet.goldexperience.effect.brick;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import vnu.uet.goldexperience.model.Brick;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ExplosionEffect {
    private final Brick brick;
    private final double x;
    private final double y;
    private final double width;
    private final double height;

    private double duration;
    private double elapsed;
    private boolean isFinished;
    private boolean isActive;

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    private final List<Particle> particles;
    private final Random random;

    // Các phase của explosion
    private static final double FLASH_PHASE = 0.1;
    private static final double EXPAND_PHASE = 0.3;
    private static final double FADE_PHASE = 1.5;

    public ExplosionEffect(Brick brick) {
        this.brick = brick;
        this.x = brick.getX() + brick.getWidth() / 2;
        this.y = brick.getY() + brick.getHeight() / 2;
        this.width = brick.getWidth();
        this.height = brick.getHeight();

        this.duration = 1.5;
        this.elapsed = 0;
        this.isFinished = false;
        this.isActive = false;

        this.random = new Random();
        this.particles = new ArrayList<>();
    }

    /**
     * Bắt đầu hiệu ứng nổ
     */
    public void trigger() {
        if (!isActive) {
            isActive = true;
            elapsed = 0;
            isFinished = false;
            createParticles();
        }
    }

    /**
     * Generate Particle
     */
    private void createParticles() {
        particles.clear();
        int particleCount = 20 + random.nextInt(10); // 20-30 particles

        for (int i = 0; i < particleCount; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double speed = 50 + random.nextDouble() * 100; // 50-150 pixels/sec
            double size = 3 + random.nextDouble() * 5; // 3-8 pixels

            particles.add(new Particle(x, y, angle, speed, size));
        }
    }

    public void update(double deltaTime) {
        if (!isActive || isFinished) {
            return;
        }

        elapsed += deltaTime;

        for (Particle particle : particles) {
            particle.update(deltaTime);
        }

        if (elapsed >= duration) {
            isFinished = true;
            isActive = false;
        }
    }

    public void render(GraphicsContext gc) {
        if (!isActive || isFinished) {
            return;
        }

        double progress = elapsed / duration;

        gc.save();

        // flash
        if (progress < FLASH_PHASE) {
            renderFlashPhase(gc, progress / FLASH_PHASE);
        }

        // particles generate and expand
        if (progress >= FLASH_PHASE && progress < EXPAND_PHASE) {
            double phaseProgress = (progress - FLASH_PHASE) / (EXPAND_PHASE - FLASH_PHASE);
            renderExpandPhase(gc, phaseProgress);
        }

        // fade
        if (progress >= EXPAND_PHASE) {
            double phaseProgress = (progress - EXPAND_PHASE) / (FADE_PHASE - EXPAND_PHASE);
            renderFadePhase(gc, phaseProgress);
        }

        gc.restore();
    }

    private void renderFlashPhase(GraphicsContext gc, double phaseProgress) {
        double alpha = 1.0 - phaseProgress;
        double radius = width * (0.5 + phaseProgress * 0.5);

        for (int i = 3; i >= 1; i--) {
            double layerRadius = radius * (i / 3.0);
            double layerAlpha = alpha * (1.0 - i / 4.0);

            gc.setGlobalAlpha(layerAlpha);
            gc.setFill(Color.rgb(255, 255, 200));
            gc.fillOval(
                    x - layerRadius,
                    y - layerRadius,
                    layerRadius * 2,
                    layerRadius * 2
            );
        }
        gc.setGlobalAlpha(alpha);
        gc.setFill(Color.WHITE);
        gc.fillOval(
                x - radius * 0.3,
                y - radius * 0.3,
                radius * 0.6,
                radius * 0.6
        );
    }

    private void renderExpandPhase(GraphicsContext gc, double phaseProgress) {
        double alpha = 1.0 - phaseProgress * 0.5;
        double expansionRadius = width * (0.8 + phaseProgress * 0.7);

        gc.setGlobalAlpha(alpha);
        gc.setLineWidth(8 - phaseProgress * 4);

        gc.setStroke(Color.rgb(255, 150, 0, alpha));
        gc.strokeOval(
                x - expansionRadius,
                y - expansionRadius,
                expansionRadius * 2,
                expansionRadius * 2
        );

        double innerRadius = expansionRadius * 0.7;
        gc.setStroke(Color.rgb(255, 50, 0, alpha));
        gc.strokeOval(
                x - innerRadius,
                y - innerRadius,
                innerRadius * 2,
                innerRadius * 2
        );

        // Vẽ particles
        renderParticles(gc, alpha);
    }

    private void renderFadePhase(GraphicsContext gc, double phaseProgress) {
        double alpha = 1.0 - phaseProgress;
        renderParticles(gc, alpha);
    }

    private void renderParticles(GraphicsContext gc, double alpha) {
        for (Particle particle : particles) {
            particle.render(gc, alpha);
        }
    }

    public boolean isFinished() {
        return isFinished;
    }

    public boolean isActive() {
        return isActive;
    }

    public double getCurrentInnerRadius() {
        if (!isActive || isFinished) {
            return 0;
        }

        double progress = elapsed / duration;


        if (progress >= FLASH_PHASE && progress < EXPAND_PHASE) {
            double phaseProgress = (progress - FLASH_PHASE) / (EXPAND_PHASE - FLASH_PHASE);
            double expansionRadius = width * (0.8 + phaseProgress * 0.7);
            return expansionRadius * 0.8; //
        }

        return 0;
    }

    private static class Particle {
        private double x, y;
        private final double angle;
        private final double speed;
        private final double size;
        private final Color color;

        public Particle(double x, double y, double angle, double speed, double size) {
            this.x = x;
            this.y = y;
            this.angle = angle;
            this.speed = speed;
            this.size = size;

            // vang->xam
            Random rand = new Random();
            double colorVariant = rand.nextDouble();
            if (colorVariant < 0.4) {
                // Vàng cam
                this.color = Color.rgb(255, 200 + rand.nextInt(55), rand.nextInt(100));
            } else if (colorVariant < 0.7) {
                // Đỏ
                this.color = Color.rgb(255, rand.nextInt(100), 0);
            } else {
                // Xám khói
                int gray = 100 + rand.nextInt(100);
                this.color = Color.rgb(gray, gray, gray);
            }
        }

        public void update(double deltaTime) {
            x += Math.cos(angle) * speed * deltaTime;
            y += Math.sin(angle) * speed * deltaTime;
        }

        public void render(GraphicsContext gc, double alpha) {
            gc.setGlobalAlpha(alpha);
            gc.setFill(color);
            gc.fillOval(x - size * 0.5, y - size * 0.5, size, size);
        }
    }
}