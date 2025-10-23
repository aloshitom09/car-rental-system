package projectui.controller;

import projectui.model.*;
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
        if (!role.equals("user")) return false;

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

    public void bookCarWithDetails(int carId, String name, String phone, String address, LocalDate startDate, LocalDate endDate) {
        if (startDate.isBefore(LocalDate.now()) || endDate.isAfter(startDate.plusDays(2))) {
            JOptionPane.showMessageDialog(null, "Booking must be within 3 days and not in the past.");
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

    public boolean updateCarAvailability(int carId, String newStatus) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "UPDATE car SET availability_status = ? WHERE car_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, newStatus);
            stmt.setInt(2, carId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error updating availability: " + e.getMessage());
            return false;
        }
    }

    public void refreshCustomerDashboardData(JTabbedPane tabbedPane) {
        JTable availableCarsTable = findTable(tabbedPane.getComponentAt(0));
        JTable bookingsTable = findTable(tabbedPane.getComponentAt(1));
        JTable transactionsTable = findTable(tabbedPane.getComponentAt(2));

        if (availableCarsTable != null) {
            loadTableData("SELECT * FROM car", availableCarsTable);
        }
        if (bookingsTable != null) {
            loadTableData("SELECT * FROM booking WHERE user_id = " + currentUserId, bookingsTable);
        }
        if (transactionsTable != null) {
            loadTableData("SELECT t.* FROM transaction t JOIN booking b ON t.booking_id = b.booking_id WHERE b.user_id = " + currentUserId, transactionsTable);
        }
    }

    public void refreshAdminDashboardData(JTable carTable) {
        loadTableData("SELECT * FROM car", carTable);
    }

    private void loadTableData(String query, JTable table) {
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
        view.showView(role.equals("user") ? "UserLogin" : "CompanyLogin");
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

   