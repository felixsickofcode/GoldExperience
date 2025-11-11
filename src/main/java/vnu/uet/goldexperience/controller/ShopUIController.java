package vnu.uet.goldexperience.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import vnu.uet.goldexperience.core.GameEngine;
import vnu.uet.goldexperience.manager.SceneManager;
import vnu.uet.goldexperience.manager.ShopManager;
import vnu.uet.goldexperience.model.ShopItem;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ShopUIController implements Initializable, ShopManager.ShopListener {

    @FXML
    private Button backButton;
    @FXML
    private Label pointsLabel;
    @FXML
    private TabPane tabPane;
    @FXML
    private GridPane paddleGrid;
    @FXML
    private GridPane effectGrid;
    @FXML
    private Label statsLabel;
    @FXML
    private Label paddleStatsLabel;
    @FXML
    private Label effectStatsLabel;

    private ShopManager shopManager;
    private SceneManager sceneManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        shopManager = ShopManager.getInstance();
        shopManager.setListener(this);

        refreshShop();
    }

    /**
     * refresh
     */
    private void refreshShop() {
        shopManager.refresh();
        updatePointsDisplay();
        updateStatistics();
        populatePaddleGrid();
        populateEffectGrid();
    }

    private void updatePointsDisplay() {
        pointsLabel.setText(String.valueOf(shopManager.getCurrentPoints()));
    }

    private void updateStatistics() {
        ShopManager.ShopStatistics stats = shopManager.getStatistics();

        statsLabel.setText(String.format("Collection: %d/%d items (%d%%)",
                stats.ownedItems, stats.totalItems, stats.getCompletionPercentage()));

        paddleStatsLabel.setText(String.format("Paddles: %d/5", stats.paddlesOwned));
        effectStatsLabel.setText(String.format("Effects: %d/3", stats.effectsOwned));
    }

    /**
     * tao cac item paddle
     */
    private void populatePaddleGrid() {
        paddleGrid.getChildren().clear();
        List<ShopItem> paddles = shopManager.getPaddleSkins();

        int col = 0, row = 0;
        for (ShopItem paddle : paddles) {
            VBox itemCard = createItemCard(paddle);
            paddleGrid.add(itemCard, col, row);

            col++;
            if (col >= 3) {
                col = 0;
                row++;
            }
        }
    }

    /**
     * tao cac item balleffect
     */
    private void populateEffectGrid() {
        effectGrid.getChildren().clear();
        List<ShopItem> effects = shopManager.getBallEffects();

        int col = 0, row = 0;
        for (ShopItem effect : effects) {
            VBox itemCard = createItemCard(effect);
            effectGrid.add(itemCard, col, row);

            col++;
            if (col >= 3) {
                col = 0;
                row++;
            }
        }
    }

    /**
     * tao item trong day
     */
    private VBox createItemCard(ShopItem item) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().add("item-card");
        card.setPrefWidth(220);
        card.setPrefHeight(300);

        // Icon/Image
        ImageView icon = createItemIcon(item);

        // Name
        Label nameLabel = new Label(item.getName());
        nameLabel.setFont(Font.font("System Bold", 18));
        nameLabel.getStyleClass().add("item-name");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(200);
        nameLabel.setAlignment(Pos.CENTER);

        // Description
        Label descLabel = new Label(item.getDescription());
        descLabel.setFont(Font.font(12));
        descLabel.getStyleClass().add("item-description");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(200);
        descLabel.setAlignment(Pos.CENTER);

        // Status badge
        Label statusLabel = new Label(item.getStatusText());
        statusLabel.getStyleClass().add("item-status");

        if (item.isSelected()) {
            statusLabel.getStyleClass().add("status-equipped");
        } else if (item.isOwned()) {
            statusLabel.getStyleClass().add("status-owned");
        } else {
            statusLabel.getStyleClass().add("status-price");
        }

        // Action button
        Button actionButton = createActionButton(item);

        // Add spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        card.getChildren().addAll(icon, nameLabel, descLabel, spacer, statusLabel, actionButton);

        return card;
    }

    /**
     * icon
     */
    private ImageView createItemIcon(ShopItem item) {
        ImageView icon = new ImageView();
        icon.setFitWidth(100);
        icon.setFitHeight(100);
        icon.setPreserveRatio(true);

        try {
            Image image = new Image(getClass().getResourceAsStream(item.getIconPath()));
            icon.setImage(image);
        } catch (Exception e) {
            icon.setImage(null);
        }

        return icon;
    }


    /**
     * Tạo action button cho item
     */
    private Button createActionButton(ShopItem item) {
        Button button = new Button();
        button.setPrefWidth(180);

        if (item.isBallEffect() && item.isOwned()) {
            if (item.isSelected()) {
                button.setText("UNEQUIP");
                button.getStyleClass().addAll("action-button", "button-unequip");
            } else {
                button.setText("EQUIP");
                button.getStyleClass().addAll("action-button", "button-equip");
            }
            button.setOnAction(e -> handleEquip(item));

        } else if (item.isPaddleSkin() && item.isOwned()) {
            if (item.isSelected()) {
                button.setText("✓ EQUIPPED");
                button.getStyleClass().addAll("action-button", "button-equipped");
                button.setDisable(true);
            } else {
                button.setText("EQUIP");
                button.getStyleClass().addAll("action-button", "button-equip");
                button.setOnAction(e -> handleEquip(item));
            }

            // Chưa mua
        } else if (!item.isOwned()) {
            button.setText("BUY - " + item.getPrice() + " 💰");
            button.getStyleClass().addAll("action-button", "button-buy");
            button.setOnAction(e -> handlePurchase(item));

            if (!shopManager.canAfford(item)) {
                button.setDisable(true);
                button.getStyleClass().add("button-disabled");
            }
        }

        return button;
    }

    private void handlePurchase(ShopItem item) {
        shopManager.purchaseItem(item);
    }

    private void handleEquip(ShopItem item) {
        if (item.isPaddleSkin()) {
            shopManager.selectPaddleSkin(item);
        } else {
            shopManager.selectBallEffect(item);
        }
    }

    @FXML
    private void handleBack() {
        if (sceneManager != null) {
            sceneManager.switchTo("menu");
        }
    }


    @Override
    public void onPurchaseSuccess(ShopItem item) {
        showNotification("✅ Purchased: " + item.getName(), "success");
        refreshShop();
    }

    @Override
    public void onPurchaseFailed(String reason) {
        showNotification("❌ " + reason, "error");
    }

    @Override
    public void onItemSelected(ShopItem item) {
        String action = item.isSelected() ? "Equipped" : "Unequipped";
        showNotification("🎯 Equipped: " + item.getName(), "success");

        if (item.isPaddleSkin()) {
            refreshPaddleSkinInGame();
        } else if (item.isBallEffect()) {
            refreshBallEffectsInGame();
        }
        refreshShop();
    }
    private void refreshPaddleSkinInGame() {
        try {
            GameEngine engine = GameEngine.getInstance();
            if (engine != null) {
                engine.refreshPaddleSkin();
            }
        } catch (Exception e) {
            System.err.println("❌ Error refreshing paddle skin: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void refreshBallEffectsInGame() {
        try {
            GameEngine engine = GameEngine.getInstance();
            if (engine != null) {
                engine.refreshBallEffects();
            } else {
                System.out.println("💡 No active game instance. Effects will apply on next game.");
            }
        } catch (Exception e) {
            System.err.println("❌ Error refreshing ball effects: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onPointsChanged(int newPoints) {
        updatePointsDisplay();
    }

    /**
     * noti khi mua, equip,...
     */
    private void showNotification(String message, String type) {
        Alert alert = new Alert(
                type.equals("success") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR
        );
        alert.setTitle("Shop");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }
}