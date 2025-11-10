package vnu.uet.goldexperience.manager;

import java.util.ArrayList;
import java.util.List;

/**
 * Class lưu dữ liệu toàn cục của game
 */
public class GlobalGameData {
    private int totalPoints;
    private float volume;
    private List<String> ownedBallEffects;
    private List<String> ownedPaddleSkins;
    private String selectedPaddleSkin;
    private List<String> selectedBallEffects;

    // Default values
    private static final float DEFAULT_VOLUME = 0.7f;
    private static final String DEFAULT_PADDLE_SKIN = "paddle_default";

    public GlobalGameData() {
        this.totalPoints = 0;
        this.volume = DEFAULT_VOLUME;
        this.ownedBallEffects = new ArrayList<>();
        this.ownedPaddleSkins = new ArrayList<>();
        this.selectedBallEffects = new ArrayList<>();
        this.ownedPaddleSkins.add(DEFAULT_PADDLE_SKIN);
        this.selectedPaddleSkin = DEFAULT_PADDLE_SKIN;
    }

    // Getters
    public int getTotalPoints() {
        return totalPoints;
    }

    public float getVolume() {
        return volume;
    }

    public List<String> getOwnedBallEffects() {
        return new ArrayList<>(ownedBallEffects);
    }

    public List<String> getSelectedBallEffects() {
        return new ArrayList<>(selectedBallEffects);
    }


    public List<String> getOwnedPaddleSkins() {
        return new ArrayList<>(ownedPaddleSkins);
    }

    public String getSelectedPaddleSkin() {
        return selectedPaddleSkin;
    }

    // Setters
    public void setTotalPoints(int totalPoints) {
        this.totalPoints = Math.max(0, totalPoints);
    }

    public void setVolume(float volume) {
        this.volume = Math.max(0f, Math.min(1f, volume));
    }

    public void setSelectedPaddleSkin(String skin) {
        System.out.println("  🔧 [GlobalGameData] setSelectedPaddleSkin: " + skin);
        System.out.println("  🔍 Checking if owned: " + ownedPaddleSkins.contains(skin));

        if (ownedPaddleSkins.contains(skin)) {
            this.selectedPaddleSkin = skin;
            System.out.println("  ✅ Skin set successfully");
        } else {
            System.out.println("  ❌ Skin NOT in owned list! Adding it...");
            ownedPaddleSkins.add(skin);
            this.selectedPaddleSkin = skin;
            System.out.println("  ✅ Skin added and set");
        }
    }

    // Point operations
    public void addPoints(int points) {
        this.totalPoints += points;
    }

    public boolean spendPoints(int points) {
        if (this.totalPoints >= points) {
            this.totalPoints -= points;
            return true;
        }
        return false;
    }

    // Ball effect operations
    public void addBallEffect(String effect) {
        if (!ownedBallEffects.contains(effect)) {
            ownedBallEffects.add(effect);
        }
    }

    public boolean hasBallEffect(String effect) {
        return ownedBallEffects.contains(effect);
    }

    public void selectBallEffect(String effect) {
        if (ownedBallEffects.contains(effect) && !selectedBallEffects.contains(effect)) {
            selectedBallEffects.add(effect);
        }
    }
    public void clearSelectedBallEffects() {
        selectedBallEffects.clear();
    }

    public boolean isBallEffectSelected(String effect) {
        return selectedBallEffects.contains(effect);
    }

    public void deselectBallEffect(String effect) {
        selectedBallEffects.remove(effect);
    }

    // Paddle skin operations
    public void addPaddleSkin(String skin) {
        if (!ownedPaddleSkins.contains(skin)) {
            ownedPaddleSkins.add(skin);
        }
    }

    public boolean hasPaddleSkin(String skin) {
        return ownedPaddleSkins.contains(skin);
    }

    // Utility
    public void reset() {
        this.totalPoints = 0;
        this.volume = DEFAULT_VOLUME;
        this.ownedBallEffects.clear();
        this.ownedPaddleSkins.clear();
        this.selectedBallEffects.clear();
        this.ownedPaddleSkins.add(DEFAULT_PADDLE_SKIN);
        this.selectedPaddleSkin = DEFAULT_PADDLE_SKIN;
    }

    @Override
    public String toString() {
        return String.format(
                "GlobalGameData{points=%d, volume=%.2f, ballEffects=%d, selectedBallEffects=%d, paddleSkins=%d, selectedPaddle=%s}",
                totalPoints, volume, ownedBallEffects.size(), selectedBallEffects.size(),
                ownedPaddleSkins.size(), selectedPaddleSkin);
    }
}