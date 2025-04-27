import db.DbConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserProfileForm extends JPanel{
    private JPanel userProfile;
    private JPanel profileForm;
    private JTextField userId;
    private JTextField fName;
    private JTextField lName;
    private JTextField email;
    private JTextField tele;
    private JButton updateBtn;
    private JButton browseBtn;
    private JLabel imagePreview;

    private String userID;
    private File selectedImageFile;

    public UserProfileForm(String userID) {
        this.userID = userID;
        loadUserDetails();

        updateBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateProfile();
            }
        });
        browseBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseImageFile();
            }
        });
    }

    private void loadUserDetails() {
        try (Connection con = DbConnection.getMyConnection()){
            String userQuery = "SELECT * FROM user WHERE user_id=?";
            PreparedStatement ps = con.prepareStatement(userQuery);
            ps.setString(1, userID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()){
                userId.setText(rs.getString("user_id"));
                fName.setText(rs.getString("firstName"));
                lName.setText(rs.getString("lastName"));
                email.setText(rs.getString("email"));
                tele.setText(rs.getString("telephone"));

                String imagePath = rs.getString("profile_picture");
                if (imagePath != null && !imagePath.isEmpty()){
                    File imgfile = new File(imagePath);
                    if (imgfile.exists()){
                        imagePreview.setIcon(new ImageIcon(new ImageIcon(imagePath).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
                    }
                }
            }
        } catch (SQLException e){
            JOptionPane.showMessageDialog(userProfile, "Error loading user details: " + e.getMessage());
        }
    }

    private void chooseImageFile() {
        JFileChooser chooser = new JFileChooser();
        int option = chooser.showOpenDialog(userProfile);
        if (option == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = chooser.getSelectedFile();
            imagePreview.setIcon(new ImageIcon(new ImageIcon(selectedImageFile.getAbsolutePath()).getImage().getScaledInstance(100,100, Image.SCALE_SMOOTH)));
        }
    }

    private void updateProfile() {
        String firstName = fName.getText().trim();
        String lastName = lName.getText().trim();
        String userEmail = email.getText().trim();
        String telephone = tele.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || userEmail.isEmpty()){
            JOptionPane.showMessageDialog(userProfile, "Please fill all the fields");
            return;
        }

        try (Connection con = DbConnection.getMyConnection()){
            String updateQuery = "UPDATE user SET firstName = ? , lastName = ? , email = ? , telephone = ? WHERE user_id = ?";
            PreparedStatement ps = con.prepareStatement(updateQuery);
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, userEmail);
            ps.setString(4, telephone);
            ps.setString(5, userID);

            int updatedRecord = ps.executeUpdate();

            if (updatedRecord > 0){
                JOptionPane.showMessageDialog(userProfile, "User profile updated");
            } else {
                JOptionPane.showMessageDialog(userProfile, "User profile not updated");
            }
        } catch (SQLException e){
            JOptionPane.showMessageDialog(userProfile, "Error in database updating: " + e.getMessage());
        }
    }

    public JPanel getRootPanel(){
        return userProfile;
    }

    /*public static void main(String[] args) {
        JFrame frame = new JFrame("UserProfileForm");
        frame.setContentPane(new UserProfileForm("TO001").userProfile);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }*/
}

