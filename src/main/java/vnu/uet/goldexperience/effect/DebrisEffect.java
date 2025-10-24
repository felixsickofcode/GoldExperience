package vnu.uet.goldexperience.effect;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import vnu.uet.goldexperience.model.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DebrisEffect {
    private List<Particle> particles;
    private boolean isFinished;

    public DebrisEffect(Brick brick) {
        this.particles = new ArrayList<>();
        this.isFinished = false;
        createParticles(brick);
    }

    private void createParticles(Brick brick) {
        Random random = new Random();
        int particleCount = 7;

        double centerX = brick.getX() + brick.getWidth() / 2;
        double centerY = brick.getY() + brick.getHeight() / 2;

        Image brickImage = brick.getImage();

        for (int i = 0; i < particleCount; i++) {
            WritableImage particleImage = createParticleImage(brickImage, random);

            double startX = centerX;
            double startY = centerY;


            double angle = random.nextDouble() * Math.PI * 2;
            double speed = 50 + random.nextDouble() * 20;

            double velocityX = Math.cos(angle) * speed;
            double velocityY = Math.sin(angle) * speed;

            double gravity = 0;


            double lifetime = 0.4 + random.nextDouble() * 0.1;

            double rotationSpeed = (random.nextDouble() - 0.5) * 360;

            particles.add(new Particle(
                    startX, startY,
                    velocityX, velocityY,
                    gravity, lifetime,
                    rotationSpeed,
                    particleImage
            ));
        }
    }

    private WritableImage createParticleImage(Image source, Random random) {
        if (source == null) {
            WritableImage img = new WritableImage(6, 6);
            var writer = img.getPixelWriter();
            javafx.scene.paint.Color color = javafx.scene.paint.Color.rgb(
                    100 + random.nextInt(156),
                    100 + random.nextInt(156),
                    100 + random.nextInt(156)
            );
            for (int x = 0; x < 6; x++) {
                for (int y = 0; y < 6; y++) {
                    writer.setColor(x, y, color);
                }
            }
            return img;
        }

        int particleSize = 6 + random.nextInt(6);
        int sourceX = random.nextInt(Math.max(1, (int)source.getWidth() - particleSize));
        int sourceY = random.nextInt(Math.max(1, (int)source.getHeight() - particleSize));

        PixelReader reader = source.getPixelReader();
        WritableImage particleImg = new WritableImage(reader,
                sourceX, sourceY,
                Math.min(particleSize, (int)source.getWidth() - sourceX),
                Math.min(particleSize, (int)source.getHeight() - sourceY)
        );

        return particleImg;
    }

    public void update(double deltaTime) {
        for (Particle p : particles) {
            p.update(deltaTime);
        }

        isFinished = particles.stream().allMatch(p -> !p.isAlive());
    }

    public void render(GraphicsContext gc) {
        for (Particle p : particles) {
            if (p.isAlive()) {
                p.render(gc);
            }
        }
    }

    public boolean isFinished() {
        return isFinished;
    }


    private static class Particle {
        private double x, y;
        private double velocityX, velocityY;
        private double gravity;
        private double lifetime;
        private double age;
        private double rotation;
        private double rotationSpeed;
        private Image image;

        public Particle(double x, double y, double vx, double vy,
                        double gravity, double lifetime,
                        double rotationSpeed, Image image) {
            this.x = x;
            this.y = y;
            this.velocityX = vx;
            this.velocityY = vy;
            this.gravity = gravity;
            this.lifetime = lifetime;
            this.age = 0;
            this.rotation = 0;
            this.rotationSpeed = rotationSpeed;
            this.image = image;
        }

        public void update(double deltaTime) {
            if (!isAlive()) return;

            age += deltaTime;


            x += velocityX * deltaTime;
            y += velocityY * deltaTime;


            velocityY += gravity * deltaTime;


            rotation += rotationSpeed * deltaTime;
        }

        public void render(GraphicsContext gc) {
            if (!isAlive() || image == null) return;


            double alpha = 1.0 - (age / lifetime);

            gc.save();
            gc.setGlobalAlpha(alpha);


            gc.translate(x, y);
            gc.rotate(rotation);
            gc.drawImage(image,
                    -image.getWidth() / 2,
                    -image.getHeight() / 2
            );

            gc.restore();
        }

        public boolean isAlive() {
            return age < lifetime;
        }
    }
}