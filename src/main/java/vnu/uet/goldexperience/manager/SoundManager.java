package vnu.uet.goldexperience.manager;

import javafx.application.Platform;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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

    private static MediaPlayer backgroundSound;
    private static ExecutorService soundExecutor;

    private static final int THREAD_POOL_SIZE = 4;

    private static final Map<String, Long> soundCooldowns = new ConcurrentHashMap<>();
    private static final long MIN_SOUND_INTERVAL = 50; // milliseconds

    private static volatile boolean isInitialized = false;

    public static void loadSound() {
        loadSounds();
        updateAllVolumes();
        preloadSounds();
        isInitialized = true;
    }

    private static void preloadSounds() {
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
            hitWallSound = new AudioClip(SoundManager.class.getResource("/sounds/hit_wall.wav").toExternalForm());
            hitBrickSound = new AudioClip(SoundManager.class.getResource("/sounds/hit_brick.wav").toExternalForm());
            hitPaddleSound = new AudioClip(SoundManager.class.getResource("/sounds/hit_paddle.wav").toExternalForm());
            loseSound = new AudioClip(SoundManager.class.getResource("/sounds/lose.mp3").toExternalForm());
            clickSound = new AudioClip(SoundManager.class.getResource("/sounds/click.wav").toExternalForm());
            breakBrickSound = new AudioClip(SoundManager.class.getResource("/sounds/break_brick.wav").toExternalForm());
            explosionSound = new AudioClip(SoundManager.class.getResource("/sounds/explosion.wav").toExternalForm());

            // MediaPlayer cho background music
            Media backgroundMedia = new Media(SoundManager.class.getResource("/sounds/background.wav").toExternalForm());
            backgroundSound = new MediaPlayer(backgroundMedia);
            backgroundSound.setCycleCount(MediaPlayer.INDEFINITE);
        } catch (Exception e) {
            System.err.println("Không thể tải sound: " + e.getMessage());
        }
    }

    public static void playClickSound() {
        playSound(clickSound, "click", false);
    }


    public static void playHitBrickSound() {
        playSound(hitBrickSound, "hitBrick", true);
    }

    public static void playHitWallSound() {
        playSound(hitWallSound, "hitWall", true);
    }

    public static void playHitPaddleSound() {
        playSound(hitPaddleSound, "hitPaddle", true);
    }

    public static void playLoseSound() {
        playSound(loseSound, "lose", false);
    }

    public static void playBreakBrickSound() {
        playSound(breakBrickSound, "breakBrick", false);
    }

    public static void playExplosionSound() {
        playSound(explosionSound, "explosion", false);
    }

    private static void playSound(AudioClip clip, String soundName, boolean checkCooldown) {
        if (clip == null || !isInitialized) {
            return;
        }

        if (checkCooldown && !canPlaySound(soundName)) {
            return;
        }

        // Play sound async
        soundExecutor.submit(() -> {
            Platform.runLater(() -> {
                double volume = GameDataManager.getGlobalData().getVolume();
                clip.play(volume);

                if (checkCooldown) {
                    soundCooldowns.put(soundName, System.currentTimeMillis());
                }
            });
        });
    }

    private static boolean canPlaySound(String soundName) {
        Long lastPlayTime = soundCooldowns.get(soundName);
        if (lastPlayTime == null) {
            return true;
        }
        return (System.currentTimeMillis() - lastPlayTime) >= MIN_SOUND_INTERVAL;
    }

    public static void playBackgroundMusic() {
        if (backgroundSound != null && isInitialized) {
            Platform.runLater(() -> {
                double volume = GameDataManager.getGlobalData().getVolume() * 0.1;
                backgroundSound.setVolume(volume);
                backgroundSound.play();
                System.out.println("Background music playing at volume: " + volume);
            });
        }
    }

    public static void stopBackgroundMusic() {
        if (backgroundSound != null) {
            Platform.runLater(() -> backgroundSound.stop());
        }
    }

    public static void pauseBackgroundMusic() {
        if (backgroundSound != null) {
            Platform.runLater(() -> backgroundSound.pause());
        }
    }

    public static void resumeBackgroundMusic() {
        if (backgroundSound != null) {
            Platform.runLater(() -> backgroundSound.play());
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

        if (backgroundSound != null) {
            double bgVolume = volume * 0.1;
            backgroundSound.setVolume(bgVolume);
        }
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