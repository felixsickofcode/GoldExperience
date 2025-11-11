package vnu.uet.goldexperience.manager;

import vnu.uet.goldexperience.core.GameState;
import vnu.uet.goldexperience.view.GameOverManager;
import vnu.uet.goldexperience.view.PauseMenuManager;
import vnu.uet.goldexperience.view.SaveFoundDialog;
import vnu.uet.goldexperience.view.TransitionManager;
//State Machine

public class GameStateManager {

    private GameState currentState = GameState.PLAYING;
    private GameState previousState = null;

    private final TransitionManager transitionManager;
    private final PauseMenuManager pauseMenuManager;
    private final GameOverManager gameOverManager;
    private final SaveFoundDialog saveFoundDialog;

    public GameStateManager(TransitionManager transitionManager,
                            PauseMenuManager pauseMenuManager,
                            GameOverManager gameOverManager,
                            SaveFoundDialog saveFoundDialog) {
        this.transitionManager = transitionManager;
        this.pauseMenuManager = pauseMenuManager;
        this.gameOverManager = gameOverManager;
        this.saveFoundDialog = saveFoundDialog;
    }

    public void setState(GameState newState) {
        if (currentState == newState) return;
//        System.out.println("GSM: " + currentState + " -> " + newState);

        ExitState(currentState);

        previousState = currentState;
        currentState = newState;

        EnterState(newState);
    }

    private void ExitState(GameState state) {
        switch (state) {
            case PAUSED:
                pauseMenuManager.hide();
                break;
            case TOLOAD:
                saveFoundDialog.hide();
                break;
            case STORY:
                break;
            case TRANSITIONING:
                break;
            case PLAYING:
            case GAME_OVER:
                gameOverManager.hide();
            case VICTORY:
                break;
        }
    }

    private void EnterState(GameState state) {
        switch (state) {
            case PLAYING:
                System.out.println("GSM: Resume");
                break;
            case PAUSED:
                pauseMenuManager.show();
                break;
            case TRANSITIONING:
                transitionManager.start();
                break;
            case GAME_OVER:
                gameOverManager.show();
                break;
            case VICTORY:
                System.out.println("GSM: Victory");
                break;
            case STORY:
                System.out.println("GSM: Story mode");
                break;
            case TOLOAD:
                saveFoundDialog.show();
                break;
        }
    }

    public boolean is(GameState state) {
        return currentState == state;
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public GameState getPreviousState() {
        return previousState;
    }

    public boolean shouldUpdateGameplay() {
        return currentState == GameState.PLAYING ||
                currentState == GameState.TRANSITIONING;
    }

    public boolean shouldFreezeGameplay() {
        return currentState == GameState.PAUSED;
    }

    public boolean shouldAcceptGameplayInput() {
        return currentState == GameState.PLAYING;
    }

    public void reset() {
        setState(GameState.PLAYING);
        transitionManager.reset();
    }
}