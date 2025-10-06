package vnu.uet.goldexperience.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class BaseObject {
    protected double x, y;
    protected int w, h;
    protected Image image;

    public BaseObject(double x, double y, int w, int h, String imagePath) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.image = new Image(imagePath);
    }

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

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public void render(GraphicsContext gc) {
        if (image != null) {
            gc.drawImage(image, x, y);
        }
    }
}
