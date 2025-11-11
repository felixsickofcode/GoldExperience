package vnu.uet.goldexperience.manager;

import javafx.scene.image.Image;

import java.net.URL;

import java.util.ArrayList;
import java.util.List;

public class AssetsManager {
    public static List<Image> bricks = new ArrayList<>();
    public static List<Image> paddles = new ArrayList<>();
    public static List<Image> balls = new ArrayList<>();
    public static List<Image> bullets = new ArrayList<>();

    public static void loadAssets() {
        loadBricks();
        loadPaddles();
        loadBalls();
        loadBullets();
    }

    private static void loadBricks() {
        try {
            List<String> imageDirs = List.of(
                    "/images/brick_15.png",
                    "/images/brick_1.png",
                    "/images/unbreakable1.png",
                    "/images/unbreakable2.png",
                    "/images/brick_3.png",
                    "/images/brick3.png"
            );

            for (String dir : imageDirs) {
                URL imgURL = AssetsManager.class.getResource(dir);

                if (imgURL == null) {
                    throw new IllegalArgumentException(
                            String.format("Không thể tải brick tại %s", dir)
                    );
                }

                bricks.add(new Image(imgURL.toExternalForm()));
            }
        } catch (Exception e) {
            System.err.println("Lỗi tải brick: " + e.getMessage());
        }
    }

    private static void loadPaddles() {
        try {
            for (int size = 2; size <= 6; size++) {
                for (int variant = 1; variant <= 5; variant++) {
                    String filename = "/images/paddle" + size + "_" + variant + ".png";
                    try {
                        URL imgURL = AssetsManager.class.getResource(filename);

                        if (imgURL == null) {
                            throw new IllegalArgumentException(
                                    String.format("Không thể tải paddle tại %s", filename)
                            );
                        }

                        paddles.add(new Image(imgURL.toExternalForm()));
                    } catch (Exception e) {
                        paddles.add(null);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi tải paddle: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void loadBalls() {
        try {
            List<String> imageDirs = List.of(
                    "/images/ball.png"
            );

            for (String dir : imageDirs) {
                URL imgURL = AssetsManager.class.getResource(dir);

                if (imgURL == null) {
                    throw new IllegalArgumentException(
                            String.format("Không thể tải ball tại %s", dir)
                    );
                }

                balls.add(new Image(imgURL.toExternalForm()));
            }
        } catch (Exception e) {
            System.err.println("Lỗi tải ball: " + e.getMessage());
        }
    }

    private static void loadBullets() {
        try {
            List<String> imageDirs = List.of(
                    "/images/blue_bullet_0.png"
            );

            for (String dir : imageDirs) {
                URL imgURL = AssetsManager.class.getResource(dir);

                if (imgURL == null) {
                    throw new IllegalArgumentException(
                            String.format("Không thể tải bullet tại %s", dir)
                    );
                }

                bullets.add(new Image(imgURL.toExternalForm()));
            }
        } catch (Exception e) {
            System.err.println("Lỗi tải bullet: " + e.getMessage());
        }
    }
}