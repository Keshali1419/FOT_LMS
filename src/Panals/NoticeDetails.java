package Panals;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import db.DbConnection;

public class NoticeDetails extends JPanel {
    private JTable noticeTable;
    private DefaultTableModel tableModel;
    private DbConnection dbConnector;

    public NoticeDetails(String userId) {
        dbConnector = new DbConnection();
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(58, 66, 86));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Notices");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 25));
        titleLabel.setForeground(Color.WHITE);

        titlePanel.add(titleLabel, BorderLayout.WEST);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(Color.WHITE);
        topPanel.add(titlePanel);

        add(topPanel, BorderLayout.NORTH);

        String[] columnNames = {"Notice Title", "Date", "File Path"};
        tableModel = new DefaultTableModel(columnNames, 0);
        noticeTable = new JTable(tableModel);
        noticeTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        noticeTable.setRowHeight(28);
        noticeTable.setSelectionBackground(new Color(173, 216, 230));
        noticeTable.setDefaultEditor(Object.class, null);

        // Set File Path column text color to blue and cursor to hand
        noticeTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {

                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                label.setForeground(Color.red);
                label.setCursor(new Cursor(Cursor.HAND_CURSOR));

                return label;
            }
        });

        JTableHeader header = noticeTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setBackground(new Color(0, 123, 167));
        header.setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(noticeTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        add(scrollPane, BorderLayout.CENTER);

        noticeTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selectedRow = noticeTable.getSelectedRow();
                if (selectedRow >= 0) {
                    selectedRow = noticeTable.convertRowIndexToModel(selectedRow);
                    String title = (String) tableModel.getValueAt(selectedRow, 0);
                    String filePath = (String) tableModel.getValueAt(selectedRow, 2);
                    String date = tableModel.getValueAt(selectedRow, 1).toString();

                    String fullFilePath = "src/files/" + filePath;

                    String content = readFileContent(fullFilePath);
                    if (content != null) {
                        JTextArea contentArea = new JTextArea(content);
                        contentArea.setEditable(false);
                        contentArea.setLineWrap(true);
                        contentArea.setWrapStyleWord(true);
                        contentArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                        contentArea.setBackground(new Color(248, 248, 248));

                        JScrollPane scroll = new JScrollPane(contentArea);
                        scroll.setPreferredSize(new Dimension(400, 250));

                        JOptionPane.showMessageDialog(
                                NoticeDetails.this,
                                scroll,
                                title + " (" + date + ")",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    } else {
                        JOptionPane.showMessageDialog(
                                NoticeDetails.this,
                                "Unable to read file: " + fullFilePath,
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            }
        });

        loadNotices();
    }

    private void loadNotices() {
        String query = "SELECT title, published_date, description FROM notice";
        try (Connection conn = dbConnector.getMyConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String title = rs.getString(1);
                Date date = rs.getDate(2);
                String filePath = rs.getString(3);

                tableModel.addRow(new Object[]{title, date, filePath});
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load notices", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String readFileContent(String filePath) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
