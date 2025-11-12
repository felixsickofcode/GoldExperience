package vnu.uet.goldexperience.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import vnu.uet.goldexperience.core.Action;
import vnu.uet.goldexperience.core.ChapterTheme;
import vnu.uet.goldexperience.manager.InputManager;
import vnu.uet.goldexperience.model.Story;

public class DialogueSystem {
    private final Canvas canvas;
    private boolean isActive = false;
    private String[] dialogueLines;
    private int currentLineIndex = 0;
    private String npcName;
    private double fadeAlpha = 0.0;
    private static final double FADE_SPEED = 3.0;
    private Image npcPortrait = null;
    private int portraitX = 150;
    private int portraitY = 150;

    private String currentDisplayText = "";
    private int charIndex = 0;
    private double typeTimer = 0;
    private static final double CHAR_DELAY = 0.03;

    private double glitchTimer = 0;
    private double hologramFlicker = 0;

    private double introTimer = 0;
    private static final double INTRO_DURATION = 0.8;
    private boolean introComplete = false;

    private DialogueCallback callback;

    private ChapterTheme currentTheme;
    private Color colorPrimary;
    private Color colorSecondary;
    private Color colorBackground;
    private Color colorBackgroundSecondary;
    private Color colorText;
    private Color colorTextDisabled;
    private Color colorOverlay;
    private Font nameFont;
    private Font textFont;

    public interface DialogueCallback {
        void onDialogueComplete();
    }

