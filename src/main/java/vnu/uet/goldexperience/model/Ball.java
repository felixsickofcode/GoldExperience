package vnu.uet.goldexperience.model;

import vnu.uet.goldexperience.core.Constants;

public class Ball extends BaseObject {

    private double dx = 300;
    private double dy = -300;
    public int size = Constants.BALL_SIZE;

    public Ball(double x, double y) {
        super(x , y, 0, 0, Ball.class.getResource("/images/bigball.png").toExternalForm());
    }

    public void update(double deltaTime) {
        x += dx * deltaTime;
        y += dy * deltaTime;
    }

    public void bounceX() { dx = -dx; }
    public void bounceY() { dy = -dy; }
}
