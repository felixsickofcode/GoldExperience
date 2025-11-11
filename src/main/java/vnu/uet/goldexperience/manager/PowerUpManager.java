package vnu.uet.goldexperience.manager;

import vnu.uet.goldexperience.core.GameContext;
import vnu.uet.goldexperience.core.PowerUpStats;
import vnu.uet.goldexperience.model.PowerUp;

import java.util.ArrayList;
import java.util.List;

public class PowerUpManager {

    private final List<ActivePowerUp> activePowerUps;
    private final GameContext context;

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
            activePowerUps.add(new ActivePowerUp(powerUp, context, duration));
        }
    }

    public void update() {
        activePowerUps.removeIf(ap -> {
            if (ap.isExpired()) {
                ap.expire();
                return true;
            }

            return false;
        });
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