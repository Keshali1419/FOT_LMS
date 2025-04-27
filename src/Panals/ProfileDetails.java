package Panals;

import Database.DatabaseConnetor;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.*;

public class ProfileDetails extends JPanel {
    private String userId;
    private DatabaseConnetor dbConnector;
    private Connection con;
    private PreparedStatement ps;
    private ResultSet rs;

    private JLabel profilePicLabel, nameValue, idValue, emailValue, phoneValue, passwordValue;
    private JButton editPhoneBtn, uploadPicBtn;
    private ImageIcon profilePicIcon;

    private String firstName, lastName, email, telephone, password, profilePicPath;

    public ProfileDetails(String userId) {
        this.userId = userId;
        setLayout(null);
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(1200, 800));

        dbConnector = new DatabaseConnetor();
        loadUserDetails();
        initComponents();
    }

    private void loadUserDetails() {
        try {
            con = dbConnector.getMyConnection();
            String query = "SELECT * FROM user WHERE user_id = ?";
            ps = con.prepareStatement(query);
            ps.setString(1, userId);
            rs = ps.executeQuery();

            if (rs.next()) {
                firstName = rs.getString("firstName");
                lastName = rs.getString("lastName");
                email = rs.getString("email");
                telephone = rs.getString("telephone");
                password = rs.getString("password");
                profilePicPath = rs.getString("profie_picture");

                if (profilePicPath != null && !profilePicPath.trim().isEmpty()) {
                    File file = new File(profilePicPath);
                    if (file.exists()) {
                        profilePicIcon = resizeImageIcon(new ImageIcon(profilePicPath), 150, 150);
                    } else {
                        profilePicIcon = resizeImageIcon(new ImageIcon("icons/pp.png"), 150, 150);
                    }
                } else {
                    profilePicIcon = resizeImageIcon(new ImageIcon("icons/pp.png"), 150, 150);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading user details: " + e.getMessage());
        } finally {
            closeResources();
        }
    }

    private void initComponents() {
        Font labelFont = new Font("Arial", Font.BOLD, 18);
        Font valueFont = new Font("Arial", Font.PLAIN, 16);

        profilePicLabel = new JLabel(profilePicIcon);
        profilePicLabel.setBounds(450, 50, 150, 150);
        add(profilePicLabel);

        uploadPicBtn = new JButton("Upload Picture");
        uploadPicBtn.setBounds(460, 210, 140, 30);
        add(uploadPicBtn);

        addLabel("Name:", 50, 230, labelFont);
        nameValue = addValueLabel(firstName + " " + lastName, 180, 230, valueFont);

        addLabel("User ID:", 50, 270, labelFont);
        idValue = addValueLabel(userId, 180, 270, valueFont);

        addLabel("Email:", 50, 310, labelFont);
        emailValue = addValueLabel(email, 180, 310, valueFont);

        addLabel("Phone:", 50, 350, labelFont);
        phoneValue = addValueLabel(telephone, 180, 350, valueFont);

        editPhoneBtn = new JButton(new ImageIcon("icons/edit_icon.png"));
        editPhoneBtn.setBounds(380, 350, 30, 30);
        editPhoneBtn.setBorderPainted(false);
        editPhoneBtn.setContentAreaFilled(false);
        editPhoneBtn.setFocusPainted(false);
        add(editPhoneBtn);

        addLabel("Password:", 50, 390, labelFont);
        passwordValue = addValueLabel(password, 180, 390, valueFont);

        editPhoneBtn.addActionListener(e -> editPhoneNumber());
        uploadPicBtn.addActionListener(e -> uploadProfilePicture());
    }

    private void addLabel(String text, int x, int y, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setBounds(x, y, 120, 30);
        add(label);
    }

    private JLabel addValueLabel(String text, int x, int y, Font font) {
        JLabel valueLabel = new JLabel(text);
        valueLabel.setFont(font);
        valueLabel.setBounds(x, y, 200, 30);
        add(valueLabel);
        return valueLabel;
    }

    private void editPhoneNumber() {
        String newPhone = JOptionPane.showInputDialog(this, "Enter new phone number:", telephone);
        if (newPhone != null && newPhone.matches("\\d{10}")) {
            try {
                con = dbConnector.getMyConnection();
                String updateQuery = "UPDATE user SET telephone = ? WHERE user_id = ?";
                ps = con.prepareStatement(updateQuery);
                ps.setString(1, newPhone);
                ps.setString(2, userId);
                ps.executeUpdate();
                phoneValue.setText(newPhone);
                JOptionPane.showMessageDialog(this, "Phone number updated successfully!");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error updating phone number: " + e.getMessage());
            } finally {
                closeResources();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid phone number! Must be 10 digits.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void uploadProfilePicture() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose Profile Picture");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "gif"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String newPath = selectedFile.getAbsolutePath();

            try {
                con = dbConnector.getMyConnection();
                String updateQuery = "UPDATE user SET profile_pic = ? WHERE user_id = ?";
                ps = con.prepareStatement(updateQuery);
                ps.setString(1, newPath);
                ps.setString(2, userId);
                ps.executeUpdate();

                profilePicIcon = resizeImageIcon(new ImageIcon(newPath), 150, 150);
                profilePicLabel.setIcon(profilePicIcon);

                JOptionPane.showMessageDialog(this, "Profile picture updated successfully!");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error updating profile picture: " + e.getMessage());
            } finally {
                closeResources();
            }
        }
    }

    private ImageIcon resizeImageIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image newImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(newImg);
    }

    private void closeResources() {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
