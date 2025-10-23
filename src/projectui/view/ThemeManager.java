package projectui.view;

import javax.swing.*;
import java.awt.*;

public class ThemeManager {

    // 🔐 Login Page Theme
    public static void applyLoginTheme(JPanel panel) {
        panel.setBackground(new Color(0xF3F3F3));
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JLabel) {
                comp.setFont(new Font("Segoe UI", Font.BOLD, 16));
                comp.setForeground(new Color(0x333333));
            } else if (comp instanceof JTextField || comp instanceof JPasswordField) {
                comp.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                comp.setBackground(Color.WHITE);
                comp.setForeground(Color.BLACK);
            } else if (comp instanceof JButton) {
                styleButton((JButton) comp, new Color(0x0078D7));
            }
        }
    }

    // 🧾 Form Theme (Registration, Booking)
    public static void applyFormTheme(JPanel panel) {
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JLabel) {
                comp.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                comp.setForeground(new Color(0x333333));
            } else if (comp instanceof JTextField || comp instanceof JPasswordField || comp instanceof JSpinner) {
                comp.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                comp.setBackground(Color.WHITE);
                comp.setForeground(Color.BLACK);
            } else if (comp instanceof JButton) {
                styleButton((JButton) comp, new Color(0x28A745)); // green for confirm
            }
        }
    }

    // 📊 Dashboard Tabs Theme
    public static void applyDashboardTheme(JTabbedPane tabs) {
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabs.setBackground(new Color(0xF3F3F3));
        tabs.setForeground(new Color(0x333333));
    }

    // 🧩 Dashboard Panel Theme
    public static void applyDashboardTheme(JPanel panel) {
        panel.setBackground(new Color(0xF9F9F9));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    // 📋 Table Theme (Generic)
    public static void applyTableTheme(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(25);
        table.setGridColor(new Color(0xDDDDDD));
        table.setShowGrid(true);
        table.setSelectionBackground(new Color(0x0078D7));
        table.setSelectionForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(0xE0E0E0));
        table.getTableHeader().setForeground(new Color(0x333333));
    }

    // 🔘 Button Styling Helper
    public static void styleButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    }
}
