package vnu.uet.goldexperience.manager;

import javafx.application.Platform;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SoundManager {

    private static final String MENU_MUSIC_FILE = "background.wav";
    private static final String[] CHAPTER_MUSIC_FILES = {
            "chapter1.wav",
            "chapter2.mp3",
            "chapter3.mp3",
            "chapter4.mp3",
            "chapter5.mp3",
            "chapter6.mp3"
    };

    private static final double[] CHAPTER_VOLUME_MULTIPLIERS = {
            1.0,
            1.0,
            1.2,
            1.0,
            0.9,
            1.2,
    };

    private static final ExecutorService audioExecutor =
            Executors.newFixedThreadPool(4);

    public static AudioClip hitPaddleSound;
    public static AudioClip hitWallSound;
    public static AudioClip hitBrickSound;
    public static AudioClip loseSound;
    public static AudioClip clickSound;
    public static AudioClip breakBrickSound;
    public static AudioClip explosionSound;

    private static MediaPlayer menuSound;
    private static final int CHAPTER_COUNT = CHAPTER_MUSIC_FILES.length;
    private static MediaPlayer[] chapterMusic = new MediaPlayer[CHAPTER_COUNT];
    private static MediaPlayer currentMusicPlayer;

    private static Duration savedChapterPosition = Duration.ZERO;
    private static int currentChapterNumber = -1;

    private static final Map<String, Long> soundCooldowns = new ConcurrentHashMap<>();
    private static final long MIN_SOUND_INTERVAL = 50;

    private static volatile boolean isInitialized = false;

    public static void loadSound() {
        loadSounds();
        updateAllVolumes();
        preloadSounds();
        isInitialized = true;
    }

    private static void preloadSounds() {
        if (clickSound != null) clickSound.play(0.0);
        if (hitWallSound != null) hitWallSound.play(0.0);
        if (hitBrickSound != null) hitBrickSound.play(0.0);
        if (hitPaddleSound != null) hitPaddleSound.play(0.0);
        if (loseSound != null) loseSound.play(0.0);
        if (breakBrickSound != null) breakBrickSound.play(0.0);
        if (explosionSound != null) explosionSound.play(0.0);
    }

    private static String getSoundPath(String fileName) {
        try {
            return SoundManager.class.getResource("/sounds/" + fileName).toExternalForm();
        } catch (Exception e) {
            System.err.println("k thay tep " + fileName);
            return null;
        }
    }

    private static void loadSounds() {
        try {
            hitWallSound = new AudioClip(getSoundPath("hit_wall.wav"));
            hitBrickSound = new AudioClip(getSoundPath("hit_brick.wav"));
            hitPaddleSound = new AudioClip(getSoundPath("hit_paddle.wav"));
            loseSound = new AudioClip(getSoundPath("lose.mp3"));
            clickSound = new AudioClip(getSoundPath("click.wav"));
            breakBrickSound = new AudioClip(getSoundPath("break_brick.wav"));
            explosionSound = new AudioClip(getSoundPath("explosion.wav"));

            String menuMusicPath = getSoundPath(MENU_MUSIC_FILE);
            if (menuMusicPath != null) {
                Media menuMedia = new Media(menuMusicPath);
                menuSound = new MediaPlayer(menuMedia);
                menuSound.setCycleCount(MediaPlayer.INDEFINITE);
            }

            for (int i = 0; i < CHAPTER_COUNT; i++) {
                String chapterMusicPath = getSoundPath(CHAPTER_MUSIC_FILES[i]);
                if (chapterMusicPath != null) {
                    Media chapterMedia = new Media(chapterMusicPath);
                    chapterMusic[i] = new MediaPlayer(chapterMedia);
                    chapterMusic[i].setCycleCount(MediaPlayer.INDEFINITE);
                } else {
                    chapterMusic[i] = null;
                }
            }
        } catch (Exception e) {
            System.err.println("d tai dc: " + e.getMessage());
        }
        updateAllVolumes();
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

        audioExecutor.submit(() -> {
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

    private static void stopCurrentMusicPlayer() {
        if (currentMusicPlayer != null) {
            MediaPlayer playerToStop = currentMusicPlayer;
            currentMusicPlayer = null;
            Platform.runLater(() -> playerToStop.stop());
        }
    }

    public static void playBackgroundMusic() {
        if (menuSound != null && isInitialized) {
            Platform.runLater(() -> {
                stopCurrentMusicPlayer();
                currentMusicPlayer = menuSound;
                double volume = GameDataManager.getGlobalData().getVolume() * 0.1;
                currentMusicPlayer.setVolume(volume);
                currentMusicPlayer.play();

                currentChapterNumber = -1;
                savedChapterPosition = Duration.ZERO;
            });
        }
    }

    public static void playChapterMusic(int chapterNumber) {
        playChapterMusic(chapterNumber, false);
    }

    public static void playChapterMusic(int chapterNumber, boolean resumeFromSaved) {
        if (!isInitialized) return;

        int index = chapterNumber - 1;

        if (index < 0 || index >= chapterMusic.length || chapterMusic[index] == null) {
            System.err.println("khong play dc: " + chapterNumber);
            return;
        }

        Platform.runLater(() -> {
            if (resumeFromSaved && currentMusicPlayer == chapterMusic[index]) {
                currentMusicPlayer.play();
                return;
            }

            stopCurrentMusicPlayer();
            currentMusicPlayer = chapterMusic[index];
            currentChapterNumber = chapterNumber;

            double volume = GameDataManager.getGlobalData().getVolume() * 0.1;
            double multiplier = (index < CHAPTER_VOLUME_MULTIPLIERS.length)
                    ? CHAPTER_VOLUME_MULTIPLIERS[index]
                    : 1.0;
            currentMusicPlayer.setVolume(volume * multiplier);

            currentMusicPlayer.play();

            if (resumeFromSaved && savedChapterPosition != null && !savedChapterPosition.equals(Duration.ZERO)) {
                currentMusicPlayer.seek(savedChapterPosition);
                System.out.println("Resumed chapter " + chapterNumber +
                        " from: " + savedChapterPosition.toSeconds() + "s");
            } else {
                savedChapterPosition = Duration.ZERO;
            }
        });
    }

    public static void pauseAndSavePosition() {
        if (currentMusicPlayer != null) {
            MediaPlayer playerToPause = currentMusicPlayer;
            Platform.runLater(() -> {
                if (playerToPause != null) {
                    if (currentChapterNumber > 0) {
                        savedChapterPosition = playerToPause.getCurrentTime();
                        System.out.println("Saved chapter " + currentChapterNumber +
                                " position: " + savedChapterPosition.toSeconds() + "s");
                    }
                    playerToPause.pause();
                }
            });
        }
    }

    public static void resumeChapterMusic() {
        if (currentMusicPlayer != null && currentChapterNumber > 0) {
            MediaPlayer playerToResume = currentMusicPlayer;
            Platform.runLater(() -> {
                if (playerToResume != null) {
                    playerToResume.play();
                    System.out.println("Resumed chapter " + currentChapterNumber);
                }
            });
        }
    }

    public static void restartChapterMusic() {
        if (currentChapterNumber > 0) {
            savedChapterPosition = Duration.ZERO;
            playChapterMusic(currentChapterNumber, false);
        }
    }

    public static void stopAllMusic() {
        Platform.runLater(() -> {
            if (currentMusicPlayer != null) {
                currentMusicPlayer.stop();
                currentMusicPlayer = null;
            }
            currentChapterNumber = -1;
            savedChapterPosition = Duration.ZERO;
        });
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

        double bgVolumeBase = volume * 0.1;

        if (menuSound != null) {
            menuSound.setVolume(bgVolumeBase);
        }

        for (int i = 0; i < CHAPTER_COUNT; i++) {
            MediaPlayer chapterPlayer = chapterMusic[i];

            if (chapterPlayer != null) {
                double chapterMultiplier = 1.0;

                if (i < CHAPTER_VOLUME_MULTIPLIERS.length) {
                    chapterMultiplier = CHAPTER_VOLUME_MULTIPLIERS[i];
                }

                double finalChapterVolume = bgVolumeBase * chapterMultiplier;
                chapterPlayer.setVolume(finalChapterVolume);
            }
        }
    }

    public static void shutdown() {
        stopAllMusic();
        audioExecutor.shutdown();
        try {
            if (!audioExecutor.awaitTermination(2, TimeUnit.SECONDS)) {
                audioExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            System.err.println("Không thể đóng luồng audio: " + e.getMessage());
            audioExecutor.shutdownNow();
        }
    }
}