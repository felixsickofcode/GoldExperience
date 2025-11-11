package vnu.uet.goldexperience.view;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import vnu.uet.goldexperience.core.ChapterTheme;
import vnu.uet.goldexperience.database.PlayerDatabase;
import vnu.uet.goldexperience.manager.GameSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ScoreboardPanel extends VBox implements GameSession.GameSessionListener {

    private Label titleLabel;
    private List<ScoreEntry> scoreEntries;
    private AnimationTimer updateTimer;
    private long lastUpdateTime = 0;
    private final long UPDATE_INTERVAL = 250_000_000L;

    private Color primaryColor;
    private Color secondaryColor;
    private Color backgroundColor;
    private Color textColor;

    private double glowPulse = 0;

    private int currentChapter = 1;
    private int currentLevel = 1;

    public ScoreboardPanel() {
        super(8);
        setupUI();
        setupUpdateTimer();
        updateScoreboard();
        GameSession.getInstance().addListener(this);
    }

    private void setupUI() {
        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(15, 15, 15, 15));
        setMaxWidth(280);
        setMinWidth(280);

        titleLabel = new Label("TOP PLAYERS");
        titleLabel.setFont(Font.font("Cynosure Straight", FontWeight.BOLD, 30));
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setMaxWidth(Double.MAX_VALUE);

        getChildren().add(titleLabel);

        scoreEntries = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ScoreEntry entry = new ScoreEntry(i + 1);
            scoreEntries.add(entry);
            getChildren().add(entry);
        }

        applyTheme(ChapterTheme.ORIGINAL);
    }

    private void setupUpdateTimer() {
        updateTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                glowPulse += 0.08;
                updateGlowEffect();

                if (now - lastUpdateTime >= UPDATE_INTERVAL) {
                    updateScoreboard();
                    lastUpdateTime = now;
                }
            }
        };
        updateTimer.start();
    }

    private void updateGlowEffect() {
        double pulse = 0.3 + Math.sin(glowPulse) * 0.4;
        String glowStyle = String.format(Locale.US,
                "-fx-effect: dropshadow(gaussian, %s, 15, %.2f, 0, 0);",
                toRGBA(primaryColor, pulse * 0.8),
                pulse
        );

        setStyle("-fx-background-color: " + toRGBA(backgroundColor, 0.85) + "; " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: " + toRGBA(primaryColor, 0.8) + "; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 10; " +
                glowStyle);
    }

    public void updateScoreboard() {
        try {
            currentChapter = GameSession.getInstance().getCurrentChapter();
            currentLevel = GameSession.getInstance().getCurrentLevel();

            List<PlayerDatabase.PlayerScore> topPlayers =
                    PlayerDatabase.getInstance().getTopPlayers(currentChapter, currentLevel, 10);

            for (int i = 0; i < scoreEntries.size(); i++) {
                if (i < topPlayers.size()) {
                    PlayerDatabase.PlayerScore player = topPlayers.get(i);
                    scoreEntries.get(i).setData(player.getName(), player.getScore());
                    scoreEntries.get(i).setVisible(true);
                } else {
                    scoreEntries.get(i).setData("---", 0);
                    scoreEntries.get(i).setVisible(true);
                }
            }
        } catch (Exception e) {
            System.err.println("Error updating scoreboard: " + e.getMessage());
        }
    }

    public void updateScoreboardForLevel(int chapter, int level) {
        this.currentChapter = chapter;
        this.currentLevel = level;
        updateScoreboard();
    }

    public void applyTheme(ChapterTheme theme) {
        switch (theme) {
            case CHAPTER_1_RUST:
                primaryColor = ChapterTheme.NEON_ORANGE;
                secondaryColor = ChapterTheme.MEDIUM_GRAY;
                backgroundColor = Color.rgb(30, 25, 20);
                textColor = Color.rgb(255, 200, 150);
                break;
            case CHAPTER_2_NEON:
                primaryColor = ChapterTheme.NEON_CYAN;
                secondaryColor = ChapterTheme.NEON_PINK;
                backgroundColor = Color.rgb(10, 5, 20);
                textColor = Color.rgb(0, 255, 255);
                break;
            case CHAPTER_3_VERDANT:
                primaryColor = ChapterTheme.NEON_GREEN;
                secondaryColor = ChapterTheme.EARTHY_YELLOW;
                backgroundColor = Color.rgb(10, 26, 10);
                textColor = Color.rgb(150, 255, 150);
                break;
            case CHAPTER_4_CATHEDRAL:
                primaryColor = ChapterTheme.GOLD;
                secondaryColor = ChapterTheme.GOLD;
                backgroundColor = Color.rgb(26, 10, 26);
                textColor = Color.rgb(255, 215, 0);
                break;
            case CHAPTER_5_NEXUS:
                primaryColor = ChapterTheme.PURE_WHITE;
                secondaryColor = ChapterTheme.PURE_WHITE;
                backgroundColor = Color.rgb(16, 16, 32);
                textColor = Color.rgb(255, 255, 255);
                break;
            case ORIGINAL:
            default:
                primaryColor = ChapterTheme.NEON_PINK;
                secondaryColor = ChapterTheme.NEON_CYAN;
                backgroundColor = Color.rgb(10, 5, 20);
                textColor = Color.rgb(255, 100, 200);
                break;
        }

        titleLabel.setTextFill(primaryColor);

        for (ScoreEntry entry : scoreEntries) {
            entry.updateColors(primaryColor, secondaryColor, textColor);
        }

        updateGlowEffect();
    }

    private String toRGBA(Color color, double alpha) {
        return String.format(Locale.US, "rgba(%d, %d, %d, %.2f)",
                (int)(color.getRed() * 255),
                (int)(color.getGreen() * 255),
                (int)(color.getBlue() * 255),
                alpha);
    }

    public void stop() {
        if (updateTimer != null) {
            updateTimer.stop();
        }
        GameSession.getInstance().removeListener(this);
    }

    @Override
    public void onChapterChanged(int newChapter) {
        updateScoreboard();
    }

    @Override
    public void onLevelChanged(int newLevel) {
        updateScoreboard();
    }

    @Override
    public void onBallHitWall(GameSession.HitSide side) {}

    private class ScoreEntry extends HBox {
        private Label rankLabel;
        private Label nameLabel;
        private Label scoreLabel;
        private int rank;

        public ScoreEntry(int rank) {
            super(10);
            this.rank = rank;
            setAlignment(Pos.CENTER_LEFT);
            setPadding(new Insets(4, 8, 4, 8));
            setMaxWidth(Double.MAX_VALUE);

            rankLabel = new Label(rank + ".");
            rankLabel.setFont(Font.font("Cynosure Straight", FontWeight.BOLD, 20));
            rankLabel.setMinWidth(30);

            nameLabel = new Label("---");
            nameLabel.setFont(Font.font("Cynosure Straight", FontWeight.NORMAL, 19));
            nameLabel.setMaxWidth(110);
            nameLabel.setMinWidth(110);

            scoreLabel = new Label("0");
            scoreLabel.setFont(Font.font("Cynosure Straight", FontWeight.BOLD, 19));
            scoreLabel.setAlignment(Pos.CENTER_RIGHT);
            scoreLabel.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(scoreLabel, javafx.scene.layout.Priority.ALWAYS);

            getChildren().addAll(rankLabel, nameLabel, scoreLabel);
        }

        public void setData(String name, int score) {
            nameLabel.setText(name.length() > 15 ? name.substring(0, 15) : name);
            scoreLabel.setText(String.valueOf(score));

            if (rank <= 3 && !name.equals("---")) {
                setStyle("-fx-background-color: " + toRGBA(primaryColor, 0.15) + "; " +
                        "-fx-background-radius: 5;");
            } else {
                setStyle("-fx-background-color: transparent;");
            }
        }

        public void updateColors(Color primary, Color secondary, Color text) {
            if (rank == 1) {
                rankLabel.setTextFill(primary);
                nameLabel.setTextFill(primary);
                scoreLabel.setTextFill(primary);
            } else if (rank == 2) {
                rankLabel.setTextFill(secondary);
                nameLabel.setTextFill(text.deriveColor(0, 1, 0.9, 1));
                scoreLabel.setTextFill(secondary);
            } else if (rank == 3) {
                rankLabel.setTextFill(secondary.deriveColor(0, 0.7, 0.7, 1));
                nameLabel.setTextFill(text.deriveColor(0, 1, 0.8, 1));
                scoreLabel.setTextFill(secondary.deriveColor(0, 0.7, 0.7, 1));
            } else {
                rankLabel.setTextFill(text.deriveColor(0, 1, 0.7, 1));
                nameLabel.setTextFill(text.deriveColor(0, 1, 0.6, 1));
                scoreLabel.setTextFill(text.deriveColor(0, 1, 0.7, 1));
            }
        }
    }
}
