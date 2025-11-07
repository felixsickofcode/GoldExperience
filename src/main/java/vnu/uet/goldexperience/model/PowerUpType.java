package vnu.uet.goldexperience.model;

import javafx.scene.image.Image;
import vnu.uet.goldexperience.core.Constants;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/* APPLY STRATEGY PATTERN */
public enum PowerUpType {
    THREE_BALLS(
            (context) -> {
                List<Ball> balls = context.balls();

                // Find the first active ball to spawn from
                for (Ball ball : balls) {
                    if (!ball.isReset()) {
                        // Get current ball's velocity (this will be the angle bisector)
                        double currentDx = ball.getDx();
                        double currentDy = ball.getDy();
                        double speed = Math.hypot(currentDx, currentDy);
                        
                        // Calculate current angle
                        double currentAngle = Math.atan2(currentDy, currentDx);
                        
                        // Create angle spread: ±30 degrees from current angle
                        double angleSpread = Math.toRadians(30);
                        double angle1 = currentAngle - angleSpread;
                        double angle2 = currentAngle + angleSpread;
                        
                        // Create two new balls at the current ball's position
                        Ball newBall1 = new Ball(ball.getX(), ball.getY(), ball.getRadius());
                        Ball newBall2 = new Ball(ball.getX(), ball.getY(), ball.getRadius());
                        
                        // Apply the current speed scale to new balls
                        newBall1.applySpeedScale(ball.getSpeedScale());
                        newBall2.applySpeedScale(ball.getSpeedScale());
                        
                        // Launch new balls with diagonal vectors
                        newBall1.setDx(speed * Math.cos(angle1));
                        newBall1.setDy(speed * Math.sin(angle1));
                        
                        newBall2.setDx(speed * Math.cos(angle2));
                        newBall2.setDy(speed * Math.sin(angle2));
                        
                        // Add to balls list
                        balls.add(newBall1);
                        balls.add(newBall2);
                        
                        // Only spawn once from the first active ball
                        break;
                    }
                }
            },
            null,
            "images/threeballs.png",
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
                    ball.applySpeedScale(Constants.BALL_SPEED_AMPLIFIER);
                }
            },
            (context) -> {
                List<Ball> balls = context.balls();
                for (Ball ball : balls) {
                    ball.applySpeedScale(1.0 / Constants.BALL_SPEED_AMPLIFIER);
                }
            },
            "images/fast.png",
            Constants.FAST_DURATION
    ),

    SLOW(
            (context) -> {
                List<Ball> balls = context.balls();
                for (Ball ball : balls) {
                    ball.applySpeedScale(1.0 / Constants.BALL_SPEED_AMPLIFIER);
                }
            },
            (context) -> {
                List<Ball> balls = context.balls();
                for (Ball ball : balls) {
                    ball.applySpeedScale(Constants.BALL_SPEED_AMPLIFIER);
                }
            },
            "images/slow.png",
            Constants.SLOW_DURATION
    );

    private final PowerUpEffect applyEffect;
    private final PowerUpRemoval removeEffect;
    private final String imagePath;
    private final long duration;

    // Cache to avoid re-checking sprite compatibility repeatedly
    private Boolean droppableCache = null;

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

    /**
     * Whether this power-up can be spawned as a falling item using the common 6-frame 48x33 spritesheet.
     * This auto-enables new power-ups as long as they provide a compatible spritesheet at {@link #imagePath}.
     */
    public boolean isDroppableItem() {
        if (droppableCache != null) return droppableCache;
        Image img = getImage();
        boolean ok = false;
        if (img != null) {
            ok = img.getWidth() >= 6 * Constants.POWER_UP_ITEM_WIDTH
                    && img.getHeight() >= Constants.POWER_UP_ITEM_HEIGHT;
        }
        droppableCache = ok;
        return ok;
    }

    /**
     * Returns a uniformly random droppable power-up type among all enum values that expose
     * a compatible spritesheet. If none qualify, defaults to EXTEND to ensure gameplay continuity.
     */
    public static PowerUpType randomDroppable() {
        List<PowerUpType> options = new ArrayList<>();
        for (PowerUpType t : values()) {
            if (t.isDroppableItem()) options.add(t);
        }
        if (options.isEmpty()) return EXTEND;
        int idx = ThreadLocalRandom.current().nextInt(options.size());
        return options.get(idx);
    }
}