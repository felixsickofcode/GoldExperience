package vnu.uet.goldexperience.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import java.util.HashSet;
import java.util.Set;

public class InputHandler {
    private final Set<KeyCode> pressedKeys = new HashSet<>();

    public InputHandler(Scene scene) {
        scene.setOnKeyPressed(event -> pressedKeys.add(event.getCode()));
        scene.setOnKeyReleased(event -> pressedKeys.remove(event.getCode()));
    }

    public boolean isRightPressed() {
        return pressedKeys.contains(KeyCode.RIGHT) || pressedKeys.contains(KeyCode.D);
    }

    public boolean isLeftPressed() {
        return pressedKeys.contains(KeyCode.LEFT) || pressedKeys.contains(KeyCode.A);
    }
}
