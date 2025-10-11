package vnu.uet.goldexperience.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import vnu.uet.goldexperience.core.Constants;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Paddle extends MovableObject {

    private final double speed = Constants.PADDLE_SPEED;
    private int direction = 0;
    private int size = Constants.DEFAULT_PADDLE_SIZE;
    private final List<Image> imageList;

    public Paddle(double x, double y, double width, double height) {
        super(x, y, width, height, 0, 0);
        this.imageList = new ArrayList<>();

        try {
            for (int i = Constants.MIN_PADDLE_SIZE; i <= Constants.MAX_PADDLE_SIZE; ++i) {
                String imagePath = String.format("/images/paddle%d.png", i);
                URL imageURL = getClass().getResource(imagePath);

                if (imageURL == null) {
                    throw new IllegalArgumentException("Paddle image not found: " + imagePath);
                }

                this.imageList.add(new Image(imageURL.toExternalForm()));
                System.out.printf("Paddle image size %d loaded successfully%n", i);
            }

            setImageBySize(Constants.DEFAULT_PADDLE_SIZE);

        } catch (Exception e) {
            System.err.println("Error loading paddle images: " + e.getMessage());
            this.image = null;
        }
    }

    public void extendPaddle() {
        if (getSize() >= Constants.MAX_PADDLE_SIZE) {
            return;
        }

        setSize(getSize() + 1);
        setImageBySize(getSize());
        updateWidth();
    }

    public void narrowPaddle() {
        if (getSize() <= Constants.MIN_PADDLE_SIZE) {
            return;
        }

        setSize(getSize() - 1);
        setImageBySize(getSize());
        updateWidth();
    }

    private void setImageBySize(int size) {
        try {
            int index = size - Constants.MIN_PADDLE_SIZE;
            if (index >= 0 && index < imageList.size()) {
                image = imageList.get(index);
                updateWidth();
            } else {
                System.err.println("Invalid paddle size index: " + index);
            }
        } catch (Exception e) {
            System.err.println("Error changing image of Paddle: " + e.getMessage());
        }
    }

    private void updateWidth() {
        if (image != null) {
            width = image.getWidth();
            handlePaddleEdgeCollision();
        }
    }

    public void moveLeft() {
        direction = -1;
    }

    public void moveRight() {
        direction = 1;
    }

    public void stop() {
        direction = 0;
    }

    @Override
    public void update(double deltaTime) {
        dx = direction * speed;
        move(deltaTime);
        handlePaddleEdgeCollision();
    }

    public void handlePaddleEdgeCollision() {
        if (x < 0) setX(0);
        if (x + width > Constants.GAMEPLAYZONE_WIDTH)
            setX(Constants.GAMEPLAYZONE_WIDTH - width);
    }
    @Override
    public void render(GraphicsContext gc) {
        if (image != null)
            gc.drawImage(image, x, y - 5);
    }

    public int getSize() {
        return size;
    }

    public void setSize(int newSize) {
        if (newSize < Constants.MIN_PADDLE_SIZE || newSize > Constants.MAX_PADDLE_SIZE) {
            System.err.println("Invalid paddle size: " + newSize);
            return;
        }

        size = newSize;
    }

    public double getSpeed() {
        return speed;
    }
}