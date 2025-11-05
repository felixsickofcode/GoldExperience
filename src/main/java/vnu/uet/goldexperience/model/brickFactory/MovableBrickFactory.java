package vnu.uet.goldexperience.model.brickFactory;

import vnu.uet.goldexperience.core.Constants;
import vnu.uet.goldexperience.model.brick.Brick;
import vnu.uet.goldexperience.model.brick.MovableBrick;
import vnu.uet.goldexperience.model.brick.UnbreakableMovableBrick;

import java.util.Map;

public class MovableBrickFactory implements BrickFactory {
    public enum PathType {
        HORIZONTAL,
        VERTICAL,
        CIRCULAR
    }
    public enum MovableBrickType {
        NORMAL,
        UNBREAKABLE
    }
    private final PathType pathType;
    private final MovableBrickType brickType;

    public MovableBrickFactory(PathType pathType, MovableBrickType brickType) {
        this.pathType = pathType;
        this.brickType = brickType;
    }

    @Override
    public Brick create(double x, double y, Map<String, Double> config) {
        double dx = config != null ? config.getOrDefault("dx", 0.0) : 0.0;
        double dy = config != null ? config.getOrDefault("dy", 0.0) : 0.0;
        double rangeX = config != null ? config.getOrDefault("rangeX", 0.0) : 0.0;
        double rangeY = config != null ? config.getOrDefault("rangeY", 0.0) : 0.0;

        switch (brickType) {
            case UNBREAKABLE:
                return new UnbreakableMovableBrick(
                        x, y,
                        Constants.NORMAL_BRICK_WIDTH,
                        Constants.NORMAL_BRICK_HEIGHT,
                        dx, dy, rangeX, rangeY, pathType
                );
            case NORMAL:
            default:
                return new MovableBrick(
                        x, y,
                        Constants.NORMAL_BRICK_WIDTH,
                        Constants.NORMAL_BRICK_HEIGHT,
                        dx, dy, rangeX, rangeY, pathType
                );
        }
    }
}