package admin;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class user_M extends JFrame {
    private JPanel panel1;
    private JButton add;
    private JButton update;
    private JButton delete;
    private JTextField userId;
    private JTextField Fname;
    private JTextField Lname;
    private JTextField Email;
    private JComboBox<String> GenderCombo;
    private JTextField Telephone;
    private JPasswordField Password;
    private JButton backButton;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/tecmis";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "1234";


    public user_M() {
        setTitle("User Management");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(panel1);
        setVisible(true);

        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addUser();
            }
        });

        update.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateUser();
            }
        });

        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteUser();
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


    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }


    private void addUser() {
        String id = userId.getText();
        String firstName = Fname.getText();
        String lastName = Lname.getText();
        String email = Email.getText();
        String gender = (String) GenderCombo.getSelectedItem();
        String telephone = Telephone.getText();
        String password = new String(Password.getPassword());

        String query = "INSERT INTO user (user_id, firstName, lastName, email, gender, telephone, password) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, id);
            ps.setString(2, firstName);
            ps.setString(3, lastName);
            ps.setString(4, email);
            ps.setString(5, gender);
            ps.setString(6, telephone);
            ps.setString(7, password);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "User added successfully!");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error adding user: " + ex.getMessage());
        }

        clearFields();
    }


    private void updateUser() {
        String id = userId.getText();
        String firstName = Fname.getText();
        String lastName = Lname.getText();
        String email = Email.getText();
        String gender = (String) GenderCombo.getSelectedItem();
        String telephone = Telephone.getText();
        String password = new String(Password.getPassword());

        String query = "UPDATE user SET firstName = ?, lastName = ?, email = ?, gender = ?, telephone = ?, password = ? WHERE user_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {


            System.out.println("Executing query: " + query);
            System.out.println("Parameters:");
            System.out.println("firstName = " + firstName);
            System.out.println("lastName = " + lastName);
            System.out.println("email = " + email);
            System.out.println("gender = " + gender);
            System.out.println("telephone = " + telephone);
            System.out.println("password = " + password);
            System.out.println("user_id = " + id);

            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, email);
            ps.setString(4, gender);
            ps.setString(5, telephone);
            ps.setString(6, password);
            ps.setString(7, id);

            int result = ps.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(null, "User updated successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "User not found or no changes made.");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error updating user: " + ex.getMessage());
        }

        clearFields();
    }


    private void deleteUser() {
        String id = userId.getText();

        String query = "DELETE FROM user WHERE user_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, id);
            int result = ps.executeUpdate();

            if (result > 0) {
                JOptionPane.showMessageDialog(null, "User deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "User ID not found.");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error deleting user: " + ex.getMessage());
        }

        clearFields();
    }


    private void clearFields() {
        userId.setText("");
        Fname.setText("");
        Lname.setText("");
        Email.setText("");
        Telephone.setText("");
        Password.setText("");
        GenderCombo.setSelectedIndex(0);
    }


    public static void main(String[] args) {
        new user_M();
    }
}
