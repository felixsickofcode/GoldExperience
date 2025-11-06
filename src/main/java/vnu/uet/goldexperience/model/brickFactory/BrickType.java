package vnu.uet.goldexperience.model.brickFactory;

import vnu.uet.goldexperience.model.brick.Brick;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum BrickType {
    NORMAL("normal",
            new StaticBrickFactory(StaticBrickFactory.StaticBrickType.NORMAL)
    ),
    UNBREAKABLE("unbreakable",
            new StaticBrickFactory(StaticBrickFactory.StaticBrickType.UNBREAKABLE)
    ),
    EXPLODE("explode",
            new StaticBrickFactory(StaticBrickFactory.StaticBrickType.EXPLODE)
    ),
    MEDIUM("medium",
            new StaticBrickFactory(StaticBrickFactory.StaticBrickType.MEDIUM)
    ),
    STRONG("strong",
            new StaticBrickFactory(StaticBrickFactory.StaticBrickType.STRONG)
    ),

    MOVABLE_HORIZONTAL("movable_horizontal",
            new MovableBrickFactory(MovableBrickFactory.PathType.HORIZONTAL, MovableBrickFactory.MovableBrickType.NORMAL)
    ),

    MOVABLE_VERTICAL("movable_vertical",
            new MovableBrickFactory(MovableBrickFactory.PathType.VERTICAL, MovableBrickFactory.MovableBrickType.NORMAL)
    ),

    MOVABLE_CIRCULAR("movable_circular",
            new MovableBrickFactory(MovableBrickFactory.PathType.CIRCULAR, MovableBrickFactory.MovableBrickType.NORMAL)
    ),

    MOVABLE_UNBREAKABLE_VERTICAL("movable_unbreakable_vertical",
            new MovableBrickFactory(MovableBrickFactory.PathType.VERTICAL, MovableBrickFactory.MovableBrickType.UNBREAKABLE)
    ),

    MOVABLE_UNBREAKABLE_HORIZONTAL("movable_unbreakable_horizontal",
            new MovableBrickFactory(MovableBrickFactory.PathType.HORIZONTAL, MovableBrickFactory.MovableBrickType.UNBREAKABLE)
    );

    private final String key;
    private final BrickFactory factory;

    BrickType(String key, BrickFactory factory) {
        this.key = key;
        this.factory = factory;
    }

    public Brick create(double x, double y, Map<String, Double> config) {
        return factory.create(x, y, config);
    }

    private static final Map<String, BrickType> KEY_TO_TYPE_MAP =
            Stream.of(values()).collect(Collectors.toMap(e -> e.key, e -> e));

    public static BrickType fromString(String key) {
        return KEY_TO_TYPE_MAP.get(key);
    }
}