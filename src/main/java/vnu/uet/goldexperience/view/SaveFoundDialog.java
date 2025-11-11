package vnu.uet.goldexperience.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import vnu.uet.goldexperience.core.Action;
import vnu.uet.goldexperience.manager.InputManager;

public class SaveFoundDialog {

    private enum DialogOption {
        NEW_GAME(0, "NEW GAME"),
        LOAD_GAME(1, "LOAD GAME");

        final int index;
        final String label;

        DialogOption(int index, String label) {
            this.index = index;
            this.label = label;
        }
    }

    private boolean isVisible = false;
    private int selectedIndex = 0;

    private final double canvasWidth;
    private final double canvasHeight;

    private static final double BOX_WIDTH = 380;
    private static final double BOX_HEIGHT = 240;
    private static final double BUTTON_WIDTH = 240;
    private static final double BUTTON_HEIGHT = 50;
    private static final double BUTTON_SPACING = 16;

    private double animationTimer = 0;

    // Theme colors
    private Color colorPrimary = Color.rgb(0, 255, 255);
    private Color colorSecondary = Color.rgb(255, 0, 255);
    private Color colorBackground = Color.rgb(15, 15, 30);
    private Color colorText = Color.WHITE;
    private Color colorOverlay = Color.rgb(0, 0, 0, 0.85);

    private Font titleFont;
    private Font buttonFont;
    private Font infoFont;

    public interface LoadGameCallback {
        void onNewGame();

        void onLoadGame();
    }

    private LoadGameCallback callback;

    public SaveFoundDialog(Canvas canvas) {
        this.canvasWidth = canvas.getWidth();
        this.canvasHeight = canvas.getHeight();

        titleFont = Font.loadFont(getClass().getResourceAsStream("/font/cyber32.ttf"), 32);
        buttonFont = Font.loadFont(getClass().getResourceAsStream("/font/cyber32.ttf"), 22);
        infoFont = Font.loadFont(getClass().getResourceAsStream("/font/cyber32.ttf"), 14);
    }

    public void setCallback(LoadGameCallback callback) {
        this.callback = callback;
    }

    public void show() {
        isVisible = true;
        selectedIndex = 1; // Default to LOAD_GAME
        animationTimer = 0;
    }

