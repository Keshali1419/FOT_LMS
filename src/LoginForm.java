import admin.admin_Dash;
import db.DbConnection;
import org.example.newlec_dash;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginForm extends JFrame {
    private JPanel Login;
    private JPanel Form;
    private JTextField userId;
    private JPasswordField pwd;
    private JButton signInBtn;
    private JButton cancelBtn;

    public User user;

    public LoginForm() {
        setContentPane(Login);
        setTitle("Login");
        setSize(1000,600);
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        signInBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userId.getText();
                String password = new String(pwd.getPassword());

                user = getAuthenticatedUser(username, password);

                if(user != null) {
                    //JOptionPane.showMessageDialog(null, "Login Successful! Welcome " + user.getFirstName());
                    String role = user.getRoleFromUserID();

                    switch (role){
                        case "AD":
                            //new AdminDashboard(user);
                            admin_Dash adminDashboard = new admin_Dash();
                            adminDashboard.setVisible(true);
                            dispose();
                            break;

                        case "LE":
                            //new LecturerDashboard(user);
                            newlec_dash lecturerDashboard = new newlec_dash();
                            lecturerDashboard.setVisible(true);
                            dispose();
                            break;

                        case "TO":
                            //new TechnicalOfficerDashboard(user);
                            TechnicalOfficerDashboard dashboard = new TechnicalOfficerDashboard(user);
                            dashboard.setVisible(true);
                            dispose();
                            break;

                        case "ST":
                            //new StudentDashboard(user);
                            Undergraduate undergraduateDashboard = new Undergraduate(user.getUser_id());
                            undergraduateDashboard.setVisible(true);
                            dispose();
                            break;

                        default:
                            JOptionPane.showMessageDialog(null, "Unknown role, access denied");
                            break;
                    }
                }else{
                    JOptionPane.showMessageDialog(null, "Invalid username or password");
                }
            }
        });

        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }
    
    private User getAuthenticatedUser(String username, String password){
        DbConnection db = new DbConnection();
        Connection con = db.getMyConnection();
        User user = null;

        if(con == null){
           JOptionPane.showMessageDialog(null, "Failed to connect to database.");
           return null;
        }

        try {
            String loginQuery = "SELECT * FROM user WHERE user_id = ? AND password = ?";
            PreparedStatement stmt = con.prepareStatement(loginQuery);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if(rs.next()){
                user = new User(
                        rs.getString("user_id"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("email"),
                        rs.getString("telephone"),
                        rs.getString("password")
                );
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
        } finally{
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("Error in closing connection: " + e.getMessage());
            }
        }

        return user;
    }

    public JPanel getRootPanel() {
        return Login;
    }
}
