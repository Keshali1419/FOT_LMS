package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class university_notice extends JFrame {
    private JPanel panel1;
    private JButton button1;
    private JTable table1;



    public university_notice() {


            setTitle("Lecture Materials");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setBounds(100, 100, 700, 500);
            setContentPane(panel1);
            setVisible(true);

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                newlec_dash dash = new newlec_dash();
                dash.setVisible(true);
                dispose();

            }
        });

        String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/tecmis";
        String user = "root";
        String password = "1234";

        try {
            Class.forName(driver);
            Connection con= DriverManager.getConnection(url,user,password);

            String query = "SELECT * FROM notice";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            DefaultTableModel model = (DefaultTableModel) table1.getModel();
            model.setRowCount(0);
            model.setColumnIdentifiers(new String[]{"notice_id", "title", "content", "posted_date", "target_role"});



            table1.setRowHeight(25);

            while (rs.next()) {
                String note_id = rs.getString("note_id");
                String adminId = rs.getString("adminId");
                String title = rs.getString("title");
                String posted_date = rs.getString("published_Date");
                String target_role = rs.getString("description");
                model.addRow(new String[]{note_id, adminId, title, posted_date, target_role});
            }
            con.close();
            rs.close();
            ps.close();



        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row=table1.getSelectedRow();

                String note_id=table1.getValueAt(row, 0).toString();
                String adminId=table1.getValueAt(row, 1).toString();
                String tittle=table1.getValueAt(row, 2).toString();
                String posted_date=table1.getValueAt(row, 3).toString();
                String target_role=table1.getValueAt(row, 4).toString();

                JOptionPane.showMessageDialog(null, "\nID:"+note_id+" \nadminId:"+adminId+"\ntittle"+tittle+"\nPost_Date:"+posted_date+"\ntarget_Role:"+target_role,
                        "Notice",JOptionPane.INFORMATION_MESSAGE);
            }
        });

    }
    public static void main(String[] args) {
        new university_notice();
    }
}
