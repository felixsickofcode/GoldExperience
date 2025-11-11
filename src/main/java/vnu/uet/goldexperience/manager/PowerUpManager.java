package vnu.uet.goldexperience.manager;

import vnu.uet.goldexperience.core.Constants;
import vnu.uet.goldexperience.core.GameContext;
import vnu.uet.goldexperience.model.PowerUp;
import vnu.uet.goldexperience.model.PowerUpType;

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

        if (!powerUp.isPermanent()) {
            activePowerUps.add(new ActivePowerUp(powerUp, context));
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

    public List<LevelSaveData.ActivePowerupInfo> captureActivePowerupsInfo() {
        List<LevelSaveData.ActivePowerupInfo> infos = new ArrayList<>();
        for (ActivePowerUp ap : activePowerUps) {
            infos.add(new LevelSaveData.ActivePowerupInfo(
                    ap.getPowerUp().getType().name(),
                    ap.getRemainingTime()
            ));
        }
        return infos;
    }

    public void restoreActivePowerup(String typeString, double remainingMs) {
        try {
            PowerUpType type = PowerUpType.valueOf(typeString);

            PowerUp powerUp = createPowerUpByType(type);

            powerUp.applyEffect(context);

            if (!powerUp.isPermanent()) {
                activePowerUps.add(new ActivePowerUp(powerUp, context, remainingMs));
            }

        } catch (IllegalArgumentException e) {
            System.err.println("Unknown PowerUpType: " + typeString);
        }
    }

    private PowerUp createPowerUpByType(PowerUpType type) {
        return new PowerUp(0, 0, 30, 30, type) {
        };
    }

    public void clearAll() {
        for (ActivePowerUp ap : activePowerUps) {
            ap.expire();
        }

        activePowerUps.clear();
    }
}