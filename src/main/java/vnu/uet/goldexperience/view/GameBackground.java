package vnu.uet.goldexperience.view;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.*;
import javafx.scene.effect.BlendMode;
import java.util.ArrayList;
import java.util.List;

public class GameBackground {

    public enum ChapterTheme {
        ORIGINAL,
        CHAPTER_1_RUST,
        CHAPTER_2_NEON,
        CHAPTER_3_VERDANT,
        CHAPTER_4_CATHEDRAL,
        CHAPTER_5_NEXUS
    }

    private class FallingShape {
        double x, y, size, speed, rotation, rotationSpeed;
        int type; // 0 = square, 1 = triangle

        FallingShape() {
            reset();
            this.y = Math.random() * height;
        }

        void reset() {
            this.x = Math.random() * width;
            this.y = -Math.random() * height - size;
            this.size = 10 + Math.random() * 20;
            this.speed = 1.0 + Math.random() * 2.0;
            this.type = (Math.random() < 0.5) ? 0 : 1;
            this.rotation = Math.random() * 360;
            this.rotationSpeed = (Math.random() - 0.5) * 2.0;
        }

        void update() {
            this.y += this.speed;
            this.rotation += this.rotationSpeed;

            if (this.y > height + this.size) {
                reset();
            }
        }

        void draw(GraphicsContext gc) {
            gc.save();
            gc.setStroke(gridColor.deriveColor(0, 1, 1, 0.4));
            gc.setLineWidth(2);

            gc.translate(x, y);
            gc.rotate(rotation);

            if (type == 0) {
                gc.strokeRect(-size / 2, -size / 2, size, size);
            } else {
                double[] xPoints = {0, size / 2, -size / 2};
                double[] yPoints = {-size / 2, size / 2, size / 2};
                gc.strokePolygon(xPoints, yPoints, 3);
            }

            gc.restore();
        }
    }

    private Canvas backgroundCanvas;
    private GraphicsContext gc;
    private AnimationTimer animationTimer;
    private final double width;
    private final double height;

    private double gridOffset = 0;
    private double scanlineOffset = 0;
    private double glitchTimer = 0;
    private boolean glitchActive = false;
    private double borderPulse = 0;

    private final Color NEON_PINK = Color.rgb(255, 0, 128);
    private final Color NEON_CYAN = Color.rgb(0, 255, 255);
    private final Color NEON_PURPLE = Color.rgb(138, 43, 226);
    private final Color DARK_BG_ORIGINAL = Color.rgb(10, 5, 20);

    private final Color DARK_BG_CH1 = Color.rgb(30, 25, 20);
    private final Color NEON_ORANGE = Color.rgb(255, 140, 0);
    private final Color MEDIUM_GRAY = Color.rgb(150, 150, 150);

    private final Color DARK_BG_CH3 = Color.rgb(10, 20, 10);
    private final Color NEON_GREEN = Color.rgb(0, 255, 100);
    private final Color EARTHY_YELLOW = Color.rgb(200, 180, 50);

    private final Color DARK_BG_CH4 = Color.rgb(20, 10, 30);
    private final Color GOLD = Color.rgb(255, 215, 0);

    private final Color DARK_BG_CH5 = Color.rgb(15, 15, 25);
    private final Color PURE_WHITE = Color.rgb(255, 255, 255);


    private ChapterTheme currentTheme;

    private Pane rootPane;

    private Color currentDarkBG;
    private Color borderColor;
    private Color cornerColor;
    private Color gridColor;

    private final double gridSpeed = 2.0;
    private final int glitchCheckFrequency = 120;
    private final double glitchChance = 0.3;
    private final int glitchDuration = 5;
    private final int glitchLines = 5;
    private final double glitchOffset = 20;

    private List<FallingShape> fallingShapes;
    private final int NUM_SHAPES = 15;


    public GameBackground(double width, double height, Pane rootPane) {
        this.width = width;
        this.height = height;
        this.rootPane = rootPane;

        backgroundCanvas = new Canvas(width, height);
        gc = backgroundCanvas.getGraphicsContext2D();

        fallingShapes = new ArrayList<>();
        for (int i = 0; i < NUM_SHAPES; i++) {
            fallingShapes.add(new FallingShape());
        }

        setupAnimation();
    }

    public Canvas getCanvas() {
        return backgroundCanvas;
    }

    private String getRootPaneStyleForTheme(ChapterTheme theme) {
        switch (theme) {
            case CHAPTER_1_RUST:
                return "-fx-background-color: linear-gradient(to bottom, " +
                        "#1e1914 0%, #4a2a0a 25%, #2b2018 50%, #4a2a0a 75%, #1e1914 100%);";
            case CHAPTER_2_NEON:
                return "-fx-background-color: linear-gradient(to bottom, " +
                        "#0a0514 0%, #2a0a38 25%, #1f0530 50%, #2a0a38 75%, #0a0514 100%);";
            case CHAPTER_3_VERDANT:
                return "-fx-background-color: linear-gradient(to bottom, " +
                        "#0a1a0a 0%, #1a2a1a 25%, #051505 50%, #1a2a1a 75%, #0a1a0a 100%);";
            case CHAPTER_4_CATHEDRAL:
                return "-fx-background-color: linear-gradient(to bottom, " +
                        "#1a0a1a 0%, #2a1a2a 25%, #150515 50%, #2a1a2a 75%, #1a0a1a 100%);";
            case CHAPTER_5_NEXUS:
                return "-fx-background-color: linear-gradient(to bottom, " +
                        "#101020 0%, #202030 25%, #101020 50%, #202030 75%, #101020 100%);";
            case ORIGINAL:
            default:
                return "-fx-background-color: linear-gradient(to bottom, " +
                        "#0a0514 0%, #1a0a28 25%, #0f0520 50%, #1a0a28 75%, #0a0514 100%);";
        }
    }

