package vnu.uet.goldexperience.manager;

import com.google.gson.Gson;
import vnu.uet.goldexperience.model.*;
import vnu.uet.goldexperience.core.Constants;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LevelManager {

    private final List<Brick> activeBricks;
    private final Gson gson;

    public LevelManager() {
        this.activeBricks = new ArrayList<>();
        this.gson = new Gson();
    }

    public void loadLevel(int levelNumber) {
        activeBricks.clear();

        String levelFile = "/levels/level" + levelNumber + ".json";
        System.out.println("Đang tải màn chơi: " + levelFile);

        try (InputStream is = getClass().getResourceAsStream(levelFile);
             Reader reader = new InputStreamReader(is)) {

            if (is == null) {
                System.err.println("LỖI: Không tìm thấy file màn chơi: " + levelFile);
                return;
            }

            LevelData levelData = gson.fromJson(reader, LevelData.class);
            Map<String, String> key = levelData.getKey();
            List<String> layout = levelData.getLayout();

            for (int row = 0; row < layout.size(); row++) {
                String currentRow = layout.get(row);
                for (int col = 0; col < currentRow.length(); col++) {
                    char symbol = currentRow.charAt(col);
                    String type = key.get(String.valueOf(symbol));
                    if (type == null) continue;
                    else if (type.equals("normal")) {
                        NormalBrick brick = new NormalBrick(
                                Constants.NORMAL_BRICK_WIDTH * col,
                                Constants.NORMAL_BRICK_HEIGHT * row,
                                Constants.NORMAL_BRICK_WIDTH,
                                Constants.NORMAL_BRICK_HEIGHT
                        );
                        activeBricks.add(brick);
                    }
                    else  if (type.equals("unbreakable")) {
                        UnbreakableBrick brick = new UnbreakableBrick(
                                Constants.NORMAL_BRICK_WIDTH * col,
                                Constants.NORMAL_BRICK_HEIGHT * row,
                                Constants.NORMAL_BRICK_WIDTH,
                                Constants.NORMAL_BRICK_HEIGHT);
                        activeBricks.add(brick);
                    }
                    else  if (type.equals("explode")) {
                        ExplodeBrick brick = new ExplodeBrick(
                                Constants.NORMAL_BRICK_WIDTH * col,
                                Constants.NORMAL_BRICK_HEIGHT * row,
                                Constants.NORMAL_BRICK_WIDTH,
                                Constants.NORMAL_BRICK_HEIGHT);
                        activeBricks.add(brick);
                    }
                    else  if (type.equals("medium")) {
                        MediumBrick brick = new MediumBrick(
                                Constants.NORMAL_BRICK_WIDTH * col,
                                Constants.NORMAL_BRICK_HEIGHT * row,
                                Constants.NORMAL_BRICK_WIDTH,
                                Constants.NORMAL_BRICK_HEIGHT);
                        activeBricks.add(brick);
                    }

                }
            }

            System.out.println("ĐỌC FILE THÀNH CÔNG!");
        } catch (Exception e) {
            System.err.println("Lỗi khi tải hoặc xử lý file màn chơi: " + levelFile);
            e.printStackTrace();
        }
    }

    public List<Brick> getActiveBricks() {
        return activeBricks;
    }
}
