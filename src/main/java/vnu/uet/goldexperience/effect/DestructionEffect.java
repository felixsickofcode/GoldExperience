package vnu.uet.goldexperience.effect;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import vnu.uet.goldexperience.model.Brick;

import java.util.Random;

//debris with higher gravity,spread range
public class DestructionEffect extends DebrisEffect{
    public DestructionEffect(Brick brick) {
        super(brick);
    }

    @Override
    protected void createParticles (Brick brick) {
        Random random = new Random();
        int particleCount = 14;

        double centerX = brick.getX() + brick.getWidth() / 2;
        double centerY = brick.getY() + brick.getHeight() / 2;

        Image brickImage = brick.getImage();

        for (int i = 0; i < particleCount; i++) {
            WritableImage particleImage = createParticleImage(brickImage, random);


            double angle = Math.toRadians(75) + random.nextDouble() * Math.toRadians(210);

            double speed = 50 + random.nextDouble() * 20;

            double velocityX = Math.cos(angle) * speed;
            double velocityY = Math.sin(angle) * speed;

            double gravity = 100;


            double lifetime = 3 + random.nextDouble() * 0.1;

            double rotationSpeed = (random.nextDouble() - 0.5) * 360;

            particles.add(new Particle(
                    centerX, centerY,
                    velocityX, velocityY,
                    gravity, lifetime,
                    rotationSpeed,
                    particleImage
            ));
        }
    }
}
