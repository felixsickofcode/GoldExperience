package vnu.uet.goldexperience.model;

public interface Movable {
    void move(double dt);
    void setDx(double dx);
    void setDy(double dy);
    double getDx();
    double getDy();
}
