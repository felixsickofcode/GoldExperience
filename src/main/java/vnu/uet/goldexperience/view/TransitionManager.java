package vnu.uet.goldexperience.view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import vnu.uet.goldexperience.core.ChapterTheme;

public class TransitionManager {

    private enum Phase {
        HIDDEN,
        FADING_IN_TITLE,
        HOLDING_TITLE,
        FADING_OUT_TITLE,
        SLIDING
    }
    private Phase currentPhase = Phase.HIDDEN;

    private double transitionTimer = 0;
    private double slideOffset = 0;

    private final double canvasWidth;
    private final double canvasHeight;

    private static final double FADE_IN_DURATION = 0.5;
    private static final double HOLD_DURATION = 1.0;
    private static final double FADE_OUT_DURATION = 0.5;
    private static final double SLIDE_SPEED = 600.0;

    private boolean shouldLoadLevel = false;

    private Color colorPrimary;
    private Color colorSecondary;
    private Color colorText;
    private Color colorBackground;
    private Font titleFont;

    public TransitionManager(double canvasWidth, double canvasHeight) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        titleFont = Font.loadFont(getClass().getResourceAsStream("/font/cyber32.ttf"), 66);
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
        currentPhase = Phase.FADING_IN_TITLE;
        transitionTimer = 0;
        shouldLoadLevel = false;
    }

    public boolean update(double deltaTime) {
        boolean needLoadLevel = false;

        if (currentPhase == Phase.FADING_IN_TITLE) {
            transitionTimer += deltaTime;
            if (transitionTimer >= FADE_IN_DURATION) {
                transitionTimer = 0;
                currentPhase = Phase.HOLDING_TITLE;
            }
        } else if (currentPhase == Phase.HOLDING_TITLE) {
            transitionTimer += deltaTime;
            if (transitionTimer >= HOLD_DURATION) {
                transitionTimer = 0;
                currentPhase = Phase.FADING_OUT_TITLE;
            }
        } else if (currentPhase == Phase.FADING_OUT_TITLE) {
            transitionTimer += deltaTime;
            if (transitionTimer >= FADE_OUT_DURATION) {
                currentPhase = Phase.SLIDING;
                slideOffset = -canvasHeight;
                shouldLoadLevel = true;
                needLoadLevel = true;
            }
        } else if (currentPhase == Phase.SLIDING) {
            slideOffset += SLIDE_SPEED * deltaTime;
            if (slideOffset >= 0) {
                slideOffset = 0;
                currentPhase = Phase.HIDDEN;
                shouldLoadLevel = false;
            }
        }

        return needLoadLevel;
    }

    public void render(GraphicsContext gc) {
        if (currentPhase == Phase.FADING_IN_TITLE ||
                currentPhase == Phase.HOLDING_TITLE ||
                currentPhase == Phase.FADING_OUT_TITLE) {

            double alpha = 0.0;
            if (currentPhase == Phase.FADING_IN_TITLE) {
                alpha = transitionTimer / FADE_IN_DURATION;
            } else if (currentPhase == Phase.HOLDING_TITLE) {
                alpha = 1.0;
            } else if (currentPhase == Phase.FADING_OUT_TITLE) {
                alpha = 1.0 - (transitionTimer / FADE_OUT_DURATION);
            }
            alpha = Math.max(0, Math.min(1, alpha));

            renderRoundClear(gc, alpha);
        }
    }

    private void renderRoundClear(GraphicsContext gc, double alpha) {

        gc.setFill(colorBackground.deriveColor(0, 1, 1, alpha * 0.7));
        gc.fillRect(0, 0, canvasWidth, canvasHeight);

        gc.save();
        gc.setGlobalAlpha(alpha);

        double centerX = canvasWidth / 2;
        double centerY = canvasHeight / 2;

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(titleFont);

        gc.save();
        gc.setEffect(new GaussianBlur(25));
        gc.setFill(colorSecondary);
        gc.fillText("ROUND CLEAR", centerX, centerY);
        gc.restore();

        gc.setFill(colorPrimary);
        gc.fillText("ROUND CLEAR", centerX, centerY);

        gc.restore();
    }

    public void applySlideTransform(GraphicsContext gc) {
        if (currentPhase == Phase.SLIDING) {
            gc.translate(0, slideOffset);
        }
    }

    public boolean isActive() {
        return currentPhase != Phase.HIDDEN;
    }

    public boolean shouldDisableCollision() {
        return currentPhase == Phase.SLIDING && slideOffset < 0;
    }

    public void reset() {
        currentPhase = Phase.HIDDEN;
        transitionTimer = 0;
        slideOffset = 0;
        shouldLoadLevel = false;
    }
}
