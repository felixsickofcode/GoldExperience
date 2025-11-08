package vnu.uet.goldexperience.model;

import javafx.scene.canvas.GraphicsContext;
import vnu.uet.goldexperience.core.Constants;
import vnu.uet.goldexperience.manager.SpriteManager;

public class SimplePowerUp extends PowerUp {

    private static final int FRAME_COUNT = 6;

    private final SpriteManager spriteLoader;

    public SimplePowerUp(double x, double y, PowerUpType type) {
        super(
                x, y, Constants.POWER_UP_ITEM_WIDTH, Constants.POWER_UP_ITEM_HEIGHT, type
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
            super.render(gc);
            return;
        }

        int frameIndex = spriteLoader.getCurrentFrame();
        double sx = frameIndex * Constants.POWER_UP_ITEM_WIDTH;
        double sy = spriteLoader.getRow() * Constants.POWER_UP_ITEM_HEIGHT;

        double dx = x;
        double dy = y;
        double dw = width;
        double dh = height;

        gc.drawImage(
                image, sx, sy,
                Constants.POWER_UP_ITEM_WIDTH,
                Constants.POWER_UP_ITEM_HEIGHT,
                dx, dy, dw, dh
        );
    }
}
