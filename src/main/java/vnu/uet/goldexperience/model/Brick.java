package vnu.uet.goldexperience.model;

import javafx.scene.canvas.GraphicsContext;
import vnu.uet.goldexperience.manager.AssetsManager;
import vnu.uet.goldexperience.effect.ParticleEffect;


public class Brick extends GameObject {
    protected int hitPoints;
    protected ParticleEffect breakEffect;
    protected boolean playingBreakEffect;

    public Brick(double x, double y, double width, double height, int hitPoints) {
        super(x, y, width, height);
        this.hitPoints = hitPoints;
        this.image = AssetsManager.bricks.getFirst();
    }

    public void takeHit() {
        hitPoints--;
        this.image = AssetsManager.bricks.get(0);
        if (isDestroyed() && !playingBreakEffect) {
            breakEffect = new ParticleEffect(this);
            playingBreakEffect = true;
            System.out.println("Tạo hiệu ứng vỡ tại: " + x + ", " + y); // Debug
        }
    }

    public boolean isDestroyed() {
        return hitPoints <= 0;
    }

    public boolean canBeRemoved() {
        return isDestroyed() && (breakEffect == null || breakEffect.isFinished());
    }

    @Override
    public void update(double deltaTime) {
        if (playingBreakEffect && breakEffect != null) {
            breakEffect.update(deltaTime);
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!isDestroyed() && image != null) {
            gc.drawImage(image, x, y);
        } else if (playingBreakEffect && breakEffect != null) {
            breakEffect.render(gc);
        }
    }
}
