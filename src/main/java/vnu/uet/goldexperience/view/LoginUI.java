package vnu.uet.goldexperience.view;

import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import vnu.uet.goldexperience.core.ChapterTheme;
import vnu.uet.goldexperience.database.PlayerDatabase;

import java.util.function.Consumer;

public class LoginUI {

    private Stage stage;
    private Pane rootPane;
    private Canvas backgroundCanvas;
    private GraphicsContext gc;
    private AnimationTimer animationTimer;
    private Consumer<String> onLoginSuccess;

    private final double WIDTH = 1280;
    private final double HEIGHT = 720;

    private double gridOffset = 0;
    private double borderPulse = 0;
    private double glitchTimer = 0;
    private boolean glitchActive = false;

    private final Color DARK_BG = ChapterTheme.DARK_BG_ORIGINAL;
    private final Color NEON_PINK = ChapterTheme.NEON_PINK;
    private final Color NEON_CYAN = ChapterTheme.NEON_CYAN;

    public LoginUI(Stage stage) {
        this.stage = stage;
        createUI();
    }

    public void setOnLoginSuccess(Consumer<String> callback) {
        this.onLoginSuccess = callback;
    }

    public Pane getRootPane() {
        return rootPane;
    }

    private void createUI() {
        rootPane = new Pane();
        rootPane.setPrefSize(WIDTH, HEIGHT);
        rootPane.setStyle("-fx-background-color: linear-gradient(to bottom, " +
                "#0a0514 0%, #1a0a28 25%, #0f0520 50%, #1a0a28 75%, #0a0514 100%);");

        // Background animation canvas
        backgroundCanvas = new Canvas(WIDTH, HEIGHT);
        gc = backgroundCanvas.getGraphicsContext2D();
        rootPane.getChildren().add(backgroundCanvas);

        // Login form container
        VBox loginBox = new VBox(25);
        loginBox.setAlignment(Pos.CENTER);
        loginBox.setPrefWidth(400);
        loginBox.setLayoutX((WIDTH - 400) / 2);
        loginBox.setLayoutY(HEIGHT / 2 - 150);
        loginBox.setStyle(
                "-fx-background-color: rgba(10, 5, 20, 0.85);" +
                        "-fx-background-radius: 15;" +
                        "-fx-border-color: #ff006e;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 15;" +
                        "-fx-padding: 40;" +
                        "-fx-effect: dropshadow(gaussian, #ff006e, 20, 0.6, 0, 0);"
        );

        // Subtitle
        Label subtitleLabel = new Label("Enter Player Name");
        subtitleLabel.setFont(Font.font("Monospace", FontWeight.NORMAL, 14));
        subtitleLabel.setTextFill(NEON_CYAN);

        // Username input
        TextField usernameField = new TextField();
        usernameField.setPromptText("Player Name");
        usernameField.setPrefHeight(50);
        usernameField.setStyle(
                "-fx-background-color: rgba(20, 10, 40, 0.8);" +
                        "-fx-text-fill: #00f5ff;" +
                        "-fx-prompt-text-fill: rgba(0, 245, 255, 0.5);" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-family: 'Monospace';" +
                        "-fx-border-color: #00f5ff;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 5;" +
                        "-fx-background-radius: 5;" +
                        "-fx-padding: 10;"
        );

        // Focus effect for input
        usernameField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                usernameField.setStyle(
                        "-fx-background-color: rgba(20, 10, 40, 0.9);" +
                                "-fx-text-fill: #00f5ff;" +
                                "-fx-prompt-text-fill: rgba(0, 245, 255, 0.5);" +
                                "-fx-font-size: 16px;" +
                                "-fx-font-family: 'Monospace';" +
                                "-fx-border-color: #ff006e;" +
                                "-fx-border-width: 2;" +
                                "-fx-border-radius: 5;" +
                                "-fx-background-radius: 5;" +
                                "-fx-padding: 10;" +
                                "-fx-effect: dropshadow(gaussian, #ff006e, 15, 0.7, 0, 0);"
                );
            } else {
                usernameField.setStyle(
                        "-fx-background-color: rgba(20, 10, 40, 0.8);" +
                                "-fx-text-fill: #00f5ff;" +
                                "-fx-prompt-text-fill: rgba(0, 245, 255, 0.5);" +
                                "-fx-font-size: 16px;" +
                                "-fx-font-family: 'Monospace';" +
                                "-fx-border-color: #00f5ff;" +
                                "-fx-border-width: 2;" +
                                "-fx-border-radius: 5;" +
                                "-fx-background-radius: 5;" +
                                "-fx-padding: 10;"
                );
            }
        });

        // Error label
        Label errorLabel = new Label("");
        errorLabel.setFont(Font.font("Monospace", 12));
        errorLabel.setTextFill(Color.rgb(255, 100, 100));
        errorLabel.setVisible(false);

        // Login button
        Button loginButton = new Button("START GAME");
        loginButton.setPrefWidth(300);
        loginButton.setPrefHeight(50);
        loginButton.setFont(Font.font("Monospace", FontWeight.BOLD, 16));
        loginButton.setStyle(
                "-fx-background-color: linear-gradient(to right, #ff006e, #8b00ff);" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: #ff006e;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 5;" +
                        "-fx-background-radius: 5;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, #ff006e, 15, 0.6, 0, 0);"
        );

        // Button hover effect
        loginButton.setOnMouseEntered(e -> {
            loginButton.setStyle(
                    "-fx-background-color: linear-gradient(to right, #ff3385, #a020f0);" +
                            "-fx-text-fill: white;" +
                            "-fx-border-color: #00f5ff;" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 5;" +
                            "-fx-background-radius: 5;" +
                            "-fx-cursor: hand;" +
                            "-fx-effect: dropshadow(gaussian, #00f5ff, 20, 0.8, 0, 0);"
            );
        });

        loginButton.setOnMouseExited(e -> {
            loginButton.setStyle(
                    "-fx-background-color: linear-gradient(to right, #ff006e, #8b00ff);" +
                            "-fx-text-fill: white;" +
                            "-fx-border-color: #ff006e;" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 5;" +
                            "-fx-background-radius: 5;" +
                            "-fx-cursor: hand;" +
                            "-fx-effect: dropshadow(gaussian, #ff006e, 15, 0.6, 0, 0);"
            );
        });

        // Login action
        loginButton.setOnAction(e -> {
            String playerName = usernameField.getText().trim();

            if (playerName.isEmpty()) {
                errorLabel.setText("⚠ Player name cannot be empty!");
                errorLabel.setVisible(true);
                return;
            }

            if (playerName.length() < 3) {
                errorLabel.setText("⚠ Name must be at least 3 characters!");
                errorLabel.setVisible(true);
                return;
            }

            // Save to database
            boolean success = PlayerDatabase.getInstance().addOrUpdatePlayer(playerName, 0);

            if (success) {
                System.out.println("Player logged in: " + playerName);

                // Call callback if set
                if (onLoginSuccess != null) {
                    onLoginSuccess.accept(playerName);
                }
            } else {
                errorLabel.setText("⚠ Database error. Please try again!");
                errorLabel.setVisible(true);
            }
        });

        // Enter key support
        usernameField.setOnAction(e -> loginButton.fire());

        // Add all elements to login box
        loginBox.getChildren().addAll(
                subtitleLabel,
                usernameField,
                errorLabel,
                loginButton
        );

        rootPane.getChildren().add(loginBox);

        // Setup animation
        setupAnimation();
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