package vnu.uet.goldexperience.model;

import vnu.uet.goldexperience.core.Constants;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum BrickType {
    NORMAL("normal") {
        @Override
        public Brick create(double x, double y) {
            return new NormalBrick(x, y, Constants.NORMAL_BRICK_WIDTH, Constants.NORMAL_BRICK_HEIGHT);
        }
    },
    UNBREAKABLE("unbreakable") {
        @Override
        public Brick create(double x, double y) {
            return new UnbreakableBrick(x, y, Constants.NORMAL_BRICK_WIDTH, Constants.NORMAL_BRICK_HEIGHT);
        }
    },
    EXPLODE("explode") {
        @Override
        public Brick create(double x, double y) {
            return new ExplodeBrick(x, y, Constants.NORMAL_BRICK_WIDTH, Constants.NORMAL_BRICK_HEIGHT);
        }
    },
    MEDIUM("medium") {
        @Override
        public Brick create(double x, double y) {
            return new MediumBrick(x, y, Constants.NORMAL_BRICK_WIDTH, Constants.NORMAL_BRICK_HEIGHT);
        }
    };

    private final String key;

    BrickType(String key) {
        this.key = key;
    }

    public abstract Brick create(double x, double y);

    private static final Map<String, BrickType> KEY_TO_TYPE_MAP =
            Stream.of(values()).collect(Collectors.toMap(e -> e.key, e -> e));

    public static BrickType fromString(String key) {
        return KEY_TO_TYPE_MAP.get(key);
    }
}