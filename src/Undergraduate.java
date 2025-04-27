import Panals.*;
import db.DbConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Undergraduate extends JFrame implements ActionListener {

    private JPanel topPanel, rightPanel;
    private CardLayout cardLayout;

    private JButton courseBtn, attendanceBtn, medicalBtn, gradeBtn, timeTableBtn, noticeBtn, profileBtn, logoutBtn;

    private String userId;
    private DbConnection dbConnector;

    public Undergraduate(String userId) {
        this.userId = userId;
        this.dbConnector = new DbConnection();

        setTitle("Undergraduate Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 600);
        setLayout(null);
        setResizable(false);

        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        Font buttonFont = new Font("Roboto", Font.PLAIN, 14);
        Font logoutFont = new Font("Roboto", Font.BOLD, 16);

        topPanel = new JPanel();
        topPanel.setBounds(0, 0, 1200, 60);
        topPanel.setBackground(new Color(227, 171, 126));
        topPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        profileBtn = createNavButton("MY PROFILE", buttonFont);
        courseBtn = createNavButton("COURSES", buttonFont);
        timeTableBtn = createNavButton("TIME TABLE", buttonFont);
        attendanceBtn = createNavButton("ATTENDANCE", buttonFont);
        medicalBtn = createNavButton("MEDICAL", buttonFont);
        gradeBtn = createNavButton("GRADES & GPA", buttonFont);
        noticeBtn = createNavButton("NOTICES", buttonFont);

        logoutBtn = new JButton("LOG OUT");
        logoutBtn.setPreferredSize(new Dimension(100, 25));
        logoutBtn.setBackground(new Color(255, 255, 255));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(logoutFont);
        logoutBtn.setFocusable(false);
        logoutBtn.setBorder(BorderFactory.createLineBorder(new Color(99, 95, 95), 2, true));
        logoutBtn.addActionListener(this);

        topPanel.add(profileBtn);
        topPanel.add(courseBtn);
        topPanel.add(timeTableBtn);
        topPanel.add(attendanceBtn);
        topPanel.add(medicalBtn);
        topPanel.add(gradeBtn);
        topPanel.add(noticeBtn);
        topPanel.add(logoutBtn);

        cardLayout = new CardLayout();
        rightPanel = new JPanel(cardLayout);
        rightPanel.setBounds(0, 60, 1200, 550);

        rightPanel.add(new CourseDetails(), "COURSES");
        rightPanel.add(new AttendanceDetails(userId, dbConnector), "ATTENDANCE");
        rightPanel.add(new MedicalDetails(userId, dbConnector), "MEDICAL");
        rightPanel.add(new GradesAndGPADetails(userId), "GRADES");
        rightPanel.add(new TimeTableDetails(dbConnector), "TIME TABLE");
        rightPanel.add(new NoticeDetails(userId), "NOTICES");
        rightPanel.add(new ProfileDetails(userId), "MY PROFILE");

        add(topPanel);
        add(rightPanel);

        showPanel(profileBtn, "MY PROFILE");
    }

    private JButton createNavButton(String text, Font font) {
        JButton btn = new JButton(text);
        btn.setForeground(new Color(104, 207, 15));
        btn.setFont(font);
        btn.setFocusable(false);
        btn.setBorderPainted(false);
        btn.addActionListener(this);
        return btn;
    }

    private void resetButtons() {
        JButton[] buttons = {
                profileBtn, courseBtn, timeTableBtn, attendanceBtn, medicalBtn, gradeBtn, noticeBtn
        };
        for (JButton btn : buttons) {
            btn.setEnabled(true);
            btn.setForeground(Color.white);
            btn.setBackground(new Color(33, 53, 85));
        }
        logoutBtn.setBackground(new Color(151, 241, 246));
        logoutBtn.setForeground(Color.black);
    }

    private void showPanel(JButton btn, String name) {
        cardLayout.show(rightPanel, name);
        resetButtons();
        btn.setEnabled(false);
        btn.setForeground(Color.white);
        btn.setBackground(new Color(243, 12, 42));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == courseBtn) showPanel(courseBtn, "COURSES");
        else if (source == attendanceBtn) showPanel(attendanceBtn, "ATTENDANCE");
        else if (source == medicalBtn) showPanel(medicalBtn, "MEDICAL");
        else if (source == gradeBtn) showPanel(gradeBtn, "GRADES");
        else if (source == timeTableBtn) showPanel(timeTableBtn, "TIME TABLE");
        else if (source == noticeBtn) showPanel(noticeBtn, "NOTICES");
        else if (source == profileBtn) showPanel(profileBtn, "MY PROFILE");
        else if (source == logoutBtn) /*{
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Log Out", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
            }*/{
            logoutBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                    new LoginForm();

                }
            });
        }
    }
}
