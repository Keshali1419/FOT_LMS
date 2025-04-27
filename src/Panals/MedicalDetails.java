package Panals;

import Database.DatabaseConnetor;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class MedicalDetails extends JPanel {

    private String userId;
    private DatabaseConnetor dbConnector;
    private JTable table;
    private DefaultTableModel tableModel;

    public MedicalDetails(String userId, DatabaseConnetor dbConnector) {
        this.userId = userId;
        this.dbConnector = dbConnector;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 249, 252));
        initUI();
    }

    private void initUI() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(58, 66, 86));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Medical Records");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 25));
        titleLabel.setForeground(Color.WHITE);

        titlePanel.add(titleLabel, BorderLayout.WEST);
        add(titlePanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{
                "Course Code", "Session Type", "Reason", "Date Submitted"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(230, 240, 250));
                } else {
                    c.setBackground(new Color(173, 216, 230));
                }
                return c;
            }
        };

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setBackground(new Color(0, 123, 167));
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);

        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setGridColor(new Color(200, 200, 200));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);

        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(leftRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(table,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 149, 237), 1),
                BorderFactory.createEmptyBorder(10, 20, 20, 20)
        ));
        add(scrollPane, BorderLayout.CENTER);

        loadMedicalData();
    }

    private void loadMedicalData() {
        String query = "SELECT courseCode, session_type, description, submissionDate " +
                "FROM medical WHERE stuId = ? ORDER BY submissionDate DESC";

        try (Connection conn = dbConnector.getMyConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String courseId = rs.getString("courseCode");
                String sessionType = rs.getString("session_type");
                String reason = rs.getString("description");
                Date date = rs.getDate("submissionDate");

                tableModel.addRow(new Object[]{courseId, sessionType, reason, date.toString()});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading medical data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
