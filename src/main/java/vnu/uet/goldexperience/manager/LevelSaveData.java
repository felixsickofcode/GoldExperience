package vnu.uet.goldexperience.manager;

import vnu.uet.goldexperience.core.Constants;
import vnu.uet.goldexperience.model.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class lưu snapshot của 1 ván chơi
 * Sử dụng BrickFactory để recreate bricks khi load
 */
public class LevelSaveData {
    private int levelNumber;
    private int score;
    private int lives;

    private List<BallData> balls;
    private PaddleData paddle;
    private List<PowerUpData> fallingPowerUps;

    private List<BrickSaveInfo> bricks;

    private List<ActivePowerupInfo> activePowerups;

    private List<BulletData> bullets;

    private String timestamp;

    public LevelSaveData() {
        this.balls = new ArrayList<>();
        this.bricks = new ArrayList<>();
        this.fallingPowerUps = new ArrayList<>();
        this.activePowerups = new ArrayList<>();
        this.bullets = new ArrayList<>();
        this.timestamp = LocalDateTime.now().toString();
    }

    public static class BallData {
        private double x, y;
        private double dx, dy;
        private boolean isAttached;
        private double speedScale;

        public BallData() {
        }

        public BallData(Ball ball) {
            this.x = ball.getX();
            this.y = ball.getY();
            this.dx = ball.getDx();
            this.dy = ball.getDy();
            this.isAttached = ball.isReset();
            this.speedScale = ball.getSpeedScale();
        }

        public Ball toBall() {
            Ball ball = new Ball(x, y, Constants.NORMAL_BALL_SIZE);
            ball.setDx(dx);
            ball.setDy(dy);
            ball.setReset(isAttached);
            if (speedScale != 1.0) {
                ball.setSpeedScale(speedScale);
            }
            return ball;
        }

        // Getters and Setters
        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        public double getDx() {
            return dx;
        }

        public void setDx(double dx) {
            this.dx = dx;
        }

        public double getDy() {
            return dy;
        }

        public void setDy(double dy) {
            this.dy = dy;
        }

        public boolean isAttached() {
            return isAttached;
        }

        public void setAttached(boolean attached) {
            isAttached = attached;
        }

        public double getSpeedScale() {
            return speedScale;
        }

        public void setSpeedScale(double speedScale) {
            this.speedScale = speedScale;
        }
    }

    /**
     * Data class cho Paddle
     */
    public static class PaddleData {
        private double x, y;
        private double width;
        private int size;

        public PaddleData() {
        }

        public PaddleData(Paddle paddle) {
            this.x = paddle.getX();
            this.y = paddle.getY();
            this.width = paddle.getWidth();
            this.size = paddle.getSize();
        }

        public Paddle toPaddle() {
            Paddle paddle = new Paddle(x, y, width, Constants.PADDLE_HEIGHT);
            paddle.setSize(size);
            return paddle;
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        public double getWidth() {
            return width;
        }

        public void setWidth(double width) {
            this.width = width;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }
    }

    public static class PowerUpData {
        private String type;
        private double x, y;
        private double dy;

        public PowerUpData() {
        }

        public PowerUpData(PowerUp powerUp) {
            this.type = powerUp.getType().name();
            this.x = powerUp.getX();
            this.y = powerUp.getY();
            this.dy = powerUp.getDy();
        }

        public PowerUp toPowerUp() {
            PowerUp powerUp = new SimplePowerUp(x, y, PowerUpType.valueOf(type));
            powerUp.setDy(dy);
            return powerUp;
        }

        // Getters and Setters
        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        public double getDy() {
            return dy;
        }

        public void setDy(double dy) {
            this.dy = dy;
        }
    }

    public static class BrickSaveInfo {
        private String type;  // Key từ BrickType enum
        private double x, y;
        private int hitPoints;
        private Map<String, Double> config;  // dx, dy, rangeX, rangeY

        public BrickSaveInfo() {
            this.config = new HashMap<>();
        }

        public BrickSaveInfo(String type, double x, double y, int hitPoints, Map<String, Double> config) {
            this.type = type;
            this.x = x;
            this.y = y;
            this.hitPoints = hitPoints;
            this.config = config != null ? new HashMap<>(config) : new HashMap<>();
        }

        // Getters and Setters
        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        public int getHitPoints() {
            return hitPoints;
        }

        public void setHitPoints(int hitPoints) {
            this.hitPoints = hitPoints;
        }

        public Map<String, Double> getConfig() {
            return config;
        }

        public void setConfig(Map<String, Double> config) {
            this.config = config;
        }
    }

    public static class BulletData {
        private double x, y;
        private double dx, dy;
        private double width, height;
        private double bulletSpawnTimer;



        public BulletData() {
        }

        public BulletData(Bullet bullet) {
            this.x = bullet.getX();
            this.y = bullet.getY();
            this.dx = bullet.getDx();
            this.dy = bullet.getDy();
            this.width = bullet.getWidth();
            this.height = bullet.getHeight();
        }

        public Bullet toBullet() {
            return new Bullet(x, y, width, height, dx, dy);
        }

        // Getters and Setters
        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        public double getDx() {
            return dx;
        }

        public void setDx(double dx) {
            this.dx = dx;
        }

        public double getDy() {
            return dy;
        }

        public void setDy(double dy) {
            this.dy = dy;
        }

        public double getWidth() {
            return width;
        }

        public void setWidth(double width) {
            this.width = width;
        }

        public double getHeight() {
            return height;
        }

        public void setHeight(double height) {
            this.height = height;
        }
    }

    public static class ActivePowerupInfo {
        private String type;
        private double remainingDuration;

        public ActivePowerupInfo() {
        }

        public ActivePowerupInfo(String type, double remainingDuration) {
            this.type = type;
            this.remainingDuration = remainingDuration;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public double getRemainingDuration() {
            return remainingDuration;
        }

        public void setRemainingDuration(double duration) {
            this.remainingDuration = duration;
        }
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public void setLevelNumber(int levelNumber) {
        this.levelNumber = levelNumber;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public List<BallData> getBalls() {
        return balls;
    }

    public void setBalls(List<BallData> balls) {
        this.balls = balls;
    }

    public List<BulletData> getBullets() {
        return bullets;
    }

    public void setBullets(List<BulletData> bullets) {
        this.bullets = bullets;
    }

    public List<BrickSaveInfo> getBricks() {
        return bricks;
    }

    public void setBricks(List<BrickSaveInfo> bricks) {
        this.bricks = bricks;
    }

    public PaddleData getPaddle() {
        return paddle;
    }

    public void setPaddle(PaddleData paddle) {
        this.paddle = paddle;
    }

    public List<PowerUpData> getFallingPowerUps() {
        return fallingPowerUps;
    }

    public void setFallingPowerUps(List<PowerUpData> powerUps) {
        this.fallingPowerUps = powerUps;
    }

    public List<ActivePowerupInfo> getActivePowerups() {
        return activePowerups;
    }

    public void setActivePowerups(List<ActivePowerupInfo> powerups) {
        this.activePowerups = powerups;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return String.format("LevelSaveData{level=%d, score=%d, lives=%d, balls=%d, bricks=%d}",
                levelNumber, score, lives, balls.size(), bricks.size());
    }
}