package admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Notice_Management extends JFrame {
    private JButton deleteButton;
    private JButton addNoticeButton;
    private JTable table1;
    private JPanel mainp;
    private JButton backButton;
    private JPanel panelMain;

    private DefaultTableModel tableModel;


    private Connection con;
    private PreparedStatement pstmt;
    private ResultSet rs;

    public Notice_Management() {
        setTitle("Notice Management");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(mainp);
        setVisible(true);


        initializeDB();


        String[] columnNames = {"Note ID", "Admin ID", "Title", "Published Date", "Description"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table1.setModel(tableModel);
        loadNotices();


        addNoticeButton.addActionListener(e -> {
            String noteId = JOptionPane.showInputDialog(this, "Enter Note ID:");
            String adminId = JOptionPane.showInputDialog(this, "Enter Admin ID:");
            String title = JOptionPane.showInputDialog(this, "Enter Title:");
            String dateStr = JOptionPane.showInputDialog(this, "Enter Published Date (yyyy-MM-dd):");
            String description = JOptionPane.showInputDialog(this, "Enter Description:");

            if (noteId != null && adminId != null && title != null && dateStr != null && description != null) {
                try {
                    java.sql.Date publishedDate = java.sql.Date.valueOf(dateStr);
                    String insertQuery = "INSERT INTO notice (note_id, adminId, title, published_Date, description) VALUES (?, ?, ?, ?, ?)";
                    pstmt = con.prepareStatement(insertQuery);
                    pstmt.setString(1, noteId);
                    pstmt.setString(2, adminId);
                    pstmt.setString(3, title);
                    pstmt.setDate(4, publishedDate);
                    pstmt.setString(5, description);
                    pstmt.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Notice added successfully!");
                    loadNotices();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error adding notice: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "All fields are required.");
            }
        });


        deleteButton.addActionListener(e -> {
            int selectedRow = table1.getSelectedRow();
            if (selectedRow != -1) {
                String noteId = (String) tableModel.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this notice?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        String deleteQuery = "DELETE FROM notice WHERE note_id = ?";
                        pstmt = con.prepareStatement(deleteQuery);
                        pstmt.setString(1, noteId);
                        pstmt.executeUpdate();

                        JOptionPane.showMessageDialog(this, "Notice deleted.");
                        loadNotices(); // Refresh table
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Error deleting notice: " + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a notice to delete.");
            }
        });
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                admin_Dash admin = new admin_Dash();
                admin.setVisible(true);
                dispose();

            }
        });
    }

    private void initializeDB() {
        try {
            String url = "jdbc:mysql://localhost:3306/tecmis"; // change DB name if needed
            String user = "root";
            String password = "1234";
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, user, password);
            System.out.println("Database connected.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage());
        }
    }

    private void loadNotices() {
        try {
            tableModel.setRowCount(0); // Clear table first
            String query = "SELECT * FROM notice";
            pstmt = con.prepareStatement(query);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                String noteId = rs.getString("note_id");
                String adminId = rs.getString("adminId");
                String title = rs.getString("title");
                Date publishedDate = rs.getDate("published_Date");
                String description = rs.getString("description");
                tableModel.addRow(new Object[]{noteId, adminId, title, publishedDate, description});
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load notices: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Notice_Management::new);
    }
}
