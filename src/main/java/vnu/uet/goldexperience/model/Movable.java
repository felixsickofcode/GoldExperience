package vnu.uet.goldexperience.model;

public interface Movable {
    void move(double deltaTime);
    void setDx(double dx);
    void setDy(double dy);
    double getDx();
    double getDy();
}
