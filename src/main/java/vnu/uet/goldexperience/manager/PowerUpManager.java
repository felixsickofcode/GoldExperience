package vnu.uet.goldexperience.manager;

import vnu.uet.goldexperience.core.Constants;
import vnu.uet.goldexperience.core.GameContext;
import vnu.uet.goldexperience.core.PowerUpStats;
import vnu.uet.goldexperience.model.Bullet;
import vnu.uet.goldexperience.model.Paddle;
import vnu.uet.goldexperience.model.PowerUp;
import vnu.uet.goldexperience.model.PowerUpType;

import java.util.ArrayList;
import java.util.List;

public class PowerUpManager {

    private final List<ActivePowerUp> activePowerUps;
    private final GameContext context;

    private double bulletSpawnTimer = 0.0;
//    private boolean isBulletActive = false;

    public List<ActivePowerUp> getActivePowerUps() {
        return activePowerUps;
    }

    public PowerUpManager(GameContext context) {
        this.context = context;
        activePowerUps = new ArrayList<>();
    }

    public void activatePowerUp(PowerUp powerUp) {
        powerUp.applyEffect(context);

        PowerUpStats stats = GameDataManager.getPowerUpStatsFor(powerUp.getType());
        long duration = stats.duration();

        if (duration > 0) {
            PowerUpType type = powerUp.getType();
            activePowerUps.removeIf(ap -> ap.getPowerUp().getType() == type);

            activePowerUps.add(new ActivePowerUp(powerUp, context, duration));

            if (powerUp.getType() == PowerUpType.BULLETS) {
//                isBulletActive = true;
                bulletSpawnTimer = 0.0;
            }
        }
    }

    public void update(double deltaTime) {
        activePowerUps.removeIf(ap -> {
            if (ap.isExpired()) {
                ap.expire();

//                if (ap.getPowerUp().getType() == PowerUpType.BULLETS) {
//                    isBulletActive = false;
//                }

                return true;
            }

            return false;
        });

        if (context.paddle().isShooting()) {
            bulletSpawnTimer -= deltaTime;

            if (bulletSpawnTimer <= 0.0) {
                PowerUpStats stats = GameDataManager.getPowerUpStatsFor(PowerUpType.BULLETS);
                bulletSpawnTimer = stats.value() / 1000.0;

                    spawnBullet();
            }
        }
    }

    private void spawnBullet() {
        Paddle paddle = context.paddle();
        double bulletY = paddle.getY() - Constants.BULLET_HEIGHT;
        double dy = -Constants.BULLET_SPEED;

        double bulletX1 = paddle.getX() + Constants.BULLET_WIDTH / 2;
        Bullet bullet1 = new Bullet(
                bulletX1, bulletY,
                Constants.BULLET_WIDTH, Constants.BULLET_HEIGHT, 0, dy
        );

        double bulletX2 = paddle.getX() + paddle.getWidth() - Constants.BULLET_WIDTH * 1.5;
        Bullet bullet2 = new Bullet(
                bulletX2, bulletY,
                Constants.BULLET_WIDTH, Constants.BULLET_HEIGHT, 0, dy
        );

        context.bullets().add(bullet1);
        context.bullets().add(bullet2);
    }

//    public List<LevelSaveData.ActivePowerupInfo> captureActivePowerupsInfo() {
//        List<LevelSaveData.ActivePowerupInfo> infos = new ArrayList<>();
//        for (ActivePowerUp ap : activePowerUps) {
//            infos.add(new LevelSaveData.ActivePowerupInfo(
//                    ap.getPowerUp().getType().name(),
//                    ap.getRemainingTime()
//            ));
//        }
//        return infos;
//    }
//    public void restoreActivePowerup(String typeString, double remainingMs) {
//        try {
//            // Convert string -> PowerUpType enum
//            PowerUpType type = PowerUpType.valueOf(typeString);
//
//            // Tạo PowerUp object từ factory hoặc create method
//            PowerUp powerUp = createPowerUpByType(type);
//
//            // Apply effect ngay
//            powerUp.applyEffect(context);
//
//            // Nếu không permanent thì thêm vào active list với remaining time
//            if (!powerUp.isPermanent()) {
//                activePowerUps.add(new ActivePowerUp(powerUp, context, remainingMs));
//            }
//
//        } catch (IllegalArgumentException e) {
//            System.err.println("Unknown PowerUpType: " + typeString);
//        }
//    }
//
//    private PowerUp createPowerUpByType(PowerUpType type) {
//        return new PowerUp(0, 0, 30, 30, type) {
//            // Anonymous class implementation
//        };
//    }

    public void clearAll() {
        for (ActivePowerUp ap : activePowerUps) {
            ap.expire();
        }

        activePowerUps.clear();
    }
}