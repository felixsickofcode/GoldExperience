package vnu.uet.goldexperience.manager;

import javafx.scene.media.AudioClip;

public class SoundManager {
    public static AudioClip hitPaddleSound;
    public static AudioClip hitWallSound;
    public static AudioClip hitBrickSound;
    public static AudioClip loseSound;
    public static AudioClip clickSound;
    public static AudioClip breakBrickSound;
    public static AudioClip explosionSound;
    public static void loadSound() {
        loadSounds();
        clickSound.play(0.0);
        hitWallSound.play(0.0);
        hitBrickSound.play(0.0);
        hitPaddleSound.play(0.0);
        loseSound.play(0.0);
        breakBrickSound.play(0.0);
        explosionSound.play(0.0);
    }
    private static void loadSounds() {
        try {
            hitWallSound = new AudioClip(AssetsManager.class.getResource("/sounds/hit_wall.wav").toExternalForm());
            hitBrickSound = new AudioClip(AssetsManager.class.getResource("/sounds/hit_brick.wav").toExternalForm());
            hitPaddleSound = new AudioClip(AssetsManager.class.getResource("/sounds/hit_paddle.wav").toExternalForm());
            loseSound = new AudioClip(AssetsManager.class.getResource("/sounds/lose.mp3").toExternalForm());
            clickSound = new AudioClip(AssetsManager.class.getResource("/sounds/click.wav").toExternalForm());
            breakBrickSound = new AudioClip(AssetsManager.class.getResource("/sounds/break_brick.wav").toExternalForm());
            explosionSound = new AudioClip(AssetsManager.class.getResource("/sounds/explosion.wav").toExternalForm());
        } catch (Exception e) {
            System.err.println("Không thể tải sound: " + e.getMessage());
        }
    }

    public static void playClickSound() {
        playSound(clickSound);
    }

    public static void playHitBrickSound() {
        playSound(hitBrickSound);
    }

    public static void playHitWallSound() {
        playSound(hitWallSound);
    }

    public static void playHitPaddleSound() {
        playSound(hitPaddleSound);
    }

    public static void playLoseSound() {
        playSound(loseSound);
    }

    public static void playBreakBrickSound() {
        playSound(breakBrickSound);
    }

    public static void playExplosionSound() {
        playSound(explosionSound);
    }

    private static void playSound(AudioClip clip) {
        if (clip != null) {
            clip.play();
        }
    }
}
