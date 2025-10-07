package vnu.uet.goldexperience.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MenuController {

    @FXML
    private VBox root;
    @FXML
    private Button startButton;

    @FXML
    private Button exitButton;

    @FXML
    public void initialize() {
        startButton.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/game.fxml"));
                Parent gameRoot = loader.load();
                Scene gameScene = new Scene(gameRoot);


                Stage stage = (Stage) startButton.getScene().getWindow();
                stage.setScene(gameScene);
                stage.setTitle("Arkanoid Game");
                stage.centerOnScreen();
                stage.show();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        exitButton.setOnAction(e -> {
            Stage stage = (Stage) exitButton.getScene().getWindow();
            stage.close();
        });
    }

}