    public void hide() {
        isVisible = false;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void update(double deltaTime) {
        if (isVisible) {
            animationTimer += deltaTime;
        }
    }

    public void handleKeyInput(InputManager input) {
        if (!isVisible) return;

        if (input.isActionJustPressed(Action.MOVE_UP) ||
                input.isActionJustPressed(Action.MOVE_LEFT)) {
            selectedIndex = 0;
        } else if (input.isActionJustPressed(Action.MOVE_DOWN) ||
                input.isActionJustPressed(Action.MOVE_RIGHT)) {
            selectedIndex = 1;
        }

        if (input.isActionJustPressed(Action.CONFIRM) ||
                input.isActionJustPressed(Action.SHOOT)) {
            executeSelectedOption();
        }
    }

    public void handleMouseInput(double mouseX, double mouseY, boolean clicked) {
        if (!isVisible) return;

        int hoveredIndex = getButtonIndexAtPosition(mouseX, mouseY);
        if (hoveredIndex >= 0) {
            if (selectedIndex != hoveredIndex) {
            }
            selectedIndex = hoveredIndex;

            if (clicked) {
                executeSelectedOption();
            }
        }
    }

    private int getButtonIndexAtPosition(double x, double y) {
        double boxX = (canvasWidth - BOX_WIDTH) / 2;
        double boxY = (canvasHeight - BOX_HEIGHT) / 2;

        double startY = boxY + 100;

        for (int i = 0; i < 2; i++) {
            double btnX = boxX + (BOX_WIDTH - BUTTON_WIDTH) / 2;
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

        DialogOption option = DialogOption.values()[selectedIndex];

        hide();

        switch (option) {
            case NEW_GAME:
                callback.onNewGame();
                break;
            case LOAD_GAME:
                callback.onLoadGame();
                break;
        }
    }

    public void render(GraphicsContext gc) {
        if (!isVisible) return;

        gc.setFill(colorOverlay);
        gc.fillRect(0, 0, canvasWidth, canvasHeight);

        double boxX = (canvasWidth - BOX_WIDTH) / 2;
        double boxY = (canvasHeight - BOX_HEIGHT) / 2;

        double pulse = Math.sin(animationTimer * 3) * 0.5 + 0.5;

        gc.setFill(colorBackground);
        gc.fillRect(boxX, boxY, BOX_WIDTH, BOX_HEIGHT);

        gc.setStroke(colorPrimary.deriveColor(0, 1, 1, 0.8 + pulse * 0.2));
        gc.setLineWidth(3);
        gc.strokeRect(boxX, boxY, BOX_WIDTH, BOX_HEIGHT);

        renderCornerAccents(gc, boxX, boxY, pulse);

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFill(colorSecondary);
        gc.setFont(titleFont);
        gc.fillText("SAVE FOUND", boxX + BOX_WIDTH / 2, boxY + 50);

        gc.setFill(colorText.deriveColor(0, 1, 1, 0.7));
        gc.setFont(infoFont);
        gc.fillText("Continue from saved progress?", boxX + BOX_WIDTH / 2, boxY + 75);

        renderButtons(gc, boxX, boxY, pulse);
    }

    private void renderCornerAccents(GraphicsContext gc, double boxX, double boxY, double pulse) {
        double size = 15;

        gc.setStroke(colorSecondary.deriveColor(0, 1, 1, 0.8 + pulse * 0.2));
        gc.setLineWidth(2);

        gc.strokeLine(boxX, boxY, boxX + size, boxY);
        gc.strokeLine(boxX, boxY, boxX, boxY + size);

        gc.strokeLine(boxX + BOX_WIDTH, boxY, boxX + BOX_WIDTH - size, boxY);
        gc.strokeLine(boxX + BOX_WIDTH, boxY, boxX + BOX_WIDTH, boxY + size);

        gc.strokeLine(boxX, boxY + BOX_HEIGHT, boxX + size, boxY + BOX_HEIGHT);
        gc.strokeLine(boxX, boxY + BOX_HEIGHT, boxX, boxY + BOX_HEIGHT - size);

        gc.strokeLine(boxX + BOX_WIDTH, boxY + BOX_HEIGHT, boxX + BOX_WIDTH - size, boxY + BOX_HEIGHT);
        gc.strokeLine(boxX + BOX_WIDTH, boxY + BOX_HEIGHT, boxX + BOX_WIDTH, boxY + BOX_HEIGHT - size);
    }

    private void renderButtons(GraphicsContext gc, double boxX, double boxY, double pulse) {
        double startY = boxY + 100;

        for (DialogOption option : DialogOption.values()) {
            double btnX = boxX + (BOX_WIDTH - BUTTON_WIDTH) / 2;
            double btnY = startY + option.index * (BUTTON_HEIGHT + BUTTON_SPACING);

            boolean isSelected = (option.index == selectedIndex);
            renderButton(gc, btnX, btnY, option.label, isSelected, pulse);
        }
    }

    private void renderButton(GraphicsContext gc, double x, double y,
                              String text, boolean selected, double pulse) {
        if (selected) {
            gc.setFill(colorPrimary.deriveColor(0, 1, 1, 0.2));
            gc.fillRect(x, y, BUTTON_WIDTH, BUTTON_HEIGHT);

            gc.setStroke(colorPrimary.deriveColor(0, 1, 1, 0.8 + pulse * 0.2));
            gc.setLineWidth(2);
            gc.strokeRect(x, y, BUTTON_WIDTH, BUTTON_HEIGHT);

            gc.setFill(colorSecondary.deriveColor(0, 1, 1, 0.8 + pulse * 0.2));
            gc.fillRect(x, y, 4, BUTTON_HEIGHT);

            gc.setTextAlign(TextAlignment.CENTER);
            gc.setFill(colorText);
            gc.setFont(buttonFont);
            gc.fillText(text, x + BUTTON_WIDTH / 2, y + BUTTON_HEIGHT / 2 + 8);

        } else {
            gc.setStroke(colorPrimary.deriveColor(0, 1, 1, 0.3));
            gc.setLineWidth(1);
            gc.strokeRect(x, y, BUTTON_WIDTH, BUTTON_HEIGHT);

            gc.setTextAlign(TextAlignment.CENTER);
            gc.setFill(colorText.deriveColor(0, 1, 1, 0.5));
            gc.setFont(buttonFont);
            gc.fillText(text, x + BUTTON_WIDTH / 2, y + BUTTON_HEIGHT / 2 + 8);
        }
    }
}