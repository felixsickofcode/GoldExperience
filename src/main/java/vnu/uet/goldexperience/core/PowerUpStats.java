package vnu.uet.goldexperience.core;

import vnu.uet.goldexperience.model.PowerUpType;

// lớp record để lưu trạng thái nâng cấp hiện tại của PU - trong tương lai có thể không cần
public record PowerUpStats(
        long duration,
        double value
) {
    public static PowerUpStats getDefault(PowerUpType type) {
        return switch(type) {
            case PowerUpType.BULLETS -> new PowerUpStats(15_000, 0.7);
            case PowerUpType.FAST -> new PowerUpStats(10_000, 1.5);
            case PowerUpType.SLOW -> new PowerUpStats(10_000, 1.0 / 1.5);
            default -> new PowerUpStats(0, 0);
        };
    }
}
