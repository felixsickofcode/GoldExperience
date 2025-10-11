package vnu.uet.goldexperience.model;

import vnu.uet.goldexperience.core.GameContext;

@FunctionalInterface
public interface PowerUpRemoval {
    void remove(GameContext context);
}
