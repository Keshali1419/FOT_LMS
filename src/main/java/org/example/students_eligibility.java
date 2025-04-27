package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class students_eligibility extends JFrame {
    private JPanel panel1;
    private JButton button1;
    private JButton viewButton;
    private JTable table1;
    private JTextField textField1;

    public students_eligibility() {
        setTitle("Student Eligibility Information");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 700, 500);
        setContentPane(panel1);
        setVisible(true);


        table1.setModel(new DefaultTableModel(new String[]{"Student ID", "Course Code", "Eligibility Status"}, 0));

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manage_students_info d1 = new manage_students_info();
                d1.setVisible(true);
                dispose();
            }
        });

        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fetchEligibleStudents();
            }
        });
    }

    private void fetchEligibleStudents() {
        String studentIdInput = textField1.getText().trim();
        if (studentIdInput.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Student ID.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String query = "SELECT stuId, courseCode, " +
                "(COALESCE(quiz1, 0) + COALESCE(quiz2, 0) + COALESCE(quiz3, 0) + COALESCE(assessment, 0) + COALESCE(mid, 0)) / 5 AS average_score " +
                "FROM marks WHERE stuId = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/tecmis", "root", "1234");
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, studentIdInput);
            ResultSet resultSet = statement.executeQuery();

            DefaultTableModel model = (DefaultTableModel) table1.getModel();
            model.setRowCount(0); // Clear old results

            boolean found = false;
            while (resultSet.next()) {
                found = true;
                String studentId = resultSet.getString("stuId");
                String courseCode = resultSet.getString("courseCode");
                double averageScore = resultSet.getDouble("average_score");
                String eligibilityStatus = determineEligibility(averageScore);

                model.addRow(new Object[]{studentId, courseCode, eligibilityStatus});
            }

            if (!found) {
                JOptionPane.showMessageDialog(this, "No records found for student ID: " + studentIdInput, "No Records", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String determineEligibility(double averageScore) {
        return averageScore >= 50 ? "Eligible" : "Not Eligible";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new students_eligibility());
    }
}
