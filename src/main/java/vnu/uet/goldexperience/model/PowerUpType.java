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

                // Danh sách list tạm để tránh lỗi vừa thêm vừa duyệt
                List<Ball> newBallsToAdd = new ArrayList<>();

                // Nhân bản cho tất cả các bóng xuất hiện trong game
                for (Ball ball : balls) {
                    if (!ball.isReset()) {
                        // Lấy bóng hiện tại
                        double currentDx = ball.getDx();
                        double currentDy = ball.getDy();
                        double speed = Math.hypot(currentDx, currentDy);

                        if (speed == 0) {
                            continue;
                        }

                        // Calculate current angle
                        double currentAngle = Math.atan2(currentDy, currentDx);

                        // Góc tự phóng mới cho 2 quả phân thân: 30/2 = 15 độ mỗi quả
                        double angleSpread = Math.toRadians(15);
                        double angle1 = currentAngle - angleSpread;
                        double angle2 = currentAngle + angleSpread;

                        Ball newBall1 = new Ball(ball.getX(), ball.getY(), ball.getRadius());
                        Ball newBall2 = new Ball(ball.getX(), ball.getY(), ball.getRadius());

                        // Sao chép tốc độ dx, dy của bóng gốc
                        newBall1.applySpeedScale(ball.getSpeedScale());
                        newBall2.applySpeedScale(ball.getSpeedScale());

                        // Bóng tự phóng từ 2 góc mới
                        newBall1.setDx(speed * Math.cos(angle1));
                        newBall1.setDy(speed * Math.sin(angle1));

                        newBall2.setDx(speed * Math.cos(angle2));
                        newBall2.setDy(speed * Math.sin(angle2));

                        newBall1.setReset(false);
                        newBall2.setReset(false);

                        // Thêm vào danh sách tạm (cho vào Balls là bay màu)
                        newBallsToAdd.add(newBall1);
                        newBallsToAdd.add(newBall2);

                        break;
                    }
                }

                if (!newBallsToAdd.isEmpty()) {
                    balls.addAll(newBallsToAdd);
                }
            },
            null,
            "images/3ball.png",
            true
    ),

    EXTEND(
            (context) -> {
                Paddle paddle = context.paddle();
                paddle.extendPaddle();
            },
            null,
            "images/extend.png",
            true
    ),

    TINY(
            (context) -> {
                Paddle paddle = context.paddle();
                paddle.narrowPaddle();
            },
            null,
            "images/tiny.png",
            true
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
            true
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
            true
    ),

    BULLETS(
            (context) -> {
                context.paddle().setShooting(true, Constants.BULLETS_DURATION);
            },
            (context) -> {
                context.paddle().setShooting(false, 0);
            },
            "images/bullet.png",
            true
    );

    private final PowerUpEffect applyEffect;
    private final PowerUpRemoval removeEffect;
    private final String imagePath;
    private final boolean droppable;

    PowerUpType(PowerUpEffect apply, PowerUpRemoval remove, String imagePath, boolean droppable) {
        this.applyEffect = apply;
        this.removeEffect = remove;
        this.imagePath = imagePath;
        this.droppable = droppable;
    }

    public PowerUpEffect getApplyEffect() {
        return applyEffect;
    }

    public PowerUpRemoval getRemoveEffect() {
        return removeEffect;
    }

    public Image getImage() {
        try {
            String path = imagePath.startsWith("/") ? imagePath : "/" + imagePath;
            URL imageURL = getClass().getResource(path);
            if (imageURL == null) {
                return null;
            }

            return new Image(imageURL.toExternalForm());
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isDroppableItem() {
        return this.droppable;
    }

    public static PowerUpType randomDroppable() {
        List<PowerUpType> options = new ArrayList<>();

        for (PowerUpType t : values()) {
            if (t.isDroppableItem()) {
                options.add(t);
            }
        }

        if (options.isEmpty()) {
            return EXTEND;
        }

        int idx = ThreadLocalRandom.current().nextInt(options.size());

        return options.get(idx);
    }
}