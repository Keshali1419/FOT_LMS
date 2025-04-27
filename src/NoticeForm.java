import db.DbConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NoticeForm extends JPanel{
    private JPanel notice;
    private JTable noticeTable;
    private JTextArea previewPane;
    private JButton viewButton;

    public NoticeForm() {
        previewPane.setLineWrap(true);
        previewPane.setWrapStyleWord(true);

        loadNotices();

        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = noticeTable.getSelectedRow();
                if(row >= 0){
                    String fileName = (String) noticeTable.getValueAt(row, 4);
                    previewTextFile(fileName);
                } else {
                    JOptionPane.showMessageDialog(notice, "Please select a row from the table");
                }
            }
        });
    }

    private void loadNotices() {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"Notice Id","Updated By", "Title", "Published Date", "Description"});
        noticeTable.setModel(model);

        try (Connection con = DbConnection.getMyConnection()){
            String viewNoticeQuery = "SELECT * FROM notice";
            ResultSet rs = con.prepareStatement(viewNoticeQuery).executeQuery();

            while(rs.next()){
                model.addRow(new Object[]{
                        rs.getString("note_id"),
                        rs.getString("adminId"),
                        rs.getString("title"),
                        rs.getString("published_Date"),
                        rs.getString("description"),
                });
            }
        } catch (SQLException e){
            JOptionPane.showMessageDialog(null, "Error loading notices" + e.getMessage());
        }
    }

    public void previewTextFile(String fileName) {
        try{
            File file = new File("src/files/" + fileName);
            if (file.exists() && fileName.endsWith(".txt")){
                FileInputStream fis = new FileInputStream(file);
                byte[] data = fis.readAllBytes();
                fis.close();
                String content = new String(data, StandardCharsets.UTF_8);
                previewPane.setText(fileName + "\n" + content);
            } else{
                previewPane.setText("File not found or unsupported format");
            }
         } catch (IOException e){
            previewPane.setText("Error reading file: " + e.getMessage());
        }
    }

    public JPanel getRootPanel(){
        return notice;
    }

    /*public static void main(String[] args) {
        JFrame frame = new JFrame("NoticeForm");
        frame.setContentPane(new NoticeForm().notice);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
    }*/
}
