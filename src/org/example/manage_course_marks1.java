package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class manage_course_marks1 extends JFrame {
    private JPanel panel1;
    private JButton backButton;
    private JComboBox<String> courseComboBox;
    private JComboBox<String> assessmentComboBox;
    private JTable table1;
    private JTextField textField1;
    private JTextField textField2;
    private JButton submitButton;
    private JButton viewButton;
    private JButton resetButton;

    private DefaultTableModel tableModel;

    private final String jdbcURL = "jdbc:mysql://localhost:3306/tecmis";
    private final String dbUser = "root";
    private final String dbPassword = "1234";

    public manage_course_marks1() {
        setTitle("Manage Course Marks");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 700, 600);
        setContentPane(panel1);
        setVisible(true);

        tableModel = new DefaultTableModel();
        tableModel.addColumn("Course Code");
        tableModel.addColumn("Assessment Type");
        tableModel.addColumn("Index Number");
        tableModel.addColumn("Marks");
        table1.setModel(tableModel);

        loadCourses();
        loadAssessments();

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String course = (String) courseComboBox.getSelectedItem();
                String assessment = (String) assessmentComboBox.getSelectedItem();
                String indexNumber = textField1.getText();
                String marksStr = textField2.getText();

                if (course == null || assessment == null || indexNumber.isEmpty() || marksStr.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill all fields.");
                    return;
                }

                try {
                    int marks = Integer.parseInt(marksStr);
                    boolean success = updateMarksInDatabase(indexNumber, course, assessment, marks);
                    if (success) {
                        tableModel.addRow(new Object[]{course, assessment, indexNumber, marks});
                        JOptionPane.showMessageDialog(null, "Marks updated successfully!");
                    } else {
                        JOptionPane.showMessageDialog(null, "No matching student/course found. No update made.");
                    }
                    textField1.setText("");
                    textField2.setText("");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Please enter valid numeric marks.");
                }
            }
        });

        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                table1.setModel(tableModel);
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tableModel.setRowCount(0);
                textField1.setText("");
                textField2.setText("");
                courseComboBox.setSelectedIndex(0);
                assessmentComboBox.setSelectedIndex(0);
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manage_students_info b1 = new manage_students_info();
                b1.setVisible(true);
                dispose();

            }
        });
    }

    private void loadCourses() {
        courseComboBox.addItem("ICT2113");
        courseComboBox.addItem("ICT2122");
        courseComboBox.addItem("ICT2133");
        courseComboBox.addItem("ICT2142");
        courseComboBox.addItem("ICT2152");
    }

    private void loadAssessments() {
        assessmentComboBox.addItem("Quiz 1");
        assessmentComboBox.addItem("Quiz 2");
        assessmentComboBox.addItem("Quiz 3");
        assessmentComboBox.addItem("Assessment");
    }

    private boolean updateMarksInDatabase(String indexNumber, String courseCode, String assessmentType, int marks) {
        Connection connection = null;
        PreparedStatement statement = null;
        boolean updated = false;

        try {
            connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
            String columnName = getColumnName(assessmentType);

            if (columnName == null) {
                return false;
            }

            String sql = "UPDATE marks SET " + columnName + " = ? WHERE stuId = ? AND courseCode = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, marks);
            statement.setString(2, indexNumber);
            statement.setString(3, courseCode);

            int rowsUpdated = statement.executeUpdate();
            updated = rowsUpdated > 0;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
        } finally {
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return updated;
    }

    private String getColumnName(String assessmentType) {
        switch (assessmentType) {
            case "Quiz 1": return "quiz1";
            case "Quiz 2": return "quiz2";
            case "Quiz 3": return "quiz3";
            case "Assessment": return "assessment";
            default: return null;
        }
    }

    public static void main(String[] args) {
        new manage_course_marks1();
    }
}
