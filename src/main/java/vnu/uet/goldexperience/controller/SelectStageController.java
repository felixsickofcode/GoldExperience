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

public class SelectStageController {
    private SceneManager sceneManager;

    @FXML
    private Button btnBack;

    @FXML
    private Button btnChooseStage;

    // Stage navigation components
    @FXML
    private StackPane leftStack;

    @FXML
    private StackPane rightStack;

    @FXML
    private AnchorPane centerStage;

    @FXML
    private Button leftTopButton;

    @FXML
    private Button rightTopButton;

    // Stage data
    private List<StageData> stages;
    private int currentCenterIndex = 3; // Start with Stage 4 in center (index 3)
    private static final int TOTAL_STAGES = 10; // Total number of stages

    // Inner class to hold stage data
    private static class StageData {
        String name;
        int stageNumber;

        StageData(int number) {
            this.stageNumber = number;
            this.name = "Stage " + number;
        }
    }

    // Set scene manager
    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @FXML
    public void initialize() {
        System.out.println("StageSelect initialized");
        initializeStages();
        updateStageDisplay();
    }

    private void initializeStages() {
        stages = new ArrayList<>();
        for (int i = 1; i <= TOTAL_STAGES; i++) {
            stages.add(new StageData(i));
        }
    }

    private void updateStageDisplay() {
        // Update center stage
        updateCenterStage();

        // Update left and right stacks
        updateLeftStack();
        updateRightStack();
    }

    private void updateCenterStage() {
        if (currentCenterIndex >= 0 && currentCenterIndex < stages.size()) {
            StageData centerData = stages.get(currentCenterIndex);
            // Find the text element in center stage and update it
            centerStage.getChildren().forEach(node -> {
                if (node instanceof Button) {
                    Button btn = (Button) node;
                    if (btn.getGraphic() instanceof AnchorPane) {
                        AnchorPane pane = (AnchorPane) btn.getGraphic();
                        pane.getChildren().forEach(child -> {
                            if (child instanceof Text) {
                                Text text = (Text) child;
                                if (text.getText().startsWith("Stage")) {
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
        // Update the visible stages on left stack
        // Index 0 (Bottom): currentCenterIndex - 3
        // Index 1 (Middle): currentCenterIndex - 2
        // Index 2 (Top): currentCenterIndex - 1

        for (int i = 0; i < leftStack.getChildren().size() && i < 3; i++) {
            int stageIndex = currentCenterIndex - (3 - i);
            if (stageIndex >= 0 && stageIndex < stages.size()) {
                AnchorPane pane = (AnchorPane) leftStack.getChildren().get(i);
                updateStagePane(pane, stages.get(stageIndex));
                pane.setVisible(true);
            } else {
                leftStack.getChildren().get(i).setVisible(false);
            }
        }
    }

    private void updateRightStack() {
        // Update the visible stages on right stack
        // Index 0 (Bottom): currentCenterIndex + 3
        // Index 1 (Middle): currentCenterIndex + 2
        // Index 2 (Top): currentCenterIndex + 1

        for (int i = 0; i < rightStack.getChildren().size() && i < 3; i++) {
            int stageIndex = currentCenterIndex + (3 - i);
            if (stageIndex >= 0 && stageIndex < stages.size()) {
                AnchorPane pane = (AnchorPane) rightStack.getChildren().get(i);
                updateStagePane(pane, stages.get(stageIndex));
                pane.setVisible(true);
            } else {
                rightStack.getChildren().get(i).setVisible(false);
            }
        }
    }

    private void updateStagePane(AnchorPane pane, StageData data) {
        pane.getChildren().forEach(node -> {
            if (node instanceof Button) {
                Button btn = (Button) node;
                if (btn.getGraphic() instanceof AnchorPane) {
                    AnchorPane graphicPane = (AnchorPane) btn.getGraphic();
                    graphicPane.getChildren().forEach(child -> {
                        if (child instanceof Text) {
                            Text text = (Text) child;
                            if (text.getText().startsWith("Stage")) {
                                text.setText(data.name);
                            }
                        }
                    });
                }
            }
        });
    }

    // Handle left navigation - move to previous stage
    @FXML
    public void onLeftClick(ActionEvent event) {
        if (currentCenterIndex > 0) {
            System.out.println("Moving left to Stage " + stages.get(currentCenterIndex - 1).stageNumber);
            currentCenterIndex--;
            sceneManager.animateStageTransition(centerStage, true, this::updateStageDisplay);
        } else {
            System.out.println("Already at first stage");
        }
    }

    // Handle right navigation - move to next stage
    @FXML
    public void onRightClick(ActionEvent event) {
        if (currentCenterIndex < stages.size() - 1) {
            System.out.println("Moving right to Stage " + stages.get(currentCenterIndex + 1).stageNumber);
            currentCenterIndex++;
            sceneManager.animateStageTransition(centerStage, false, this::updateStageDisplay);
        } else {
            System.out.println("Already at last stage");
        }
    }

    // When click on choose stage - Load game scene with selected stage
    @FXML
    private void handleChooseStage(ActionEvent event) {
        System.out.println("Choose Stage " + stages.get(currentCenterIndex).stageNumber + " clicked");
        if (sceneManager != null) {
            sceneManager.switchTo("game");
            Object controller = sceneManager.getController("game");
            if (controller instanceof GameController) {
                GameController gameController = (GameController) controller;
                gameController.startGame();
            }
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

    // Get current selected stage number
    public int getCurrentStageNumber() {
        return stages.get(currentCenterIndex).stageNumber;
    }
}