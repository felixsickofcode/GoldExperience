package vnu.uet.goldexperience.model;

import java.util.List;
import java.util.Map;

public class LevelData {
    private int levelNumber;
    private Map<String, String> key;
    private List<String> layout;

    // Getters
    public int getLevelNumber() { return levelNumber; }
    public Map<String, String> getKey() { return key; }
    public List<String> getLayout() { return layout; }
}