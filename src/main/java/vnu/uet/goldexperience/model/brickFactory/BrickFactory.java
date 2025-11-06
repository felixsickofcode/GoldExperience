package vnu.uet.goldexperience.model.brickFactory;

import vnu.uet.goldexperience.model.brick.Brick;

import java.util.Map;

public interface BrickFactory {
    Brick create(double x, double y, Map<String, Double> config);
}