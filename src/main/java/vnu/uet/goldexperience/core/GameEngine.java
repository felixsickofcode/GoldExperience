package vnu.uet.goldexperience.core;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import vnu.uet.goldexperience.effect.brick.ExplosionEffect;
import vnu.uet.goldexperience.manager.*;
import vnu.uet.goldexperience.model.*;
import vnu.uet.goldexperience.model.brick.*;
import vnu.uet.goldexperience.model.brick.Brick.BrickListener;
import vnu.uet.goldexperience.model.brickFactory.*;
import vnu.uet.goldexperience.view.LoadGameDialog;

import java.util.*;

public class GameEngine implements BrickListener {
    private static GameEngine instance;
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final InputManager input;
    private final LevelManager levelManager;

    private final GameStateManager stateManager;
    private final TransitionManager transitionManager;
    private final PauseMenuManager pauseMenuManager;
    private final GameOverManager gameOverManager;
    private final DialogueSystem dialogueSystem;

    private SceneManager sceneManager;
    private CursorChangeListener cursorChangeListener;
    private GameUICallback uiCallback;

    private Paddle paddle;
    private List<Brick> bricks = new ArrayList<>();

    private final List<PowerUp> fallingPowerUps = new ArrayList<>();
    // Game sẽ maintain một mảng các Ball, thay vì một biến Ball đơn lẻ
    private final List<Ball> balls = new ArrayList<>();
    private final List<Bullet> bullets = new ArrayList<>();
    private PowerUpManager powerUpManager;

    private AnimationTimer loop;
    private long lastTime = 0;

    private GameSession.GameMode mode;
    private int comboCount = 0;

    private boolean levelCompleteSoundPlayed = false;
    private final Set<Brick> soundForExplosionChains = new HashSet<>();

    /**
     * saveload
     */
    private LoadGameDialog loadGameDialog;

    public GameEngine(Canvas canvas, InputManager input) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.input = input;
        this.levelManager = new LevelManager();
        this.transitionManager = new TransitionManager(canvas.getWidth(), canvas.getHeight());
        this.pauseMenuManager = new PauseMenuManager(canvas, null);
        this.gameOverManager = new GameOverManager(canvas, null);
        this.stateManager = new GameStateManager(transitionManager, pauseMenuManager, gameOverManager);
        this.dialogueSystem = new DialogueSystem(canvas);
        this.mode = GameSession.getInstance().getMode();
        this.loadGameDialog = new LoadGameDialog(canvas);
        setupLoadGameDialogCallbacks();
        setupDialogueCallbacks();
        setupPauseMenuCallbacks();
        setupGameOverCallbacks();

