import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserDashboard extends JFrame {

    private JPanel UserDashboard;
    private JPanel Navigation;
    private JPanel ContentPanel;
    private JLabel header;
    private JButton profileBtn;
    private JButton coursesBtn;
    private JButton marksBtn;
    private JButton attendanceBtn;
    private JButton noticesBtn;
    private JButton timetablesBtn;
    private JButton medicalsBtn;
    private JButton logoutBtn;

    private User user;

    public UserDashboard(User user, String title) {
        this.user = user;
        setTitle(title);
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setContentPane(UserDashboard);
        setLayout(new BorderLayout());

        header.setText("Welcome " + user.getFirstName() + " (" + user.getUser_id() + ")");

        logoutBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(UserDashboard);
                frame.dispose();
                new LoginForm();
            }
        });
        profileBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchContent("Profile");
            }
        });
        coursesBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchContent("Courses");
            }
        });
    }

    private void switchContent(String label){
        ContentPanel.removeAll();
        ContentPanel.add(header);
        ContentPanel.revalidate();
        ContentPanel.repaint();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("UserDashboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000,600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
