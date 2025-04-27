package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Manage_profile extends JFrame {
    private JPanel panel1;
    private JButton updateButton;
    private JButton resetButton;
    private JTextField textField1;
    private JButton backbtn;
    private JPasswordField passwordField1;
    private JTextField textField2;
    private JTextField textField3;
    private JTextField textField4;
    private JTextField textField5;

    public Manage_profile() {
        setTitle("Lecture Profile");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);
        setLocationRelativeTo(null);
        setContentPane(panel1);
        setVisible(true);

        backbtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newlec_dash d1 = new newlec_dash();
                d1.setVisible(true);
                dispose();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String firstName = textField1.getText().trim();
                String lastName = textField3.getText().trim();
                String email = textField4.getText().trim();
                String telephone = textField5.getText().trim();
                String password1 = String.valueOf(passwordField1.getPassword()).trim();
                String userId = textField2.getText().trim();

                if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || telephone.isEmpty() || password1.isEmpty() || userId.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter all required values.");
                    return;
                }

                String driver = "com.mysql.cj.jdbc.Driver";
                String url = "jdbc:mysql://localhost:3306/tecmis";
                String user = "root";
                String dbPassword = "1234";

                try {
                    Class.forName(driver);
                    Connection con = DriverManager.getConnection(url, user, dbPassword);

                    String query = "UPDATE user SET firstname = ?, lastname = ?, email = ?, telephone = ?, password = ? WHERE user_id = ?";
                    PreparedStatement ps = con.prepareStatement(query);
                    ps.setString(1, firstName);
                    ps.setString(2, lastName);
                    ps.setString(3, email);
                    ps.setString(4, telephone);
                    ps.setString(5, password1);
                    ps.setString(6, userId);

                    int rows = ps.executeUpdate();

                    if (rows > 0) {
                        JOptionPane.showMessageDialog(null, "Lecture Profile Updated Successfully!");
                    } else {
                        JOptionPane.showMessageDialog(null, "No matching record found for the provided User ID.");
                    }

                    con.close();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textField1.setText("");
                textField2.setText("");
                passwordField1.setText("");
                textField3.setText("");
                textField4.setText("");
                textField5.setText("");
            }
        });
    }

    public static void main(String[] args) {
        new Manage_profile();
    }
}
