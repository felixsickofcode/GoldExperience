package vnu.uet.goldexperience.manager;

import java.util.ArrayList;
import java.util.List;

public class GameSession {
    private static GameSession instance;

    private int currentChapter = 1;
    private int currentLevel = 1;

    private int lives = 1;
    private boolean recentLifeLost = false;
    private static final int MAX_LIVES = 3;
    private int score = 0;

    private final List<GameSessionListener> listeners = new ArrayList<>();

    private GameSession() {}

    public enum GameMode {
        STORY,
        ENDLESS
    }
    public enum HitSide {
        TOP,
        LEFT,
        RIGHT
    }

    private GameMode mode = GameMode.STORY;

    public void setMode(GameMode mode) {
        this.mode = mode;
    }

    public GameMode getMode() {
        return mode;
    }

    //OBSERVER + SINGLETON
    public static GameSession getInstance() {
        if (instance == null) {
            instance = new GameSession();
        }
        return instance;
    }

    public interface GameSessionListener {
        void onChapterChanged(int newChapter);
        void onLevelChanged(int newLevel);
        void onBallHitWall(HitSide hitSide);
    }

    public void addListener(GameSessionListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(GameSessionListener listener) {
        listeners.remove(listener);
    }

    public int getLives() {
        return lives;
    }

    public boolean hasRecentlyLostLife() {
        return recentLifeLost;
    }

    public void clearRecentLifeFlag() {
        recentLifeLost = false;
    }

    public void loseLife() {
        if (lives > 0) {
            lives--;
            recentLifeLost = true;
            System.out.println("Lives remaining: " + lives);
        }
    }

    public void resetLives() {
        lives = MAX_LIVES;
        score = 0;
    }

    public boolean stillAlive() {
        return lives > 0;
    }

    public void addLife() {
        if (lives < MAX_LIVES) {
            lives++;
        }
    }

    public int getScore() {
        return score;
    }

    public void addScore(int points) {
        score += points;
    }

    public void resetScore() {
        score = 0;
    }
    private void notifyChapterChanged() {
        for (GameSessionListener listener : listeners) {
            listener.onChapterChanged(currentChapter);
        }
    }

    private void notifyLevelChanged() {
        for (GameSessionListener listener : listeners) {
            listener.onLevelChanged(currentLevel);
        }
    }

    public void notifyBallHitWall(HitSide hitSide) {
        for (GameSessionListener listener : listeners) {
            listener.onBallHitWall(hitSide);
        }
    }

    public void setChapterAndLevel(int chapter, int level) {
        boolean chapterChanged = this.currentChapter != chapter;
        boolean levelChanged = this.currentLevel != level;

        this.currentChapter = chapter;
        this.currentLevel = level;

        if (chapterChanged) notifyChapterChanged();
        if (levelChanged) notifyLevelChanged();
    }

    public void setChapter(int chapter) {
        if (this.currentChapter != chapter) {
            this.currentChapter = chapter;
            notifyChapterChanged();
        }
    }

    public void setLevel(int level) {
        if (this.currentLevel != level) {
            this.currentLevel = level;
            notifyLevelChanged();
        }
    }

    public String getLevelFileName() {
        return "level" + getLevelNumber() + ".json";
    }

    public int getLevelNumber() {
        return (currentChapter - 1) * 5 + currentLevel;
    }

    public int getCurrentChapter() {
        return currentChapter;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public boolean nextLevel() {
        if (currentLevel < 5) {
            setLevel(currentLevel + 1);
            return true;
        } else if (currentChapter < 5) {
            setChapter(currentChapter + 1);
            setLevel(1);
            return true;
        }
        return false;
    }

}