package vnu.uet.goldexperience.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public abstract class GameObject {

    protected double x, y, width, height;
    protected Image image;

    public GameObject(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void update(double deltaTime);
    public abstract void render(GraphicsContext gc);

    public void drawHitBox(GraphicsContext gc, double x, double y, double width, double height) {
        gc.setStroke(Color.RED);
        gc.setLineWidth(2);
        gc.strokeRect(x, y, width, height);
    }
    /**
     * Getter and Setter
     */
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }
}
