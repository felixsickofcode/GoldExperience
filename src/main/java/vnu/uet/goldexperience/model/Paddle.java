package vnu.uet.goldexperience.model;

import vnu.uet.goldexperience.core.Constants;

public class Paddle extends BaseObject {
    public double speed = 400;

    public Paddle(double x, double y) {
        super(x, y, Constants.PADDLE_WIDTH, Constants.PADDLE_HEIGHT,
                Paddle.class.getResource("/images/paddle.png").toExternalForm());
    }

    public void moveLeft(double deltaTime) {
        x -= speed * deltaTime;
        if (x < 0) x = 0;
    }

    public void moveRight(double deltaTime) {
        x += speed * deltaTime;
        if (x + w > Constants.WINDOW_WIDTH)
            x = Constants.WINDOW_WIDTH - w;
    }
}
