package Panals;

import Database.DatabaseConnetor;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.sql.*;

public class CourseDetails extends JPanel {
    private JTable courseTable;
    private DefaultTableModel tableModel;

    public CourseDetails() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 248, 255));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(58, 66, 86));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Courses Introduction");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 25));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.WEST);
        add(titlePanel, BorderLayout.NORTH);

        String[] columns = {"Course Code", "Course Title", "Lecturer ID", "Credit", "Course Type", "Lecture Materials"};
        tableModel = new DefaultTableModel(columns, 0);
        courseTable = new JTable(tableModel) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        JTableHeader header = courseTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(0, 123, 167));
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);

        courseTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        courseTable.setRowHeight(30);
        courseTable.setGridColor(Color.GRAY);
        courseTable.setShowGrid(true);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);

        TableColumnModel columnModel = courseTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(100);
        columnModel.getColumn(1).setPreferredWidth(300);
        columnModel.getColumn(2).setPreferredWidth(80);
        columnModel.getColumn(3).setPreferredWidth(60);
        columnModel.getColumn(4).setPreferredWidth(100);
        columnModel.getColumn(5).setPreferredWidth(150);

        for (int i = 0; i < courseTable.getColumnCount(); i++) {
            if (i == 1) {
                courseTable.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);
            } else if (i == 5) {
                courseTable.getColumnModel().getColumn(i).setCellRenderer(new DefaultTableCellRenderer() {
                    public Component getTableCellRendererComponent(JTable table, Object value,
                                                                   boolean isSelected, boolean hasFocus,
                                                                   int row, int column) {
                        JLabel cell = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                        cell.setHorizontalAlignment(JLabel.CENTER);
                        if (value != null && value.toString().startsWith("http")) {
                            cell.setForeground(new Color(6, 17, 136));
                            cell.setText("<html><u>" + value.toString() + "</u></html>");
                        } else {
                            cell.setForeground(Color.BLACK);
                        }
                        cell.setBackground(isSelected ? new Color(173, 216, 230) : Color.WHITE);
                        return cell;
                    }
                });
            } else {
                courseTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }

        courseTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = courseTable.rowAtPoint(e.getPoint());
                int column = courseTable.columnAtPoint(e.getPoint());
                if (column == 5) {
                    String url = courseTable.getValueAt(row, column).toString();
                    try {
                        Desktop.getDesktop().browse(new URI(url));
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(CourseDetails.this, "Failed to open link.");
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(courseTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                BorderFactory.createEmptyBorder(10, 20, 20, 20)
        ));
        add(scrollPane, BorderLayout.CENTER);

        loadCourseData();
    }

    private void loadCourseData() {
        DatabaseConnetor mdc = new DatabaseConnetor();
        try (Connection con = mdc.getMyConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM course")) {

            while (rs.next()) {
                String id = rs.getString("course_code");
                String name = rs.getString("title");
                String lecId = rs.getString("lecId");
                int credit = rs.getInt("credit");
                String type = rs.getString("course_type");
                String link = rs.getString("course_content");

                tableModel.addRow(new Object[]{id, name, lecId, credit, type, link});
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading course data: " + e.getMessage());
        }
    }
}
