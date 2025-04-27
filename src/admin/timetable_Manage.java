package admin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class timetable_Manage extends JFrame {
    private JPanel timeTablePanel;
    private JTextField timetableIdField, deptField, courseCodeField, lecIdField, startTimeField, endTimeField;
    private JComboBox<String> sessionTypeComboBox, dayComboBox;
    private JButton addTimetableButton, searchTimetableButton, updateTimetableButton, deleteTimetableButton;

    private final String URL = "jdbc:mysql://localhost:3306/tecmis";
    private final String USER = "root";
    private final String PASSWORD = "1234";

    public timetable_Manage() {

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton("Back");
        backButton.setBackground(Color.decode("#3E5879"));
        backButton.setForeground(Color.WHITE);
        topPanel.add(backButton);


        timeTablePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;


        timetableIdField = new JTextField(10);
        deptField = new JTextField(10);
        courseCodeField = new JTextField(10);
        lecIdField = new JTextField(10);
        startTimeField = new JTextField(10);
        endTimeField = new JTextField(10);
        sessionTypeComboBox = new JComboBox<>(new String[]{"theory", "practical"});
        dayComboBox = new JComboBox<>(new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"});

        addTimetableButton = new JButton("Add");
        searchTimetableButton = new JButton("Search");
        updateTimetableButton = new JButton("Update");
        deleteTimetableButton = new JButton("Delete");

        // Updated button colors
        Color btnColor = Color.decode("#3E5879");

        addTimetableButton.setBackground(btnColor);
        searchTimetableButton.setBackground(btnColor);
        updateTimetableButton.setBackground(btnColor);
        deleteTimetableButton.setBackground(btnColor);

        addTimetableButton.setForeground(Color.WHITE);
        searchTimetableButton.setForeground(Color.WHITE);
        updateTimetableButton.setForeground(Color.WHITE);
        deleteTimetableButton.setForeground(Color.WHITE);


        String[] labels = {"Timetable ID (5 chars):", "Department (3 chars):", "Course Code (10 chars):", "Session Type:", "Lecturer ID (10 chars):", "Day:", "Start Time (HH:MM:SS):", "End Time (HH:MM:SS):"};
        Component[] fields = {timetableIdField, deptField, courseCodeField, sessionTypeComboBox, lecIdField, dayComboBox, startTimeField, endTimeField};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            timeTablePanel.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1;
            timeTablePanel.add(fields[i], gbc);
        }

        gbc.gridx = 0;
        gbc.gridy = labels.length;
        timeTablePanel.add(addTimetableButton, gbc);
        gbc.gridx = 1;
        timeTablePanel.add(searchTimetableButton, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        timeTablePanel.add(updateTimetableButton, gbc);
        gbc.gridx = 1;
        timeTablePanel.add(deleteTimetableButton, gbc);


        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(timeTablePanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
        setTitle("Timetable Manager");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);


        addTimetableButton.addActionListener(e -> addTimeTable());
        searchTimetableButton.addActionListener(e -> searchTimeTable());
        updateTimetableButton.addActionListener(e -> updateTimetable());
        deleteTimetableButton.addActionListener(e -> deleteTimetable());

        backButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Back button clicked.");
            dispose();
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                admin_Dash admin = new admin_Dash();
                admin.setVisible(true);
                dispose();
            }
        });

        setVisible(true);
    }

    private boolean validateInputs(boolean isIdRequired) {
        String id = timetableIdField.getText().trim();
        String dept = deptField.getText().trim();
        String courseCode = courseCodeField.getText().trim();
        String lecId = lecIdField.getText().trim();
        String startTime = startTimeField.getText().trim();
        String endTime = endTimeField.getText().trim();

        if (isIdRequired && (id.isEmpty() || id.length() != 5)) {
            JOptionPane.showMessageDialog(this, "Timetable ID must be exactly 5 characters.");
            return false;
        }

        if (dept.isEmpty() || dept.length() > 3) {
            JOptionPane.showMessageDialog(this, "Department must be 3 characters.");
            return false;
        }

        if (courseCode.isEmpty() || courseCode.length() > 10) {
            JOptionPane.showMessageDialog(this, "Course code must not exceed 10 characters.");
            return false;
        }

        if (lecId.isEmpty() || lecId.length() > 10) {
            JOptionPane.showMessageDialog(this, "Lecturer ID must not exceed 10 characters.");
            return false;
        }

        try {
            Time.valueOf(startTime);
            Time.valueOf(endTime);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Start/End Time must be in HH:MM:SS format.");
            return false;
        }

        return true;
    }

    private void addTimeTable() {
        if (!validateInputs(true)) return;

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "INSERT INTO timetable (ttId, department, courseCode, sessionType, lecId, day, startTime, endTime) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, timetableIdField.getText().trim());
            stmt.setString(2, deptField.getText().trim());
            stmt.setString(3, courseCodeField.getText().trim());
            stmt.setString(4, (String) sessionTypeComboBox.getSelectedItem());
            stmt.setString(5, lecIdField.getText().trim());
            stmt.setString(6, (String) dayComboBox.getSelectedItem());
            stmt.setTime(7, Time.valueOf(startTimeField.getText().trim()));
            stmt.setTime(8, Time.valueOf(endTimeField.getText().trim()));

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Timetable added successfully.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }
    }

    private void searchTimeTable() {
        String id = timetableIdField.getText().trim();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Timetable ID to search.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "SELECT * FROM timetable WHERE ttId = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                deptField.setText(rs.getString("department"));
                courseCodeField.setText(rs.getString("courseCode"));
                sessionTypeComboBox.setSelectedItem(rs.getString("sessionType"));
                lecIdField.setText(rs.getString("lecId"));
                dayComboBox.setSelectedItem(rs.getString("day"));
                startTimeField.setText(rs.getTime("startTime").toString());
                endTimeField.setText(rs.getTime("endTime").toString());
                JOptionPane.showMessageDialog(null, "Timetable found.");
            } else {
                JOptionPane.showMessageDialog(null, "No record found.");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void updateTimetable() {
        if (!validateInputs(true)) return;

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "UPDATE timetable SET department = ?, courseCode = ?, sessionType = ?, lecId = ?, day = ?, startTime = ?, endTime = ? WHERE ttId = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, deptField.getText().trim());
            stmt.setString(2, courseCodeField.getText().trim());
            stmt.setString(3, (String) sessionTypeComboBox.getSelectedItem());
            stmt.setString(4, lecIdField.getText().trim());
            stmt.setString(5, (String) dayComboBox.getSelectedItem());
            stmt.setTime(6, Time.valueOf(startTimeField.getText().trim()));
            stmt.setTime(7, Time.valueOf(endTimeField.getText().trim()));
            stmt.setString(8, timetableIdField.getText().trim());

            int rows = stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, rows + " record(s) updated.");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void deleteTimetable() {
        String id = timetableIdField.getText().trim();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Timetable ID to delete.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "DELETE FROM timetable WHERE ttId = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);

            int rows = stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, rows + " record(s) deleted.");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new timetable_Manage();
    }
}
