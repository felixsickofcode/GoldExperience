package vnu.uet.goldexperience.manager;

import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

public class PaddleImageHelper {

    private static final Map<String, Integer> SKIN_VARIANT_MAP = new HashMap<>();

    static {
        SKIN_VARIANT_MAP.put("paddle_default", 1);  // _1 variant
        SKIN_VARIANT_MAP.put("paddle_bloody", 2);    // _2 variant
        SKIN_VARIANT_MAP.put("paddle_neon", 3);      // _3 variant
        SKIN_VARIANT_MAP.put("paddle_slime", 4);   // _4 variant
        SKIN_VARIANT_MAP.put("paddle_alien", 5);   // _5 variant
    }

    public static int getImageIndex(int paddleSize, String skinId) {
        int skinVariant = SKIN_VARIANT_MAP.getOrDefault(skinId, 1);

        // AssetsManager.paddles layout:
        // Index 0-4: paddle2_1 to paddle2_5 (size 0)
        // Index 5-9: paddle3_1 to paddle3_5 (size 1)
        // Index 10-14: paddle4_1 to paddle4_5 (size 2)
        // Index 15-19: paddle5_1 to paddle5_5 (size 3)
        // Index 20-24: paddle6_1 to paddle6_5 (size 4)

        // size * 5 + (variant - 1)
        int index = paddleSize * 5 + (skinVariant - 1);

        // Clamp to valid range
        return Math.max(0, Math.min(24, index));
    }

    public static Image getImage(int paddleSize, String skinId) {
        // Nếu skinId null, lấy từ GlobalGameData
        if (skinId == null) {
            skinId = GameDataManager.getGlobalData().getSelectedPaddleSkin();
        }

        int index = getImageIndex(paddleSize, skinId);

        if (index >= 0 && index < AssetsManager.paddles.size()) {
            return AssetsManager.paddles.get(index);
        }
        return AssetsManager.paddles.get(10);
    }

    public static Image getImageWithCurrentSkin(int paddleSize) {
        String selectedSkin = GameDataManager.getGlobalData().getSelectedPaddleSkin();
        int variant = SKIN_VARIANT_MAP.getOrDefault(selectedSkin, 1);
        int index = paddleSize * 5 + (variant - 1);

        if (index >= 0 && index < AssetsManager.paddles.size()) {
            return AssetsManager.paddles.get(index);
        }
        return AssetsManager.paddles.get(10);
    }

    public static void debugPrintMappings() {
        System.out.println("=== PADDLE IMAGE MAPPINGS ===");
        String[] sizeNames = {"TINY(2)", "SMALL(3)", "MEDIUM(4)", "LARGE(5)", "BIG(6)"};

        for (Map.Entry<String, Integer> entry : SKIN_VARIANT_MAP.entrySet()) {
            System.out.println("\n" + entry.getKey() + " (variant " + entry.getValue() + "):");
            for (int size = 0; size < 5; size++) {
                int index = getImageIndex(size, entry.getKey());
                System.out.println("  Size " + size + " " + sizeNames[size] + " -> index " + index);
            }
        }
        System.out.println("\nCurrent selected: " + GameDataManager.getGlobalData().getSelectedPaddleSkin());
        System.out.println("=============================");
    }
}