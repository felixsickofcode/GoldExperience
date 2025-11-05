package vnu.uet.goldexperience.model;
import vnu.uet.goldexperience.model.brickFactory.MovableBrickFactory.PathType;
public class MovableBrick extends MediumBrick implements Movable {
    protected double dx, dy;
    private final double originX;
    private final double originY;
    private final double moveRangeX;
    private final double moveRangeY;
    private final PathType pathType;
    private double angle;

    public MovableBrick(double x, double y, double width, double height,
                        double dx, double dy, double moveRangeX, double moveRangeY,
                        PathType pathType) {
        super(x, y, width, height);
        this.dx = dx;
        this.dy = dy;
        this.originX = x;
        this.originY = y;
        this.moveRangeX = moveRangeX;
        this.moveRangeY = moveRangeY;
        this.pathType = pathType;
        this.angle = 0;
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        move(deltaTime);
    }

    @Override
    public void move(double deltaTime) {
        switch (pathType) {
            case HORIZONTAL -> moveHorizontal(deltaTime);
            case VERTICAL -> moveVertical(deltaTime);
            case CIRCULAR -> moveCircular(deltaTime);
        }
    }

    private void moveHorizontal(double dt) {
        x += dx * dt;
        if (x > originX + moveRangeX) {
            x = originX + moveRangeX;
            dx = -dx;
        } else if (x < originX - moveRangeX) {
            x = originX - moveRangeX;
            dx = -dx;
        }
    }

    private void moveVertical(double dt) {
        y += dy * dt;
        if (y > originY + moveRangeY) {
            y = originY + moveRangeY;
            dy = -dy;
        } else if (y < originY - moveRangeY) {
            y = originY - moveRangeY;
            dy = -dy;
        }
    }

    private void moveCircular(double dt) {
        angle += 1.5 * dt;
        if (angle > 2 * Math.PI) {
            angle -= 2 * Math.PI;
        }
        x = originX + moveRangeX * Math.cos(angle);
        y = originY + moveRangeY * Math.sin(angle);
    }


    @Override
    public double getDx() {
        return dx;
    }

    @Override
    public void setDx(double dx) {
        this.dx = dx;
    }

    @Override
    public double getDy() {
        return dy;
    }

    @Override
    public void setDy(double dy) {
        this.dy = dy;
    }
}
