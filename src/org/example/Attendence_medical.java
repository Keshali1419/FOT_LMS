package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Attendence_medical extends JFrame {
    private JPanel panel1;
    private JButton button1;
    private JComboBox<String> comboBox1;
    private JButton viewButton;
    private JTable table1;

    public Attendence_medical() {
        setTitle("Student Information");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 600, 500);
        setContentPane(panel1);
        setVisible(true);


        comboBox1.addItem("ICT2113");
        comboBox1.addItem("ICT2142");
        comboBox1.addItem("ICT2152");
        comboBox1.addItem("ICT2122");
        comboBox1.addItem("ICT2133");

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newlec_dash d1 = new newlec_dash();
                d1.setVisible(true);
                dispose();
            }
        });

        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedCourseCode = (String) comboBox1.getSelectedItem();
                fetchAttendance(selectedCourseCode);
            }
        });
    }

    private void fetchAttendance(String courseCode) {
        String query = "SELECT stuId, attenDate, session_type, atten_status FROM attendance WHERE courseCode = ?";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/tecmis", "root", "1234");
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, courseCode);
            ResultSet resultSet = statement.executeQuery();


            DefaultTableModel model = new DefaultTableModel(new String[]{"Student ID", "Attendance Date", "Session Type", "Attendance Status"}, 0);

            while (resultSet.next()) {
                String studentId = resultSet.getString("stuId");
                String attenDate = resultSet.getString("attenDate");
                String sessionType = resultSet.getString("session_type");
                String attenStatus = resultSet.getString("atten_status");
                model.addRow(new Object[]{studentId, attenDate, sessionType, attenStatus});
            }

            table1.setModel(model);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching attendance data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new Attendence_medical();
    }
}