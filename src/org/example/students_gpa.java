package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class students_gpa extends JFrame {
    private JPanel panel1;
    private JButton button1;
    private JTextField textField1;
    private JButton viewButton;
    private JTable table1;

    public students_gpa() {
        setTitle("Student Information");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 600, 500);
        setContentPane(panel1);
        setVisible(true);


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
                String stuId = textField1.getText().trim();
                if (stuId.isEmpty()) {
                    displayAllGpa();
                } else {
                    displayStudentGpa(stuId);
                }
            }
        });

        displayAllGpa();
    }


    private void displayAllGpa() {
        String url = "jdbc:mysql://localhost:3306/tecmis";
        String user = "root";
        String password = "1234";
        String query = "SELECT * FROM student_gpa";

        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            DefaultTableModel tableModel = new DefaultTableModel();
            tableModel.addColumn("Student ID");
            tableModel.addColumn("Course Code");
            tableModel.addColumn("Weighted Score");
            tableModel.addColumn("GPA");

            while (resultSet.next()) {
                Object[] row = {
                        resultSet.getString("stuId"),
                        resultSet.getString("courseCode"),
                        resultSet.getDouble("weighted_score"),
                        resultSet.getDouble("gpa")
                };
                tableModel.addRow(row);
            }

            table1.setModel(tableModel);
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void displayStudentGpa(String stuId) {
        String url = "jdbc:mysql://localhost:3306/tecmis";
        String user = "root";
        String password = "1234";
        String query = "SELECT * FROM student_gpa WHERE stuId = ?";

        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, stuId);
            ResultSet resultSet = preparedStatement.executeQuery();

            DefaultTableModel tableModel = new DefaultTableModel();
            tableModel.addColumn("Student ID");
            tableModel.addColumn("Course Code");
            tableModel.addColumn("Weighted Score");
            tableModel.addColumn("GPA");

            double totalGpa = 0.0;
            int subjectCount = 0;

            while (resultSet.next()) {
                Object[] row = {
                        resultSet.getString("stuId"),
                        resultSet.getString("courseCode"),
                        resultSet.getDouble("weighted_score"),
                        resultSet.getDouble("gpa")
                };
                tableModel.addRow(row);


                totalGpa += resultSet.getDouble("gpa");
                subjectCount++;
            }

            if (subjectCount > 0) {
                double finalGpa = totalGpa / subjectCount;  // Calculate average GPA


                JOptionPane.showMessageDialog(this, "Final GPA for Student " + stuId + ": " + String.format("%.2f", finalGpa), "Final GPA", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No GPA data found for Student ID: " + stuId, "No Data", JOptionPane.INFORMATION_MESSAGE);
            }


            table1.setModel(tableModel);

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new students_gpa();
    }
}
