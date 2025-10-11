package vnu.uet.goldexperience.manager;

import vnu.uet.goldexperience.core.GameContext;
import vnu.uet.goldexperience.model.PowerUp;

import java.util.ArrayList;
import java.util.List;

public class PowerUpManager {

    private final List<ActivePowerUp> activePowerUps;
    private final GameContext context;

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

    public void clearAll() {
        for (ActivePowerUp ap : activePowerUps) {
            ap.expire();
        }

        activePowerUps.clear();
    }
}