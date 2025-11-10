package vnu.uet.goldexperience.model;

/**
 * Model class cho items trong shop
 */
public class ShopItem {
    private final String id;
    private final String name;
    private final String description;
    private final int price;
    private final ShopItemType type;
    private final String iconPath;
    private boolean owned;
    private boolean selected;

    public enum ShopItemType {
        BALL_EFFECT,
        PADDLE_SKIN
    }

    public ShopItem(String id, String name, String description, int price, ShopItemType type, String iconPath) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.type = type;
        this.iconPath = iconPath;
        this.owned = false;
        this.selected = false;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public ShopItemType getType() {
        return type;
    }

    public String getIconPath() {
        return iconPath;
    }

    public boolean isOwned() {
        return owned;
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isBallEffect() {
        return type == ShopItemType.BALL_EFFECT;
    }

    public boolean isPaddleSkin() {
        return type == ShopItemType.PADDLE_SKIN;
    }

    // Setters
    public void setOwned(boolean owned) {
        this.owned = owned;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * status
     */
    public String getStatusText() {
        if (selected) {
            return "EQUIPPED";
        } else if (owned) {
            return "OWNED";
        } else {
            return price + " Points";
        }
    }

    public boolean canPurchase(int currentPoints) {
        return !owned && currentPoints >= price;
    }

    public boolean canSelect() {
        return owned && isPaddleSkin() && !selected || isBallEffect() && owned && !selected;
    }

    @Override
    public String toString() {
        return String.format("ShopItem{id='%s', name='%s', price=%d, type=%s, owned=%s, selected=%s}",
                id, name, price, type, owned, selected);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShopItem shopItem = (ShopItem) o;
        return id.equals(shopItem.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}