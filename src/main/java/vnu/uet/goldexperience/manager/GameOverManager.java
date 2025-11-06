package vnu.uet.goldexperience.manager;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import vnu.uet.goldexperience.core.Action;
import vnu.uet.goldexperience.core.ChapterTheme;

public class GameOverManager {

    private enum MenuOption {
        RETRY(0, "RETRY"),
        LEVEL_SELECT(1, "LEVEL SELECT"),
        MAIN_MENU(2, "MAIN MENU");

        final int index;
        final String label;

        MenuOption(int index, String label) {
            this.index = index;
            this.label = label;
        }
    }

    private enum Phase {
        HIDDEN,
        FADING_IN_TITLE,
        FADING_IN_MENU,
        SHOWN
    }
    private Phase currentPhase = Phase.HIDDEN;

    private final Canvas canvas;
    private final SceneManager sceneManager;

    private int selectedIndex = 0;
    private double mouseX = 0;
    private double mouseY = 0;

    private final double canvasWidth;
    private final double canvasHeight;

    private static final double BUTTON_WIDTH = 280;
    private static final double BUTTON_HEIGHT = 45;
    private static final double BUTTON_SPACING = 12;
    private static final int MENU_OPTION_COUNT = 3;

    private ChapterTheme currentTheme;
    private Color colorPrimary;
    private Color colorSecondary;
    private Color colorBackground;
    private Color colorBackgroundSecondary;
    private Color colorText;
    private Color colorTextDisabled;
    private Color colorOverlay;
    private Font titleFont;
    private Font buttonFont;

    private double animationTimer = 0;

    private double transitionTimer = 0.0;
    private static final double TITLE_FADE_DURATION = 1.5;
    private static final double MENU_FADE_DURATION = 0.5;

    public interface GameOverCallback {
        void onRetry();
        void onLevelSelect();
        void onMainMenu();
    }
    private GameOverCallback callback;

    public GameOverManager(Canvas canvas, SceneManager sceneManager) {
        this.canvas = canvas;
        this.sceneManager = sceneManager;
        this.canvasWidth = canvas.getWidth();
        this.canvasHeight = canvas.getHeight();
        setTheme(ChapterTheme.ORIGINAL);
        titleFont = Font.loadFont(getClass().getResourceAsStream("/font/cyber32.ttf"), 77);
        buttonFont = Font.loadFont(getClass().getResourceAsStream("/font/cyber32.ttf"), 20);
    }

    public void setTheme(ChapterTheme theme) {
        this.currentTheme = theme;
        switch (theme) {
            case CHAPTER_1_RUST:
                colorPrimary = ChapterTheme.NEON_ORANGE;
                colorSecondary = ChapterTheme.MEDIUM_GRAY;
                colorBackground = ChapterTheme.DARK_BG_CH1;
                colorBackgroundSecondary = ChapterTheme.DARK_BG_CH1.brighter();
                colorText = ChapterTheme.PURE_WHITE;
                colorTextDisabled = ChapterTheme.MEDIUM_GRAY;
                colorOverlay = Color.rgb(30, 25, 20, 0.8);
                break;
            case CHAPTER_3_VERDANT:
                colorPrimary = ChapterTheme.NEON_GREEN;
                colorSecondary = ChapterTheme.EARTHY_YELLOW;
                colorBackground = ChapterTheme.DARK_BG_CH3;
                colorBackgroundSecondary = ChapterTheme.DARK_BG_CH3.brighter();
                colorText = ChapterTheme.PURE_WHITE;
                colorTextDisabled = ChapterTheme.MEDIUM_GRAY;
                colorOverlay = Color.rgb(10, 20, 10, 0.8);
                break;
            case CHAPTER_4_CATHEDRAL:
                colorPrimary = ChapterTheme.GOLD;
                colorSecondary = ChapterTheme.GOLD;
                colorBackground = ChapterTheme.DARK_BG_CH4;
                colorBackgroundSecondary = ChapterTheme.DARK_BG_CH4.brighter();
                colorText = ChapterTheme.PURE_WHITE;
                colorTextDisabled = ChapterTheme.MEDIUM_GRAY;
                colorOverlay = Color.rgb(20, 10, 30, 0.8);
                break;
            case CHAPTER_5_NEXUS:
                colorPrimary = ChapterTheme.PURE_WHITE;
                colorSecondary = ChapterTheme.PURE_WHITE;
                colorBackground = ChapterTheme.DARK_BG_CH5;
                colorBackgroundSecondary = ChapterTheme.DARK_BG_CH5.brighter();
                colorText = Color.BLACK;
                colorTextDisabled = ChapterTheme.MEDIUM_GRAY;
                colorOverlay = Color.rgb(15, 15, 25, 0.8);
                break;
            case CHAPTER_2_NEON:
            case ORIGINAL:
            default:
                colorPrimary = ChapterTheme.NEON_CYAN;
                colorSecondary = ChapterTheme.NEON_PINK;
                colorBackground = ChapterTheme.DARK_BG;
                colorBackgroundSecondary = ChapterTheme.DARK_PURPLE;
                colorText = ChapterTheme.PURE_WHITE;
                colorTextDisabled = Color.rgb(150, 150, 150);
                colorOverlay = Color.rgb(0, 0, 0, 0.8);
                break;
        }
    }

