package vnu.uet.goldexperience.manager;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import vnu.uet.goldexperience.core.Action;
import vnu.uet.goldexperience.core.ChapterTheme; // <-- IMPORT THÊM

public class PauseMenuManager {

    private enum MenuOption {
        RESUME(0, "RESUME"),
        RESTART(1, "RESTART"),
        LEVEL_SELECT(2, "LEVEL SELECT"),
        QUIT(3, "QUIT");

        final int index;
        final String label;

        MenuOption(int index, String label) {
            this.index = index;
            this.label = label;
        }
    }

    private final Canvas canvas;
    private final SceneManager sceneManager;

    private boolean isVisible = false;
    private int selectedIndex = 0;
    private double mouseX = 0;
    private double mouseY = 0;

    private final double canvasWidth;
    private final double canvasHeight;

    private static final double BOX_SIZE = 400;

    private static final double BUTTON_WIDTH = 280;
    private static final double BUTTON_HEIGHT = 45;
    private static final double BUTTON_SPACING = 12;
    private static final int MENU_OPTION_COUNT = 4;

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

    public interface PauseMenuCallback {
        void onResume();
        void onRestart();
        void onLevelSelect();
        void onQuit();
    }

    private PauseMenuCallback callback;

    public PauseMenuManager(Canvas canvas, SceneManager sceneManager) {
        this.canvas = canvas;
        this.sceneManager = sceneManager;
        this.canvasWidth = canvas.getWidth();
        this.canvasHeight = canvas.getHeight();
        setTheme(ChapterTheme.ORIGINAL);

        titleFont = Font.loadFont(getClass().getResourceAsStream("/font/cyber32.ttf"), 52);
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

    public void setCallback(PauseMenuCallback callback) {
        this.callback = callback;
    }

    public void show() {
        isVisible = true;
        selectedIndex = 0;
        animationTimer = 0;
    }

    public void hide() {
        isVisible = false;
    }

    public void update(double deltaTime) {
        if (isVisible) {
            System.out.println(animationTimer);
            animationTimer += deltaTime;
        }
    }

    public void handleKeyInput(InputManager input) {
        if (!isVisible) return;

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
        if (!isVisible) return;

        this.mouseX = mouseX;
        this.mouseY = mouseY;

        int hoveredIndex = getButtonIndexAtPosition(mouseX, mouseY);
        if (hoveredIndex >= 0) {
            selectedIndex = hoveredIndex;

            if (clicked) {
                executeSelectedOption();
            }
        }
    }

    private int getButtonIndexAtPosition(double x, double y) {
        double boxX = (canvasWidth - BOX_SIZE) / 2;
        double boxY = (canvasHeight - BOX_SIZE) / 2;

        double startY = boxY + 140;

        for (int i = 0; i < MENU_OPTION_COUNT; i++) {
            double btnX = boxX + (BOX_SIZE - BUTTON_WIDTH) / 2;
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
            case RESUME:
                callback.onResume();
                break;
            case RESTART:
                callback.onRestart();
                break;
            case LEVEL_SELECT:
                callback.onLevelSelect();
                break;
            case QUIT:
                callback.onQuit();
                break;
        }
    }

    public void render(GraphicsContext gc) {
        if (!isVisible) return;

        gc.setFill(colorOverlay);
        gc.fillRect(0, 0, canvasWidth, canvasHeight);

        double boxX = (canvasWidth - BOX_SIZE) / 2;
        double boxY = (canvasHeight - BOX_SIZE) / 2;

        double pulse = Math.sin(animationTimer * 3) * 0.5 + 0.5;

        gc.setFill(colorBackground);
        gc.fillRect(boxX, boxY, BOX_SIZE, BOX_SIZE);

        gc.setFill(colorBackgroundSecondary.deriveColor(0, 1, 1, 0.3));
        gc.fillRect(boxX, boxY, BOX_SIZE, BOX_SIZE);

        gc.setStroke(colorPrimary.deriveColor(0, 1, 1, 0.3));
        gc.setLineWidth(8);
        gc.strokeRect(boxX - 4, boxY - 4, BOX_SIZE + 8, BOX_SIZE + 8);

        gc.setStroke(colorPrimary.deriveColor(0, 1, 1, 0.5));
        gc.setLineWidth(4);
        gc.strokeRect(boxX - 2, boxY - 2, BOX_SIZE + 4, BOX_SIZE + 4);

        gc.setStroke(colorPrimary.deriveColor(0, 1, 1, 0.8 + pulse * 0.2));
        gc.setLineWidth(3);
        gc.strokeRect(boxX, boxY, BOX_SIZE, BOX_SIZE);

        renderCornerAccents(gc, boxX, boxY, pulse);

        renderGridPattern(gc, boxX, boxY);

        renderTitle(gc, boxX, boxY);

        renderButtons(gc, boxX, boxY, pulse);

        renderScanlines(gc, boxX, boxY);
    }

    private void renderCornerAccents(GraphicsContext gc, double boxX, double boxY, double pulse) {
        double size = 20;

        gc.setStroke(colorSecondary.deriveColor(0, 1, 1, 0.8 + pulse * 0.2));
        gc.setLineWidth(3);

        gc.strokeLine(boxX, boxY, boxX + size, boxY);
        gc.strokeLine(boxX, boxY, boxX, boxY + size);

        gc.strokeLine(boxX + BOX_SIZE, boxY, boxX + BOX_SIZE - size, boxY);
        gc.strokeLine(boxX + BOX_SIZE, boxY, boxX + BOX_SIZE, boxY + size);

        gc.strokeLine(boxX, boxY + BOX_SIZE, boxX + size, boxY + BOX_SIZE);
        gc.strokeLine(boxX, boxY + BOX_SIZE, boxX, boxY + BOX_SIZE - size);

        gc.strokeLine(boxX + BOX_SIZE, boxY + BOX_SIZE, boxX + BOX_SIZE - size, boxY + BOX_SIZE);
        gc.strokeLine(boxX + BOX_SIZE, boxY + BOX_SIZE, boxX + BOX_SIZE, boxY + BOX_SIZE - size);
    }

    private void renderGridPattern(GraphicsContext gc, double boxX, double boxY) {
        gc.setStroke(colorPrimary.deriveColor(0, 1, 1, 0.08));
        gc.setLineWidth(1);

        for (int i = 0; i < BOX_SIZE; i += 20) {
            gc.strokeLine(boxX + i, boxY, boxX + i, boxY + BOX_SIZE);
            gc.strokeLine(boxX, boxY + i, boxX + BOX_SIZE, boxY + i);
        }
    }

    private void renderTitle(GraphicsContext gc, double boxX, double boxY) {
        double titleY = boxY + 80;

        gc.setTextAlign(TextAlignment.CENTER);

        gc.setFill(colorSecondary);
        gc.setFont(titleFont);
        gc.fillText("PAUSED", boxX + BOX_SIZE / 2, titleY);

        double lineWidth = 150;
        double lineX = boxX + (BOX_SIZE - lineWidth) / 2;

        gc.setStroke(colorPrimary);
        gc.setLineWidth(2);
        gc.strokeLine(lineX, titleY + 10, lineX + lineWidth, titleY + 10);
    }

    private void renderButtons(GraphicsContext gc, double boxX, double boxY, double pulse) {
        double startY = boxY + 140;

        for (MenuOption option : MenuOption.values()) {
            double btnX = boxX + (BOX_SIZE - BUTTON_WIDTH) / 2;
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

    private void renderScanlines(GraphicsContext gc, double boxX, double boxY) {
        gc.setStroke(colorPrimary.deriveColor(0, 1, 1, 0.03));
        gc.setLineWidth(1);

        for (int i = 0; i < BOX_SIZE; i += 3) {
            gc.strokeLine(boxX, boxY + i, boxX + BOX_SIZE, boxY + i);
        }
    }
}