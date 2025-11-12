package vnu.uet.goldexperience.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PlayerDatabase {
    private static PlayerDatabase instance;
    private Connection connection;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/gold_experience";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Bminh0910@";

    private PlayerDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Database connected successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database connection failed!");
            e.printStackTrace();
        }
    }

    public static PlayerDatabase getInstance() {
        if (instance == null) {
            instance = new PlayerDatabase();
        }
        return instance;
    }

    /**
     * Thêm hoặc cập nhật player cho chapter và level cụ thể
     * CHỈ cập nhật nếu điểm mới cao hơn điểm hiện tại
     */
    public boolean addOrUpdatePlayer(String name, int chapter, int level, int score) {
        if (score <= 0) {
            System.out.println("Score must be greater than 0, skipping save");
            return false;
        }

        int currentScore = getPlayerScore(name, chapter, level);

        if (currentScore >= score) {
            System.out.println("Current score (" + currentScore + ") is higher than or equal to new score (" + score + "), skipping update");
            score = currentScore;
        }

        String sql = "INSERT INTO players (name, chapter, level, score) VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE score = GREATEST(score, ?), updated_at = CURRENT_TIMESTAMP";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setInt(2, chapter);
            stmt.setInt(3, level);
            stmt.setInt(4, score);
            stmt.setInt(5, score);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
//                System.out.println("✓ Saved score " + score + " for " + name + " (Chapter " + chapter + ", Level " + level + ")");
            }

            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("✗ Error adding/updating player: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy điểm số của player cho chapter và level cụ thể
     */
    public int getPlayerScore(String name, int chapter, int level) {
        String sql = "SELECT score FROM players WHERE name = ? AND chapter = ? AND level = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setInt(2, chapter);
            stmt.setInt(3, level);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("score");
            }

        } catch (SQLException e) {
            System.err.println("✗ Error getting player score: " + e.getMessage());
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * Cập nhật điểm số - CHỈ cập nhật nếu điểm mới cao hơn
     */
    public boolean updateScore(String name, int chapter, int level, int newScore) {
        if (newScore <= 0) {
            return false;
        }

        String sql = "UPDATE players SET score = ?, updated_at = CURRENT_TIMESTAMP " +
                "WHERE name = ? AND chapter = ? AND level = ? AND score < ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, newScore);
            stmt.setString(2, name);
            stmt.setInt(3, chapter);
            stmt.setInt(4, level);
            stmt.setInt(5, newScore);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("✗ Error updating score: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy top N players theo điểm cho chapter và level cụ thể
     */
    public List<PlayerScore> getTopPlayers(int chapter, int level, int limit) {
        List<PlayerScore> topPlayers = new ArrayList<>();
        String sql = "SELECT name, score FROM players " +
                "WHERE chapter = ? AND level = ? " +
                "ORDER BY score DESC LIMIT ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, chapter);
            stmt.setInt(2, level);
            stmt.setInt(3, limit);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("name");
                int score = rs.getInt("score");
                topPlayers.add(new PlayerScore(name, score));
            }

        } catch (SQLException e) {
            System.err.println("✗ Error getting top players: " + e.getMessage());
            e.printStackTrace();
        }

        return topPlayers;
    }

    /**
     * Lấy tổng điểm cao nhất của tất cả players (tổng từ tất cả chapter/level)
     */
    public List<PlayerScore> getTopPlayersByTotalScore(int limit) {
        List<PlayerScore> topPlayers = new ArrayList<>();
        String sql = "SELECT name, SUM(score) AS total_score FROM players " +
                "GROUP BY name ORDER BY total_score DESC LIMIT ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("name");
                int totalScore = rs.getInt("total_score");
                topPlayers.add(new PlayerScore(name, totalScore));
            }

        } catch (SQLException e) {
            System.err.println("✗ Error getting top players by total score: " + e.getMessage());
            e.printStackTrace();
        }

        return topPlayers;
    }

    /**
     * Kiểm tra xem player đã tồn tại cho chapter/level cụ thể chưa
     */
    public boolean playerExists(String name, int chapter, int level) {
        String sql = "SELECT COUNT(*) FROM players WHERE name = ? AND chapter = ? AND level = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setInt(2, chapter);
            stmt.setInt(3, level);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("✗ Error checking player existence: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Lấy tất cả records của một player
     */
    public List<PlayerLevelScore> getPlayerAllScores(String name) {
        List<PlayerLevelScore> scores = new ArrayList<>();
        String sql = "SELECT chapter, level, score FROM players " +
                "WHERE name = ? ORDER BY chapter, level";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int chapter = rs.getInt("chapter");
                int level = rs.getInt("level");
                int score = rs.getInt("score");
                scores.add(new PlayerLevelScore(name, chapter, level, score));
            }

        } catch (SQLException e) {
            System.err.println("✗ Error getting player scores: " + e.getMessage());
            e.printStackTrace();
        }

        return scores;
    }

    /**
     * Xóa player record cho chapter/level cụ thể
     */
    public boolean deletePlayer(String name, int chapter, int level) {
        String sql = "DELETE FROM players WHERE name = ? AND chapter = ? AND level = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setInt(2, chapter);
            stmt.setInt(3, level);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("✗ Error deleting player: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa tất cả records của một player
     */
    public boolean deletePlayerAllRecords(String name) {
        String sql = "DELETE FROM players WHERE name = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("✗ Error deleting player records: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Đóng kết nối database
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("✗ Error closing connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Inner class để lưu thông tin player (chỉ name + score)
     */
    public static class PlayerScore {
        private String name;
        private int score;

        public PlayerScore(String name, int score) {
            this.name = name;
            this.score = score;
        }

        public String getName() {
            return name;
        }

        public int getScore() {
            return score;
        }

        @Override
        public String toString() {
            return name + ": " + score;
        }
    }

    /**
     * Inner class để lưu thông tin đầy đủ (name + chapter + level + score)
     */
    public static class PlayerLevelScore {
        private String name;
        private int chapter;
        private int level;
        private int score;

        public PlayerLevelScore(String name, int chapter, int level, int score) {
            this.name = name;
            this.chapter = chapter;
            this.level = level;
            this.score = score;
        }

        public String getName() {
            return name;
        }

        public int getChapter() {
            return chapter;
        }

        public int getLevel() {
            return level;
        }

        public int getScore() {
            return score;
        }

        @Override
        public String toString() {
            return String.format("%s - Ch%d L%d: %d", name, chapter, level, score);
        }
    }
}