    public void setCallback(GameOverCallback callback) {
        this.callback = callback;
    }

    public void show() {
        if (currentPhase != Phase.HIDDEN) return;
        currentPhase = Phase.FADING_IN_TITLE;
        transitionTimer = 0;
        selectedIndex = 0;
        animationTimer = 0;
    }

    public void hide() {
        currentPhase = Phase.HIDDEN;
    }

    public boolean isVisible() {
        return currentPhase != Phase.HIDDEN;
    }

    public void update(double deltaTime) {
        if (!isVisible()) return;
        animationTimer += deltaTime;
        if (currentPhase == Phase.FADING_IN_TITLE) {
            transitionTimer += deltaTime;
            if (transitionTimer >= TITLE_FADE_DURATION) {
                transitionTimer = 0;
                currentPhase = Phase.FADING_IN_MENU;
            }
        } else if (currentPhase == Phase.FADING_IN_MENU) {
            transitionTimer += deltaTime;
            if (transitionTimer >= MENU_FADE_DURATION) {
                transitionTimer = MENU_FADE_DURATION;
                currentPhase = Phase.SHOWN;
            }
        }
    }

    public void handleKeyInput(InputManager input) {
        if (currentPhase != Phase.SHOWN) return;
        if (input.isActionJustPressed(Action.MOVE_UP)) {
            selectedIndex = (selectedIndex - 1 + MENU_OPTION_COUNT) % MENU_OPTION_COUNT;
        } else if (input.isActionJustPressed(Action.MOVE_DOWN)) {
            selectedIndex = (selectedIndex + 1) % MENU_OPTION_COUNT;
        }
        if (input.isActionJustPressed(Action.CONFIRM) ||
                input.isActionJustPressed(Action.SHOOT)) {
            executeSelectedOption();
        }
    }

    public void handleMouseInput(double mouseX, double mouseY, boolean clicked) {
        if (currentPhase == Phase.HIDDEN) return;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        int hoveredIndex = getButtonIndexAtPosition(mouseX, mouseY);
        if (hoveredIndex >= 0) {
            selectedIndex = hoveredIndex;
        }
        if (clicked && currentPhase == Phase.SHOWN && hoveredIndex >= 0) {
            executeSelectedOption();
        }
    }

    private int getButtonIndexAtPosition(double x, double y) {
        double startY = canvasHeight / 2 + 20;
        double btnX = (canvasWidth - BUTTON_WIDTH) / 2;

        for (int i = 0; i < MENU_OPTION_COUNT; i++) {
            double btnY = startY + i * (BUTTON_HEIGHT + BUTTON_SPACING);
            if (x >= btnX && x <= btnX + BUTTON_WIDTH &&
                    y >= btnY && y <= btnY + BUTTON_HEIGHT) {
                return i;
            }
        }
        return -1;
    }

    private void executeSelectedOption() {
        if (callback == null) return;
        MenuOption option = MenuOption.values()[selectedIndex];
        switch (option) {
            case RETRY:
                callback.onRetry();
                break;
            case LEVEL_SELECT:
                callback.onLevelSelect();
                break;
            case MAIN_MENU:
                callback.onMainMenu();
                break;
        }
    }

    public void render(GraphicsContext gc) {
        if (currentPhase == Phase.HIDDEN) return;

        double titleAlpha = 0.0;
        double menuAlpha = 0.0;

        if (currentPhase == Phase.FADING_IN_TITLE) {
            titleAlpha = transitionTimer / TITLE_FADE_DURATION;
        } else if (currentPhase == Phase.FADING_IN_MENU) {
            titleAlpha = 1.0;
            menuAlpha = transitionTimer / MENU_FADE_DURATION;
        } else if (currentPhase == Phase.SHOWN) {
            titleAlpha = 1.0;
            menuAlpha = 1.0;
        }

        titleAlpha = Math.max(0, Math.min(1, titleAlpha));
        menuAlpha = Math.max(0, Math.min(1, menuAlpha));

        double baseOverlayAlpha = colorOverlay.getOpacity();
        gc.setFill(colorOverlay.deriveColor(0, 1, 1, baseOverlayAlpha * titleAlpha));
        gc.fillRect(0, 0, canvasWidth, canvasHeight);

        gc.save();
        gc.setGlobalAlpha(titleAlpha);
        renderTitle(gc);
        gc.restore();

        gc.save();
        gc.setGlobalAlpha(menuAlpha);
        double pulse = Math.sin(animationTimer * 3) * 0.5 + 0.5;
        renderButtons(gc, pulse);
        gc.restore();
    }

