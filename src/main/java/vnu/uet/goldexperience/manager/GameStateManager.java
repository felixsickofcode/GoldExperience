package vnu.uet.goldexperience.manager;

import vnu.uet.goldexperience.core.GameState;
//State Machine

public class GameStateManager {

    private GameState currentState = GameState.PLAYING;
    private GameState previousState = null;

    private final TransitionManager transitionManager;
    private final PauseMenuManager pauseMenuManager;

    public GameStateManager(TransitionManager transitionManager,
                            PauseMenuManager pauseMenuManager) {
        this.transitionManager = transitionManager;
        this.pauseMenuManager = pauseMenuManager;
    }

    public void setState(GameState newState) {
        if (currentState == newState) return;
        System.out.println("GSM: " + currentState + " -> " + newState);

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
            case TRANSITIONING:
                break;
            case PLAYING:
            case GAME_OVER:
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
                System.out.println("GSM: GameOver");
                break;
            case VICTORY:
                System.out.println("GSM: Victory");
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