package vnu.uet.goldexperience;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import vnu.uet.goldexperience.core.GameLoop;
import vnu.uet.goldexperience.core.Constants;
import vnu.uet.goldexperience.controller.InputHandler;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas( Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root);

        InputHandler input = new InputHandler(scene);

        GameLoop gameLoop = new GameLoop(canvas, input);
        gameLoop.start();

        stage.setTitle("Gold Experience");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
