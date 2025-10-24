package vnu.uet.goldexperience.manager;

import java.util.ArrayList;
import java.util.List;

public class GameSession {
    private static GameSession instance;

    private int currentChapter = 1;
    private int currentLevel = 1;

    private final List<GameSessionListener> listeners = new ArrayList<>();

    private GameSession() {}

    //OBSERVER
    public static GameSession getInstance() {
        if (instance == null) {
            instance = new GameSession();
        }
        return instance;
    }

    public interface GameSessionListener {
        void onChapterChanged(int newChapter);
        void onLevelChanged(int newLevel);
    }

    public void addListener(GameSessionListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(GameSessionListener listener) {
        listeners.remove(listener);
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