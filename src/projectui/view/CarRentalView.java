package projectui.view;

import projectui.controller.CarRentalController;
import projectui.model.User;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class CarRentalView extends JFrame {
    private CarRentalController controller;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    private JTabbedPane customerTabbedPane;
    private JTable availableCarsTable;
    private JTable bookingsTable;
    private JTable transactionsTable;
    private JTable adminCarTable;



    public CarRentalView(CarRentalController controller) {
        this.controller = controller;
        setTitle("Car Rental System");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createRoleSelectionPanel(), "RoleSelection");
        mainPanel.add(createUserLoginPanel(), "UserLogin");
        mainPanel.add(createAdminLoginPanel(), "CompanyLogin");
        mainPanel.add(createRegisterPanel(), "UserRegister");
        mainPanel.add(createCompanyRegisterPanel(), "CompanyRegister");
        mainPanel.add(createUserDashboardPanel(), "UserDashboard");
        mainPanel.add(createCompanyDashboardPanel(), "CompanyDashboard");
        mainPanel.add(createAddCarPanel(), "AddCar");
        mainPanel.add(createEditSingleFieldPanel(), "EditSingleField");





        add(mainPanel);
        showView("RoleSelection");
    }

    private JPanel createRoleSelectionPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        JLabel title = new JLabel("Welcome to Car Rental System", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JButton customerBtn = new JButton("Customer Login/Register");
        JButton adminBtn = new JButton("Company/Admin Login");

        customerBtn.addActionListener(e -> showView("UserLogin"));
        adminBtn.addActionListener(e -> showView("CompanyLogin"));

        panel.add(title);
        panel.add(customerBtn);
        panel.add(adminBtn);

        ThemeManager.applyLoginTheme(panel);
        return panel;
    }

    private JPanel createUserLoginPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("New Customer? Register");
        JButton backBtn = new JButton("← Back");

        loginBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            boolean success = controller.authenticateUser(email, password, "user");
            JOptionPane.showMessageDialog(this, success ? "Login successful!" : "Login failed.");
        });

        registerBtn.addActionListener(e -> showView("UserRegister"));
        backBtn.addActionListener(e -> showView("RoleSelection"));

        panel.add(loginBtn);
        panel.add(registerBtn);
        panel.add(backBtn);

        ThemeManager.applyLoginTheme(panel);
        return panel;
    }

    private JPanel createAdminLoginPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Phone Number:"));
        panel.add(phoneField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        JButton loginBtn = new JButton("Login");
        JButton backBtn = new JButton("← Back");

        loginBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            boolean success = controller.authenticateAdmin(email, phone, password);
            JOptionPane.showMessageDialog(this, success ? "Login successful!" : "Login failed.");
        });

        backBtn.addActionListener(e -> showView("RoleSelection"));

        panel.add(loginBtn);
        panel.add(backBtn);

        // 🔧 INSERT THIS HERE
        JButton registerBtn = new JButton("New Company? Register");
        registerBtn.addActionListener(e -> showView("CompanyRegister"));
        panel.add(registerBtn);

        ThemeManager.applyLoginTheme(panel);
        return panel;
    }

    
    private JPanel createCompanyRegisterPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        JTextField companyNameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        panel.add(new JLabel("Company Name:"));
        panel.add(companyNameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        JButton registerBtn = new JButton("Register");
        JButton backBtn = new JButton("← Back");

        registerBtn.addActionListener(e -> {
            String companyName = companyNameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            boolean success = controller.registerCompany(companyName, email, phone, password);
            JOptionPane.showMessageDialog(this, success ? "Company registered successfully!" : "Registration failed.");
            if (success) showView("CompanyLogin");
        });

        backBtn.addActionListener(e -> showView("CompanyLogin"));

        panel.add(registerBtn);
        panel.add(backBtn);

        ThemeManager.applyFormTheme(panel);
        return panel;
    }


    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField addressField = new JTextField();

        panel.add(new JLabel("Full Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);

        JButton registerBtn = new JButton("Register");
        JButton backBtn = new JButton("← Back");

        registerBtn.addActionListener(e -> {
            User user = new User(
                nameField.getText().trim(),
                emailField.getText().trim(),
                phoneField.getText().trim(),
                new String(passwordField.getPassword()).trim(),
                addressField.getText().trim()
            );
            boolean success = controller.registerUser(user);
            JOptionPane.showMessageDialog(this, success ? "Registration successful!" : "Registration failed.");
            if (success) showView("UserLogin");
        });

        backBtn.addActionListener(e -> showView("UserLogin"));

        panel.add(registerBtn);
        panel.add(backBtn);

        ThemeManager.applyFormTheme(panel);
        return panel;
    }

    private JPanel createUserDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        customerTabbedPane = new JTabbedPane();
        ThemeManager.applyDashboardTheme(customerTabbedPane);

        availableCarsTable = new JTable();
        bookingsTable = new JTable();
        transactionsTable = new JTable();

        ThemeManager.applyTableTheme(availableCarsTable);
        ThemeManager.applyTableTheme(bookingsTable);
        ThemeManager.applyTableTheme(transactionsTable);

        JPanel availableCarsPanel = new JPanel(new BorderLayout());
        availableCarsPanel.add(new JScrollPane(availableCarsTable), BorderLayout.CENTER);

        JPanel bookingFormPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        JTextField nameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField addressField = new JTextField();
        JSpinner startDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner endDateSpinner = new JSpinner(new SpinnerDateModel());

        bookingFormPanel.setBorder(BorderFactory.createTitledBorder("Booking Details"));
        bookingFormPanel.add(new JLabel("Name:"));
        bookingFormPanel.add(nameField);
        bookingFormPanel.add(new JLabel("Phone:"));
        bookingFormPanel.add(phoneField);
        bookingFormPanel.add(new JLabel("Address:"));
        bookingFormPanel.add(addressField);
        bookingFormPanel.add(new JLabel("Start Date:"));
        bookingFormPanel.add(startDateSpinner);
        bookingFormPanel.add(new JLabel("End Date:"));
        bookingFormPanel.add(endDateSpinner);

        ThemeManager.applyFormTheme(bookingFormPanel);

        JButton confirmBookingBtn = new JButton("Confirm Booking");
        confirmBookingBtn.addActionListener(e -> {
            int selectedRow = availableCarsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(panel, "Please select a car to book.");
                return;
            }

            int carId = (int) availableCarsTable.getValueAt(selectedRow, 0);
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String address = addressField.getText().trim();

            if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Please fill in all booking details.");
                return;
            }

            LocalDate startDate = ((Date) startDateSpinner.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate endDate = ((Date) endDateSpinner.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            if (endDate.isBefore(startDate)) {
                JOptionPane.showMessageDialog(panel, "End date cannot be before start date.");
                return;
            }

            controller.bookCarWithDetails(carId, name, phone, address, startDate, endDate);

            nameField.setText("");
            phoneField.setText("");
            addressField.setText("");
            startDateSpinner.setValue(new Date());
            endDateSpinner.setValue(Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));

            controller.refreshCustomerDashboardData(customerTabbedPane);
        });

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(bookingFormPanel, BorderLayout.CENTER);
        bottomPanel.add(confirmBookingBtn, BorderLayout.SOUTH);

        availableCarsPanel.add(bottomPanel, BorderLayout.SOUTH);
        customerTabbedPane.addTab("Available Cars", availableCarsPanel);
        customerTabbedPane.addTab("My Bookings", new JScrollPane(bookingsTable));
        customerTabbedPane.addTab("Transactions", new JScrollPane(transactionsTable));

        panel.add(customerTabbedPane, BorderLayout.CENTER);
        ThemeManager.applyDashboardTheme(panel);
        return panel;
    }


    private JPanel createCompanyDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        adminCarTable = new JTable();
        JScrollPane tableScroll = new JScrollPane(adminCarTable);
        panel.add(tableScroll, BorderLayout.CENTER);
        ThemeManager.applyTableTheme(adminCarTable);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel statusLabel = new JLabel("Set Availability:");
        JComboBox<String> statusDropdown = new JComboBox<>(new String[]{"Available", "Booked", "Maintenance"});
        JButton updateButton = new JButton("Update");
        JButton addCarButton = new JButton("Add New Car");
        addCarButton.addActionListener(e -> showView("AddCar")); // This assumes you’ve registered "AddCar" view
        controlPanel.add(addCarButton);
        JButton editFieldBtn = new JButton("Edit Field");
        editFieldBtn.addActionListener(e -> showView("EditSingleField"));
        controlPanel.add(editFieldBtn);

        JButton deleteButton = new JButton("Delete Car");
        deleteButton.addActionListener(e -> {
            int selectedRow = adminCarTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a car to delete.");
                return;
            }

            int carId = (int) adminCarTable.getValueAt(selectedRow, 0); // Assuming car_id is column 0
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this car?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = controller.deleteCarById(carId);
                JOptionPane.showMessageDialog(this, success ? "Car deleted successfully." : "Failed to delete car.");
                if (success) controller.refreshAdminDashboardData(adminCarTable);
            }
        });
        controlPanel.add(deleteButton);

        updateButton.addActionListener(e -> {
            int selectedRow = adminCarTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a car from the table.");
                return;
            }

            int carId = (int) adminCarTable.getValueAt(selectedRow, 0); // Assuming car_id is in column 0
            String newStatus = (String) statusDropdown.getSelectedItem();

            boolean success = controller.updateCarAvailability(carId, newStatus);
            if (success) {
                JOptionPane.showMessageDialog(this, "Availability updated successfully.");
                controller.refreshAdminDashboardData(adminCarTable);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update availability.");
            }
        });

        controlPanel.add(statusLabel);
        controlPanel.add(statusDropdown);
        controlPanel.add(updateButton);
        panel.add(controlPanel, BorderLayout.SOUTH);

        ThemeManager.applyDashboardTheme(panel);
        return panel;
    }
    
    private JPanel createEditSingleFieldPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        JComboBox<String> fieldDropdown = new JComboBox<>(new String[]{
            "Model Name", "Car Type", "Registration No", "Seat Capacity", "Rent Per Day"
        });

        JTextField newValueField = new JTextField();
        JButton updateBtn = new JButton("Update Field");
        JButton backBtn = new JButton("← Back");

        panel.add(new JLabel("Select Field to Edit:"));
        panel.add(fieldDropdown);
        panel.add(new JLabel("New Value:"));
        panel.add(newValueField);
        panel.add(updateBtn);
        panel.add(backBtn);

        updateBtn.addActionListener(e -> {
            int selectedRow = adminCarTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a car from the table.");
                return;
            }

            int carId = (int) adminCarTable.getValueAt(selectedRow, 0);
            String fieldLabel = (String) fieldDropdown.getSelectedItem();
            String newValue = newValueField.getText().trim();

            // Map label to actual DB column
            String fieldName = switch (fieldLabel) {
                case "Model Name" -> "model_name";
                case "Car Type" -> "car_type";
                case "Registration No" -> "registration_no";
                case "Seat Capacity" -> "seat_capacity";
                case "Rent Per Day" -> "rent_per_day";
                default -> "";
            };

            boolean success = controller.updateCarField(carId, fieldName, newValue);
            JOptionPane.showMessageDialog(this, success ? "Field updated successfully." : "Failed to update field.");
            if (success) {
                controller.refreshAdminDashboardData(adminCarTable);
                showView("CompanyDashboard");
            }
        });

        backBtn.addActionListener(e -> showView("CompanyDashboard"));
        ThemeManager.applyFormTheme(panel);
        return panel;
    }


    
    private JPanel createAddCarPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        JTextField modelField = new JTextField();
        JTextField typeField = new JTextField();
        JTextField regField = new JTextField();
        JTextField seatField = new JTextField();
        JTextField rentField = new JTextField();

        panel.add(new JLabel("Model Name:"));
        panel.add(modelField);
        panel.add(new JLabel("Car Type:"));
        panel.add(typeField);
        panel.add(new JLabel("Registration No:"));
        panel.add(regField);
        panel.add(new JLabel("Seat Capacity:"));
        panel.add(seatField);
        panel.add(new JLabel("Rent Per Day:"));
        panel.add(rentField);

        JButton addBtn = new JButton("Add Car");
        JButton backBtn = new JButton("← Back");

        addBtn.addActionListener(e -> {
            try {
                String model = modelField.getText().trim();
                String type = typeField.getText().trim();
                String regNo = regField.getText().trim();
                int seats = Integer.parseInt(seatField.getText().trim());
                double rent = Double.parseDouble(rentField.getText().trim());

                boolean success = controller.addNewCar(model, type, regNo, seats, rent);
                JOptionPane.showMessageDialog(this, success ? "Car added successfully!" : "Failed to add car.");
                if (success) controller.refreshAdminDashboardData(adminCarTable); // if you have a table reference
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage());
            }
        });

        backBtn.addActionListener(e -> showView("CompanyDashboard"));

        panel.add(addBtn);
        panel.add(backBtn);

        ThemeManager.applyFormTheme(panel);
        return panel;
    }
    
    


    public void showView(String viewName) {
        cardLayout.show(mainPanel, viewName);
        switch (viewName) {
            case "UserLogin":
                setTitle("Car Rental - Customer Login");
                break;
            case "CompanyLogin":
                setTitle("Car Rental - Admin Login");
                break;
            case "UserRegister":
                setTitle("Car Rental - Register");
                break;
            case "UserDashboard":
                controller.refreshCustomerDashboardData(customerTabbedPane);
                setTitle("Customer Dashboard");
                break;
            case "CompanyDashboard":
                controller.refreshAdminDashboardData(adminCarTable);
                setTitle("Admin Dashboard");
                break;
            default:
                setTitle("Car Rental System");
        }
    }

    public void refreshCustomerDashboard() {
        controller.refreshCustomerDashboardData(customerTabbedPane);
    }

    public void refreshAdminDashboard() {
        controller.refreshAdminDashboardData(adminCarTable);
    }

    public JTable getAvailableCarsTable() {
        return availableCarsTable;
    }

    public JTabbedPane getCustomerTabbedPane() {
        return customerTabbedPane;
    }
}