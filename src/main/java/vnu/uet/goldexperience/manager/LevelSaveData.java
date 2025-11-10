package vnu.uet.goldexperience.manager;

import vnu.uet.goldexperience.model.*;

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

    // Lưu trực tiếp objects đơn giản
    private List<Ball> balls;
    private Paddle paddle;
    private List<PowerUp> fallingPowerUps;

    // Brick cần lưu theo cách khác vì có factory
    private List<BrickSaveInfo> bricks;

    // Active powerups chỉ cần type + duration
    private List<ActivePowerupInfo> activePowerups;

    private String timestamp;

    public LevelSaveData() {
        this.balls = new ArrayList<>();
        this.bricks = new ArrayList<>();
        this.fallingPowerUps = new ArrayList<>();
        this.activePowerups = new ArrayList<>();
        this.timestamp = java.time.LocalDateTime.now().toString();
    }

    /**
     * Info để recreate brick bằng factory
     * Config chứa: dx, dy, rangeX, rangeY cho movable bricks
     */
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

    /**
     * Info cho active powerups
     */
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

    // Getters and Setters
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

    public List<Ball> getBalls() {
        return balls;
    }

    public void setBalls(List<Ball> balls) {
        this.balls = balls;
    }

    public List<BrickSaveInfo> getBricks() {
        return bricks;
    }

    public void setBricks(List<BrickSaveInfo> bricks) {
        this.bricks = bricks;
    }

    public Paddle getPaddle() {
        return paddle;
    }

    public void setPaddle(Paddle paddle) {
        this.paddle = paddle;
    }

    public List<PowerUp> getFallingPowerUps() {
        return fallingPowerUps;
    }

    public void setFallingPowerUps(List<PowerUp> powerUps) {
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