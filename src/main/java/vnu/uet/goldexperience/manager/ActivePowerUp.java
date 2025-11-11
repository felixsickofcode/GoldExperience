package vnu.uet.goldexperience.manager;

import vnu.uet.goldexperience.core.GameContext;
import vnu.uet.goldexperience.model.PowerUp;

public class ActivePowerUp {

    private final PowerUp powerUp;
    private final long startTime;
    private final GameContext context;
    private final long duration;

    public ActivePowerUp(PowerUp powerUp, GameContext context, long duration) {
        this.powerUp = powerUp;
        this.startTime = System.currentTimeMillis();
        this.context = context;
        this.duration = duration;
    }

    public ActivePowerUp(PowerUp powerUp, GameContext context, double remainingMs, long duration) {
        this.powerUp = powerUp;
        this.context = context;
        this.duration = duration;

        long elapsed = (long) (this.duration - remainingMs);
        this.startTime = System.currentTimeMillis() - elapsed;
    }

    public boolean isExpired() {
        if (isPermanent()) {
            return false;
        }

        return System.currentTimeMillis() - startTime >= this.duration;
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
            return Long.MAX_VALUE;
        }

        long elapsed = System.currentTimeMillis() - startTime;
        return Math.max(0, duration - elapsed);
    }
}