package vnu.uet.goldexperience.view;

import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import vnu.uet.goldexperience.core.ChapterTheme;

public class MenuUI {

    private Stage stage;
    private Pane rootPane;
    private Canvas backgroundCanvas;
    private GraphicsContext gc;
    private AnimationTimer animationTimer;

    // --- Callbacks để Controller gán hành động ---
    private Runnable onStoryMode;
    private Runnable on2PlayerMode;
    private Runnable onMoveToSetting;
    // ---

    private Label welcomeLabel; // Label để chào mừng người chơi

    private final double WIDTH = 1280;
    private final double HEIGHT = 720;

    private double gridOffset = 0;
    private double borderPulse = 0;

    private final Color DARK_BG = ChapterTheme.DARK_BG_ORIGINAL;
    private final Color NEON_PINK = ChapterTheme.NEON_PINK;
    private final Color NEON_CYAN = ChapterTheme.NEON_CYAN;

    public MenuUI(Stage stage) {
        this.stage = stage;
        createUI();
    }

    // --- Các hàm setter cho Callbacks ---
    public void setOnStoryMode(Runnable callback) {
        this.onStoryMode = callback;
    }

    public void setOn2PlayerMode(Runnable callback) {
        this.on2PlayerMode = callback;
    }

    public void setOnMoveToSetting(Runnable callback) {
        this.onMoveToSetting = callback;
    }
    // ---

    /**
     * Cập nhật tên người chơi trên UI
     * @param name Tên người chơi
     */
    public void setPlayerName(String name) {
        if (welcomeLabel != null) {
            welcomeLabel.setText("PLAYER: " + name.toUpperCase());
        }
    }

    public Pane getRootPane() {
        return rootPane;
    }

    private void createUI() {
        rootPane = new Pane();
        rootPane.setPrefSize(WIDTH, HEIGHT);
        rootPane.setStyle("-fx-background-color: linear-gradient(to bottom, " +
                "#0a0514 0%, #1a0a28 25%, #0f0520 50%, #1a0a28 75%, #0a0514 100%);");

        // Background animation canvas (Giữ nguyên)
        backgroundCanvas = new Canvas(WIDTH, HEIGHT);
        gc = backgroundCanvas.getGraphicsContext2D();
        rootPane.getChildren().add(backgroundCanvas);

        // --- Bố cục (Layout) ---

        // Tiêu đề chính
        Label titleLabel = new Label("GOLD EXPERIENCE");
        titleLabel.setFont(Font.font("Monospace", FontWeight.BOLD, 52));
        titleLabel.setTextFill(NEON_CYAN);
        titleLabel.setStyle("-fx-effect: dropshadow(gaussian, #00f5ff, 25, 0.8, 0, 0);");
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setPrefWidth(WIDTH);
        titleLabel.setLayoutY(HEIGHT / 2 - 220); // Đặt vị trí
        rootPane.getChildren().add(titleLabel);

        // Chào mừng người chơi
        welcomeLabel = new Label("PLAYER: ");
        welcomeLabel.setFont(Font.font("Monospace", FontWeight.NORMAL, 14));
        welcomeLabel.setTextFill(NEON_PINK);
        welcomeLabel.setAlignment(Pos.CENTER);
        welcomeLabel.setPrefWidth(WIDTH);
        welcomeLabel.setLayoutY(HEIGHT / 2 - 150); // Đặt vị trí
        rootPane.getChildren().add(welcomeLabel);


        // Hộp Menu
        VBox menuBox = new VBox(20); // Giữ khoảng cách
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setPrefWidth(400); // Khớp FXML
        menuBox.setLayoutX(440.0); // Khớp FXML
        menuBox.setLayoutY(300.0); // Khớp FXML
        menuBox.setStyle(
                "-fx-background-color: rgba(10, 5, 20, 0.85);" +
                        "-fx-background-radius: 15;" +
                        "-fx-border-color: #00f5ff;" + // Đổi viền sang màu Cyan
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 15;" +
                        "-fx-padding: 40;" +
                        "-fx-effect: dropshadow(gaussian, #00f5ff, 20, 0.6, 0, 0);" // Đổi hiệu ứng sang Cyan
        );

        // --- Các nút ---
        Button storyButton = createStyledButton("Story Mode");
        Button player2Button = createStyledButton("2 Player Mode");
        Button settingButton = createStyledButton("Setting");

        // Gán hành động cho các nút
        storyButton.setOnAction(e -> {
            if (onStoryMode != null) onStoryMode.run();
        });

        player2Button.setOnAction(e -> {
            if (on2PlayerMode != null) on2PlayerMode.run();
        });

        settingButton.setOnAction(e -> {
            if (onMoveToSetting != null) onMoveToSetting.run();
        });

        // Thêm tất cả vào hộp menu
        menuBox.getChildren().addAll(
                storyButton,
                player2Button,
                settingButton
        );
        // --- KẾT THÚC HỘP MENU ---

        rootPane.getChildren().add(menuBox);

        // Setup animation (Giữ nguyên)
        setupAnimation();
    }

