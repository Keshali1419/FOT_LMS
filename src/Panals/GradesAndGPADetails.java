package Panals;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Database.DatabaseConnetor;

public class GradesAndGPADetails extends JPanel {

    private JPanel titlePanel;
    private JLabel titleLabel;
    private Font titleFont;
    private Color titleColor;
    private JTable gradesTable;
    private DefaultTableModel gradesTableModel;
    private JScrollPane scrollPane;
    private JTableHeader gradesTableHeader;
    private Connection con;
    private ResultSet rs;
    private PreparedStatement ps;

    public GradesAndGPADetails(String userId) {
        initUI(userId);
    }

    private void initUI(String userId) {
        setLayout(null);

        titleFont = new Font("Dialog.", Font.BOLD, 25);
        titleColor = new Color(54, 57, 63);

        titlePanel = new JPanel();
        titlePanel.setBounds(0, 0, 1200, 60);
        titlePanel.setBackground(new Color(58, 66, 86));
        titlePanel.setOpaque(true);

        titleLabel = new JLabel("Grades & GPA");
        titleLabel.setBounds(20, 5, 1200, 50);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(titleFont);

        add(titleLabel);
        add(titlePanel);

        createGradeTable(userId);
        loadGrades(userId);
    }

    private void createGradeTable(String userId) {
        String[] columnNames = {"Course Code", "Course Name", "Grades", "SGPA", "CGPA"};

        gradesTableModel = new DefaultTableModel(columnNames, 0);
        gradesTable = new JTable(gradesTableModel);

        gradesTable.setDefaultEditor(Object.class, null);

        gradesTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gradesTable.setRowHeight(28);
        gradesTable.setGridColor(new Color(220, 230, 240));
        gradesTable.setShowVerticalLines(false);
        gradesTable.setShowHorizontalLines(true);

        gradesTableHeader = gradesTable.getTableHeader();
        gradesTableHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gradesTableHeader.setBackground(new Color(0, 123, 167));
        gradesTableHeader.setForeground(Color.WHITE);
        gradesTableHeader.setReorderingAllowed(false);

        scrollPane = new JScrollPane(gradesTable);
        scrollPane.setBounds(20, 100, 1200, 300);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        scrollPane.getViewport().setBackground(Color.WHITE);

        DefaultTableCellRenderer centerAlign = new DefaultTableCellRenderer();
        centerAlign.setHorizontalAlignment(SwingConstants.CENTER);
        gradesTable.getColumnModel().getColumn(2).setCellRenderer(centerAlign);
        gradesTable.getColumnModel().getColumn(3).setCellRenderer(centerAlign);
        gradesTable.getColumnModel().getColumn(4).setCellRenderer(centerAlign);

        gradesTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        gradesTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        gradesTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        gradesTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        gradesTable.getColumnModel().getColumn(4).setPreferredWidth(80);


        for (int i = 0; i < gradesTable.getColumnCount(); i++) {
            gradesTable.getColumnModel().getColumn(i).setResizable(false);
        }

        gradesTable.setSelectionBackground(new Color(184, 207, 251));
        gradesTable.setSelectionForeground(Color.BLACK);

        add(scrollPane);
    }


    private void loadGrades(String userId) {
        String sql = "SELECT * FROM gradesOfUgs WHERE stuId = ?";
        DatabaseConnetor dbConnector = new DatabaseConnetor();
        con = dbConnector.getMyConnection();
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, userId);
            rs = ps.executeQuery();

            while (rs.next()) {
                String courseCode = rs.getString("courseCode");
                String courseName = rs.getString("title");
                String grade = rs.getString("grade");
                String sgpa = rs.getString("SGPA");
                String cgpa = rs.getString("CGPA");

                gradesTableModel.addRow(new Object[]{courseCode, courseName, grade, sgpa, cgpa});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading Grades data: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
