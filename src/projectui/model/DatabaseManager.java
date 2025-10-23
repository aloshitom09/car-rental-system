package projectui.model;

import java.sql.*;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/car_rental_system?useSSL=false";
    private static final String USER = "root"; // or your MySQL username
    private static final String PASSWORD = ""; // or your MySQL password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

public static boolean updateAvailability(int carId, String newStatus) {
    String query = "UPDATE Car SET availability_status = ? WHERE car_id = ?";
    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, newStatus);
        stmt.setInt(2, carId);
        return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
}

