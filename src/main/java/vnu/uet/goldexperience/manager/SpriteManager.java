package vnu.uet.goldexperience.manager;

public class SpriteManager {
    private int frameCount;
    private int row;
    private double frameDuration;
    private double timeSinceLastFrame;
    private int currentFrame = 0;

    public SpriteManager(int frameCount, int row, int frameSpeed) {
        this.frameCount = frameCount;
        this.row = row;
        this.frameDuration = 1.0 / frameSpeed;
    }

    public void start() {
        currentFrame = 0;
        timeSinceLastFrame = 0;
    }

    public void reset() {
        currentFrame = 0;
        timeSinceLastFrame = 0;
    }

    public void update(double deltaTime) {
        timeSinceLastFrame += deltaTime;
        if (timeSinceLastFrame >= frameDuration) {
            timeSinceLastFrame -= frameDuration;
            currentFrame = (currentFrame + 1) % frameCount;
        }
    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    public int getRow() {
        return row;
    }

    public int getFrameCount() {
        return frameCount;
    }

    public double getTimeSinceLastFrame() {
        return timeSinceLastFrame;
    }
}
