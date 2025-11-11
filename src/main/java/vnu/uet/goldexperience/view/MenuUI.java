package vnu.uet.goldexperience.view;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.shape.SVGPath;
import vnu.uet.goldexperience.core.ChapterTheme;

import java.util.function.Consumer;

public class MenuUI {

    private StackPane rootPane;
    private Canvas backgroundCanvas;
    private GraphicsContext gc;
    private AnimationTimer animationTimer;

    private final double WIDTH = 1280;
    private final double HEIGHT = 720;

    private double gridOffset = 0;
    private double borderPulse = 0;

    private final Color DARK_BG = ChapterTheme.DARK_BG_ORIGINAL;
    private final Color NEON_PINK = ChapterTheme.NEON_PINK;
    private final Color NEON_CYAN = ChapterTheme.NEON_CYAN;
    private final Color NEON_GREEN = Color.rgb(0, 255, 136);

    private Consumer<String> onMenuAction;

    public MenuUI() {
        createUI();
    }

    public void setOnMenuAction(Consumer<String> callback) {
        this.onMenuAction = callback;
    }

    public StackPane getRootPane() {
        return rootPane;
    }

    private void createUI() {
        rootPane = new StackPane();
        rootPane.setPrefSize(WIDTH, HEIGHT);
        rootPane.getStyleClass().add("menu-root");

        backgroundCanvas = new Canvas(WIDTH, HEIGHT);
        gc = backgroundCanvas.getGraphicsContext2D();
        rootPane.getChildren().add(backgroundCanvas);

        Label titleLabel = new Label("A.R.K.A");
        titleLabel.getStyleClass().add("menu-title");

        Label subtitleLabel = new Label("SELECT YOUR ADVENTURE");
        subtitleLabel.getStyleClass().add("menu-subtitle");

        VBox menuBox = new VBox(20);
        menuBox.setMaxWidth(400);
        menuBox.setMaxHeight(300);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.getStyleClass().add("menu-box");

        Button storyModeBtn = createMenuButton("STORY MODE");
        Button twoPlayerBtn = createMenuButton("ENLESS MODE");
        Button shopBtn = createMenuButton("SHOP");

        storyModeBtn.setOnAction(e -> { if (onMenuAction != null) onMenuAction.accept("story"); });
        twoPlayerBtn.setOnAction(e -> { if (onMenuAction != null) onMenuAction.accept("2player"); });
        shopBtn.setOnAction(e -> { if (onMenuAction != null) onMenuAction.accept("shop"); });

        menuBox.getChildren().addAll(storyModeBtn, twoPlayerBtn, shopBtn);

        // Create settings icon button at top-right
        Button settingsIconBtn = createSettingsIconButton();
        settingsIconBtn.setOnAction(e -> { if (onMenuAction != null) onMenuAction.accept("settings"); });

        // Position settings icon at top-right
        StackPane.setAlignment(settingsIconBtn, Pos.TOP_RIGHT);
        StackPane.setMargin(settingsIconBtn, new Insets(40, 40, 0, 0));

        rootPane.getChildren().addAll(titleLabel, menuBox, settingsIconBtn);

        setupAnimation();
    }

    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("menu-button");
        return button;
    }

    private Button createSettingsIconButton() {
        Button button = new Button();
        button.getStyleClass().add("settings-icon-button");

        // Create gear icon using SVG path
        SVGPath gearIcon = new SVGPath();
        gearIcon.setContent("M 12 2 C 11.172 2 10.5 2.672 10.5 3.5 L 10.5 4.1875 C 9.5184 4.4611 8.6128 4.9193 7.8125 5.5 L 7.28125 5.09375 C 6.63925 4.58575 5.70175 4.68125 5.1875 5.3125 L 4.1875 6.6875 C 3.6735 7.3195 3.76875 8.25675 4.40625 8.78125 L 4.9375 9.1875 C 4.6515 9.9875 4.5 10.847 4.5 11.75 C 4.5 12.653 4.6515 13.5125 4.9375 14.3125 L 4.40625 14.71875 C 3.76925 15.24375 3.67375 16.1813 4.1875 16.8125 L 5.1875 18.1875 C 5.70175 18.81875 6.63925 18.91425 7.28125 18.40625 L 7.8125 18 C 8.6128 18.5807 9.5184 19.0389 10.5 19.3125 L 10.5 19.5 C 10.5 20.328 11.172 21 12 21 L 13.5 21 C 14.328 21 15 20.328 15 19.5 L 15 19.3125 C 15.9816 19.0389 16.8872 18.5807 17.6875 18 L 18.21875 18.40625 C 18.86075 18.91425 19.79825 18.81875 20.3125 18.1875 L 21.3125 16.8125 C 21.8265 16.1805 21.73125 15.24325 21.09375 14.71875 L 20.5625 14.3125 C 20.8485 13.5125 21 12.653 21 11.75 C 21 10.847 20.8485 9.9875 20.5625 9.1875 L 21.09375 8.78125 C 21.73075 8.25625 21.82625 7.3187 21.3125 6.6875 L 20.3125 5.3125 C 19.79825 4.68125 18.86075 4.58575 18.21875 5.09375 L 17.6875 5.5 C 16.8872 4.9193 15.9816 4.4611 15 4.1875 L 15 3.5 C 15 2.672 14.328 2 13.5 2 L 12 2 z M 12.75 8.5 C 14.544 8.5 16 9.956 16 11.75 C 16 13.544 14.544 15 12.75 15 C 10.956 15 9.5 13.544 9.5 11.75 C 9.5 9.956 10.956 8.5 12.75 8.5 z");
        gearIcon.getStyleClass().add("settings-icon");
        gearIcon.setScaleX(1.8);
        gearIcon.setScaleY(1.8);

        StackPane iconContainer = new StackPane(gearIcon);
        iconContainer.setAlignment(Pos.CENTER);
        button.setGraphic(iconContainer);

        return button;
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
        // Clear background
        gc.setFill(DARK_BG);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        drawPerspectiveGrid();
        drawNeonBorder();
        drawCornerAccents();
        drawScanlines();
    }

    private void drawPerspectiveGrid() {
        gridOffset += 2.5;
        if (gridOffset > 50) gridOffset = 0;

        // Horizontal lines
        for (int i = 0; i < 15; i++) {
            double y = (i * 50) + gridOffset;
            if (y > HEIGHT) continue;

            double alpha = 0.25 - (i * 0.015);
            gc.setStroke(NEON_CYAN.deriveColor(0, 1, 1, Math.max(0, alpha)));
            gc.setLineWidth(1.5);
            gc.strokeLine(0, y, WIDTH, y);
        }

        // Vertical lines with perspective
        for (int i = 0; i < 25; i++) {
            double x = (i * 60);
            double alpha = 0.15;
            gc.setStroke(NEON_CYAN.deriveColor(0, 1, 1, alpha));
            gc.setLineWidth(1);
            gc.strokeLine(x, 0, x, HEIGHT);
        }
    }

    private void drawNeonBorder() {
        borderPulse += 0.05;
        double pulse = 0.6 + Math.sin(borderPulse) * 0.3;

        // Outer glow
        gc.setStroke(NEON_PINK.deriveColor(0, 1, 1, pulse * 0.3));
        gc.setLineWidth(10);
        gc.strokeRect(5, 5, WIDTH - 10, HEIGHT - 10);

        // Main border
        gc.setStroke(NEON_PINK.deriveColor(0, 1, 1, pulse * 0.8));
        gc.setLineWidth(3);
        gc.strokeRect(5, 5, WIDTH - 10, HEIGHT - 10);

        // Inner highlight
        gc.setStroke(NEON_PINK.deriveColor(1, 1, 1.5, pulse));
        gc.setLineWidth(1);
        gc.strokeRect(7, 7, WIDTH - 14, HEIGHT - 14);
    }

    private void drawCornerAccents() {
        double cornerSize = 40;
        double cornerThick = 4;
        int margin = 12;

        gc.setStroke(NEON_CYAN);
        gc.setLineWidth(cornerThick);

        // Top-left
        gc.strokeLine(margin, margin, margin + cornerSize, margin);
        gc.strokeLine(margin, margin, margin, margin + cornerSize);

        // Top-right
        gc.strokeLine(WIDTH - margin, margin, WIDTH - margin - cornerSize, margin);
        gc.strokeLine(WIDTH - margin, margin, WIDTH - margin, margin + cornerSize);

        // Bottom-left
        gc.strokeLine(margin, HEIGHT - margin, margin + cornerSize, HEIGHT - margin);
        gc.strokeLine(margin, HEIGHT - margin, margin, HEIGHT - margin - cornerSize);

        // Bottom-right
        gc.strokeLine(WIDTH - margin, HEIGHT - margin, WIDTH - margin - cornerSize, HEIGHT - margin);
        gc.strokeLine(WIDTH - margin, HEIGHT - margin, WIDTH - margin, HEIGHT - margin - cornerSize);

        // Add small dots at corners
        gc.setFill(NEON_GREEN);
        gc.fillOval(margin - 2, margin - 2, 4, 4);
        gc.fillOval(WIDTH - margin - 2, margin - 2, 4, 4);
        gc.fillOval(margin - 2, HEIGHT - margin - 2, 4, 4);
        gc.fillOval(WIDTH - margin - 2, HEIGHT - margin - 2, 4, 4);
    }

    private void drawScanlines() {
        for (int y = 0; y < HEIGHT; y += 4) {
            gc.setFill(Color.rgb(0, 0, 0, 0.1));
            gc.fillRect(0, y, WIDTH, 2);
        }
    }

    public void startAnimation() {
        if (animationTimer != null) {
            animationTimer.start();
        }
    }

    public void stopAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }
}