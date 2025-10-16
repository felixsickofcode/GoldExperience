package vnu.uet.goldexperience.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import vnu.uet.goldexperience.manager.AssetsManager;
import vnu.uet.goldexperience.manager.SpriteManager;

public class UnbreakableBrick extends Brick {
    private final SpriteManager spriteLoader;
    private boolean hitAnimating = false;;

    public UnbreakableBrick(double x, double y, double width, double height) {
        super(x, y, width, height, 100);
        image = AssetsManager.bricks.get(1);
        spriteLoader = new SpriteManager(6, 0, 10);
    }

    @Override
    public void takeHit() {
        if (!this.isDestroyed() && !hitAnimating) {
            hitAnimating = true;
            spriteLoader.start();
        }
    }



    @Override
    public void update(double deltaTime) {
        if (hitAnimating) {
            int prevFrame = spriteLoader.getCurrentFrame();
            spriteLoader.update(deltaTime);
            if (spriteLoader.getCurrentFrame() == 0&& prevFrame == spriteLoader.getFrameCount() - 1) {
                hitAnimating = false;
            }
        }
    }

    @Override
    public void render(GraphicsContext g) {
        if (!isDestroyed() && image != null) {
            int frame = spriteLoader.getCurrentFrame();
            int row = spriteLoader.getRow();

            double srcX = frame * width;
            double srcY = row * height;

            g.drawImage(
                    image,
                    srcX, srcY, width, height,
                    x, y, width, height
            );
        }
    }
}
