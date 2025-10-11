package vnu.uet.goldexperience.model;

import vnu.uet.goldexperience.core.GameContext;

@FunctionalInterface
public interface PowerUpEffect {
    void apply(GameContext context);
}
