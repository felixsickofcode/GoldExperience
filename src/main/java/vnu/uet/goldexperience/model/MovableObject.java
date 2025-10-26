package vnu.uet.goldexperience.model;

import javafx.scene.canvas.GraphicsContext;

public abstract class MovableObject extends GameObject implements Movable {

    protected double dx, dy;

    public MovableObject(double x, double y, double width, double height, double dx, double dy) {
        super(x, y, width, height);
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public abstract void update(double deltaTime);

    @Override
    public abstract void render(GraphicsContext gc);

    @Override
    public void setDx(double dx) {
        this.dx = dx;
    }

    @Override
    public void setDy(double dy) {
        this.dy = dy;
    }

    @Override
    public double getDy() {
        return dy;
    }

    @Override
    public double getDx() {
        return dx;
    }

    @Override
    public void move(double deltaTime) {
        x += dx * deltaTime;
        y += dy * deltaTime;
    }
}
