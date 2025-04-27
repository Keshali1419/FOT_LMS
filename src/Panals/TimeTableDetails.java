package Panals;

import db.DbConnection;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class TimeTableDetails extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private DbConnection dbConnector;

    public TimeTableDetails(DbConnection dbConnector) {
        this.dbConnector = dbConnector;
        setLayout(new BorderLayout());
        setBackground(new Color(15, 79, 230));
        initUI();
    }

    private void initUI() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(58, 66, 86));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Lecture Time Table");
        titleLabel.setFont(new Font("Dialog.", Font.BOLD, 25));
        titleLabel.setForeground(Color.WHITE);

        titlePanel.add(titleLabel, BorderLayout.WEST);
        add(titlePanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new Object[]{
                "Day", "Lecturer ID", "Course ID", "Session Type", "Start Time", "End Time"
        });

        table = new JTable(tableModel);
        styleTable();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        add(scrollPane, BorderLayout.CENTER);

        loadTimeTableData();
    }

    private void styleTable() {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(28);
        table.setGridColor(new Color(220, 230, 240));
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setDefaultEditor(Object.class, null);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(0, 123, 167));
        header.setForeground(Color.WHITE);

        table.setSelectionBackground(new Color(184, 207, 251));
        table.setSelectionForeground(Color.BLACK);
    }

    private void loadTimeTableData() {
        String query = "SELECT day, lecId, courseCode, sessionType, startTime, endTime from timetable";

        try (Connection conn = dbConnector.getMyConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String day = rs.getString(1);
                String startTime = rs.getString(5);
                String endTime = rs.getString(6);
                String courseId = rs.getString(3);
                String sessionType = rs.getString(4);
                String lecId = rs.getString(2);


                tableModel.addRow(new Object[]{day,lecId, courseId, sessionType, startTime,endTime});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load timetable: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
