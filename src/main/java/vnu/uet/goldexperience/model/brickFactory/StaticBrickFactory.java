package vnu.uet.goldexperience.model.brickFactory;

import vnu.uet.goldexperience.core.Constants;
import vnu.uet.goldexperience.model.brick.*;

import java.util.Map;

public class StaticBrickFactory implements BrickFactory {
    public enum StaticBrickType {
        NORMAL, UNBREAKABLE, EXPLODE, MEDIUM, STRONG
    }

    private final StaticBrickType brickType;

    public StaticBrickFactory(StaticBrickType brickType) {
        this.brickType = brickType;
    }
    @Override
    public Brick create(double x, double y, Map<String, Double> config) {
        return switch (brickType) {
            case NORMAL -> new NormalBrick(x, y, Constants.NORMAL_BRICK_WIDTH, Constants.NORMAL_BRICK_HEIGHT);
            case UNBREAKABLE -> new UnbreakableBrick(x, y, Constants.NORMAL_BRICK_WIDTH, Constants.NORMAL_BRICK_HEIGHT);
            case EXPLODE -> new ExplodeBrick(x, y, Constants.NORMAL_BRICK_WIDTH, Constants.NORMAL_BRICK_HEIGHT);
            case MEDIUM -> new MediumBrick(x, y, Constants.NORMAL_BRICK_WIDTH, Constants.NORMAL_BRICK_HEIGHT);
            case STRONG -> new StrongBrick(x, y, Constants.NORMAL_BRICK_WIDTH, Constants.NORMAL_BRICK_HEIGHT);
        };
    }
}