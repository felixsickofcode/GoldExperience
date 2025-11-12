package vnu.uet.goldexperience.manager;

import javafx.scene.media.AudioClip;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SoundManager {

    // thread pool riêng dành cho Sound
    private static final ExecutorService audioExecutor =
            Executors.newFixedThreadPool(4); // cứ tạm 4 threads đã, hỏng thì tính sau

    private static final Object playLock = new Object();
    private static volatile long lastPlayTime = 0;
    private static final long MIN_PLAY_INTERVAL = 50; // 50ms

    public static AudioClip hitPaddleSound;
    public static AudioClip hitWallSound;
    public static AudioClip hitBrickSound;
    public static AudioClip loseSound;
    public static AudioClip clickSound;
    public static AudioClip breakBrickSound;
    public static AudioClip explosionSound;

    public static void loadSound() {
        loadSounds();
        updateAllVolumes();
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
        audioExecutor.submit(() -> {
            synchronized (playLock) {
                long now = System.currentTimeMillis();

                if (now - lastPlayTime < MIN_PLAY_INTERVAL) {
                    return;
                }

                lastPlayTime = now;
            }

            if (clickSound != null) {
                clickSound.play();
            }
        });
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
            audioExecutor.submit(() -> clip.play());
        }
    }

    public static void updateAllVolumes() {
        double volume = GameDataManager.getGlobalData().getVolume();

        if (hitPaddleSound != null) hitPaddleSound.setVolume(volume);
        if (hitWallSound != null) hitWallSound.setVolume(volume);
        if (hitBrickSound != null) hitBrickSound.setVolume(volume);
        if (loseSound != null) loseSound.setVolume(volume);
        if (clickSound != null) clickSound.setVolume(volume);
        if (breakBrickSound != null) breakBrickSound.setVolume(volume);
        if (explosionSound != null) explosionSound.setVolume(volume);
    }

    public static void shutdown() {
        System.out.println("Bắt đầu đóng luồng audio executor nè");
        audioExecutor.shutdown();

        try {
            if (!audioExecutor.awaitTermination(2, TimeUnit.SECONDS)) {
                audioExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            System.err.printf("Không thể đóng luồng audio: %s\n", e.getMessage());
            // phải gì... phải đóng
            audioExecutor.shutdownNow();
        }

        System.out.println("Hoàn tất đóng luồng audio");
    }
}