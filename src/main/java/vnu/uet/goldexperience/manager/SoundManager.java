package vnu.uet.goldexperience.manager;

import javafx.application.Platform;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.concurrent.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SoundManager {
    // Audio clips
    private static AudioClip hitPaddleSound;
    private static AudioClip hitWallSound;
    private static AudioClip hitBrickSound;
    private static AudioClip loseSound;
    private static AudioClip clickSound;
    private static AudioClip breakBrickSound;
    private static AudioClip explosionSound;
    private static MediaPlayer backgroundSound;

    // ThreadPool configuration
    private static ExecutorService soundExecutor;
    private static ScheduledExecutorService scheduledExecutor;
    private static final int CORE_POOL_SIZE = 2;
    private static final int MAX_POOL_SIZE = 4;
    private static final long KEEP_ALIVE_TIME = 30L;

    // Sound queue và caching
    private static final BlockingQueue<SoundTask> soundQueue = new LinkedBlockingQueue<>(50);
    private static final Map<String, Long> soundCooldowns = new ConcurrentHashMap<>();
    private static final long MIN_SOUND_INTERVAL = 50; // milliseconds

    // Statistics
    private static final AtomicInteger totalSoundsPlayed = new AtomicInteger(0);
    private static final AtomicInteger soundsInQueue = new AtomicInteger(0);
    private static volatile boolean isInitialized = false;

    /**
     * Khởi tạo ThreadPool
     */
    private static synchronized void initializeThreadPool() {
        if (soundExecutor != null) {
            return;
        }

        // Khởi tạo ThreadPool
        soundExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100),
                new ThreadFactory() {
                    private int counter = 0;

                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r, "SoundManager-Worker-" + counter++);
                        thread.setDaemon(true);
                        thread.setPriority(Thread.NORM_PRIORITY - 1);
                        return thread;
                    }
                },
                new ThreadPoolExecutor.DiscardOldestPolicy()
        );

        // ScheduledExecutor cho cleanup và maintenance
        scheduledExecutor = Executors.newScheduledThreadPool(1, r -> {
            Thread thread = new Thread(r, "SoundManager-Scheduler");
            thread.setDaemon(true);
            return thread;
        });

        // Start sound processor thread
        startSoundProcessor();

        // Schedule periodic cleanup
        schedulePeriodicCleanup();
    }

    /**
     * Load tất cả sounds - PHẢI GỌI TỪ JavaFX Application Thread
     */
    public static void loadSound() {
        // Khởi tạo ThreadPool trước
        initializeThreadPool();

        // Load sounds synchronously
        loadSounds();
        updateAllVolumes();

        // Preload sounds
        preloadSounds();

        isInitialized = true;
    }

    /**
     * Load sounds từ resources
     */
    private static void loadSounds() {
        try {
            hitWallSound = new AudioClip(SoundManager.class.getResource("/sounds/hit_wall.wav").toExternalForm());
            hitBrickSound = new AudioClip(SoundManager.class.getResource("/sounds/hit_brick.wav").toExternalForm());
            hitPaddleSound = new AudioClip(SoundManager.class.getResource("/sounds/hit_paddle.wav").toExternalForm());
            loseSound = new AudioClip(SoundManager.class.getResource("/sounds/lose.mp3").toExternalForm());
            clickSound = new AudioClip(SoundManager.class.getResource("/sounds/click.wav").toExternalForm());
            breakBrickSound = new AudioClip(SoundManager.class.getResource("/sounds/break_brick.wav").toExternalForm());
            explosionSound = new AudioClip(SoundManager.class.getResource("/sounds/explosion.wav").toExternalForm());
            Media backgroundMedia = new Media(SoundManager.class.getResource("/sounds/background.wav").toExternalForm());
            backgroundSound = new MediaPlayer(backgroundMedia);
            backgroundSound.setCycleCount(MediaPlayer.INDEFINITE);
        } catch (Exception e) {
            System.err.println("Không thể tải sound: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Preload sounds để tránh delay lần đầu play
     */
    private static void preloadSounds() {
        if (clickSound != null) clickSound.play(0.0);
        if (hitWallSound != null) hitWallSound.play(0.0);
        if (hitBrickSound != null) hitBrickSound.play(0.0);
        if (hitPaddleSound != null) hitPaddleSound.play(0.0);
        if (loseSound != null) loseSound.play(0.0);
        if (breakBrickSound != null) breakBrickSound.play(0.0);
        if (explosionSound != null) explosionSound.play(0.0);
    }

    /**
     * Start thread xử lý sound queue
     */
    private static void startSoundProcessor() {
        soundExecutor.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    SoundTask task = soundQueue.poll(100, TimeUnit.MILLISECONDS);
                    if (task != null) {
                        soundsInQueue.decrementAndGet();
                        processSoundTask(task);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.err.println("Error processing sound: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Xử lý sound task
     */
    private static void processSoundTask(SoundTask task) {
        // Kiểm tra cooldown để tránh spam sound
        if (task.checkCooldown && !canPlaySound(task.soundName)) {
            return;
        }

        Platform.runLater(() -> {
            try {
                if (task.clip != null) {
                    task.clip.play(task.volume);
                    totalSoundsPlayed.incrementAndGet();

                    if (task.checkCooldown) {
                        soundCooldowns.put(task.soundName, System.currentTimeMillis());
                    }
                }
            } catch (Exception e) {
                System.err.println("Error playing sound " + task.soundName + ": " + e.getMessage());
            }
        });
    }

    /**
     * Kiểm tra xem sound có thể play không (cooldown)
     */
    private static boolean canPlaySound(String soundName) {
        Long lastPlayTime = soundCooldowns.get(soundName);
        if (lastPlayTime == null) {
            return true;
        }
        return (System.currentTimeMillis() - lastPlayTime) >= MIN_SOUND_INTERVAL;
    }

    /**
     * Dọn dẹp cooldown map định kỳ
     */
    private static void schedulePeriodicCleanup() {
        scheduledExecutor.scheduleAtFixedRate(() -> {
            long currentTime = System.currentTimeMillis();
            soundCooldowns.entrySet().removeIf(entry ->
                    (currentTime - entry.getValue()) > 1000
            );
        }, 1, 1, TimeUnit.SECONDS);
    }

    // ===== PUBLIC PLAY METHODS (Async) =====

    public static void playClickSound() {
        playSoundAsync(clickSound, "click", false);
    }

    public static void playHitBrickSound() {
        playSoundAsync(hitBrickSound, "hitBrick", true);
    }

    public static void playHitWallSound() {
        playSoundAsync(hitWallSound, "hitWall", true);
    }

    public static void playHitPaddleSound() {
        playSoundAsync(hitPaddleSound, "hitPaddle", true);
    }

    public static void playLoseSound() {
        playSoundAsync(loseSound, "lose", false);
    }

    public static void playBreakBrickSound() {
        playSoundAsync(breakBrickSound, "breakBrick", false);
    }

    public static void playExplosionSound() {
        playSoundAsync(explosionSound, "explosion", false);
    }

    public static void playBackgroundMusic() {
        if (backgroundSound != null && isInitialized) {
            Platform.runLater(() -> {
                double volume = GameDataManager.getGlobalData().getVolume() * 0.1;
                backgroundSound.setVolume(volume);
                backgroundSound.play();
                System.out.println("Background music started with volume: " + volume);
            });
        }
    }

    /**
     * Stop background music
     */
    public static void stopBackgroundMusic() {
        if (backgroundSound != null) {
            Platform.runLater(() -> {
                backgroundSound.stop();
                System.out.println("Background music stopped");
            });
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

    /**
     * Play sound bất đồng bộ với volume mặc định
     */
    private static void playSoundAsync(AudioClip clip, String soundName, boolean checkCooldown) {
        if (clip == null || !isInitialized) {
            return;
        }

        double volume = GameDataManager.getGlobalData().getVolume();
        playSoundAsync(clip, soundName, checkCooldown, volume);
    }

    /**
     * Play sound bất đồng bộ với custom volume
     */
    private static void playSoundAsync(AudioClip clip, String soundName, boolean checkCooldown, double volume) {
        if (clip == null || !isInitialized) {
            return;
        }

        try {
            SoundTask task = new SoundTask(clip, soundName, checkCooldown, volume);
            boolean added = soundQueue.offer(task);
            if (added) {
                soundsInQueue.incrementAndGet();
            } else {
                System.err.println("Sound queue is full, dropping sound: " + soundName);
            }
        } catch (Exception e) {
            System.err.println("Error queuing sound " + soundName + ": " + e.getMessage());
        }
    }

    /**
     * Play sound đồng bộ (cho trường hợp cần chắc chắn play ngay)
     */
    private static void playSound(AudioClip clip) {
        if (clip != null) {
            clip.play();
        }
    }

    /**
     * Play nhiều sounds cùng lúc (combo sounds)
     */
    public static void playComboSounds(AudioClip... clips) {
        if (!isInitialized || soundExecutor == null) {
            return;
        }

        CompletableFuture.runAsync(() -> {
            for (AudioClip clip : clips) {
                if (clip != null) {
                    Platform.runLater(() -> clip.play());
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }, soundExecutor);
    }

    /**
     * Update volume cho tất cả sounds
     */
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
    /**
     * Fade in sound effect
     */
    public static void fadeInSound(AudioClip clip, double targetVolume, long durationMs) {
        if (clip == null || !isInitialized || soundExecutor == null) return;

        CompletableFuture.runAsync(() -> {
            int steps = 20;
            long stepDelay = durationMs / steps;
            double volumeStep = targetVolume / steps;

            for (int i = 0; i <= steps; i++) {
                final double volume = volumeStep * i;
                Platform.runLater(() -> {
                    clip.setVolume(volume);
                    if (volume == 0) {
                        clip.play();
                    }
                });

                try {
                    Thread.sleep(stepDelay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, soundExecutor);
    }

    /**
     * Fade out sound effect
     */
    public static void fadeOutSound(AudioClip clip, long durationMs) {
        if (clip == null || !isInitialized || soundExecutor == null) return;

        CompletableFuture.runAsync(() -> {
            double currentVolume = clip.getVolume();
            int steps = 20;
            long stepDelay = durationMs / steps;
            double volumeStep = currentVolume / steps;

            for (int i = steps; i >= 0; i--) {
                final double volume = volumeStep * i;
                Platform.runLater(() -> clip.setVolume(volume));

                try {
                    Thread.sleep(stepDelay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            Platform.runLater(() -> clip.stop());
        }, soundExecutor);
    }

    /**
     * Stop tất cả sounds
     */
    public static void stopAllSounds() {
        if (hitPaddleSound != null) hitPaddleSound.stop();
        if (hitWallSound != null) hitWallSound.stop();
        if (hitBrickSound != null) hitBrickSound.stop();
        if (loseSound != null) loseSound.stop();
        if (clickSound != null) clickSound.stop();
        if (breakBrickSound != null) breakBrickSound.stop();
        if (explosionSound != null) explosionSound.stop();
        if (backgroundSound != null) backgroundSound.stop();
        if (backgroundSound != null) backgroundSound.stop();


        // Clear queue
        soundQueue.clear();
        soundsInQueue.set(0);
    }

    /**
     * Lấy thống kê
     */
    public static SoundStatistics getStatistics() {
        if (soundExecutor == null || !(soundExecutor instanceof ThreadPoolExecutor)) {
            return new SoundStatistics(totalSoundsPlayed.get(), soundsInQueue.get(), 0, 0, soundCooldowns.size());
        }

        ThreadPoolExecutor executor = (ThreadPoolExecutor) soundExecutor;
        return new SoundStatistics(
                totalSoundsPlayed.get(),
                soundsInQueue.get(),
                executor.getActiveCount(),
                executor.getQueue().size(),
                soundCooldowns.size()
        );
    }

    /**
     * Shutdown SoundManager
     */
    public static void shutdown() {
        stopAllSounds();

        if (soundExecutor != null) {
            soundExecutor.shutdown();
            try {
                if (!soundExecutor.awaitTermination(2, TimeUnit.SECONDS)) {
                    soundExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                soundExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        if (scheduledExecutor != null) {
            scheduledExecutor.shutdown();
            try {
                if (!scheduledExecutor.awaitTermination(2, TimeUnit.SECONDS)) {
                    scheduledExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduledExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        isInitialized = false;
    }

    /**
     * Reset statistics
     */
    public static void resetStatistics() {
        totalSoundsPlayed.set(0);
        soundsInQueue.set(0);
        soundCooldowns.clear();
    }

    // ===== INNER CLASSES =====

    /**
     * Sound task để queue
     */
    private static class SoundTask {
        final AudioClip clip;
        final String soundName;
        final boolean checkCooldown;
        final double volume;
        final long timestamp;

        SoundTask(AudioClip clip, String soundName, boolean checkCooldown, double volume) {
            this.clip = clip;
            this.soundName = soundName;
            this.checkCooldown = checkCooldown;
            this.volume = volume;
            this.timestamp = System.currentTimeMillis();
        }
    }

    /**
     * Statistics class
     */
    public static class SoundStatistics {
        public final int totalSoundsPlayed;
        public final int soundsInQueue;
        public final int activeThreads;
        public final int queueSize;
        public final int activeCooldowns;

        public SoundStatistics(int totalSoundsPlayed, int soundsInQueue,
                               int activeThreads, int queueSize, int activeCooldowns) {
            this.totalSoundsPlayed = totalSoundsPlayed;
            this.soundsInQueue = soundsInQueue;
            this.activeThreads = activeThreads;
            this.queueSize = queueSize;
            this.activeCooldowns = activeCooldowns;
        }

        @Override
        public String toString() {
            return String.format(
                    "Sound Stats: Played=%d, InQueue=%d, ActiveThreads=%d, QueueSize=%d, Cooldowns=%d",
                    totalSoundsPlayed, soundsInQueue, activeThreads, queueSize, activeCooldowns
            );
        }
    }
}