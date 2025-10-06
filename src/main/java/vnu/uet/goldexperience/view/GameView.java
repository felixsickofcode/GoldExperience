package vnu.uet.goldexperience.view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import vnu.uet.goldexperience.model.Ball;
import vnu.uet.goldexperience.model.Paddle;
import vnu.uet.goldexperience.core.Constants;

public class GameView {

    private GraphicsContext gc;

    public GameView(GraphicsContext gc) {
        this.gc = gc;
    }

    public void render(Ball ball, Paddle paddle) {
        gc.clearRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

        gc.setFill(Color.GRAY);
        gc.fillRect(40, 0, Constants.GAME_WIDTH, Constants.GAME_HEIGHT);

        ball.render(gc);

        paddle.render(gc);
    }
}
