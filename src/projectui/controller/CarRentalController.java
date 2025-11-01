package projectui.controller;

import projectui.model.*;
import java.util.List;
import java.util.Arrays;
import projectui.view.CarRentalView;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class CarRentalController {
    private CarRentalView view;
    private int currentUserId = -1;
    private String currentUserName = "";
    private String currentUserRole = "";

    public CarRentalController() {
        view = new CarRentalView(this);
        view.setVisible(true);
    }

    public boolean registerUser(User user) {
        if (user.getFullName().isEmpty() || user.getEmail().isEmpty() || user.getPhone().isEmpty()
                || user.getPassword().isEmpty() || user.getAddress().isEmpty()) {
            JOptionPane.showMessageDialog(null, "All fields are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "INSERT INTO user (full_name, email, phone, password, address) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPhone());
            stmt.setString(4, user.getPassword());
            stmt.setString(5, user.getAddress());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            return false;
        }
    }

    public boolean authenticateUser(String email, String password, String role) {
        // Only allow user role to call this method
        if (role == null || !role.equals("user")) return false;

        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT user_id, full_name FROM user WHERE email = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                currentUserId = rs.getInt("user_id");
                currentUserName = rs.getString("full_name");
                currentUserRole = "user";
                view.showView("UserDashboard");
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Invalid user credentials.");
                return false;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            return false;
        }
    }

    public boolean authenticateAdmin(String email, String phone, String password) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT company_id, company_name FROM company WHERE email = ? AND phone = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, phone);
            stmt.setString(3, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                currentUserId = rs.getInt("company_id");
                currentUserName = rs.getString("company_name");
                currentUserRole = "company";

                System.out.println("Logged-in company ID: " + currentUserId);

                view.showView("CompanyDashboard");
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Invalid admin credentials.");
                return false;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            return false;
        }
    }

    public boolean registerCompany(String name, String email, String phone, String password) {
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            return false;
        }

        String query = "INSERT INTO company (company_name, email, phone, password) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.setString(4, password); // consider hashing in future

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void bookCarWithDetails(int carId, String name, String phone, String address, LocalDate startDate, LocalDate endDate) {
        if (startDate.isBefore(LocalDate.now()) || endDate.isAfter(startDate.plusDays(2))) {
            JOptionPane.showMessageDialog(null, "Booking must be within 3 days and not in the past.");
            return;
        }

        if (currentUserId <= 0) {
            JOptionPane.showMessageDialog(null, "User not authenticated. Please login.");
            return;
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            String checkSql = "SELECT availability_status, rent_per_day FROM car WHERE car_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, carId);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next() || !"Available".equalsIgnoreCase(rs.getString("availability_status"))) {
                JOptionPane.showMessageDialog(null, "Car is not available.");
                return;
            }

            double rentPerDay = rs.getDouble("rent_per_day");
            long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
            double totalAmount = rentPerDay * days;

            String bookingSql = "INSERT INTO booking (user_id, car_id, start_date, end_date, status) VALUES (?, ?, ?, ?, 'Active')";
            PreparedStatement bookingStmt = conn.prepareStatement(bookingSql, Statement.RETURN_GENERATED_KEYS);
            bookingStmt.setInt(1, currentUserId);
            bookingStmt.setInt(2, carId);
            bookingStmt.setDate(3, Date.valueOf(startDate));
            bookingStmt.setDate(4, Date.valueOf(endDate));
            bookingStmt.executeUpdate();

            ResultSet keys = bookingStmt.getGeneratedKeys();
            if (keys.next()) {
                int bookingId = keys.getInt(1);

                String txnSql = "INSERT INTO transaction (booking_id, amount, payment_method) VALUES (?, ?, 'Online')";
                PreparedStatement txnStmt = conn.prepareStatement(txnSql);
                txnStmt.setInt(1, bookingId);
                txnStmt.setDouble(2, totalAmount);
                txnStmt.executeUpdate();

                String updateSql = "UPDATE car SET availability_status = 'Booked' WHERE car_id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setInt(1, carId);
                updateStmt.executeUpdate();

                JOptionPane.showMessageDialog(null, "Booking successful. ₹" + totalAmount + " paid.");
                view.refreshCustomerDashboard();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    public boolean addNewCar(String modelName, String carType, String regNo, int seatCapacity, double rentPerDay) {
        String query = """
            INSERT INTO car (company_id, model_name, car_type, registration_no, seat_capacity, rent_per_day)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        if (currentUserId <= 0) return false;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, currentUserId);
            stmt.setString(2, modelName);
            stmt.setString(3, carType);
            stmt.setString(4, regNo);
            stmt.setInt(5, seatCapacity);
            stmt.setDouble(6, rentPerDay);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCarById(int carId) {
        if (currentUserId <= 0) return false;
        String query = "DELETE FROM car WHERE car_id = ? AND company_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, carId);
            stmt.setInt(2, currentUserId); // Ensures company can only delete its own cars

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCarField(int carId, String fieldName, String newValue) {
        // Validate allowed fields to prevent SQL injection
        List<String> allowedFields = Arrays.asList("model_name", "car_type", "registration_no", "seat_capacity", "rent_per_day");

        if (!allowedFields.contains(fieldName)) return false;
        if (currentUserId <= 0) return false;

        String query = "UPDATE car SET " + fieldName + " = ? WHERE car_id = ? AND company_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Handle numeric fields
            if (fieldName.equals("seat_capacity")) {
                stmt.setInt(1, Integer.parseInt(newValue));
            } else if (fieldName.equals("rent_per_day")) {
                stmt.setDouble(1, Double.parseDouble(newValue));
            } else {
                stmt.setString(1, newValue);
            }

            stmt.setInt(2, carId);
            stmt.setInt(3, currentUserId);

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCarAvailability(int carId, String newStatus) {
        if (currentUserId <= 0) return false;
        String query = "UPDATE car SET availability_status = ? WHERE car_id = ? AND company_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, carId);
            stmt.setInt(3, currentUserId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void refreshCustomerDashboardData(JTabbedPane tabbedPane) {
        if (tabbedPane == null) return;

        JTable availableCarsTable = findTable(tabbedPane.getComponentAt(0));
        JTable bookingsTable = findTable(tabbedPane.getComponentAt(1));
        JTable transactionsTable = findTable(tabbedPane.getComponentAt(2));

        if (availableCarsTable != null) {
            String query = """
                SELECT 
                    c.car_id,
                    comp.company_name,
                    c.model_name,
                    c.car_type,
                    c.registration_no,
                    c.seat_capacity,
                    c.rent_per_day,
                    c.availability_status,
                    c.created_at
                FROM car c
                JOIN company comp ON c.company_id = comp.company_id
            """;
            loadTableData(query, availableCarsTable);
        }

        if (bookingsTable != null && currentUserId > 0) {
            loadTableData("SELECT * FROM booking WHERE user_id = " + currentUserId, bookingsTable);
        }

        if (transactionsTable != null && currentUserId > 0) {
            loadTableData("SELECT t.* FROM transaction t JOIN booking b ON t.booking_id = b.booking_id WHERE b.user_id = " + currentUserId, transactionsTable);
        }
    }

    public void refreshAdminDashboardData(JTable carTable) {
        if (carTable == null || currentUserId <= 0) return;

        String query = """
            SELECT 
                c.car_id,
                comp.company_name,
                c.model_name,
                c.car_type,
                c.registration_no,
                c.seat_capacity,
                c.rent_per_day,
                c.availability_status,
                c.created_at
            FROM car c
            JOIN company comp ON c.company_id = comp.company_id
            WHERE c.company_id = """ + currentUserId;

        loadTableData(query, carTable);
    }

    public boolean recordTransaction(int bookingId, String method, double amount) {
        String query = "INSERT INTO transaction (booking_id, payment_method, amount, status, created_at) VALUES (?, ?, ?, 'Completed', NOW())";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bookingId);
            stmt.setString(2, method);
            stmt.setDouble(3, amount);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private double calculateRent(LocalDate start, LocalDate end, double rentPerDay) {
        long days = ChronoUnit.DAYS.between(start, end) + 1;
        return days * rentPerDay;
    }

    private void loadTableData(String query, JTable table) {
        if (table == null || query == null || query.isEmpty()) return;

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            DefaultTableModel model = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            for (int i = 1; i <= columnCount; i++) {
                model.addColumn(meta.getColumnName(i));
            }

            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                model.addRow(row);
            }

            table.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private JTable findTable(Component comp) {
        if (comp instanceof JTable) return (JTable) comp;
        if (comp instanceof Container) {
            for (Component child : ((Container) comp).getComponents()) {
                JTable result = findTable(child);
                if (result != null) return result;
            }
        }
        return null;
    }

    public void showLogin(String role) {
        view.showView(role != null && role.equals("user") ? "UserLogin" : "CompanyLogin");
    }

    public int getCurrentUserId() {
        return currentUserId;
    }

    public String getCurrentUserName() {
        return currentUserName;
    }

    public String getCurrentUserRole() {
        return currentUserRole;
    }
}
