package vnu.uet.goldexperience.core;

public class Constants {
    public static final int WINDOW_WIDTH = 1280;
    public static final int WINDOW_HEIGHT = 720;
    public static final int GAMEPLAYZONE_WIDTH = 585;
    public static final int GAMEPLAYZONE_HEIGHT = 720;
    public static final int GAME_START = (1280 - 720) / 2;
    public static final int GAME_OFFSET = (WINDOW_WIDTH - GAMEPLAYZONE_WIDTH) / 2;

    public static final double TINY_PADDLE_WIDTH = 51;
    public static final double SMALL_PADDLE_WIDTH = 72;
    public static final double MEDIUM_PADDLE_WIDTH = 95;
    public static final double LARGE_PADDLE_WIDTH = 120;
    public static final double BIG_PADDLE_WIDTH = 144;
    public static final double PADDLE_HEIGHT = 14;
    public static final double SMALL_BALL_SIZE = 6;
    public static final double NORMAL_BALL_SIZE = 8;
    public static final double NORMAL_BRICK_WIDTH = 48;
    public static final double NORMAL_BRICK_HEIGHT = 24;
    public static final int MIN_PADDLE_SIZE = 0;
    public static final int MAX_PADDLE_SIZE = 4;

    //regenerateBrick
    public static final double REGENERATION_TIME = 10.0;
    public static final int REGENERATION_THRESHOLD = 3;
    public static final int MAX_HIT_POINTS = 4;

    public static final double SPAWN_THRESHOLD = 50.0;
    public static final double BALL_SPEED = 350;
    public static final double BALL_ROLLING_SPEED = 5.0;
    public static final double MAX_SHOOT_ANGLE = 60;
    public static final double PADDLE_SPEED = 600;
    public static final double MOUSE_LERP_SPEED = 0.4;
    public static final double BALL_MAX_SPEED = 600;

    public static final double BALL_INIT_POSITION = 590;
    public static final double PADDLE_INIT_POSITION = 420;

    public static final double BULLET_SPEED = 450;
    public static final double BULLET_WIDTH = 8;
    public static final double BULLET_HEIGHT = 14;

    // Power up constants
    public static final double DROP_CHANCE = 0.25;
    public static final double POWER_UP_ITEM_WIDTH = 48;
    public static final double POWER_UP_ITEM_HEIGHT = 33;
    public static final double POWER_UP_DROP_SPEED = 100;
    public static final long BULLETS_DURATION = 5_000;
    public static final double BULLET_COOLDOWN_MS = 400;
    public static final long FAST_DURATION = 10_000;
    public static final long SLOW_DURATION = 10_000;
    public static final double BALL_SPEED_AMPLIFIER = 1.5;
}