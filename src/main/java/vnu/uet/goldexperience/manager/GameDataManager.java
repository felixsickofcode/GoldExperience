package vnu.uet.goldexperience.manager;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Manager quản lý save/load - KHÔNG CẦN RuntimeTypeAdapter!
 * Vì có BrickFactory để recreate bricks
 */
public class GameDataManager {
    private static final String SAVE_DIR = "saves/";
    private static final String LEVELS_DIR = SAVE_DIR + "levels/";
    private static final String GLOBAL_DATA_FILE = SAVE_DIR + "gamedata.json";

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .setExclusionStrategies(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes field) {
                    // Skip tất cả JavaFX types
                    Class<?> fieldType = field.getDeclaredClass();
                    return Image.class.isAssignableFrom(fieldType)
                            || Color.class.isAssignableFrom(fieldType)||
                            java.util.Random.class.isAssignableFrom(fieldType);

                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return false;
                }
            })
            .create();

    private static GlobalGameData globalData;

    static {
        try {
            Files.createDirectories(Paths.get(SAVE_DIR));
            Files.createDirectories(Paths.get(LEVELS_DIR));
        } catch (IOException e) {
            System.err.println("Failed to create save directories: " + e.getMessage());
        }

        loadGlobalData();
    }

    // ========== GLOBAL DATA OPERATIONS ==========

    public static GlobalGameData getGlobalData() {
        if (globalData == null) {
            loadGlobalData();
        }
        return globalData;
    }

    public static void loadGlobalData() {

        try {
            File file = new File(GLOBAL_DATA_FILE);
            if (file.exists()) {
                System.out.println("   File exists, size: " + file.length() + " bytes");
                try (FileReader reader = new FileReader(file)) {
                    globalData = gson.fromJson(reader, GlobalGameData.class);
                    System.out.println("✅ Global data loaded from file:");
                    System.out.println("   Selected paddle: " + globalData.getSelectedPaddleSkin());
                    return;
                }
            } else {
                System.out.println("⚠️ File does not exist, creating new");
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to load global data: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Creating new GlobalGameData");
        globalData = new GlobalGameData();
        saveGlobalData();

    }

    public static boolean saveGlobalData() {
        if (globalData == null) {
            System.err.println("Cannot save null global data");
            return false;
        }

        try (FileWriter writer = new FileWriter(GLOBAL_DATA_FILE)) {
            gson.toJson(globalData, writer);
            writer.flush();
            System.out.println("Global data saved: " + globalData);
            return true;
        } catch (IOException e) {
            System.err.println("Failed to save global data: " + e.getMessage());
            return false;
        }
    }

    public static void addPointsToGlobal(int points) {
        globalData.addPoints(points);
        saveGlobalData();
    }

    public static boolean purchaseWithPoints(int cost) {
        if (globalData.spendPoints(cost)) {
            saveGlobalData();
            return true;
        }
        return false;
    }

    public static void setVolume(float volume) {
        globalData.setVolume(volume);
        saveGlobalData();
    }

    public static boolean purchaseBallEffect(String effect, int cost) {
        if (globalData.hasBallEffect(effect)) {
            System.out.println("Already owned: " + effect);
            return false;
        }

        if (globalData.spendPoints(cost)) {
            globalData.addBallEffect(effect);
            saveGlobalData();
            System.out.println("Purchased ball effect: " + effect);
            return true;
        }

        System.out.println("Not enough points to purchase: " + effect);
        return false;
    }

    public static boolean purchasePaddleSkin(String skin, int cost) {
        if (globalData.hasPaddleSkin(skin)) {
            System.out.println("Already owned: " + skin);
            return false;
        }

        if (globalData.spendPoints(cost)) {
            globalData.addPaddleSkin(skin);
            saveGlobalData();
            System.out.println("Purchased paddle skin: " + skin);
            return true;
        }

        System.out.println("Not enough points to purchase: " + skin);
        return false;
    }

    public static void selectPaddleSkin(String skin) {
        globalData.setSelectedPaddleSkin(skin);
        saveGlobalData();
    }

    public static void selectBallEffect(String effectId) {
        if (!globalData.hasBallEffect(effectId)) {
            System.out.println("Cannot select unowned effect: " + effectId);
            return;
        }

        if (!globalData.isBallEffectSelected(effectId)) {
            globalData.selectBallEffect(effectId);
            System.out.println("Selected ball effect: " + effectId);
            saveGlobalData();
        }
    }

    public static void deselectBallEffect(String effectId) {
        if (globalData.isBallEffectSelected(effectId)) {
            globalData.deselectBallEffect(effectId);
            System.out.println("Deselected ball effect: " + effectId);
            saveGlobalData();
        }
    }

    public static boolean isBallEffectSelected(String effectId) {
        return globalData.isBallEffectSelected(effectId);
    }

    public static void clearSelectedBallEffects() {
        globalData.clearSelectedBallEffects();
        System.out.println("Cleared all selected ball effects");
        saveGlobalData();
    }

    public static java.util.List<String> getSelectedBallEffects() {
        return globalData.getSelectedBallEffects();
    }

    // ========== LEVEL SAVE DATA OPERATIONS ==========

    public static boolean saveLevelProgress(int levelNumber, LevelSaveData saveData) {
        if (saveData == null) {
            System.err.println("Cannot save null level data");
            return false;
        }

        saveData.setLevelNumber(levelNumber);
        String fileName = LEVELS_DIR + "level" + levelNumber + ".json";

        try (FileWriter writer = new FileWriter(fileName)) {
            gson.toJson(saveData, writer);
            System.out.println("Level " + levelNumber + " saved: " + saveData);
            return true;
        } catch (IOException e) {
            System.err.println("Failed to save level " + levelNumber + ": " + e.getMessage());
            return false;
        }
    }

    public static LevelSaveData loadLevelProgress(int levelNumber) {
        String fileName = LEVELS_DIR + "level" + levelNumber + ".json";
        File file = new File(fileName);

        if (!file.exists()) {
            System.out.println("No save found for level " + levelNumber);
            return null;
        }

        try (FileReader reader = new FileReader(file)) {
            LevelSaveData data = gson.fromJson(reader, LevelSaveData.class);
            System.out.println("Level " + levelNumber + " loaded: " + data);
            return data;
        } catch (Exception e) {
            System.err.println("Failed to load level " + levelNumber + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static boolean hasLevelSave(int levelNumber) {
        String fileName = LEVELS_DIR + "level" + levelNumber + ".json";
        return new File(fileName).exists();
    }

    public static boolean deleteLevelSave(int levelNumber) {
        String fileName = LEVELS_DIR + "level" + levelNumber + ".json";
        File file = new File(fileName);

        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                System.out.println("Level " + levelNumber + " save deleted");
            }
            return deleted;
        }

        return false;
    }

    public static void deleteAllLevelSaves() {
        for (int i = 1; i <= 25; i++) {
            deleteLevelSave(i);
        }
        System.out.println("All level saves deleted");
    }

    public static void resetAllData() {
        globalData.reset();
        saveGlobalData();
        deleteAllLevelSaves();
        System.out.println("All game data reset");
    }

    public static void completeLevel(int levelNumber, int earnedPoints) {
        addPointsToGlobal(earnedPoints);
        deleteLevelSave(levelNumber);
        System.out.println("Level " + levelNumber + " completed! Points earned: " + earnedPoints);
    }
}