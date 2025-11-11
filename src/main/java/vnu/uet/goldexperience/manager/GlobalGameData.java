package vnu.uet.goldexperience.manager;

import java.util.ArrayList;
import java.util.List;

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

    public int getTotalPoints() {
        return totalPoints;
    }

    public List<String> getSelectedBallEffects() {
        return new ArrayList<>(selectedBallEffects);
    }

    public String getSelectedPaddleSkin() {
        return selectedPaddleSkin;
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

    public void addPaddleSkin(String skin) {
        if (!ownedPaddleSkins.contains(skin)) {
            ownedPaddleSkins.add(skin);
        }
    }

    public boolean hasPaddleSkin(String skin) {
        return ownedPaddleSkins.contains(skin);
    }

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