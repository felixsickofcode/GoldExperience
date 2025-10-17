package vnu.uet.goldexperience.manager;

import javafx.scene.input.KeyCode;
import vnu.uet.goldexperience.core.Action;

import java.util.*;

public class InputManager {
    private final Set<KeyCode> pressed = new HashSet<>();
    private final Map<KeyCode, Action> keyBindings = new HashMap<>();

    public InputManager() {
        keyBindings.put(KeyCode.LEFT, Action.MOVE_LEFT);
        keyBindings.put(KeyCode.J, Action.MOVE_LEFT);

        keyBindings.put(KeyCode.RIGHT, Action.MOVE_RIGHT);
        keyBindings.put(KeyCode.L, Action.MOVE_RIGHT);

        keyBindings.put(KeyCode.SPACE, Action.SHOOT);
    }

    public void keyPressed(KeyCode code) {
        pressed.add(code);
    }

    public void keyReleased(KeyCode code) {
        pressed.remove(code);
    }

    public boolean isPressed(KeyCode code) {
        return pressed.contains(code);
    }

    public boolean isActionActive(Action action) {
        for (KeyCode code : pressed) {
            if (keyBindings.get(code) == action) {
                return true;
            }
        }
        return false;
    }
}
