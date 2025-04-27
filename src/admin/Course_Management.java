package admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;

public class Course_Management extends JFrame {
    private JPanel coursePanel;
    private JTextField courseId;
    private JTextField courseName;
    private JTable table1;
    private JButton addCourseButton;
    private JButton updateCourseButton;
    private JButton deleteButton;
    private JTextField coursecredit;
    private JComboBox<String> coursetype;
    private JComboBox<String> lecID;
    private JPanel heading;
    private JButton backButton;

    private Connection con;
    private PreparedStatement pstmt;
    private ResultSet rs;
    private DefaultTableModel dtm;

    public Course_Management() {
        setContentPane(coursePanel);
        setTitle("Course Management");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);


        initializeDBConnection();
        loadLecturers();
        tableLoad();

        addCourseButton.addActionListener(e -> addCourse());
        updateCourseButton.addActionListener(e -> updateCourse());
        deleteButton.addActionListener(e -> deleteCourse());

        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table1.getSelectedRow();
                if (selectedRow >= 0) {
                    courseId.setText(table1.getValueAt(selectedRow, 0).toString());
                    courseName.setText(table1.getValueAt(selectedRow, 1).toString());
                    coursecredit.setText(table1.getValueAt(selectedRow, 2).toString());
                    coursetype.setSelectedItem(table1.getValueAt(selectedRow, 3).toString());
                    lecID.setSelectedItem(table1.getValueAt(selectedRow, 4).toString());
                }
            }
        });
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                admin_Dash admin = new admin_Dash();
                admin.setVisible(true);
                dispose();
            }
        });
    }

    private void initializeDBConnection() {
        String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/tecmis";
        String username = "root";
        String password = "1234";

        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, username, password);
            System.out.println("Database connected.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Connection failed: " + e.getMessage());
        }
    }

    private void loadLecturers() {
        try {
            lecID.removeAllItems();
            String sql = "SELECT lecId FROM lecturer";
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                lecID.addItem(rs.getString("lecId"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to load lecturers: " + e.getMessage());
        }
    }

    private void addCourse() {
        String credit = coursecredit.getText();
        String checkQuery = "SELECT * FROM course WHERE course_code = ?";
        String insertQuery = "INSERT INTO course(course_code, lecId, title, course_type, credit) VALUES (?, ?, ?, ?, ?)";

        if (isInputInvalid()) return;

        try {
            pstmt = con.prepareStatement(checkQuery);
            pstmt.setString(1, courseId.getText());
            rs = pstmt.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "Course already exists.");
                return;
            }

            int Ccredit = validateCredit(credit);


            pstmt = con.prepareStatement(insertQuery);
            pstmt.setString(1, courseId.getText());                           // course_code
            pstmt.setString(2, (String) lecID.getSelectedItem());            // lecId
            pstmt.setString(3, courseName.getText());                        // title
            pstmt.setString(4, (String) coursetype.getSelectedItem());       // course_type
            pstmt.setInt(5, Ccredit);                                        // credit

            int result = pstmt.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(null, "Course added successfully.");
                clearFields();
                tableLoad();
            }

        } catch (ValidationException | SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    private void updateCourse() {
        String credit = coursecredit.getText();
        String updateQuery = "UPDATE course SET course_code=?, lecId=?, title=?, course_type=?, credit=? WHERE course_code = ?";

        if (isInputInvalid()) return;

        try {
            int Ccredit = validateCredit(credit);


            pstmt = con.prepareStatement(updateQuery);
            pstmt.setString(1, courseId.getText());                           // course_code (new)
            pstmt.setString(2, (String) lecID.getSelectedItem());            // lecId
            pstmt.setString(3, courseName.getText());                        // title
            pstmt.setString(4, (String) coursetype.getSelectedItem());       // course_type
            pstmt.setInt(5, Ccredit);                                        // credit
            pstmt.setString(6, courseId.getText());                          // WHERE course_code

            int result = pstmt.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(null, "Course updated successfully.");
                clearFields();
                tableLoad();
            }

        } catch (ValidationException | SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    private void deleteCourse() {
        String deleteQuery = "DELETE FROM course WHERE course_code = ?";

        if (courseId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Enter Course ID to delete.");
            return;
        }

        try {
            pstmt = con.prepareStatement(deleteQuery);
            pstmt.setString(1, courseId.getText());

            int result = pstmt.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(null, "Course deleted.");
                clearFields();
                tableLoad();
            } else {
                JOptionPane.showMessageDialog(null, "Course not found.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    private void tableLoad() {
        String[] columnNames = {"Course ID", "Course Name", "Credit", "Course Type", "Lecturer ID"};
        dtm = new DefaultTableModel(columnNames, 0);

        try {
            String sql = "SELECT * FROM course";
            Statement stmt = con.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String id = rs.getString("course_code");
                String name = rs.getString("title");
                int credit = rs.getInt("credit");
                String typeStr = rs.getString("course_type");
                String lecturerId = rs.getString("lecId");

                dtm.addRow(new Object[]{id, name, credit, typeStr, lecturerId});
            }

            table1.setModel(dtm);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to load table: " + e.getMessage());
        }
    }

    private boolean isInputInvalid() {
        if (courseId.getText().isEmpty() || courseName.getText().isEmpty() || coursecredit.getText().isEmpty()
                || coursetype.getSelectedItem() == null || lecID.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(null, "Please fill in all fields.");
            return true;
        }
        return false;
    }

    private void clearFields() {
        courseId.setText("");
        courseName.setText("");
        coursecredit.setText("");
        coursetype.setSelectedItem(null);
        lecID.setSelectedItem(null);
    }

    private int validateCredit(String credit) throws ValidationException {
        try {
            int c = Integer.parseInt(credit);
            if (c <= 0 || c > 10) {
                throw new ValidationException("Credit must be between 1 and 10.");
            }
            return c;
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid credit value.");
        }
    }

    static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }

    public static void main(String[] args) {
        new Course_Management();
    }
}
