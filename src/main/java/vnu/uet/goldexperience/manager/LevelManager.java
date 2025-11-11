package vnu.uet.goldexperience.manager;

import com.google.gson.Gson;
import vnu.uet.goldexperience.core.Constants;
import vnu.uet.goldexperience.model.*;
import vnu.uet.goldexperience.model.brick.Brick;
import vnu.uet.goldexperience.model.brickFactory.BrickType;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LevelManager {

    private final Gson gson;

    public LevelManager() {
        this.gson = new Gson();
    }

    public List<Brick> loadLevel(int levelNumber) {
        List<Brick> bricks = new ArrayList<>();
        String levelFile = "/levels/level" + levelNumber + ".json";
        System.out.println("Đang tải màn chơi: " + levelFile);

        try (InputStream is = getClass().getResourceAsStream(levelFile)) {
            if (is == null) {
                System.err.println("LỖI: Không tìm thấy file: " + levelFile);
                return bricks;
            }

            Reader reader = new InputStreamReader(Objects.requireNonNull(is));
            LevelData levelData = gson.fromJson(reader, LevelData.class);
            Map<String, String> key = levelData.getKey();
            List<String> layout = levelData.getLayout();
            Map<String, Map<String, Double>> props = levelData.getProperties();

            for (int row = 0; row < layout.size(); row++) {
                String currentRow = layout.get(row);
                for (int col = 0; col < currentRow.length(); col++) {
                    char symbol = currentRow.charAt(col);
                    String typeString = key.get(String.valueOf(symbol));
                    double brickX = Constants.NORMAL_BRICK_WIDTH * col;
                    double brickY = Constants.NORMAL_BRICK_HEIGHT * row;
                    BrickType brickType = BrickType.fromString(typeString);
                    if (brickType != null) {
                        Map<String, Double> config = props != null ? props.get(typeString) : null;
                        Brick brick = brickType.create(brickX, brickY, config);
                        bricks.add(brick);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tải hoặc xử lý file: " + levelFile);
            e.printStackTrace();
        }
        return bricks;
    }
}