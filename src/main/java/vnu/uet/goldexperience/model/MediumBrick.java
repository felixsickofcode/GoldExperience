package vnu.uet.goldexperience.model;

import vnu.uet.goldexperience.effect.ParticleEffect;
import vnu.uet.goldexperience.manager.AssetsManager;

public class MediumBrick extends Brick{
    public MediumBrick(double x, double y, double width, double height, int hitPoints) {
        super(x, y, width, height, hitPoints);
        this.image=AssetsManager.bricks.get(1);
    }
    @Override
    public void takeHit() {
        hitPoints--;
        if(hitPoints==2)
            this.image=AssetsManager.bricks.get(2);
        if(hitPoints==1)
            this.image=AssetsManager.bricks.get(3);
        if (isDestroyed() && !playingBreakEffect) {
            breakEffect = new ParticleEffect(this);
            playingBreakEffect = true;
            System.out.println("Tạo hiệu ứng vỡ tại: " + x + ", " + y); // Debug
        }
    }
}
