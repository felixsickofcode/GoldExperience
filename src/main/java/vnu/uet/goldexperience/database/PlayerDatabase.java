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

    // Thông tin kết nối database
    private static final String DB_URL = "jdbc:mysql://localhost:3306/gold_experience";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private PlayerDatabase() {
        try {
            // Load MySQL JDBC Driver
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

    // Singleton instance
    public static PlayerDatabase getInstance() {
        if (instance == null) {
            instance = new PlayerDatabase();
        }
        return instance;
    }

    /**
     * Thêm hoặc cập nhật player
     * @param name Tên player
     * @param score Điểm số
     * @return true nếu thành công
     */
    public boolean addOrUpdatePlayer(String name, int score) {
        String sql = "INSERT INTO players (name, score) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE score = ?, updated_at = CURRENT_TIMESTAMP";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setInt(2, score);
            stmt.setInt(3, score);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error adding/updating player: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy điểm số của player
     * @param name Tên player
     * @return Điểm số, hoặc -1 nếu không tìm thấy
     */
    public int getPlayerScore(String name) {
        String sql = "SELECT score FROM players WHERE name = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
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
     * Cập nhật điểm số của player
     * @param name Tên player
     * @param newScore Điểm số mới
     * @return true nếu thành công
     */
    public boolean updateScore(String name, int newScore) {
        String sql = "UPDATE players SET score = ?, updated_at = CURRENT_TIMESTAMP WHERE name = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, newScore);
            stmt.setString(2, name);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("✗ Error updating score: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy top N players theo điểm
     * @param limit Số lượng players
     * @return Danh sách PlayerScore
     */
    public List<PlayerScore> getTopPlayers(int limit) {
        List<PlayerScore> topPlayers = new ArrayList<>();
        String sql = "SELECT name, score FROM players ORDER BY score DESC LIMIT ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, limit);
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
     * Kiểm tra xem player đã tồn tại chưa
     * @param name Tên player
     * @return true nếu tồn tại
     */
    public boolean playerExists(String name) {
        String sql = "SELECT COUNT(*) FROM players WHERE name = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
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
     * Lấy tất cả players
     * @return Danh sách tất cả players
     */
    public List<PlayerScore> getAllPlayers() {
        List<PlayerScore> allPlayers = new ArrayList<>();
        String sql = "SELECT name, score FROM players ORDER BY score DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String name = rs.getString("name");
                int score = rs.getInt("score");
                allPlayers.add(new PlayerScore(name, score));
            }

        } catch (SQLException e) {
            System.err.println("✗ Error getting all players: " + e.getMessage());
            e.printStackTrace();
        }

        return allPlayers;
    }

    /**
     * Xóa player
     * @param name Tên player
     * @return true nếu thành công
     */
    public boolean deletePlayer(String name) {
        String sql = "DELETE FROM players WHERE name = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("✗ Error deleting player: " + e.getMessage());
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
     * Inner class để lưu thông tin player
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
}