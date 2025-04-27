import db.DbConnection;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TechnicalOfficerDashboard extends JFrame {
    private JPanel TODashboard;
    private JButton updateProfileBtn;
    private JButton manageAttendanceBtn;
    private JButton manageMedicalsButton;
    private JButton viewTimetablesBtn;
    private JButton viewNoticesBtn;
    private JPanel ContentPanel;
    private JLabel header;
    private JPanel NavigationalPanel;
    private JButton logOutBtn;

    private User user;

    public TechnicalOfficerDashboard(User user) {
        this.user = user;

        setTitle("Technical Officer Dashboard");
        setContentPane(TODashboard);
        setSize(1000,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        header.setText("Welcome " + user.getFirstName() + "(" + user.getUser_id() + ")");

        updateProfileBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchContent(new UserProfileForm(user.getUser_id()).getRootPanel());
            }
        });

        manageAttendanceBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchContent(new AttendanceForm().getRootPanel());
            }
        });

        manageMedicalsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchContent(new MedicalForm().getRootPanel());
            }
        });

        viewTimetablesBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchContent(new TimetableForm(user.getUser_id()).getRootPanel());
            }
        });

        viewNoticesBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchContent(new NoticeForm().getRootPanel());
            }
        });

        logOutBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginForm();

            }
        });
    }

    private void switchContent(JPanel panel) {
        ContentPanel.removeAll();
        ContentPanel.add(panel);
        ContentPanel.revalidate();
        ContentPanel.repaint();
    }

    /*public static void main(String[] args) {
        JFrame frame = new JFrame("TechnicalOfficerDashboard"); //no need because extends JFrame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        User user = new User("TO001", "Janitha", "Fernando", "janitha@gmail.com", "0761234567", "janitha@1234");

        TechnicalOfficerDashboard dashboard = new TechnicalOfficerDashboard(user);
        dashboard.setVisible(true);
    }*/
}
