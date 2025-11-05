package vnu.uet.goldexperience.view;

import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import vnu.uet.goldexperience.core.ChapterTheme;

public class GameUIComponents {

    private static final Font CYBER_FONT_SCORE = Font.font("Consolas", FontWeight.BOLD, 28);
    private static final double HP_WIDTH= 50.0;
    private static final double HP_HEIGHT= 10.0;

    private static Color getPrimaryColor(ChapterTheme theme) {
        switch (theme) {
            case CHAPTER_1_RUST:
                return ChapterTheme.NEON_ORANGE;
            case CHAPTER_3_VERDANT:
                return ChapterTheme.NEON_GREEN;
            case CHAPTER_4_CATHEDRAL:
                return ChapterTheme.GOLD;
            case CHAPTER_5_NEXUS:
                return ChapterTheme.PURE_WHITE;
            case CHAPTER_2_NEON:
            default:
                return ChapterTheme.NEON_CYAN;
        }
    }

    private static Color getSecondaryColor(ChapterTheme theme) {
        switch (theme) {
            case CHAPTER_1_RUST:
                return ChapterTheme.MEDIUM_GRAY;
            case CHAPTER_3_VERDANT:
                return ChapterTheme.EARTHY_YELLOW;
            case CHAPTER_4_CATHEDRAL:
                return ChapterTheme.GOLD;
            case CHAPTER_5_NEXUS:
                return ChapterTheme.PURE_WHITE;
            case CHAPTER_2_NEON:
            default:
                return ChapterTheme.NEON_PINK;
        }
    }

    public static void updateScoreLabel(Label label, int score) {
        if (label != null) {
            label.setText("SCORE: " + score);

            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(150), label);
            scaleUp.setToX(1.15);
            scaleUp.setToY(1.15);

            ScaleTransition scaleDown = new ScaleTransition(Duration.millis(150), label);
            scaleDown.setToX(1.0);
            scaleDown.setToY(1.0);

            SequentialTransition seq = new SequentialTransition(scaleUp, scaleDown);
            seq.play();

            Glow glow = new Glow(0.8);
            label.setEffect(glow);
        }
    }

    private static StackPane createHP(boolean filled, ChapterTheme theme) {
        StackPane hpContainer = new StackPane();
        hpContainer.setPrefSize(HP_WIDTH, HP_HEIGHT);
        hpContainer.setAlignment(Pos.CENTER);

        Rectangle hp = new Rectangle(HP_WIDTH, HP_HEIGHT);
        hp.setArcWidth(7);
        hp.setArcHeight(4);

        Color color = filled ? getPrimaryColor(theme) : Color.gray(0.3, 0.5);

        if (filled) {
            hp.setFill(color);
            hp.setStroke(color.brighter());
            hp.setStrokeWidth(1.0);

            DropShadow shadow = new DropShadow();
            shadow.setColor(color);
            shadow.setRadius(8);
            shadow.setSpread(0.3);
            hp.setEffect(shadow);
        } else {
            hp.setFill(Color.TRANSPARENT);
            hp.setStroke(Color.gray(0.5, 0.4));
            hp.setStrokeWidth(2);
        }

        hpContainer.getChildren().add(hp);
        return hpContainer;
    }

    public static void updateHPContainer(HBox container, int currentLives, int maxLives, ChapterTheme theme) {
        if (container == null) return;

        container.getChildren().clear();
        container.setAlignment(Pos.CENTER_LEFT);
        container.setSpacing(10);

        for (int i = 0; i < currentLives; i++) {
            StackPane hp = createHP(true, theme);
            container.getChildren().add(hp);
        }

        for (int i = currentLives; i < maxLives; i++) {
            StackPane hp = createHP(false, theme);
            container.getChildren().add(hp);
        }

    }

    public static void applyTheme(Label label, ChapterTheme theme) {
        if (label == null) return;

        Color color = getPrimaryColor(theme);

        label.setFont(CYBER_FONT_SCORE);

        label.setStyle(
                "-fx-text-fill: " + toHex(color) + ";" +
                        "-fx-font-weight: bold;"
        );
    }


    private static String toHex(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    public static void animateHPLoss(HBox container, ChapterTheme theme) {
        if (container == null || container.getChildren().isEmpty()) return;

        StackPane lastHphpContainer = null;

        for (int i = container.getChildren().size() - 1; i >= 0; i--) {
            if (!(container.getChildren().get(i) instanceof StackPane)) {
                continue;
            }

            StackPane sp = (StackPane) container.getChildren().get(i);
            if (sp.getChildren().isEmpty() || !(sp.getChildren().get(0) instanceof Rectangle)) {
                continue;
            }

            Rectangle rect = (Rectangle) sp.getChildren().get(0);
            if (rect.getFill() != Color.TRANSPARENT) {
                lastHphpContainer = sp;
                break;
            }
        }

        if (lastHphpContainer == null) return;

        final StackPane finalLastHphpContainer = lastHphpContainer;
        Rectangle lastHpSquare = (Rectangle) finalLastHphpContainer.getChildren().get(0);

        TranslateTransition shake = new TranslateTransition(Duration.millis(50), finalLastHphpContainer);
        shake.setByX(5);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);

        FadeTransition fade = new FadeTransition(Duration.millis(300), finalLastHphpContainer);
        fade.setToValue(0.3);

        ScaleTransition shrink = new ScaleTransition(Duration.millis(300), finalLastHphpContainer);
        shrink.setToX(0.5);
        shrink.setToY(0.5);

        ParallelTransition parallel = new ParallelTransition(fade, shrink);
        SequentialTransition seq = new SequentialTransition(shake, parallel);

        seq.setOnFinished(e -> {
            lastHpSquare.setFill(Color.TRANSPARENT);
            lastHpSquare.setStroke(Color.gray(0.5, 0.4));
            lastHpSquare.setStrokeWidth(2);
            lastHpSquare.setEffect(null);

            finalLastHphpContainer.setOpacity(1.0);
            finalLastHphpContainer.setScaleX(1.0);
            finalLastHphpContainer.setScaleY(1.0);
        });

        seq.play();
    }
}

