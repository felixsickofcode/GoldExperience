package vnu.uet.goldexperience.view;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.input.MouseEvent;

public class MenuEffect extends StackPane {

    private Canvas canvas;
    private GraphicsContext gc;
    private Button button;
    private AnimationTimer animationTimer;

    private double width;
    private double height;

    // Animation state
    private double borderPulse = 0;
    private double glitchTimer = 0;
    private boolean glitchActive = false;
    private boolean isHovered = false;
    private boolean isPressed = false;
    private double hoverIntensity = 0;

    // Colors
    private static final Color NEON_GREEN = Color.rgb(0, 255, 136);
    private static final Color DARK_BG = Color.rgb(26, 31, 58);

    public MenuEffect(String text, double width, double height) {
        this.width = width;
        this.height = height;

        // Create canvas for background effects
        canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();

        // Create button
        button = new Button(text);
        button.setPrefSize(width, height);
        button.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #00ff88;" +
                        "-fx-font-family: 'Orbitron', 'Arial';" +
                        "-fx-font-size: 24px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;"
        );

        // Add both to stack
        this.getChildren().addAll(canvas, button);

        // Mouse events
        button.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            isHovered = true;
        });

        button.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            isHovered = false;
        });

        button.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            isPressed = true;
        });

        button.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            isPressed = false;
        });

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
        animationTimer.start();
    }

    private void render() {
        // Clear canvas
        gc.clearRect(0, 0, width, height);

        // Update hover intensity
        if (isHovered) {
            hoverIntensity = Math.min(1.0, hoverIntensity + 0.1);
        } else {
            hoverIntensity = Math.max(0.0, hoverIntensity - 0.1);
        }

        // Background
        if (isPressed) {
            gc.setFill(NEON_GREEN.deriveColor(0, 1, 1, 0.15));
        } else if (isHovered) {
            gc.setFill(NEON_GREEN.deriveColor(0, 1, 1, 0.1));
        } else {
            gc.setFill(DARK_BG);
        }
        gc.fillRect(0, 0, width, height);

        // Update glitch
        updateGlitch();
        if (glitchActive && isHovered) {
            drawGlitchEffect();
        }

        // Animated border
        drawAnimatedBorder();

        // Corner accents
        drawCornerAccents();

        // Inner shadow effect when hovered
        if (hoverIntensity > 0) {
            drawInnerGlow();
        }
    }

    private void updateGlitch() {
        glitchTimer++;
        if (glitchTimer > 60) {
            if (Math.random() < 0.2) {
                glitchActive = true;
                glitchTimer = 0;
            }
        }

        if (glitchActive && glitchTimer > 3) {
            glitchActive = false;
        }
    }

    private void drawGlitchEffect() {
        gc.save();

        for (int i = 0; i < 3; i++) {
            double y = Math.random() * height;
            double h = 3 + Math.random() * 10;
            double offset = (Math.random() - 0.5) * 8;

            gc.setFill(Color.rgb(255, 0, 0, 0.3));
            gc.fillRect(offset, y, width, h);

            gc.setFill(Color.rgb(0, 255, 255, 0.3));
            gc.fillRect(-offset, y + 1, width, h);
        }

        gc.restore();
    }

    private void drawAnimatedBorder() {
        borderPulse += 0.08;
        double pulse = 0.5 + Math.sin(borderPulse) * 0.3;

        if (isHovered) {
            pulse *= 1.5;
        }

        // Outer glow
        gc.setStroke(NEON_GREEN.deriveColor(0, 1, 1, pulse * 0.5 * hoverIntensity));
        gc.setLineWidth(6);
        gc.strokeRect(1, 1, width - 2, height - 2);

        // Main border
        gc.setStroke(NEON_GREEN.deriveColor(0, 1, 1, pulse * 0.9));
        gc.setLineWidth(2);
        gc.strokeRect(1, 1, width - 2, height - 2);

        // Inner bright line
        if (isHovered) {
            gc.setStroke(NEON_GREEN.deriveColor(1, 1, 1.5, pulse));
            gc.setLineWidth(1);
            gc.strokeRect(3, 3, width - 6, height - 6);
        }
    }

    private void drawCornerAccents() {
        double cornerSize = 15;
        double cornerThick = 2;
        int margin = 5;

        double alpha = 0.6 + hoverIntensity * 0.4;
        gc.setStroke(NEON_GREEN.deriveColor(0, 1, 1, alpha));
        gc.setLineWidth(cornerThick);

        // Top-left
        gc.strokeLine(margin, margin, margin + cornerSize, margin);
        gc.strokeLine(margin, margin, margin, margin + cornerSize);

        // Top-right
        gc.strokeLine(width - margin, margin, width - margin - cornerSize, margin);
        gc.strokeLine(width - margin, margin, width - margin, margin + cornerSize);

        // Bottom-left
        gc.strokeLine(margin, height - margin, margin + cornerSize, height - margin);
        gc.strokeLine(margin, height - margin, margin, height - margin - cornerSize);

        // Bottom-right
        gc.strokeLine(width - margin, height - margin, width - margin - cornerSize, height - margin);
        gc.strokeLine(width - margin, height - margin, width - margin, height - margin - cornerSize);
    }

    private void drawInnerGlow() {
        gc.save();
        gc.setFill(NEON_GREEN.deriveColor(0, 1, 1, 0.1 * hoverIntensity));
        gc.fillRect(5, 5, width - 10, height - 10);
        gc.restore();
    }

    public Button getButton() {
        return button;
    }

    public void setOnAction(javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        button.setOnAction(handler);
    }

    public void stop() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }
}