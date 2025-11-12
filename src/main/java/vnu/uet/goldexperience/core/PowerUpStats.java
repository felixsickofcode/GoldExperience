package vnu.uet.goldexperience.core;

import vnu.uet.goldexperience.model.PowerUpType;

// lớp record để lưu trạng thái nâng cấp hiện tại của PU - trong tương lai có thể không cần (KHÔNG CẦN THẬT Ạ)
public record PowerUpStats(
        long duration,
        double value
) {
    public static PowerUpStats getDefault(PowerUpType type) {
        return switch(type) {
            case PowerUpType.BULLETS -> new PowerUpStats(
                    Constants.BULLETS_DURATION, Constants.BULLET_COOLDOWN_MS);

            case PowerUpType.FAST -> new PowerUpStats(
                    Constants.FAST_DURATION, Constants.BALL_SPEED_AMPLIFIER);

            case PowerUpType.SLOW -> new PowerUpStats(
                    Constants.SLOW_DURATION, 1.0 / Constants.BALL_SPEED_AMPLIFIER);

            default -> new PowerUpStats(0, 0);
        };
    }
}
