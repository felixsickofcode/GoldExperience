package vnu.uet.goldexperience.effect;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.List;

public class BallTrail {
    public void render(GraphicsContext gc, List<double[]> trail, double width, double height) {
        for (int i = 0; i < trail.size(); i++) {
            double[] pos = trail.get(i);
            double t = (double) i / trail.size();
            if (Math.random() < t * 0.25) continue;

            double alpha = (1 - t * 0.8) * (0.4 + Math.random() * 0.2);
            double scale = 0.8 - t * 0.7;

            double w = width * scale;
            double h = height * scale;

            Color start = Color.web("#ffffff");
            Color end = Color.web("#66ffff");
            Color trailColor = start.interpolate(end, t);

            gc.setGlobalAlpha(alpha);
            gc.setFill(trailColor);
            gc.fillOval(pos[0] - w / 2, pos[1] - h / 2, w, h);
        }
    }
}
