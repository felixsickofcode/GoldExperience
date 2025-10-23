package vnu.uet.goldexperience.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import vnu.uet.goldexperience.manager.SceneManager;

import java.util.ArrayList;
import java.util.List;

public class SelectChapterController {
    private SceneManager sceneManager;

    @FXML
    private Button btnBack;

    @FXML
    private Button btnChooseChapter;

    // Chapter navigation components
    @FXML
    private StackPane leftStack;

    @FXML
    private StackPane rightStack;

    @FXML
    private AnchorPane centerChapter;

    @FXML
    private Button leftTopButton;

    @FXML
    private Button rightTopButton;

    // Chapter data
    private List<ChapterData> chapters;
    private int currentCenterIndex = 2; // Start with Chapter 3 in center
    private static final int TOTAL_CHAPTERS = 5; // Total number of chapters

    protected int getCurrentCenterIndex() {
        return currentCenterIndex;
    }

    public void setCurrentCenterIndex(int currentCenterIndex) {
        this.currentCenterIndex = currentCenterIndex;
    }

    // Inner class to hold chapter data
    private static class ChapterData {
        String name;
        int chapterNumber;

        ChapterData(int number) {
            this.chapterNumber = number;
            this.name = "Chapter " + number;
        }
    }

    // Set scene manager
    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @FXML
    public void initialize() {
        System.out.println("ChapterSelect initialized");
        initializeChapters();
        updateChapterDisplay();
    }

    private void initializeChapters() {
        chapters = new ArrayList<>();
        for (int i = 1; i <= TOTAL_CHAPTERS; i++) {
            chapters.add(new ChapterData(i));
        }
    }

    private void updateChapterDisplay() {
        // Update center chapter
        updateCenterChapter();

        // Update left and right stacks
        updateLeftStack();
        updateRightStack();
    }

    private void updateCenterChapter() {
        if (currentCenterIndex >= 0 && currentCenterIndex < chapters.size()) {
            ChapterData centerData = chapters.get(currentCenterIndex);
            // Find the text element in center chapter and update it
            centerChapter.getChildren().forEach(node -> {
                if (node instanceof Button) {
                    Button btn = (Button) node;
                    if (btn.getGraphic() instanceof AnchorPane) {
                        AnchorPane pane = (AnchorPane) btn.getGraphic();
                        pane.getChildren().forEach(child -> {
                            if (child instanceof Text) {
                                Text text = (Text) child;
                                if (text.getText().startsWith("Chapter")) {
                                    text.setText(centerData.name);
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    private void updateLeftStack() {
        // Update the visible chapters on left stack
        // leftStack has 2 children in FXML:
        // Index 0 (Bottom): currentCenterIndex - 2
        // Index 1 (Top): currentCenterIndex - 1

        if (leftStack.getChildren().size() >= 2) {
            // Update bottom card (Index 0)
            int bottomChapterIndex = currentCenterIndex - 2;
            if (bottomChapterIndex >= 0 && bottomChapterIndex < chapters.size()) {
                AnchorPane pane = (AnchorPane) leftStack.getChildren().get(0);
                updateChapterPane(pane, chapters.get(bottomChapterIndex));
                pane.setVisible(true);
            } else {
                leftStack.getChildren().get(0).setVisible(false);
            }

            // Update top card (Index 1)
            int topChapterIndex = currentCenterIndex - 1;
            if (topChapterIndex >= 0 && topChapterIndex < chapters.size()) {
                AnchorPane pane = (AnchorPane) leftStack.getChildren().get(1);
                updateChapterPane(pane, chapters.get(topChapterIndex));
                pane.setVisible(true);
            } else {
                leftStack.getChildren().get(1).setVisible(false);
            }
        }
    }

    private void updateRightStack() {
        // Update the visible chapters on right stack
        // rightStack has 2 children in FXML:
        // Index 0 (Bottom): currentCenterIndex + 2
        // Index 1 (Top): currentCenterIndex + 1

        if (rightStack.getChildren().size() >= 2) {
            // Update bottom card (Index 0)
            int bottomChapterIndex = currentCenterIndex + 2;
            if (bottomChapterIndex >= 0 && bottomChapterIndex < chapters.size()) {
                AnchorPane pane = (AnchorPane) rightStack.getChildren().get(0);
                updateChapterPane(pane, chapters.get(bottomChapterIndex));
                pane.setVisible(true);
            } else {
                rightStack.getChildren().get(0).setVisible(false);
            }

            // Update top card (Index 1)
            int topChapterIndex = currentCenterIndex + 1;
            if (topChapterIndex >= 0 && topChapterIndex < chapters.size()) {
                AnchorPane pane = (AnchorPane) rightStack.getChildren().get(1);
                updateChapterPane(pane, chapters.get(topChapterIndex));
                pane.setVisible(true);
            } else {
                rightStack.getChildren().get(1).setVisible(false);
            }
        }
    }

    private void updateChapterPane(AnchorPane pane, ChapterData data) {
        pane.getChildren().forEach(node -> {
            if (node instanceof Button) {
                Button btn = (Button) node;
                if (btn.getGraphic() instanceof AnchorPane) {
                    AnchorPane graphicPane = (AnchorPane) btn.getGraphic();
                    graphicPane.getChildren().forEach(child -> {
                        if (child instanceof Text) {
                            Text text = (Text) child;
                            if (text.getText().startsWith("Chapter")) {
                                text.setText(data.name);
                            }
                        }
                    });
                }
            }
        });
    }

    // Handle left navigation - move to previous chapter
    @FXML
    public void onLeftClick(ActionEvent event) {
        if (currentCenterIndex > 0) {
            System.out.println("Moving left to Chapter " + chapters.get(currentCenterIndex - 1).chapterNumber);
            currentCenterIndex--;
            sceneManager.animateStageTransition(centerChapter, true, this::updateChapterDisplay);
        } else {
            System.out.println("Already at first chapter");
        }
    }

    @FXML
    public void onRightClick(ActionEvent event) {
        if (currentCenterIndex < TOTAL_CHAPTERS - 1) {
            System.out.println("Moving right to Chapter " + chapters.get(currentCenterIndex + 1).chapterNumber);
            currentCenterIndex++;
            sceneManager.animateStageTransition(centerChapter, false, this::updateChapterDisplay);
        } else {
            System.out.println("Already at last chapter");
        }
    }

    @FXML
    private void handleChooseChapter(ActionEvent event) {
        System.out.println("Choose Chapter " + chapters.get(currentCenterIndex).chapterNumber + " clicked");
        if (sceneManager != null) {
            sceneManager.switchTo("level");
        }
    }

    // Handle back to menu
    @FXML
    private void handleBackToMenu(ActionEvent event) {
        System.out.println("Back to menu clicked");
        if (sceneManager != null) {
            sceneManager.switchTo("menu");
        }
    }

    // Get current selected chapter number
    public int getCurrentChapterNumber() {
        return chapters.get(currentCenterIndex).chapterNumber;
    }
}