    /**
     * Hàm trợ giúp để tạo nút theo style
     */
    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(350);
        button.setPrefHeight(50);
        button.setFont(Font.font("Monospace", FontWeight.BOLD, 18)); // Chữ to hơn

        String baseStyle = "-fx-background-color: linear-gradient(to right, #ff006e, #8b00ff);" +
                "-fx-text-fill: white;" +
                "-fx-border-color: #ff006e;" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 5;" +
                "-fx-background-radius: 5;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, #ff006e, 15, 0.6, 0, 0);";

        String hoverStyle = "-fx-background-color: linear-gradient(to right, #ff3385, #a020f0);" +
                "-fx-text-fill: white;" +
                "-fx-border-color: #00f5ff;" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 5;" +
                "-fx-background-radius: 5;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, #00f5ff, 20, 0.8, 0, 0);";

        button.setStyle(baseStyle);
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(baseStyle));
        return button;
    }

    // --- PHẦN ANIMATION (GIỮ NGUYÊN TỪ LOGINUI) ---

    private void setupAnimation() {
        animationTimer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 33_333_333) { // ~30 FPS
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

        // Vẽ lưới (Giữ nguyên)
        drawPerspectiveGrid();

        // Vẽ viền (Giữ nguyên)
        drawNeonBorder();

        // Vẽ góc (Giữ nguyên)
        drawCornerAccents();
    }

    private void drawPerspectiveGrid() {
        gridOffset += 2.0;
        if (gridOffset > 40) gridOffset = 0;

        for (int i = 0; i < 15; i++) {
            double y = (i * 40) + gridOffset;
            if (y > HEIGHT) continue;

            double alpha = 0.3 - (i * 0.02);
            gc.setStroke(NEON_CYAN.deriveColor(0, 1, 1, Math.max(0, alpha)));
            gc.strokeLine(10, y, WIDTH - 10, y);

            gc.setStroke(NEON_CYAN.deriveColor(0, 1, 1, Math.max(0.05, alpha * 0.3)));
            gc.setLineWidth(3);
            gc.strokeLine(10, y, WIDTH - 10, y);
            gc.setLineWidth(1.5);
        }
    }

    private void drawNeonBorder() {
        borderPulse += 0.05;
        double pulse = 0.5 + Math.sin(borderPulse) * 0.3;

        gc.setStroke(NEON_PINK.deriveColor(0, 1, 1, pulse * 0.4));
        gc.setLineWidth(8);
        gc.strokeRect(4, 4, WIDTH - 8, HEIGHT - 8);

        gc.setStroke(NEON_PINK.deriveColor(0, 1, 1, pulse * 0.9));
        gc.setLineWidth(3);
        gc.strokeRect(4, 4, WIDTH - 8, HEIGHT - 8);

        gc.setStroke(NEON_PINK.deriveColor(1, 1, 1.5, pulse));
        gc.setLineWidth(1);
        gc.strokeRect(6, 6, WIDTH - 12, HEIGHT - 12);
    }

    private void drawCornerAccents() {
        double cornerSize = 30;
        double cornerThick = 3;
        int margin = 10;

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
    }

    public void show() {
        if (stage != null) {
            stage.show();
        }
        startAnimation();
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