    public void setTheme(ChapterTheme theme) {
        this.currentTheme = theme;

        if (rootPane != null) {
            rootPane.setStyle(getRootPaneStyleForTheme(theme));
        }

        switch (theme) {
            case CHAPTER_1_RUST:
                currentDarkBG = DARK_BG_CH1;
                borderColor = NEON_ORANGE;
                cornerColor = MEDIUM_GRAY;
                gridColor = NEON_ORANGE;
                break;
            case CHAPTER_2_NEON:
                currentDarkBG = DARK_BG_ORIGINAL;
                borderColor = NEON_CYAN;
                cornerColor = NEON_PINK;
                gridColor = NEON_CYAN;
                break;
            case CHAPTER_3_VERDANT:
                currentDarkBG = DARK_BG_CH3;
                borderColor = NEON_GREEN;
                cornerColor = EARTHY_YELLOW;
                gridColor = NEON_GREEN;
                break;
            case CHAPTER_4_CATHEDRAL:
                currentDarkBG = DARK_BG_CH4;
                borderColor = GOLD;
                cornerColor = GOLD;
                gridColor = GOLD;
                break;
            case CHAPTER_5_NEXUS:
                currentDarkBG = DARK_BG_CH5;
                borderColor = PURE_WHITE;
                cornerColor = PURE_WHITE;
                gridColor = PURE_WHITE;
                break;
            case ORIGINAL:
            default:
                currentDarkBG = DARK_BG_ORIGINAL;
                borderColor = NEON_PINK;
                cornerColor = NEON_CYAN;
                gridColor = NEON_CYAN;
                break;
        }
    }

    private void setupAnimation() {
        animationTimer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 33_333_333) {
                    render();
                    lastUpdate = now;
                }
            }
        };
    }

    private void render() {
        gc.setFill(currentDarkBG);
        gc.fillRect(0, 0, width, height);

        drawPerspectiveGrid();

        updateFallingShapes();
        drawFallingShapes();

        updateGlitch();
        if (glitchActive) {
            drawGlitchEffect();
        }

        drawNeonBorder();

        drawCornerAccents();
    }

    private void updateFallingShapes() {
        for (FallingShape shape : fallingShapes) {
            shape.update();
        }
    }

    private void drawFallingShapes() {
        for (FallingShape shape : fallingShapes) {
            shape.draw(gc);
        }
    }

    private void drawPerspectiveGrid() {
        gridOffset += gridSpeed;
        if (gridOffset > 40) gridOffset = 0;

        for (int i = 0; i < 15; i++) {
            double y = (i * 40) + gridOffset;
            if (y > height) continue;

            double alpha = 0.3 - (i * 0.02);
            gc.setStroke(gridColor.deriveColor(0, 1, 1, Math.max(0, alpha)));
            gc.strokeLine(10, y, width - 10, y);

            gc.setStroke(gridColor.deriveColor(0, 1, 1, Math.max(0.05, alpha * 0.3)));
            gc.setLineWidth(3);
            gc.strokeLine(10, y, width - 10, y);
            gc.setLineWidth(1.5);
        }
    }

    private void updateGlitch() {
        glitchTimer++;
        if (glitchTimer > glitchCheckFrequency) {
            if (Math.random() < glitchChance) {
                glitchActive = true;
                glitchTimer = 0;
            }
        }

        if (glitchActive && glitchTimer > glitchDuration) {
            glitchActive = false;
        }
    }

    private void drawGlitchEffect() {
        gc.save();

        for (int i = 0; i < glitchLines; i++) {
            double y = Math.random() * height;
            double h = 5 + Math.random() * 20;
            double offset = (Math.random() - 0.5) * glitchOffset;

            gc.setFill(Color.rgb(255, 0, 0, 0.3));
            gc.fillRect(offset, y, width, h);

            gc.setFill(Color.rgb(0, 255, 255, 0.3));
            gc.fillRect(-offset, y + 2, width, h);
        }

        gc.restore();
    }

    private void drawNeonBorder() {
        borderPulse += 0.05;
        double pulse = 0.5 + Math.sin(borderPulse) * 0.3;

        gc.setLineWidth(4);

        gc.setStroke(borderColor.deriveColor(0, 1, 1, pulse * 0.4));
        gc.setLineWidth(8);
        gc.strokeRect(4, 4, width - 8, height - 8);

        gc.setStroke(borderColor.deriveColor(0, 1, 1, pulse * 0.9));
        gc.setLineWidth(3);
        gc.strokeRect(4, 4, width - 8, height - 8);

        gc.setStroke(borderColor.deriveColor(1, 1, 1.5, pulse));
        gc.setLineWidth(1);
        gc.strokeRect(6, 6, width - 12, height - 12);
    }

    private void drawCornerAccents() {
        double cornerSize = 30;
        double cornerThick = 3;

        gc.setStroke(cornerColor);
        gc.setLineWidth(cornerThick);

        gc.strokeLine(10, 10, 10 + cornerSize, 10);
        gc.strokeLine(10, 10, 10, 10 + cornerSize);

        gc.strokeLine(width - 10, 10, width - 10 - cornerSize, 10);
        gc.strokeLine(width - 10, 10, width - 10, 10 + cornerSize);

        gc.strokeLine(10, height - 10, 10 + cornerSize, height - 10);
        gc.strokeLine(10, height - 10, 10, height - 10 - cornerSize);

        gc.strokeLine(width - 10, height - 10, width - 10 - cornerSize, height - 10);
        gc.strokeLine(width - 10, height - 10, width - 10, height - 10 - cornerSize);
    }

    public void start() {
        if (animationTimer != null) {
            animationTimer.start();
        }
    }

    public void stop() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }
}

