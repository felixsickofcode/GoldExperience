package vnu.uet.goldexperience.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import java.util.HashSet;
import java.util.Set;

public class InputHandler {
    private final Set<KeyCode> pressed = new HashSet<>();

    public void keyPressed(KeyCode code) { pressed.add(code); }
    public void keyReleased(KeyCode code) { pressed.remove(code); }
    public boolean isPressed(KeyCode code) { return pressed.contains(code); }
}

