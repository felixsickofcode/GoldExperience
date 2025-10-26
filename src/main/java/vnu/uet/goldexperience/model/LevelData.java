package vnu.uet.goldexperience.model;

import java.util.List;
import java.util.Map;

public class LevelData {
    private int levelNumber;
    private Map<String, String> key;
    private List<String> layout;
    private Map<String, Map<String, Double>> properties;
    // Getters
    public int getLevelNumber() { return levelNumber; }
    public Map<String, String> getKey() { return key; }
    public List<String> getLayout() { return layout; }
    public Map<String, Map<String, Double>> getProperties() { return properties; }
}