package vnu.uet.goldexperience.model;

import javafx.scene.canvas.GraphicsContext;
import vnu.uet.goldexperience.core.Constants;
import vnu.uet.goldexperience.manager.SpriteManager;

public class SimplePowerUp extends PowerUp {

    // Spritesheet details for EXTEND: 6 frames, each 48x33, arranged horizontally on row 0
    private static final int FRAME_COUNT = 6;
    private static final int FRAME_WIDTH = 48;
    private static final int FRAME_HEIGHT = 33;

    private final SpriteManager spriteLoader;

    public SimplePowerUp(double x, double y, PowerUpType type) {
        super(
                x, y, Constants.POWER_UP_ITEM_SIZE, Constants.POWER_UP_ITEM_SIZE, type
        );
        spriteLoader = new SpriteManager(FRAME_COUNT, 0, 10); // 10 FPS animation
        spriteLoader.start();
        setDy(Constants.POWER_UP_DROP_SPEED);
    }

    @Override
    public void update(double deltaTime) {
        // Advance animation and fall down
        spriteLoader.update(deltaTime);
        super.update(deltaTime);
    }

    @Override
    public void render(GraphicsContext gc) {
        if (image == null) {
            // Fallback to base rendering (placeholder circle)
            super.render(gc);
            return;
        }

        int frameIndex = spriteLoader.getCurrentFrame();
        double sx = frameIndex * FRAME_WIDTH;
        double sy = spriteLoader.getRow() * FRAME_HEIGHT;
        double sw = FRAME_WIDTH;
        double sh = FRAME_HEIGHT;

        double dx = x;
        double dy = y;
        double dw = width;
        double dh = height;

        gc.drawImage(image, sx, sy, sw, sh, dx, dy, dw, dh);
    }
}