        initPaddle();
        instance = this;
    }

    public static GameEngine getInstance() {
        return instance;
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    private void initPaddle() {
        paddle = new Paddle(Constants.PADDLE_INIT_POSITION, canvas.getHeight() - 120,
                Constants.MEDIUM_PADDLE_WIDTH, Constants.PADDLE_HEIGHT);
        paddle.refreshSkin();
    }

    private void loadCurrentLevel() {
        comboCount = 0;
        int levelNumber = GameSession.getInstance().getLevelNumber();
        System.out.println("Loading level: " + levelNumber +
                " (Chapter " + GameSession.getInstance().getCurrentChapter() +
                ", Level " + GameSession.getInstance().getCurrentLevel() + ")");

        // load va nhan copy cua brick
        if (mode.equals(GameSession.GameMode.STORY)) {
            bricks = levelManager.loadLevel(levelNumber);
        } else {
            int min = 31;
            int max = 40;
            int randomValue = min + (int) (Math.random() * (max - min + 1));
            bricks = levelManager.loadLevel(randomValue);
        }

        // Rồi mới add listener
        if (bricks != null) {
            for (Brick brick : bricks) {
                brick.addListener(this);
            }
        }

        paddle.reset();
        paddle.refreshSkin();

        if (mode.equals(GameSession.GameMode.ENDLESS)) {
            GameSession.getInstance().addLife();
        } else {
            GameSession.getInstance().resetLives();
        }

        if (uiCallback != null) {
            uiCallback.onLivesChanged(GameSession.getInstance().getLives());
            uiCallback.onScoreChanged(GameSession.getInstance().getScore());
        }

        soundForExplosionChains.clear();
        levelCompleteSoundPlayed = false;

        // Clear dãy bóng (từ màn trước)
        balls.clear();
        // tạo một trạng thái bắt đầu Ball cho mạng mới
        initNewPrimaryBall();

        bullets.clear();
        fallingPowerUps.clear();
        powerUpManager = new PowerUpManager(new GameContext(balls, paddle, bullets, bricks));
    }

    public void reloadLevel() {
        loadCurrentLevel();
        stateManager.setState(GameState.PLAYING);
    }

    private void setupPauseMenuCallbacks() {
        pauseMenuManager.setCallback(new PauseMenuManager.PauseMenuCallback() {
            @Override
            public void onResume() {
                System.out.println("Resume clicked");
                stateManager.setState(GameState.PLAYING);
                notifyCursorChange();
            }

            @Override
            public void onRestart() {
                System.out.println("Restart clicked");
                reloadLevel();
                notifyCursorChange();
            }

            @Override
            public void onBack() {
                SoundManager.playClickSound();
                System.out.println("Back");
                saveCurrentGame();
                if (mode.equals(GameSession.GameMode.STORY)) {
                    saveCurrentGame();
                }
                if (sceneManager != null) {
                    end();
                    if (mode.equals(GameSession.GameMode.STORY))
                        sceneManager.switchTo("level");
                    else
                        sceneManager.switchTo("menu");
                }
            }

            @Override
            public void onQuit() {
                SoundManager.playClickSound();
                System.out.println("Quit clicked");
                if (mode.equals(GameSession.GameMode.STORY)) {
                    saveCurrentGame();
                }
                Platform.exit();
            }
        });
    }

    private void setupGameOverCallbacks() {
        gameOverManager.setCallback(new GameOverManager.GameOverCallback() {
            @Override
            public void onRetry() {
                SoundManager.playClickSound();
                System.out.println("Retry clicked");
                GameSession.getInstance().resetLives();
                reloadLevel();
            }

            @Override
            public void onMainMenu() {
                SoundManager.playClickSound();
                System.out.println("Main Menu clicked");
                if (sceneManager != null) {
                    end();
                    sceneManager.switchTo("menu");
                }
            }

            @Override
            public void onQuit() {
                SoundManager.playClickSound();
                System.out.println("Quit clicked");
                javafx.application.Platform.exit();
            }
        });
    }

    private void setupDialogueCallbacks() {
        dialogueSystem.setCallback(() -> {
            System.out.println("Dialogue complete");
            if (stateManager.is(GameState.STORY)) {
                if (dialogueSystem.isAfterLevelDialogue()) {
                    boolean hasNext = GameSession.getInstance().nextLevel();
                    if (hasNext) {
                        stateManager.setState(GameState.TRANSITIONING);
                    } else {
                        if (sceneManager != null) {
                            end();
                            sceneManager.switchTo("menu");
                        }
                    }
                } else {
                    stateManager.setState(GameState.PLAYING);
                }
            }
            notifyCursorChange();
        });
    }

    public void start() {
        this.mode = GameSession.getInstance().getMode();
        // ENDLESS mode không có save/load
        if (mode.equals(GameSession.GameMode.ENDLESS)) {
            loadCurrentLevel();
            stateManager.setState(GameState.PLAYING);
            engineLoop();
            return;
        }

        // STORY mode - check for saves
        int levelNumber = GameSession.getInstance().getLevelNumber();
        if (hasLevelSave(levelNumber)) {
            System.out.println("💾 Save file found for level " + levelNumber);
            loadGameDialog.show();
            stateManager.setState(GameState.PAUSED);
        } else {
            loadCurrentLevel();
            checkAndShowBeforeDialogue();
        }
        engineLoop();
    }

    private void engineLoop() {
        loop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastTime == 0) {
                    lastTime = now;
                    return;
                }
                double dt = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;
                dt = Math.min(dt, 0.1);
                handleInput();
                input.update();
                update(dt);
                render();
            }
        };
        loop.start();
    }

    public void end() {
        if (loop != null) {
            loop.stop();
            loop = null;
        }
        instance = null;
    }

    private void handleInput() {
        if (loadGameDialog.isVisible()) {
            loadGameDialog.handleKeyInput(input);
            return;
        }
        if (input.isActionJustPressed(Action.PAUSE)) {
            if (stateManager.is(GameState.PLAYING)) {
                stateManager.setState(GameState.PAUSED);
                notifyCursorChange();
            } else if (stateManager.is(GameState.PAUSED)) {
                stateManager.setState(GameState.PLAYING);
                notifyCursorChange();
            }

            return;
        }

        if (stateManager.is(GameState.STORY)) {
            dialogueSystem.handleInput(input);
            return;
        }

        if (stateManager.is(GameState.PAUSED)) {
            pauseMenuManager.handleKeyInput(input);
            return;
        }

        if (stateManager.is(GameState.GAME_OVER)) {
            gameOverManager.handleKeyInput(input);
            notifyCursorChange();
            return;
        }

        if (stateManager.shouldAcceptGameplayInput()) {
            handleGameplayInput();
        }
    }

    public interface CursorChangeListener {
        void onCursorVisibilityChanged();
    }

    // Observer
    public interface GameUICallback {
        void onScoreChanged(int score);

        void onLivesChanged(int lives);
    }

    public void setCursorChangeListener(CursorChangeListener listener) {
        this.cursorChangeListener = listener;
    }

    public void setUICallback(GameUICallback callback) {
        this.uiCallback = callback;
    }

    private void notifyCursorChange() {
        if (cursorChangeListener != null) {
            cursorChangeListener.onCursorVisibilityChanged();
        }
    }

    private void handleGameplayInput() {
        if (input.isMouseActive()) {
            double targetX = input.getMouseX() - Constants.GAME_OFFSET
                    - paddle.getWidth() / 2;
            paddle.setTargetX(targetX);
        } else {
            if (input.isActionActive(Action.MOVE_LEFT)) {
                paddle.moveLeft();
            } else if (input.isActionActive(Action.MOVE_RIGHT)) {
                paddle.moveRight();
            } else {
                paddle.stop();
            }
        }

        // Shoot all reset balls
        if (input.isActionActive(Action.SHOOT)) {
            for (Ball b : balls) {
                if (b.isReset()) {
                    b.shoot();
                }
            }
        }
    }

    private void update(double deltaTime) {
        if (loadGameDialog.isVisible()) {
            loadGameDialog.update(deltaTime);
            return;
        }

        if (stateManager.is(GameState.PAUSED)) {
            pauseMenuManager.update(deltaTime);
            return;
        }


        if (stateManager.is(GameState.STORY)) {
            dialogueSystem.update(deltaTime);
            return;
        }

        if (stateManager.is(GameState.GAME_OVER)) {
            gameOverManager.update(deltaTime);
            return;
        }

        if (stateManager.is(GameState.TRANSITIONING)) {
            if (transitionManager.update(deltaTime)) {
                paddle.reset();
                loadCurrentLevel();
            }

            if (!transitionManager.isActive()) {
                checkAndShowBeforeDialogue();
            }
        }

        if (stateManager.shouldUpdateGameplay()) {
            updateGameplay(deltaTime);
        }

        if (stateManager.is(GameState.PLAYING) && isLevelComplete()) {
            handleLevelComplete();
        }
    }

    private void updateGameplay(double deltaTime) {
        mode = GameSession.getInstance().getMode();
        paddle.update(deltaTime);

        List<Ball> ballsToRemove = new ArrayList<>();

        for (Ball b : balls) {
            b.update(deltaTime);

            if (b.bounceOffWithPaddle(paddle)) {
                SoundManager.playHitPaddleSound();
            }

            if (b.getY() > canvas.getHeight() && stateManager.is(GameState.PLAYING)) {
                ballsToRemove.add(b);
            }
        }

        // Kiểm tra va chạm của toàn bộ tất cả các bóng
        if (!transitionManager.shouldDisableCollision()) {
            for (Ball b : balls) {
                if (b.isReset()) continue;

                for (Brick brick : bricks) {
                    if (!brick.isDestroyed() && b.bounceOffWithBrick(brick)) {
                        brick.takeHit();

                        // Ai đó cản thằng Minh, thằng Phong lại đi, 2 thằng code game kinh vch

                        if (brick instanceof ExplodeBrick) {
                            SoundManager.playExplosionSound();
                        } else if (brick.getHitPoints() == 0 || brick instanceof UnbreakableBrick) {
                            SoundManager.playBreakBrickSound();
                        } else {
                            SoundManager.playHitBrickSound();
                        }

                        break;
                    }
                }
            }
        }

        // Xoá các bóng đã bị rơi
        if (!ballsToRemove.isEmpty()) {
            balls.removeAll(ballsToRemove);
        }

        if (balls.isEmpty() && !ballsToRemove.isEmpty()) {
            GameSession.getInstance().loseLife();

            // cập nhật lại số mạng sau khi hẹo
            if (uiCallback != null) {
                uiCallback.onLivesChanged(GameSession.getInstance().getLives());
            }

            // nếu còn mạng thì init quả mới, không thì game over
            if (GameSession.getInstance().isStillAlive()) {
                initNewPrimaryBall();
            } else {
                stateManager.setState(GameState.GAME_OVER);
            }
        }

        for (Brick brick : bricks) {
            brick.update(deltaTime);
        }

        // Update active falling power-ups
        if (!fallingPowerUps.isEmpty()) {
            List<PowerUp> collected = new ArrayList<>();
            for (PowerUp pu : fallingPowerUps) {
                pu.update(deltaTime);

                if (pu.getY() > canvas.getHeight()) {
                    collected.add(pu);
                    continue;
                }

                if (pu.getX() < paddle.getX() + paddle.getWidth() &&
                        pu.getX() + pu.getWidth() > paddle.getX() &&
                        pu.getY() < paddle.getY() + paddle.getHeight() &&
                        pu.getY() + pu.getHeight() > paddle.getY()) {

                    if (powerUpManager != null) {
                        powerUpManager.activatePowerUp(pu);
                    }
                    collected.add(pu);
                }
            }
            if (!collected.isEmpty()) {
                fallingPowerUps.removeAll(collected);
            }
        }

        if (powerUpManager != null) {
            powerUpManager.update();
        }

        checkChainExplosions();
        bricks.removeIf(Brick::canBeRemoved);
    }

    private void initNewPrimaryBall() {
        Ball primaryBall = new Ball(Constants.BALL_INIT_POSITION,
                paddle.getY() - Constants.NORMAL_BALL_SIZE, Constants.NORMAL_BALL_SIZE);
        primaryBall.refreshEffects();
        balls.add(primaryBall);
        primaryBall.reset(paddle);
    }

    private void spawnRandomDrop(Brick destroyedBrick) {
        double spawnX = destroyedBrick.getX() + destroyedBrick.getWidth() / 2
                - Constants.POWER_UP_ITEM_WIDTH / 2;
        double spawnY = destroyedBrick.getY() + destroyedBrick.getHeight() / 2
                - Constants.POWER_UP_ITEM_HEIGHT / 2;

        PowerUpType dropType = PowerUpType.randomDroppable();
        fallingPowerUps.add(new SimplePowerUp(spawnX, spawnY, dropType));
    }

    private void render() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.save();
        transitionManager.applySlideTransform(gc);

        for (PowerUp pu : fallingPowerUps) {
            pu.render(gc);
        }

        for (Ball b : balls) {
            b.render(gc);
        }
        for (Brick brick : bricks) {
            brick.render(gc);
        }

        gc.restore();
        paddle.render(gc);

        if (stateManager.is(GameState.STORY)) {
            dialogueSystem.render(gc);
        }

        if (stateManager.is(GameState.TRANSITIONING)) {
            transitionManager.render(gc);
        } else if (stateManager.is(GameState.PAUSED)) {
            pauseMenuManager.render(gc);
        } else if (stateManager.is(GameState.GAME_OVER)) {
            gameOverManager.render(gc);
        }
        if (loadGameDialog.isVisible()) {
            loadGameDialog.render(gc);
        }
    }

    public void refreshPaddleSkin() {
        if (paddle != null) {
            paddle.refreshSkin();
        }
    }

    public void refreshBallEffects() {
        for (Ball b : balls) {
            if (b != null) {
                b.refreshEffects();
            }
        }
    }

    private void checkChainExplosions() {
        List<Brick> newlyExploded = new ArrayList<>();

        for (Brick brick : bricks) {
            ExplosionEffect effect = brick.getExplosionEffect();

            if (effect != null && effect.isActive()) {
                for (Brick otherBrick : bricks) {
                    if (otherBrick != brick
                            && !otherBrick.isDestroyed()
                            && otherBrick.isInExplosionRadius(effect)) {
                        newlyExploded.add(otherBrick);
                    }
                }
            }
        }

        for (Brick brick : newlyExploded) {
            brick.explodeByChainReaction();

            if (!soundForExplosionChains.contains(brick)) {
                SoundManager.playExplosionSound();
                soundForExplosionChains.add(brick);
            }
        }
    }

    private boolean isLevelComplete() {
        for (Brick brick : bricks) {
            if ((!brick.isDestroyed() && !(brick instanceof UnbreakableBrick))
                    || (brick.getBreakEffect() != null && !(brick.getBreakEffect().isFinished()))
                    || (brick.getExplosionEffect() != null && brick.getExplosionEffect().isActive())) {
                return false;
            }
        }

        return true;
    }

    private void handleLevelComplete() {
        GameDataManager.completeLevel(GameSession.getInstance().getLevelNumber(),
                GameSession.getInstance().getScore());
        boolean hasUnbreakableBricks = false;

        for (Brick brick : bricks) {
            if (brick instanceof UnbreakableBrick) {
                ((UnbreakableBrick) brick).destroy();
                hasUnbreakableBricks = true;
            }
        }

        if (hasUnbreakableBricks && !levelCompleteSoundPlayed) {
            SoundManager.playBreakBrickSound();
            levelCompleteSoundPlayed = true;
        }

        if (areAllEffectsFinished()) {
            System.out.println("Level Complete!");

            if (mode.equals(GameSession.GameMode.ENDLESS)) {
                boolean hasNext = GameSession.getInstance().nextLevel();
                if (hasNext) {
                    stateManager.setState(GameState.TRANSITIONING);
                } else {
                    stateManager.setState(GameState.VICTORY);
                }
                return;
            }

            int currentLevelNumber = GameSession.getInstance().getLevelNumber();

            if (Story.hasAfterDialogue(currentLevelNumber)) {
                checkAndShowAfterDialogue();
            } else {
                boolean hasNext = GameSession.getInstance().nextLevel();
                if (hasNext) {
                    stateManager.setState(GameState.TRANSITIONING);
                } else {
                    System.out.println("Game Complete! All levels finished!");
                    stateManager.setState(GameState.VICTORY);
                }
            }
        }
    }

    private boolean areAllEffectsFinished() {
        for (Brick brick : bricks) {
            if (brick instanceof UnbreakableBrick)
                if (((UnbreakableBrick) brick).getDestructionEffect() != null && !((UnbreakableBrick) brick).getDestructionEffect().isFinished()) {
                    return false;
                }
            if (brick.getExplosionEffect() != null && brick.getExplosionEffect().isActive()) {
                return false;
            }
            if (brick.getBreakEffect() != null && !brick.getBreakEffect().isFinished()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void onBrickDestroyed(Brick brick) {
        int points = 125;
        GameSession.getInstance().addScore(points + comboCount);
        comboCount += 5;

        if (!(brick instanceof UnbreakableBrick) && Math.random() < Constants.DROP_CHANCE) {
            spawnRandomDrop(brick);
        }

        if (uiCallback != null) {
            uiCallback.onScoreChanged(GameSession.getInstance().getScore());
        }
    }

    private void checkAndShowBeforeDialogue() {
        if (mode.equals(GameSession.GameMode.ENDLESS)) {
            stateManager.setState(GameState.PLAYING);
            return;
        }

        int levelNumber = GameSession.getInstance().getLevelNumber();
        Story.DialogueData dialogue = Story.getBeforeDialogue(levelNumber);

        if (dialogue != null) {
            dialogueSystem.show(dialogue);
            stateManager.setState(GameState.STORY);
        } else {
            stateManager.setState(GameState.PLAYING);
        }
    }

    private void checkAndShowAfterDialogue() {
        if (mode.equals(GameSession.GameMode.ENDLESS)) {
            stateManager.setState(GameState.PLAYING);
            return;
        }

        int currentLevelNumber = GameSession.getInstance().getLevelNumber();
        Story.DialogueData dialogue = Story.getAfterDialogue(currentLevelNumber);

        if (dialogue != null) {
            System.out.println(dialogue);
            dialogueSystem.show(dialogue);
            stateManager.setState(GameState.STORY);
        }
    }

    public void refreshMode() {
        this.mode = GameSession.getInstance().getMode();
    }

    public GameStateManager getStateManager() {
        return stateManager;
    }

    public PauseMenuManager getPauseMenuManager() {
        return pauseMenuManager;
    }

    public TransitionManager getTransitionManager() {
        return transitionManager;
    }

    public GameOverManager getGameOverManager() {
        return gameOverManager;
    }

    public DialogueSystem getDialogueSystem() {
        return dialogueSystem;
    }

    /**
     * savegame
     */
    private void setupLoadGameDialogCallbacks() {
        loadGameDialog.setCallback(new LoadGameDialog.LoadGameCallback() {
            @Override
            public void onNewGame() {
                System.out.println("🆕 Starting new game");
                loadCurrentLevel();
                stateManager.setState(GameState.PLAYING);
            }

            @Override
            public void onLoadGame() {
                System.out.println("💾 Loading saved game");
                int levelNumber = GameSession.getInstance().getLevelNumber();
                if (loadSavedGame(levelNumber)) {
                    stateManager.setState(GameState.PLAYING);
                } else {
                    // Fallback to new game if load fails
                    loadCurrentLevel();
                    stateManager.setState(GameState.PLAYING);
                }
            }
        });
    }

    public void saveCurrentGame() {
        if (mode.equals(GameSession.GameMode.ENDLESS)) {
            System.out.println("⚠️ Endless mode does not support save/load");
            return;
        }
        if (!stateManager.is(GameState.PLAYING) && !stateManager.is(GameState.PAUSED)) {
            System.out.println("⚠️ Can only save during gameplay");
            return;
        }

        int levelNumber = GameSession.getInstance().getLevelNumber();
        LevelSaveData saveData = new LevelSaveData();

        // Basic info
        saveData.setLevelNumber(levelNumber);
        saveData.setScore(GameSession.getInstance().getScore());
        saveData.setLives(GameSession.getInstance().getLives());

        // Convert balls to BallData
        List<LevelSaveData.BallData> ballDataList = new ArrayList<>();
        for (Ball ball : balls) {
            ballDataList.add(new LevelSaveData.BallData(ball));
        }
        saveData.setBalls(ballDataList);

        // Convert paddle to PaddleData
        saveData.setPaddle(new LevelSaveData.PaddleData(paddle));

        // Save bricks (only non-destroyed ones)
        List<LevelSaveData.BrickSaveInfo> brickInfos = new ArrayList<>();
        for (Brick brick : bricks) {
            if (!brick.isDestroyed()) {
                String typeKey = brick.getBrickTypeKey();
                Map<String, Double> config = brick.getConfig();

                brickInfos.add(new LevelSaveData.BrickSaveInfo(
                        typeKey,
                        brick.getX(),
                        brick.getY(),
                        brick.getHitPoints(),
                        config
                ));
            }
        }
        saveData.setBricks(brickInfos);

        // Convert falling powerups to PowerUpData
        List<LevelSaveData.PowerUpData> powerUpDataList = new ArrayList<>();
        for (PowerUp powerUp : fallingPowerUps) {
            powerUpDataList.add(new LevelSaveData.PowerUpData(powerUp));
        }
        saveData.setFallingPowerUps(powerUpDataList);

        // Capture active powerups info
        if (powerUpManager != null) {
            saveData.setActivePowerups(powerUpManager.captureActivePowerupsInfo());
        }

        // Save to disk
        boolean success = GameDataManager.saveLevelProgress(levelNumber, saveData);
        if (success) {
            System.out.println("✅ Game saved successfully! [" + saveData + "]");
        } else {
            System.out.println("❌ Failed to save game!");
        }
    }

    /**
     * Load game đã save
     * Recreate bricks bằng BrickFactory
     */
    public boolean loadSavedGame(int levelNumber) {
        LevelSaveData saveData = GameDataManager.loadLevelProgress(levelNumber);
        if (saveData == null) {
            return false;
        }

        GameSession.getInstance().setScore(saveData.getScore());
        GameSession.getInstance().setLives(saveData.getLives());

        this.paddle = saveData.getPaddle().toPaddle();
        paddle.refreshSkin();

        this.balls.clear();
        for (LevelSaveData.BallData ballData : saveData.getBalls()) {
            Ball ball = ballData.toBall();
            ball.refreshEffects();
            this.balls.add(ball);
        }

        this.bricks = new ArrayList<>();
        for (LevelSaveData.BrickSaveInfo info : saveData.getBricks()) {
            BrickType type = BrickType.fromString(info.getType());
            if (type == null) {
                System.err.println("❌ Unknown brick type: " + info.getType());
                continue;
            }

            Brick brick = type.create(info.getX(), info.getY(), info.getConfig());
            brick.setHitPoints(info.getHitPoints());
            brick.addListener(this);
            this.bricks.add(brick);
        }
        // Restore falling powerups from PowerUpData
        this.fallingPowerUps.clear();
        for (LevelSaveData.PowerUpData powerUpData : saveData.getFallingPowerUps()) {
            this.fallingPowerUps.add(powerUpData.toPowerUp());
        }

        // Recreate PowerUpManager and restore active effects
        powerUpManager = new PowerUpManager(new GameContext(balls, paddle, bullets, bricks));
        for (LevelSaveData.ActivePowerupInfo info : saveData.getActivePowerups()) {
            powerUpManager.restoreActivePowerup(info.getType(), info.getRemainingDuration());
        }

        // Update UI callbacks
        if (uiCallback != null) {
            uiCallback.onLivesChanged(GameSession.getInstance().getLives());
            uiCallback.onScoreChanged(GameSession.getInstance().getScore());
        }

        // Reset volatile state
        soundForExplosionChains.clear();
        levelCompleteSoundPlayed = false;
        hitsSinceLastDrop = 0;
        bullets.clear();

        System.out.println("✅ Game loaded successfully!");
        return true;
    }


    public boolean hasLevelSave(int levelNumber) {
        return GameDataManager.hasLevelSave(levelNumber);
    }


    public void setupAutoSave() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (mode.equals(GameSession.GameMode.STORY) &&
                    (stateManager.is(GameState.PLAYING) || stateManager.is(GameState.PAUSED))) {
                saveCurrentGame();
                System.out.println("🔄 Auto-saved on exit");
            }
        }));
    }

    public LoadGameDialog getLoadGameDialog() {
        return loadGameDialog;
    }

}