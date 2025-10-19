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
            bricks.add(new Image(AssetsManager.class.getResource("/images/brick_1_break.png").toExternalForm()));
            bricks.add(new Image(AssetsManager.class.getResource("/images/brick_1_broken.png").toExternalForm()));
            bricks.add(new Image(AssetsManager.class.getResource("/images/unbreakable1.png").toExternalForm()));
            bricks.add(new Image(AssetsManager.class.getResource("/images/unbreakable2.png").toExternalForm()));
        } catch (Exception e) {
            System.err.println("Không thể tải brick: " + e.getMessage());
        }
    }

    private static void loadPaddles() {
        try {
            paddles.add(new Image(AssetsManager.class.getResource("/images/paddle2.png").toExternalForm()));
            paddles.add(new Image(AssetsManager.class.getResource("/images/paddle3.png").toExternalForm()));
            paddles.add(new Image(AssetsManager.class.getResource("/images/paddle4.png").toExternalForm()));
            paddles.add(new Image(AssetsManager.class.getResource("/images/paddle5.png").toExternalForm()));
            paddles.add(new Image(AssetsManager.class.getResource("/images/paddle6.png").toExternalForm()));
        } catch (Exception e) {
            System.err.println("Không thể tải paddle: " + e.getMessage());
        }
    }

    private static void loadBalls() {
        try {
            balls.add(new Image(AssetsManager.class.getResource("/images/smallball.png").toExternalForm()));
            balls.add(new Image(AssetsManager.class.getResource("/images/normalball.png").toExternalForm()));
            balls.add(new Image(AssetsManager.class.getResource("/images/bigball.png").toExternalForm()));
        } catch (Exception e) {
            System.err.println("Không thể tải ball: " + e.getMessage());
        }
    }
}
