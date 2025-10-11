package vnu.uet.goldexperience.core;

import vnu.uet.goldexperience.model.Ball;
import vnu.uet.goldexperience.model.Brick;
import vnu.uet.goldexperience.model.Bullet;
import vnu.uet.goldexperience.model.Paddle;

import java.util.List;

public record GameContext(List<Ball> balls, Paddle paddle, List<Bullet> bullets, List<Brick> bricks) {

}
