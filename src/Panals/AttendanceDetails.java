package Panals;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import Database.DatabaseConnetor;

public class AttendanceDetails extends JPanel {
    private String userId;
    private DatabaseConnetor dbConnector;
    private JTable theoryTable, practicalTable;
    private DefaultTableModel theoryModel, practicalModel;
    private JLabel theoryLabel, practicalLabel;
    private JPanel titlePanel;
    private JLabel mainTitle;

    public AttendanceDetails(String userId, DatabaseConnetor dbConnector) {
        this.userId = userId;
        this.dbConnector = dbConnector;

        initUI();
        loadAttendanceData();

    }

    private void initUI() {

        setLayout(null);
        setBounds(0, 0, 1200, 800);
        setBackground(Color.WHITE);

        titlePanel = new JPanel();
        titlePanel.setBounds(0, 0, 1200, 60);
        titlePanel.setBackground(new Color(58, 66, 86));
        titlePanel.setLayout(null);

        mainTitle = new JLabel("View Attendance");
        mainTitle.setBounds(20, 5, 1200, 50);
        mainTitle.setForeground(Color.WHITE);
        mainTitle.setFont(new Font("Dialog.", Font.BOLD, 25));
        titlePanel.add(mainTitle);
        add(titlePanel);

        theoryLabel = new JLabel("Theory Attendance");
        theoryLabel.setBounds(20, 70, 300, 25);
        theoryLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        add(theoryLabel);

        practicalLabel = new JLabel("Practical Attendance");
        practicalLabel.setBounds(20, 280, 300, 25);
        practicalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        add(practicalLabel);

        theoryModel = new DefaultTableModel(new String[]{"Course Code", "Total", "Attended(With Medical)", "Percentage(With Medical)", "Attended(Without Medical)", "Percentage(Without Medical)"}, 0);
        theoryTable = new JTable(theoryModel);
        setupTable(theoryTable);
        JScrollPane theoryScroll = new JScrollPane(theoryTable);
        theoryScroll.setBounds(20, 100, 1200, 175);
        add(theoryScroll);

        practicalModel = new DefaultTableModel(new String[]{"Course Code", "Total", "Attended(With Medical)", "Percentage(With Medical)", "Attended(Without Medical)", "Percentage(Without Medical)"}, 0);
        practicalTable = new JTable(practicalModel);
        setupTable(practicalTable);
        JScrollPane practicalScroll = new JScrollPane(practicalTable);
        practicalScroll.setBounds(20, 300, 1200, 175);
        add(practicalScroll);
    }

    private void setupTable(JTable table) {
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(0, 123, 167));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setDefaultEditor(Object.class, null);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        if (table.getColumnCount() >= 2) {
            table.getColumnModel().getColumn(0).setPreferredWidth(5);
            table.getColumnModel().getColumn(1).setPreferredWidth(5);
        }
    }


    private void loadAttendanceData() {
        loadTableData("theory", theoryModel);
        loadTableData("practical", practicalModel);
    }

    private void loadTableData(String sessionType, DefaultTableModel model) {
        model.setRowCount(0);
        String sql = "SELECT courseCode, " +
                "COUNT(*) AS total_classes, " +
                "SUM(CASE WHEN atten_status IN ('present', 'medical') THEN 1 ELSE 0 END) AS attended_with_medical, " +
                "SUM(CASE WHEN atten_status = 'present' THEN 1 ELSE 0 END) AS attended_without_medical, " +
                "ROUND(100.0 * SUM(CASE WHEN atten_status IN ('present', 'medical') THEN 1 ELSE 0 END) / COUNT(*), 2) AS percentage_with_medical, " +
                "ROUND(100.0 * SUM(CASE WHEN atten_status = 'present' THEN 1 ELSE 0 END) / COUNT(*), 2) AS percentage_without_medical " +
                "FROM attendance " +
                "WHERE stuId = ? AND session_type = ? " +
                "GROUP BY courseCode";

        try (Connection con = dbConnector.getMyConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, userId);
            ps.setString(2, sessionType);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String course = rs.getString("courseCode");
                int total = rs.getInt("total_classes");
                int attendedWithMedical = rs.getInt("attended_with_medical");
                int attendedWithoutMedical = rs.getInt("attended_without_medical");
                double percentageWithMedical = rs.getDouble("percentage_with_medical");
                double percentageWithoutMedical = rs.getDouble("percentage_without_medical");

                model.addRow(new Object[]{
                        course,
                        total,
                        attendedWithMedical,
                        String.format("%.2f%%", percentageWithMedical),
                        attendedWithoutMedical,
                        String.format("%.2f%%", percentageWithoutMedical)
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading attendance data.");
        }
    }
}
