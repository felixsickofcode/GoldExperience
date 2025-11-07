package vnu.uet.goldexperience.manager;

import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

public class AssetsManager {
    public static List<Image> bricks = new ArrayList<>();
    public static List<Image> paddles = new ArrayList<>();
    public static List<Image> balls = new ArrayList<>();
    public static void loadAssets() {
        loadBricks();
        loadPaddles();
        loadBalls();
    }
    private static void loadBricks() {
        try {
            bricks.add(new Image(AssetsManager.class.getResource("/images/brick_15.png").toExternalForm()));
            bricks.add(new Image(AssetsManager.class.getResource("/images/brick_1.png").toExternalForm()));
            bricks.add(new Image(AssetsManager.class.getResource("/images/unbreakable1.png").toExternalForm()));
            bricks.add(new Image(AssetsManager.class.getResource("/images/unbreakable2.png").toExternalForm()));
            bricks.add(new Image(AssetsManager.class.getResource("/images/brick_3.png").toExternalForm()));
            bricks.add(new Image(AssetsManager.class.getResource("/images/brick3.png").toExternalForm()));
        } catch (Exception e) {
            System.err.println("Không thể tải brick: " + e.getMessage());
        }
    }

    private static void loadPaddles() {
        try {
            paddles.add(new Image(AssetsManager.class.getResource("/images/paddle2_1.png").toExternalForm()));
            paddles.add(new Image(AssetsManager.class.getResource("/images/paddle3_1.png").toExternalForm()));
            paddles.add(new Image(AssetsManager.class.getResource("/images/paddle4_1.png").toExternalForm()));
            paddles.add(new Image(AssetsManager.class.getResource("/images/paddle5_1.png").toExternalForm()));
            paddles.add(new Image(AssetsManager.class.getResource("/images/paddle6_1.png").toExternalForm()));
            paddles.add(new Image(AssetsManager.class.getResource("/images/paddle2_2.png").toExternalForm()));
            paddles.add(new Image(AssetsManager.class.getResource("/images/paddle3_2.png").toExternalForm()));
            paddles.add(new Image(AssetsManager.class.getResource("/images/paddle4_2.png").toExternalForm()));
            paddles.add(new Image(AssetsManager.class.getResource("/images/paddle5_2.png").toExternalForm()));
            paddles.add(new Image(AssetsManager.class.getResource("/images/paddle6_2.png").toExternalForm()));
            paddles.add(new Image(AssetsManager.class.getResource("/images/paddle2_3.png").toExternalForm()));
            paddles.add(new Image(AssetsManager.class.getResource("/images/paddle3_3.png").toExternalForm()));
            paddles.add(new Image(AssetsManager.class.getResource("/images/paddle4_3.png").toExternalForm()));
            paddles.add(new Image(AssetsManager.class.getResource("/images/paddle5_3.png").toExternalForm()));
            paddles.add(new Image(AssetsManager.class.getResource("/images/paddle6_3.png").toExternalForm()));
            paddles.add(new Image(AssetsManager.class.getResource("/images/paddle2_4.png").toExternalForm()));
            paddles.add(new Image(AssetsManager.class.getResource("/images/paddle3_4.png").toExternalForm()));
            paddles.add(new Image(AssetsManager.class.getResource("/images/paddle4_4.png").toExternalForm()));
            paddles.add(new Image(AssetsManager.class.getResource("/images/paddle5_4.png").toExternalForm()));
            paddles.add(new Image(AssetsManager.class.getResource("/images/paddle6_4.png").toExternalForm()));
            paddles.add(new Image(AssetsManager.class.getResource("/images/paddle2_5.png").toExternalForm()));
            paddles.add(new Image(AssetsManager.class.getResource("/images/paddle3_5.png").toExternalForm()));
            paddles.add(new Image(AssetsManager.class.getResource("/images/paddle4_5.png").toExternalForm()));
            paddles.add(new Image(AssetsManager.class.getResource("/images/paddle5_5.png").toExternalForm()));
            paddles.add(new Image(AssetsManager.class.getResource("/images/paddle6_5.png").toExternalForm()));
        } catch (Exception e) {
            System.err.println("Không thể tải paddle: " + e.getMessage());
        }
    }

    private static void loadBalls() {
        try {
            balls.add(new Image(AssetsManager.class.getResource("/images/ball.png").toExternalForm()));
        } catch (Exception e) {
            System.err.println("Không thể tải ball: " + e.getMessage());
        }
    }
}
