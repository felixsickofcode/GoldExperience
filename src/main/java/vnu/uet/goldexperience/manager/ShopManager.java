package vnu.uet.goldexperience.manager;

import vnu.uet.goldexperience.model.ShopItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ShopManager {
    private static ShopManager instance;
    private final List<ShopItem> allItems;
    private ShopListener listener;

    private ShopManager() {
        this.allItems = new ArrayList<>();
        initializeShopItems();
        syncWithGlobalData();
    }

    public static ShopManager getInstance() {
        if (instance == null) {
            instance = new ShopManager();
        }
        return instance;
    }

    /**
     * Interface để notify UI khi có thay đổi
     */
    public interface ShopListener {
        void onPurchaseSuccess(ShopItem item);

        void onPurchaseFailed(String reason);

        void onItemSelected(ShopItem item);

        void onPointsChanged(int newPoints);
    }

    public void setListener(ShopListener listener) {
        this.listener = listener;
    }

    /**
     * tao item o day
     */
    private void initializeShopItems() {
        // Paddle Skins (5 total, 1 default + 4 purchasable)
        allItems.add(new ShopItem(
                "paddle_default",
                "Classic Paddle",
                "The original paddle design",
                0,
                ShopItem.ShopItemType.PADDLE_SKIN,
                "/images/paddle4_1.png"
        ));

        allItems.add(new ShopItem(
                "paddle_bloody",
                "Bloody Paddle",
                "Acquired with blood and tears",
                500,
                ShopItem.ShopItemType.PADDLE_SKIN,
                "/images/paddle4_2.png"
        ));

        allItems.add(new ShopItem(
                "paddle_neon",
                "Neon Paddle",
                "Silver neon style paddle",
                750,
                ShopItem.ShopItemType.PADDLE_SKIN,
                "/images/paddle4_3.png"
        ));

        allItems.add(new ShopItem(
                "paddle_slime",
                "Slime Paddle",
                "Helping ball stick better",
                1000,
                ShopItem.ShopItemType.PADDLE_SKIN,
                "/images/paddle4_4.png"
        ));

        allItems.add(new ShopItem(
                "paddle_alien",
                "Alien Paddle",
                "Gain through the travel to Earth-15",
                1500,
                ShopItem.ShopItemType.PADDLE_SKIN,
                "/images/paddle4_5.png"
        ));

        allItems.add(new ShopItem(
                "ball_trail",
                "Trail Effect",
                "Blazing trail behind the ball",
                500,
                ShopItem.ShopItemType.BALL_EFFECT,
                "/images/balltrail.png"
        ));

        allItems.add(new ShopItem(
                "ball_bubble",
                "Bubble Effect",
                "Generating bubble at the end",
                900,
                ShopItem.ShopItemType.BALL_EFFECT,
                "/images/bubble.png"
        ));

        allItems.add(new ShopItem(
                "ball_friction",
                "Friction Effect",
                "Travelling at speed of light",
                1800,
                ShopItem.ShopItemType.BALL_EFFECT,
                "/images/friction.png"
        ));
    }

    /**
     * globaldata
     */
    public void syncWithGlobalData() {
        GlobalGameData globalData = GameDataManager.getGlobalData();

        for (ShopItem item : allItems) {
            if (item.isPaddleSkin()) {
                item.setOwned(globalData.hasPaddleSkin(item.getId()));
                item.setSelected(item.getId().equals(globalData.getSelectedPaddleSkin()));
            } else if (item.isBallEffect()) {
                item.setOwned(globalData.hasBallEffect(item.getId()));
                item.setSelected(globalData.isBallEffectSelected(item.getId()));
            }
        }
    }

    /**
     * Mua item
     */
    public boolean purchaseItem(ShopItem item) {
        if (item == null) {
            notifyPurchaseFailed("Invalid item");
            return false;
        }

        if (item.isOwned()) {
            notifyPurchaseFailed("Already owned");
            return false;
        }

        int currentPoints = GameDataManager.getGlobalData().getTotalPoints();
        if (currentPoints < item.getPrice()) {
            notifyPurchaseFailed("Not enough points. Need " + item.getPrice() + ", have " + currentPoints);
            return false;
        }

        boolean success = false;

        if (item.isPaddleSkin()) {
            success = GameDataManager.purchasePaddleSkin(item.getId(), item.getPrice());
        } else if (item.isBallEffect()) {
            success = GameDataManager.purchaseBallEffect(item.getId(), item.getPrice());
        }

        if (success) {
            item.setOwned(true);
            notifyPurchaseSuccess(item);
            notifyPointsChanged();
            return true;
        } else {
            notifyPurchaseFailed("Purchase failed");
            return false;
        }
    }

    /**
     * Select paddle skin
     */
    public boolean selectPaddleSkin(ShopItem item) {
        if (item == null || !item.isPaddleSkin()) {
            return false;
        }

        if (!item.isOwned()) {
            return false;
        }

        if (item.isSelected()) {
            return true; // Already selected
        }

        // Deselect all paddles
        for (ShopItem paddle : getPaddleSkins()) {
            paddle.setSelected(false);
        }

        // Select this paddle
        item.setSelected(true);
        GameDataManager.selectPaddleSkin(item.getId());
        notifyItemSelected(item);

        return true;
    }

    public void selectBallEffect(ShopItem item) {
        String id = item.getId();

        if (!GameDataManager.getGlobalData().hasBallEffect(id)) {
            notifyPurchaseFailed("You don't own this effect!");
            return;
        }

        if (GameDataManager.isBallEffectSelected(id)) {
            GameDataManager.deselectBallEffect(id);
            item.setSelected(false);
        } else {
            GameDataManager.selectBallEffect(id);
            item.setSelected(true);
        }

        syncWithGlobalData();
        notifyItemSelected(item);
    }

    public List<ShopItem> getAllItems() {
        return new ArrayList<>(allItems);
    }

    public List<ShopItem> getPaddleSkins() {
        return allItems.stream()
                .filter(ShopItem::isPaddleSkin)
                .collect(Collectors.toList());
    }

    public List<ShopItem> getBallEffects() {
        return allItems.stream()
                .filter(ShopItem::isBallEffect)
                .collect(Collectors.toList());
    }

    public List<ShopItem> getOwnedItems() {
        return allItems.stream()
                .filter(ShopItem::isOwned)
                .collect(Collectors.toList());
    }

    public List<ShopItem> getAvailableItems() {
        return allItems.stream()
                .filter(item -> !item.isOwned())
                .collect(Collectors.toList());
    }


    public ShopItem getSelectedPaddle() {
        return allItems.stream()
                .filter(item -> item.isPaddleSkin() && item.isSelected())
                .findFirst()
                .orElse(null);
    }

    public List<ShopItem> getOwnedBallEffects() {
        return allItems.stream()
                .filter(item -> item.isBallEffect() && item.isOwned())
                .collect(Collectors.toList());
    }

    public ShopItem findItemById(String id) {
        return allItems.stream()
                .filter(item -> item.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public int getCurrentPoints() {
        return GameDataManager.getGlobalData().getTotalPoints();
    }

    public boolean canAfford(ShopItem item) {
        return getCurrentPoints() >= item.getPrice();
    }

    public void refresh() {
        syncWithGlobalData();
    }

    // Notification methods
    private void notifyPurchaseSuccess(ShopItem item) {
        if (listener != null) {
            listener.onPurchaseSuccess(item);
        }
    }

    private void notifyPurchaseFailed(String reason) {
        if (listener != null) {
            listener.onPurchaseFailed(reason);
        }
    }

    private void notifyItemSelected(ShopItem item) {
        if (listener != null) {
            listener.onItemSelected(item);
        }
    }

    private void notifyPointsChanged() {
        if (listener != null) {
            listener.onPointsChanged(getCurrentPoints());
        }
    }

    public ShopStatistics getStatistics() {
        int totalItems = allItems.size();
        int ownedItems = (int) allItems.stream().filter(ShopItem::isOwned).count();
        int paddlesOwned = (int) getPaddleSkins().stream().filter(ShopItem::isOwned).count();
        int effectsOwned = (int) getBallEffects().stream().filter(ShopItem::isOwned).count();

        return new ShopStatistics(totalItems, ownedItems, paddlesOwned, effectsOwned);
    }

    public static class ShopStatistics {
        public final int totalItems;
        public final int ownedItems;
        public final int paddlesOwned;
        public final int effectsOwned;

        public ShopStatistics(int totalItems, int ownedItems, int paddlesOwned, int effectsOwned) {
            this.totalItems = totalItems;
            this.ownedItems = ownedItems;
            this.paddlesOwned = paddlesOwned;
            this.effectsOwned = effectsOwned;
        }

        public int getCompletionPercentage() {
            return totalItems > 0 ? (ownedItems * 100 / totalItems) : 0;
        }

        @Override
        public String toString() {
            return String.format("Shop Progress: %d/%d items (%d%%) - Paddles: %d/5, Effects: %d/5",
                    ownedItems, totalItems, getCompletionPercentage(), paddlesOwned, effectsOwned);
        }
    }
}