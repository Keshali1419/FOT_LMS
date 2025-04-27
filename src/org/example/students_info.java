package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class students_info extends JFrame {
    private JPanel panel1;
    private JButton backButton;
    private JTextField textField1;
    private JButton searchButton;
    private JTable table1;

    public students_info() {
        setTitle("Student Information");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 600);
        setContentPane(panel1);
        setVisible(true);


        loadAllData();

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String index = textField1.getText().trim();
                if (index.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter the index");
                    return;
                }
                searchData(index);
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manage_students_info b1 = new manage_students_info();
                b1.setVisible(true);
                dispose(); // Close window
            }
        });
    }

    private void loadAllData() {
        fetchData("SELECT * FROM user");
    }

    private void searchData(String index) {
        fetchData("SELECT * FROM user WHERE user_id = '" + index + "'");
    }

    private void fetchData(String query) {
        String url = "jdbc:mysql://localhost:3306/tecmis";
        String user = "root";
        String password = "1234";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, password);
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();


            String[] columnNames = {"User ID", "First Name", "Last Name", "Email", "Gender", "Telephone", "Password"};
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);

            while (rs.next()) {
                Object[] row = {
                        rs.getString("user_id"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("email"),
                        rs.getString("gender"),
                        rs.getString("telephone"),
                        rs.getString("password")
                };
                model.addRow(row);
            }

            table1.setModel(model);

            stmt.close();
            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new students_info();
    }
}
