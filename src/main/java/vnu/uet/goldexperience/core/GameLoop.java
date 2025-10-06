package vnu.uet.goldexperience.core;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import vnu.uet.goldexperience.controller.InputHandler;

import vnu.uet.goldexperience.controller.GameController;
import vnu.uet.goldexperience.view.GameView;

public class GameLoop extends AnimationTimer {
    private long lastTime = System.nanoTime();
    private double deltaTime = 0;

    private final GameController controller;
    private final GameView view;

    public GameLoop(Canvas canvas, InputHandler input) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        view = new GameView(gc);
        controller = new GameController(view, input);
    }

    @Override
    public void handle(long now) {
        deltaTime = (now - lastTime) / 1_000_000_000.0;

        if (now - lastTime < Constants.TIME_PER_FRAME) {
            return;
        }

        controller.update(deltaTime);

        view.render(controller.getBall(), controller.getPaddle());

        lastTime = now;
    }
}
