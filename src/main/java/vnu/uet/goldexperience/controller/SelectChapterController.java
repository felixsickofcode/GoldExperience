package vnu.uet.goldexperience.controller;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import vnu.uet.goldexperience.manager.GameSession;
import vnu.uet.goldexperience.manager.SceneManager;
import vnu.uet.goldexperience.manager.SoundManager;

public class SelectChapterController {

    @FXML
    private AnchorPane leftStack;

    @FXML
    private AnchorPane centerChapter;

    @FXML
    private AnchorPane rightStack;

    @FXML
    private Text centerChapterText;

    @FXML
    private Text leftChapterText;

    @FXML
    private Text rightChapterText;

    private SceneManager sceneManager;
    private int currentChapterIndex = 1;
    private boolean isAnimating = false;

    private static final double SLIDE_DISTANCE = 360.0;
    private static final Duration ANIMATION_DURATION = Duration.millis(450);
    private static final double SIDE_CARD_SCALE = 0.75;
    private static final double SIDE_CARD_OPACITY = 0.3;

    @FXML
    public void initialize() {
        System.out.println("Chapter Select initialized");
        initializeCardPositions();
        updateChapterDisplay();
    }

    private void initializeCardPositions() {
        leftStack.setScaleX(SIDE_CARD_SCALE);
        leftStack.setScaleY(SIDE_CARD_SCALE);
        leftStack.setOpacity(SIDE_CARD_OPACITY);

        rightStack.setScaleX(SIDE_CARD_SCALE);
        rightStack.setScaleY(SIDE_CARD_SCALE);
        rightStack.setOpacity(SIDE_CARD_OPACITY);

        centerChapter.setScaleX(1.0);
        centerChapter.setScaleY(1.0);
        centerChapter.setOpacity(1.0);
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @FXML
    private void handleBackToMenu() {
        SoundManager.playClickSound();
        if (sceneManager != null) {
            sceneManager.switchTo("menu");
        }
    }

    @FXML
    private void onLeftClick() {
        SoundManager.playClickSound();
        if (isAnimating || currentChapterIndex <= 1) return;
        navigateTo(currentChapterIndex - 1);
    }

    @FXML
    private void onRightClick() {
        SoundManager.playClickSound();
        if (isAnimating || currentChapterIndex >= 5) return;
        navigateTo(currentChapterIndex + 1);
    }


    private void navigateTo(int targetIndex) {
        if (targetIndex == currentChapterIndex) return;

        boolean movingLeft = targetIndex > currentChapterIndex;
        isAnimating = true;

        ParallelTransition carouselAnimation = createCarouselAnimation(movingLeft);

        carouselAnimation.setOnFinished(e -> {
            currentChapterIndex = targetIndex;
            resetCardPositions();
            updateChapterDisplay();
            isAnimating = false;
        });

        carouselAnimation.play();
    }

    private ParallelTransition createCarouselAnimation(boolean movingLeft) {
        double direction = movingLeft ? -1 : 1;


        ParallelTransition leftAnim = new ParallelTransition(
                createSlideAnimation(leftStack, direction * SLIDE_DISTANCE),
                createFadeAnimation(leftStack, movingLeft ? 0.0 : 1.0),
                createScaleAnimation(leftStack, movingLeft ? 0.6 : 1.0)
        );

        ParallelTransition centerAnim = new ParallelTransition(
                createSlideAnimation(centerChapter, direction * SLIDE_DISTANCE),
                createFadeAnimation(centerChapter, SIDE_CARD_OPACITY),
                createScaleAnimation(centerChapter, SIDE_CARD_SCALE)
        );

        ParallelTransition rightAnim = new ParallelTransition(
                createSlideAnimation(rightStack, direction * SLIDE_DISTANCE),
                createFadeAnimation(rightStack, movingLeft ? 1.0 : 0.0),
                createScaleAnimation(rightStack, movingLeft ? 1.0 : 0.6)
        );

        ParallelTransition allAnimations = new ParallelTransition(
                leftAnim, centerAnim, rightAnim
        );

        allAnimations.setInterpolator(Interpolator.EASE_BOTH);

        return allAnimations;
    }

    private TranslateTransition createSlideAnimation(javafx.scene.Node node, double distance) {
        TranslateTransition slide = new TranslateTransition(ANIMATION_DURATION, node);
        slide.setByX(distance);
        return slide;
    }

    private FadeTransition createFadeAnimation(javafx.scene.Node node, double targetOpacity) {
        FadeTransition fade = new FadeTransition(ANIMATION_DURATION, node);
        fade.setToValue(targetOpacity);
        return fade;
    }

    private ParallelTransition createScaleAnimation(javafx.scene.Node node, double targetScale) {
        ScaleTransition scaleX = new ScaleTransition(ANIMATION_DURATION, node);
        scaleX.setToX(targetScale);

        ScaleTransition scaleY = new ScaleTransition(ANIMATION_DURATION, node);
        scaleY.setToY(targetScale);

        return new ParallelTransition(scaleX, scaleY);
    }

    private void resetCardPositions() {
        leftStack.setTranslateX(0);
        leftStack.setTranslateY(0);
        leftStack.setScaleX(SIDE_CARD_SCALE);
        leftStack.setScaleY(SIDE_CARD_SCALE);
        leftStack.setOpacity(SIDE_CARD_OPACITY);

        centerChapter.setTranslateX(0);
        centerChapter.setTranslateY(0);
        centerChapter.setScaleX(1.0);
        centerChapter.setScaleY(1.0);
        centerChapter.setOpacity(1.0);

        rightStack.setTranslateX(0);
        rightStack.setTranslateY(0);
        rightStack.setScaleX(SIDE_CARD_SCALE);
        rightStack.setScaleY(SIDE_CARD_SCALE);
        rightStack.setOpacity(SIDE_CARD_OPACITY);
    }


    private void updateChapterDisplay() {
        centerChapterText.setText("Chapter " + (currentChapterIndex ));

        leftStack.setVisible(currentChapterIndex > 1);
        if (leftStack.isVisible()) {
            leftChapterText.setText("Chapter " + (currentChapterIndex - 1));
        }

        rightStack.setVisible(currentChapterIndex < 5);
        if (rightStack.isVisible()) {
            rightChapterText.setText("Chapter " + (currentChapterIndex + 1));
        }

    }

    @FXML
    private void handleChooseChapter() {
        SoundManager.playClickSound();
        if (sceneManager != null) {
            GameSession.getInstance().setChapter(currentChapterIndex);
            sceneManager.switchTo("level");
        }
    }

}