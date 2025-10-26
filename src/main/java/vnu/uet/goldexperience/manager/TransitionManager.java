package vnu.uet.goldexperience.manager;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import vnu.uet.goldexperience.core.ChapterTheme;

public class TransitionManager {

    private enum Phase {
        IDLE,
        ROUND_CLEAR,
        SLIDING
    }

    private Phase currentPhase = Phase.IDLE;
    private double timer = 0;
    private double slideOffset = 0;

    private final double canvasWidth;
    private final double canvasHeight;

    // Kích thước mới theo yêu cầu
    private double BOX_WIDTH; // Sẽ được gán bằng canvasWidth
    private static final double BOX_HEIGHT = 150; // Chiều cao 300

    private static final double ROUND_CLEAR_DURATION = 2.0;
    private static final double SLIDE_SPEED = 600.0;

    private boolean shouldLoadLevel = false;

    private Color colorPrimary;
    private Color colorSecondary;
    private Color colorText;
    private Color colorBackground;
    private Font tileFont;

    public TransitionManager(double canvasWidth, double canvasHeight) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        this.BOX_WIDTH = canvasWidth; // Gán chiều rộng
        setTheme(ChapterTheme.ORIGINAL);
        tileFont = Font.loadFont(getClass().getResourceAsStream("/font/cyber32.ttf"), 62);
    }

    public void setTheme(ChapterTheme theme) {
        switch (theme) {
            case CHAPTER_1_RUST:
                colorPrimary = ChapterTheme.NEON_ORANGE;
                colorSecondary = ChapterTheme.MEDIUM_GRAY;
                colorText = ChapterTheme.PURE_WHITE;
                colorBackground = ChapterTheme.DARK_BG_CH1;
                break;
            case CHAPTER_3_VERDANT:
                colorPrimary = ChapterTheme.NEON_GREEN;
                colorSecondary = ChapterTheme.EARTHY_YELLOW;
                colorText = ChapterTheme.PURE_WHITE;
                colorBackground = ChapterTheme.DARK_BG_CH3;
                break;
            case CHAPTER_4_CATHEDRAL:
                colorPrimary = ChapterTheme.GOLD;
                colorSecondary = ChapterTheme.GOLD;
                colorText = ChapterTheme.PURE_WHITE;
                colorBackground = ChapterTheme.DARK_BG_CH4;
                break;
            case CHAPTER_5_NEXUS:
                colorPrimary = ChapterTheme.PURE_WHITE;
                colorSecondary = ChapterTheme.PURE_WHITE;
                colorText = ChapterTheme.DARK_BG;
                colorBackground = ChapterTheme.DARK_BG_CH5;
                break;
            case CHAPTER_2_NEON:
            case ORIGINAL:
            default:
                colorPrimary = ChapterTheme.NEON_CYAN;
                colorSecondary = ChapterTheme.NEON_PINK;
                colorText = ChapterTheme.PURE_WHITE;
                colorBackground = ChapterTheme.DARK_BG;
                break;
        }
    }

    public void start() {
        currentPhase = Phase.ROUND_CLEAR;
        timer = 0;
        shouldLoadLevel = false;
    }

    public boolean update(double deltaTime) {
        boolean needLoadLevel = false;

        if (currentPhase == Phase.ROUND_CLEAR) {
            timer += deltaTime;
            if (timer >= ROUND_CLEAR_DURATION) {
                currentPhase = Phase.SLIDING;
                slideOffset = -canvasHeight;
                shouldLoadLevel = true;
                needLoadLevel = true;
            }
        } else if (currentPhase == Phase.SLIDING) {
            slideOffset += SLIDE_SPEED * deltaTime;
            if (slideOffset >= 0) {
                slideOffset = 0;
                currentPhase = Phase.IDLE;
                shouldLoadLevel = false;
            }
        }

        return needLoadLevel;
    }

    public void render(GraphicsContext gc) {
        if (currentPhase == Phase.ROUND_CLEAR) {
            renderRoundClear(gc);
        }
    }

    private void renderRoundClear(GraphicsContext gc) {
        double boxX = (canvasWidth - BOX_WIDTH) / 2;
        double boxY = (canvasHeight - BOX_HEIGHT) / 2; //

        double centerX = boxX + BOX_WIDTH / 2;
        double centerY = boxY + BOX_HEIGHT / 2;

        double alpha;
        if (timer < 0.3) {
            alpha = timer / 0.3;
        } else if (timer > ROUND_CLEAR_DURATION - 0.3) {
            alpha = (ROUND_CLEAR_DURATION - timer) / 0.3;
        } else {
            alpha = 1.0;
        }

        gc.setFill(colorBackground.deriveColor(0, 1, 1, alpha * 0.7));
        gc.fillRect(0, 0, canvasWidth, canvasHeight);

        gc.setFill(colorBackground.darker().deriveColor(0, 1, 1, alpha * 0.8));
        gc.fillRect(boxX, boxY, BOX_WIDTH, BOX_HEIGHT);

        gc.setTextAlign(TextAlignment.CENTER);

        gc.setFill(colorPrimary.deriveColor(0, 1, 1, alpha));
        gc.setFont(tileFont);
        gc.fillText("ROUND CLEAR", centerX, centerY+20);

        double pulse = Math.sin(timer * 10) * 0.3 + 0.7;

        gc.setStroke(colorSecondary.deriveColor(0, 1, 1, alpha * pulse * 0.4));
        gc.setLineWidth(6);
        gc.strokeRect(boxX, boxY, BOX_WIDTH, BOX_HEIGHT);

        gc.setStroke(colorPrimary.deriveColor(0, 1, 1, alpha * pulse));
        gc.setLineWidth(2);
        gc.strokeRect(boxX + 4, boxY + 4, BOX_WIDTH - 8, BOX_HEIGHT - 8);

        drawCornerAccents(gc, alpha * pulse, boxX, boxY);
    }

    private void drawCornerAccents(GraphicsContext gc, double alpha, double boxX, double boxY) {
        double cornerSize = 30;
        double offset = 4;

        gc.setStroke(colorSecondary.deriveColor(0, 1, 1, alpha));
        gc.setLineWidth(3);

        gc.strokeLine(boxX + offset, boxY + offset, boxX + offset + cornerSize, boxY + offset);
        gc.strokeLine(boxX + offset, boxY + offset, boxX + offset, boxY + offset + cornerSize);

        gc.strokeLine(boxX + BOX_WIDTH - offset, boxY + offset, boxX + BOX_WIDTH - offset - cornerSize, boxY + offset);
        gc.strokeLine(boxX + BOX_WIDTH - offset, boxY + offset, boxX + BOX_WIDTH - offset, boxY + offset + cornerSize);

        gc.strokeLine(boxX + offset, boxY + BOX_HEIGHT - offset, boxX + offset + cornerSize, boxY + BOX_HEIGHT - offset);
        gc.strokeLine(boxX + offset, boxY + BOX_HEIGHT - offset, boxX + offset, boxY + BOX_HEIGHT - offset - cornerSize);

        gc.strokeLine(boxX + BOX_WIDTH - offset, boxY + BOX_HEIGHT - offset,
                boxX + BOX_WIDTH - offset - cornerSize, boxY + BOX_HEIGHT - offset);
        gc.strokeLine(boxX + BOX_WIDTH - offset, boxY + BOX_HEIGHT - offset,
                boxX + BOX_WIDTH - offset, boxY + BOX_HEIGHT - offset - cornerSize);
    }

    public void applySlideTransform(GraphicsContext gc) {
        if (currentPhase == Phase.SLIDING) {
            gc.translate(0, slideOffset);
        }
    }

    public boolean isActive() {
        return currentPhase != Phase.IDLE;
    }

    public boolean shouldDisableCollision() {
        return currentPhase == Phase.SLIDING && slideOffset < 0;
    }

    public void reset() {
        currentPhase = Phase.IDLE;
        timer = 0;
        slideOffset = 0;
        shouldLoadLevel = false;
    }
}