    public DialogueSystem(Canvas canvas) {
        this.canvas = canvas;
        this.nameFont = Font.loadFont(getClass().getResourceAsStream("/font/cyber32.ttf"), 22);
        this.textFont = Font.loadFont(getClass().getResourceAsStream("/font/dialogue2.ttf"), 20);
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
                colorOverlay = Color.rgb(30, 25, 20, 0.7);
                break;
            case CHAPTER_3_VERDANT:
                colorPrimary = ChapterTheme.NEON_GREEN;
                colorSecondary = ChapterTheme.EARTHY_YELLOW;
                colorBackground = ChapterTheme.DARK_BG_CH3;
                colorBackgroundSecondary = ChapterTheme.DARK_BG_CH3.brighter();
                colorText = ChapterTheme.PURE_WHITE;
                colorTextDisabled = ChapterTheme.MEDIUM_GRAY;
                colorOverlay = Color.rgb(10, 20, 10, 0.7);
                break;
            case CHAPTER_4_CATHEDRAL:
                colorPrimary = ChapterTheme.GOLD;
                colorSecondary = ChapterTheme.GOLD;
                colorBackground = ChapterTheme.DARK_BG_CH4;
                colorBackgroundSecondary = ChapterTheme.DARK_BG_CH4.brighter();
                colorText = ChapterTheme.PURE_WHITE;
                colorTextDisabled = ChapterTheme.MEDIUM_GRAY;
                colorOverlay = Color.rgb(20, 10, 30, 0.7);
                break;
            case CHAPTER_5_NEXUS:
                colorPrimary = ChapterTheme.PURE_WHITE;
                colorSecondary = ChapterTheme.PURE_WHITE;
                colorBackground = ChapterTheme.DARK_BG_CH5;
                colorBackgroundSecondary = ChapterTheme.DARK_BG_CH5.brighter();
                colorText = ChapterTheme.PURE_WHITE;
                colorTextDisabled = ChapterTheme.MEDIUM_GRAY;
                colorOverlay = Color.rgb(15, 15, 25, 0.7);
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
                colorOverlay = Color.rgb(0, 0, 0, 0.7);
                break;
        }
    }

    public void setCallback(DialogueCallback callback) {
        this.callback = callback;
    }

    private Story.DialogueData currentDialogue = null;

    public void show(Story.DialogueData dialogue) {
        this.npcName = dialogue.npcName;
        this.dialogueLines = dialogue.lines;
        this.currentDialogue = dialogue;
        this.currentLineIndex = 0;
        this.isActive = true;
        this.fadeAlpha = 0.0;
        this.npcPortrait = loadNpcPortrait(dialogue.npcName);
        this.introTimer = 0;
        this.introComplete = false;
        startTyping();
    }

    public boolean isAfterLevelDialogue() {
        return currentDialogue != null && !currentDialogue.showBefore;
    }

    private void startTyping() {
        if (currentLineIndex < dialogueLines.length) {
            currentDisplayText = "";
            charIndex = 0;
            typeTimer = 0;
        }
    }

    public void hide() {
        isActive = false;
        fadeAlpha = 0.0;
    }

    public boolean isActive() {
        return isActive;
    }

    public void handleInput(InputManager input) {
        if (!isActive) return;

        if (input.isActionJustPressed(Action.CONFIRM)) {
            if (isTypingComplete()) {
                nextLine();
            } else {
                skipTyping();
            }
        }
    }

    private boolean isTypingComplete() {
        return currentLineIndex < dialogueLines.length &&
                charIndex >= dialogueLines[currentLineIndex].length();
    }

    private void skipTyping() {
        if (currentLineIndex < dialogueLines.length) {
            currentDisplayText = dialogueLines[currentLineIndex];
            charIndex = currentDisplayText.length();
        }
    }

    private void nextLine() {
        currentLineIndex++;
        if (currentLineIndex >= dialogueLines.length) {
            hide();
            if (callback != null) {
                callback.onDialogueComplete();
            }
        } else {
            startTyping();
        }
    }

    public void update(double deltaTime) {
        if (!isActive) return;

        if (fadeAlpha < 1.0) {
            fadeAlpha = Math.min(1.0, fadeAlpha + FADE_SPEED * deltaTime);
        }

        if (currentLineIndex < dialogueLines.length && !isTypingComplete()) {
            typeTimer += deltaTime;
            if (typeTimer >= CHAR_DELAY) {
                typeTimer = 0;
                charIndex++;
                if (charIndex <= dialogueLines[currentLineIndex].length()) {
                    currentDisplayText = dialogueLines[currentLineIndex].substring(0, charIndex);
                }
            }
        }

        glitchTimer += deltaTime;
        hologramFlicker += deltaTime * 4;
    }

    private Image loadNpcPortrait(String portraitName) {
        if (portraitName == null || portraitName.isEmpty()) {
            return null;
        }
        String imagePath = "/images/" + portraitName + ".png";

        try {
            return new Image(getClass().getResourceAsStream(imagePath));
        } catch (NullPointerException e) {
            System.err.println("Không tìm thấy ảnh chân dung: " + imagePath);
            return null;
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi tải ảnh: " + imagePath);
            e.printStackTrace();
            return null;
        }
    }

    private double getTextWidth(String text, Font font) {
        Text tempText = new Text(text);
        tempText.setFont(font);
        return tempText.getLayoutBounds().getWidth();
    }

    private void renderPortraitWithEffects(GraphicsContext gc, double x, double y, double alpha) {
        if (npcPortrait == null) return;

        if (npcName.equals("E.L.A.R.A")) {
            gc.save();

            // 1. Hologram flicker effect
            double flickerAlpha = alpha * (0.85 + Math.sin(hologramFlicker) * 0.15);
            gc.setGlobalAlpha(flickerAlpha);

            // 2. Glitch offset
            double offsetX = 0;
            double offsetY = 0;
            if (glitchTimer % 3.0 < 0.1) {
                offsetX = (Math.random() - 0.5) * 4;
                offsetY = (Math.random() - 0.5) * 4;
            }

            // Draw main portrait
            gc.drawImage(npcPortrait, x + offsetX, y + offsetY, portraitX, portraitY);

            // 3. Chromatic aberration
            if (glitchTimer % 3.0 < 0.1) {
                gc.setGlobalAlpha(alpha * 0.3);
                gc.setGlobalBlendMode(javafx.scene.effect.BlendMode.ADD);
                gc.drawImage(npcPortrait, x - 2, y, portraitX, portraitY);
                gc.drawImage(npcPortrait, x + 2, y, portraitX, portraitY);
                gc.setGlobalBlendMode(javafx.scene.effect.BlendMode.SRC_OVER);
            }

            gc.restore();

        } else {
            gc.save();
            gc.setGlobalAlpha(alpha);
            gc.drawImage(npcPortrait, x, y, portraitX, portraitY);
            gc.restore();
        }
    }

    public void render(GraphicsContext gc) {
        if (!isActive || fadeAlpha <= 0) return;

        double width = canvas.getWidth();
        double height = canvas.getHeight();

        gc.setFill(colorOverlay.deriveColor(0, 1, 1, colorOverlay.getOpacity() * fadeAlpha));
        gc.fillRect(0, 0, width, height);

        double boxWidth = width * 0.9;
        double boxHeight = 160;
        double boxX = (width - boxWidth) / 2;
        double boxY = height - boxHeight - 40;

        gc.setFill(colorBackground.deriveColor(0, 1, 1, 0.95 * fadeAlpha));
        gc.fillRect(boxX, boxY, boxWidth, boxHeight);

        gc.setFill(colorBackgroundSecondary.deriveColor(0, 1, 1, 0.3 * fadeAlpha));
        gc.fillRect(boxX, boxY, boxWidth, boxHeight);

        renderGridPattern(gc, boxX, boxY, boxWidth, boxHeight, fadeAlpha);

        gc.setStroke(colorPrimary.deriveColor(0, 1, 1, 0.3 * fadeAlpha));
        gc.setLineWidth(8);
        gc.strokeRect(boxX - 4, boxY - 4, boxWidth + 8, boxHeight + 8);

        gc.setStroke(colorPrimary.deriveColor(0, 1, 1, 0.5 * fadeAlpha));
        gc.setLineWidth(4);
        gc.strokeRect(boxX - 2, boxY - 2, boxWidth + 4, boxHeight + 4);

        gc.setStroke(colorPrimary.deriveColor(0, 1, 1, 0.8 * fadeAlpha));
        gc.setLineWidth(3);
        gc.strokeRect(boxX, boxY, boxWidth, boxHeight);

        renderCornerAccents(gc, boxX, boxY, boxWidth, boxHeight, fadeAlpha);
        renderScanlines(gc, boxX, boxY, boxWidth, boxHeight, fadeAlpha);

        double nameWidth = Math.max(200, getTextWidth(npcName, nameFont) + 40);
        double nameHeight = 40;
        double nameX = boxX;
        double nameY = boxY - nameHeight / 2;

        switch (npcName) {
            case "E.L.A.R.A":
                portraitX = 150;
                portraitY = 150;
                break;
            default:
                portraitY = 150;
                portraitX = 150;
                nameX = boxX + boxWidth - nameWidth;
        }

        if (npcPortrait != null) {
            renderPortraitWithEffects(gc,
                    nameX + nameWidth/2 - portraitX/2,
                    nameY - portraitY,
                    fadeAlpha);
        }

        gc.setFill(colorBackground.deriveColor(0, 1, 1, fadeAlpha));
        gc.fillRect(nameX, nameY, nameWidth, nameHeight);

        gc.setStroke(colorSecondary.deriveColor(0, 1, 1, fadeAlpha));
        gc.setLineWidth(2);
        gc.strokeRect(nameX, nameY, nameWidth, nameHeight);

        gc.setFill(colorPrimary.deriveColor(0, 1, 1, fadeAlpha));
        gc.setFont(nameFont);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(npcName, nameX + nameWidth / 2, nameY + 28);
        gc.setTextAlign(TextAlignment.LEFT);

        gc.setFont(textFont);
        gc.setFill(colorText.deriveColor(0, 1, 1, fadeAlpha));

        String[] words = currentDisplayText.split(" ");
        StringBuilder line = new StringBuilder();
        double textX = boxX + 30;
        double textY = boxY + 60;
        double lineHeight = 30;
        double maxWidth = boxWidth - 60;

        for (String word : words) {
            String testLine = line + word + " ";
            if (getTextWidth(testLine, textFont) > maxWidth && line.length() > 0) {
                gc.fillText(line.toString().trim(), textX, textY);
                textY += lineHeight;
                line = new StringBuilder(word + " ");
            } else {
                line.append(word).append(" ");
            }
        }
        if (line.length() > 0) {
            gc.fillText(line.toString().trim(), textX, textY);
        }

        if (isTypingComplete()) {
            double blink = (System.currentTimeMillis() % 1000) / 1000.0;
            double pulse = (blink > 0.5) ? 1.0 : 0.7;

            gc.setTextAlign(TextAlignment.RIGHT);
            gc.setFill(colorText.deriveColor(0, 1, 1, pulse * fadeAlpha));
            gc.setFont(textFont);
            gc.fillText("Press ENTER ", boxX + boxWidth - 45, boxY + boxHeight - 25);

            gc.setFill(colorPrimary.deriveColor(0, 1, 1, pulse * fadeAlpha));
            gc.fillText(">", boxX + boxWidth - 30, boxY + boxHeight - 25);
            gc.setTextAlign(TextAlignment.LEFT);
        }
    }

    private void renderCornerAccents(GraphicsContext gc, double boxX, double boxY, double width, double height, double alpha) {
        double size = 20;
        gc.setStroke(colorSecondary.deriveColor(0, 1, 1, 0.8 * alpha));
        gc.setLineWidth(3);

        gc.strokeLine(boxX, boxY, boxX + size, boxY);
        gc.strokeLine(boxX, boxY, boxX, boxY + size);
        gc.strokeLine(boxX + width, boxY, boxX + width - size, boxY);
        gc.strokeLine(boxX + width, boxY, boxX + width, boxY + size);
        gc.strokeLine(boxX, boxY + height, boxX + size, boxY + height);
        gc.strokeLine(boxX, boxY + height, boxX, boxY + height - size);
        gc.strokeLine(boxX + width, boxY + height, boxX + width - size, boxY + height);
        gc.strokeLine(boxX + width, boxY + height, boxX + width, boxY + height - size);
    }

    private void renderGridPattern(GraphicsContext gc, double boxX, double boxY, double width, double height, double alpha) {
        gc.setStroke(colorPrimary.deriveColor(0, 1, 1, 0.08 * alpha));
        gc.setLineWidth(1);

        for (double i = boxX; i < boxX + width; i += 20) {
            gc.strokeLine(i, boxY, i, boxY + height);
        }
        for (double i = boxY; i < boxY + height; i += 20) {
            gc.strokeLine(boxX, i, boxX + width, i);
        }
    }

    private void renderScanlines(GraphicsContext gc, double boxX, double boxY, double width, double height, double alpha) {
        gc.setStroke(colorPrimary.deriveColor(0, 1, 1, 0.03 * alpha));
        gc.setLineWidth(1);

        for (double i = 0; i < height; i += 3) {
            gc.strokeLine(boxX, boxY + i, boxX + width, boxY + i);
        }
    }
}