    private void renderTitle(GraphicsContext gc) {
        double centerX = canvasWidth / 2;
        double titleY = canvasHeight / 2 - 80;

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(titleFont);

        gc.save();
        gc.setEffect(new javafx.scene.effect.GaussianBlur(25));
        gc.setFill(colorSecondary);
        gc.fillText("GAME OVER!", centerX, titleY);
        gc.restore();

        gc.setFill(colorPrimary);
        gc.fillText("GAME OVER!", centerX, titleY);
    }

    private void renderButtons(GraphicsContext gc, double pulse) {
        double startY = canvasHeight / 2 + 20;
        double btnX = (canvasWidth - BUTTON_WIDTH) / 2;

        double padding = 20.0;
        double boxWidth = BUTTON_WIDTH + (padding * 2);
        double boxHeight = (MENU_OPTION_COUNT * BUTTON_HEIGHT) + ((MENU_OPTION_COUNT - 1) * BUTTON_SPACING) + (padding * 2);
        double boxX = (canvasWidth - boxWidth) / 2;
        double boxY = startY - padding;

        gc.setFill(colorBackground.deriveColor(0, 1, 1, 0.3));
        gc.fillRect(boxX, boxY, boxWidth, boxHeight);

        gc.setStroke(colorPrimary.deriveColor(0, 1, 1, 0.5));
        gc.setLineWidth(2);
        gc.strokeRect(boxX, boxY, boxWidth, boxHeight);

        double cornerSize = 20;
        gc.setStroke(colorSecondary.deriveColor(0, 1, 1, 0.8 + pulse * 0.2));
        gc.setLineWidth(3);

        gc.strokeLine(boxX, boxY, boxX + cornerSize, boxY);
        gc.strokeLine(boxX, boxY, boxX, boxY + cornerSize);

        gc.strokeLine(boxX + boxWidth, boxY, boxX + boxWidth - cornerSize, boxY);
        gc.strokeLine(boxX + boxWidth, boxY, boxX + boxWidth, boxY + cornerSize);

        gc.strokeLine(boxX, boxY + boxHeight, boxX + cornerSize, boxY + boxHeight);
        gc.strokeLine(boxX, boxY + boxHeight, boxX, boxY + boxHeight - cornerSize);

        gc.strokeLine(boxX + boxWidth, boxY + boxHeight, boxX + boxWidth - cornerSize, boxY + boxHeight);
        gc.strokeLine(boxX + boxWidth, boxY + boxHeight, boxX + boxWidth, boxY + boxHeight - cornerSize);

        for (MenuOption option : MenuOption.values()) {
            double btnY = startY + option.index * (BUTTON_HEIGHT + BUTTON_SPACING);
            boolean isSelected = (option.index == selectedIndex);
            renderButton(gc, btnX, btnY, option.label, isSelected, pulse);
        }
    }

    private void renderButton(GraphicsContext gc, double x, double y, String text, boolean selected, double pulse) {
        if (selected) {
            gc.setFill(colorPrimary.deriveColor(0, 1, 1, 0.15));
            gc.fillRect(x, y, BUTTON_WIDTH, BUTTON_HEIGHT);
            gc.setStroke(colorPrimary.deriveColor(0, 1, 1, 0.8 + pulse * 0.2));
            gc.setLineWidth(2);
            gc.strokeRect(x, y, BUTTON_WIDTH, BUTTON_HEIGHT);
            gc.setFill(colorSecondary.deriveColor(0, 1, 1, 0.8 + pulse * 0.2));
            gc.fillRect(x, y, 4, BUTTON_HEIGHT);
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setFill(colorText);
            gc.setFont(buttonFont);
            gc.fillText(text, x + BUTTON_WIDTH / 2, y + BUTTON_HEIGHT / 2 + 7);
            gc.setFill(colorPrimary);
            gc.fillText("> ", x + BUTTON_WIDTH - 20, y + BUTTON_HEIGHT / 2 + 7);
        } else {
            gc.setStroke(colorPrimary.deriveColor(0, 1, 1, 0.3));
            gc.setLineWidth(1);
            gc.strokeRect(x, y, BUTTON_WIDTH, BUTTON_HEIGHT);
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setFill(colorTextDisabled);
            gc.setFont(buttonFont);
            gc.fillText(text, x + BUTTON_WIDTH / 2, y + BUTTON_HEIGHT / 2 + 6);
        }
    }
}
