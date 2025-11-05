package vnu.uet.goldexperience.effect.brick;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import vnu.uet.goldexperience.model.Brick;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RegenerateEffect {
    private final double x;
    private final double y;
    private final double width;
    private final double height;
    private boolean isRegenerating;
    private double rangeY;
    private final List<Particle> particles;
    private final Random random;

    public RegenerateEffect(Brick brick) {
        this.x = brick.getX() + brick.getWidth() / 2;
        this.y = brick.getY() + brick.getHeight() / 2;
        this.width = brick.getWidth();
        this.height = brick.getHeight();
        this.particles = new ArrayList<>();
        this.random = new Random();
        this.isRegenerating = false;
        this.rangeY = y - height / 1.8;
    }

    public void trigger() {
        if (!isRegenerating) {
            isRegenerating = true;
            createParticles();
        }
    }

    public void end() {
        if (isRegenerating) {
            isRegenerating = false;
            particles.clear();
        }
    }

    private void createParticles() {
        particles.clear();
        for (int i = 0; i <= 15; i++) {
            double size = 1 + random.nextDouble() * 8;
            double xPos = x + (random.nextDouble() * 2 - 1) * width/1.8;
            double yPos = y + (random.nextDouble()*2 -1) *height/3;
            particles.add(new Particle(xPos, yPos, rangeY));
        }
    }

    public void update(double dt) {
        for (Particle particle : particles) {
            particle.update(dt);
        }

    }

    public void render(GraphicsContext gc) {
        for (Particle p : particles) {
            p.render(gc);
        }
    }

    private static class Particle {
        private double x;
        private double y;
        private double speed;
        private double thickness;
        private Color color;
        private final double rangeY;
        private double life;
        private double maxLife;
        public Particle(double x, double y, double rangeY) {
            this.x = x;
            this.y = y;
            this.speed = 20+Math.random()*10;
            this.color = Color.web("#00ff00");
            this.thickness = 1.5 + Math.random() * 1.5;
            this.rangeY = rangeY;
            this.maxLife=0.9 + Math.random() * 0.4;
            this.life=maxLife;
        }

        public void update(double dt) {
            y -= speed * dt;
            if (y < rangeY) {
                life=maxLife;
                y += 24;
            }
            life -= dt/2;
        }

        public void render(GraphicsContext gc) {
            double alpha = Math.max(0, life / maxLife);
            gc.setGlobalAlpha(alpha * 0.7);
            gc.setStroke(color);
            gc.setLineWidth(thickness);
            gc.strokeLine(x, y, x, y - 6);
            gc.setGlobalAlpha(1.0);
        }


    }
}


