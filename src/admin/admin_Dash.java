package admin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class admin_Dash extends JFrame {
    private JPanel mainPanel;
    private JButton userManagementButton;
    private JButton noticeManagementButton;
    private JButton courseManagementButton;
    private JButton timeTableManagementButton;

    public admin_Dash() {
        setTitle("Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //setContentPane(mainPanel);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        //setVisible(true);

        /*mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(4, 1, 20, 20)); // 5 rows (4 buttons + optional logout)

        userManagementButton = new JButton("User Management");
        noticeManagementButton = new JButton("Notice Management");
        courseManagementButton = new JButton("Course Management");
        timeTableManagementButton = new JButton("Timetable Management");
        */
        setContentPane(mainPanel);
        setVisible(true);

        courseManagementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Course_Management cm = new Course_Management();
                cm.setVisible(true);
                dispose();
            }
        });

        noticeManagementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Notice_Management nm = new Notice_Management();
                nm.setVisible(true);
                dispose();
            }
        });

        timeTableManagementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timetable_Manage tm = new timetable_Manage();
                tm.setVisible(true);
                dispose();
            }
        });

        userManagementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                user_M u = new user_M();
                u.setVisible(true);
                dispose();
            }
        });
    }

    public static void main(String[] args) {
        // Start the dashboard
        SwingUtilities.invokeLater(() -> new admin_Dash());
    }
}
