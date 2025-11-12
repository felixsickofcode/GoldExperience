package vnu.uet.goldexperience.manager;

import vnu.uet.goldexperience.core.GameContext;
import vnu.uet.goldexperience.model.PowerUp;

public class ActivePowerUp {

    private final PowerUp powerUp;
    private final GameContext context;
    private final long duration;
    private double remainingMs;

    public ActivePowerUp(PowerUp powerUp, GameContext context, long duration) {
        this.powerUp = powerUp;
        this.context = context;
        this.duration = duration;
        this.remainingMs = duration;
    }

    public ActivePowerUp(PowerUp powerUp, GameContext context, double remainingMs, long duration) {
        this.powerUp = powerUp;
        this.context = context;
        this.duration = duration;
        this.remainingMs = remainingMs;
    }
    public void update(double deltaTimeMs) {
        if (!isPermanent()) {
            remainingMs -= deltaTimeMs;
        }
    }
    public boolean isExpired() {
        if (isPermanent()) {
            return false;
        }

        return remainingMs <= 0;
    }

    public void expire() {
        powerUp.removeEffect(context);
    }

    public boolean isPermanent() {
        return duration == 0;
    }

    public PowerUp getPowerUp() {
        return powerUp;
    }

    public double getRemainingTime() {
        if (isPermanent()) {
            return Double.MAX_VALUE;
        }

        return Math.max(0, remainingMs);
    }
}