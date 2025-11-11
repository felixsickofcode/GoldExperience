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
            for (int size = 2; size <= 6; size++) {
                for (int variant = 1; variant <= 5; variant++) {
                    String filename = "/images/paddle" + size + "_" + variant + ".png";
                    try {
                        Image img = new Image(AssetsManager.class.getResource(filename).toExternalForm());
                        paddles.add(img);
                    } catch (Exception e) {
                        paddles.add(null);
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Không thể tải paddle: " + e.getMessage());
            e.printStackTrace();
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