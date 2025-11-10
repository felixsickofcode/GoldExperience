package vnu.uet.goldexperience.manager;

import vnu.uet.goldexperience.core.GameContext;
import vnu.uet.goldexperience.model.PowerUp;

public class ActivePowerUp {

    private final PowerUp powerUp;
    private final long startTime;
    private final GameContext context;

    public ActivePowerUp(PowerUp powerUp, GameContext context) {
        this.powerUp = powerUp;
        this.startTime = System.currentTimeMillis();
        this.context = context;
    }
//    public ActivePowerUp(PowerUp powerUp, GameContext context, double remainingMs) {
//        this.powerUp = powerUp;
//        this.context = context;
//
//        long elapsed = (long)(powerUp.getDuration() - remainingMs);
//        this.startTime = System.currentTimeMillis() - elapsed;
//    }
    public boolean isExpired() {
        if (powerUp.isPermanent())
            return false;

        return System.currentTimeMillis() - startTime >= powerUp.getDuration();
    }

    public void expire() {
        powerUp.removeEffect(context);
    }

    public double getRemainingTime() {
        if (powerUp.isPermanent())
            return Long.MAX_VALUE;

        long elapsed = System.currentTimeMillis() - startTime;
        return Math.max(0, powerUp.getDuration() - elapsed);
    }

    public PowerUp getPowerUp() {
        return powerUp;
    }
}