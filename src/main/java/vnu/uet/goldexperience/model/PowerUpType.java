package vnu.uet.goldexperience.model;

import javafx.scene.image.Image;
import vnu.uet.goldexperience.core.Constants;

import java.net.URL;
import java.util.List;

/* APPLY STRATEGY PATTERN */
public enum PowerUpType {
    THREE_BALLS(
            (context) -> {
                List<Ball> balls = context.balls();

                for (Ball ball : balls) {
                    if (!ball.isReset()) {
                        Ball newBall1 = new Ball(ball.getX(), ball.getY(), ball.getRadius());
                        Ball newBall2 = new Ball(ball.getX(), ball.getY(), ball.getRadius());
                        balls.add(newBall1);
                        balls.add(newBall2);
                    }
                }
            },
            null,
            "images/powerUp_BigBall.png",
            Constants.THREE_BALLS_DURATION
    ),

    EXTEND(
            (context) -> {
                Paddle paddle = context.paddle();
                paddle.extendPaddle();
            },
            null,
            "images/extend.png",
            Constants.EXTEND_DURATION
    ),

    TINY(
            (context) -> {
                Paddle paddle = context.paddle();
                paddle.narrowPaddle();
            },
            null,
            "images/tiny.png",
            Constants.TINY_DURATION
    ),

    FAST(
            (context) -> {
                List<Ball> balls = context.balls();

                for (Ball ball : balls) {
                    ball.setDx(ball.getDx() * Constants.BALL_SPEED_AMPLIFIER);
                    ball.setDy(ball.getDy() * Constants.BALL_SPEED_AMPLIFIER);
                }
            },
            (context) -> {
                List<Ball> balls = context.balls();

                for (Ball ball : balls) {
                    ball.setDx(ball.getDx() / Constants.BALL_SPEED_AMPLIFIER);
                    ball.setDy(ball.getDy() / Constants.BALL_SPEED_AMPLIFIER);
                }
            },
            "images/powerUp_Fast.png",
            Constants.FAST_DURATION
    ),

    SLOW(
            (context) -> {
                List<Ball> balls = context.balls();

                for (Ball ball : balls) {
                    ball.setDx(ball.getDx() / Constants.BALL_SPEED_AMPLIFIER);
                    ball.setDy(ball.getDy() / Constants.BALL_SPEED_AMPLIFIER);
                }
            },
            (context) -> {
                List<Ball> balls = context.balls();

                for (Ball ball : balls) {
                    ball.setDx(ball.getDx() * Constants.BALL_SPEED_AMPLIFIER);
                    ball.setDy(ball.getDy() * Constants.BALL_SPEED_AMPLIFIER);
                }
            },
            "images/powerUp_Slow.png",
            Constants.SLOW_DURATION
    );

    private final PowerUpEffect applyEffect;
    private final PowerUpRemoval removeEffect;
    private final String imagePath;
    private final long duration;

    PowerUpType(PowerUpEffect apply, PowerUpRemoval remove, String imagePath, long duration) {
        this.applyEffect = apply;
        this.removeEffect = remove;
        this.imagePath = imagePath;
        this.duration = duration;
    }

    public PowerUpEffect getApplyEffect() {
        return applyEffect;
    }

    public PowerUpRemoval getRemoveEffect() {
        return removeEffect;
    }

    public long getDuration() {
        return duration;
    }

    public Image getImage() {
        try {
            String path = imagePath.startsWith("/") ? imagePath : "/" + imagePath;
            URL imageURL = getClass().getResource(path);
            if (imageURL == null) return null;
            return new Image(imageURL.toExternalForm());
        } catch (Exception e) {
            return null;
        }
    }
}