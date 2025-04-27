import db.DbConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MedicalForm extends JPanel{
    private JPanel Medical;
    private JPanel FormMedical;
    private JTextField MedId;
    private JButton addBtn;
    private JPanel ViewMedicals;
    private JTextField medIdforView;
    private JButton viewBtn;
    private JButton deleteBtn;
    private JTable MedicalTable;
    private JTextField courseCode;
    private JTextField stuId;
    private JRadioButton theoryRadioButton;
    private JRadioButton practicalRadioButton;
    private JTextField submissionDate;
    private JTextField desc;

    private ButtonGroup sessionGroup;

    public MedicalForm() {
        sessionGroup = new ButtonGroup();
        sessionGroup.add(theoryRadioButton);
        sessionGroup.add(practicalRadioButton);

        loadAllMedicals();

        addBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addMedicalAndUpdateAttendance();
            }
        });
        viewBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewMedicalById();
            }
        });
        deleteBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteMedical();
            }
        });
    }

    private void loadAllMedicals() {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"Med_Id","Attendance_Id", "Student_Id", "Course_Code", "Type", "Date", "Description"});
        MedicalTable.setModel(model);

        try(Connection con = DbConnection.getMyConnection()) {
            String medQuery = "SELECT * FROM medical";
            ResultSet rs = con.prepareStatement(medQuery).executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("med_id"),
                        rs.getInt("atten_id"),
                        rs.getString("stuId"),
                        rs.getString("courseCode"),
                        rs.getString("session_type"),
                        rs.getString("submissionDate"),
                        rs.getString("description"),
                });
            }
        } catch (SQLException e){
            JOptionPane.showMessageDialog(Medical, "DB load error" + e.getMessage());
        }
    }

    private void addMedicalAndUpdateAttendance() {
        String student = stuId.getText().trim();
        String course = courseCode.getText().trim();
        String date = submissionDate.getText().trim();
        String type = theoryRadioButton.isSelected() ? "theory" : "practical";
        String description = desc.getText().trim();

        if(student.isEmpty() || course.isEmpty() || date.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(Medical, "Please fill all the fields");
            return;
        }

        try(Connection con = DbConnection.getMyConnection()){
            String findAttenIdQuery = "SELECT atten_id FROM attendance WHERE stuId = ? AND courseCode = ? AND attenDate = ? AND session_type = ?";
            PreparedStatement ps = con.prepareStatement(findAttenIdQuery);
            ps.setString(1, student);
            ps.setString(2, course);
            ps.setString(3, date);
            ps.setString(4, type);

            ResultSet rs = ps.executeQuery();

            if(rs.next()) {
                int attId = rs.getInt("atten_id");
                String updateAtt = "UPDATE attendance SET atten_status = 'medical' WHERE atten_id = ?";
                PreparedStatement ps2 = con.prepareStatement(updateAtt);
                ps2.setInt(1, attId);
                ps2.executeUpdate();

                String addMedical = "INSERT INTO medical (atten_id, stuId, courseCode, session_type, submissionDate, description) VALUES (?,?,?,?,?,?)";
                PreparedStatement ps3 = con.prepareStatement(addMedical);
                ps3.setInt(1, attId);
                ps3.setString(2, student);
                ps3.setString(3, course);
                ps3.setString(4, type);
                ps3.setString(5, date);
                ps3.setString(6, description);
                ps3.executeUpdate();

                JOptionPane.showMessageDialog(Medical, "Successfully updated attendance and medical tables");
                loadAllMedicals();
            } else {
                JOptionPane.showMessageDialog(Medical, "No attendance record found for link with medical");
            }
        }
        catch(SQLException e){
            JOptionPane.showMessageDialog(Medical, "DB load error: " + e.getMessage());
        }
    }

    private void viewMedicalById() {
        String id = medIdforView.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(Medical, "Please enter a valid id");
            return;
        }

        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"Med_Id", "Atten_id", "Student_Id", "Course_Code", "Session_Type", "Date", "Description"});
        MedicalTable.setModel(model);

        try(Connection con = DbConnection.getMyConnection()){
            String medViewQuery = "SELECT * FROM medical WHERE med_id = ?";
            PreparedStatement ps = con.prepareStatement(medViewQuery);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()){
                model.addRow(new Object[]{
                        rs.getString("med_id"),
                        rs.getString("atten_id"),
                        rs.getString("stuId"),
                        rs.getString("courseCode"),
                        rs.getString("session_type"),
                        rs.getString("submissionDate"),
                        rs.getString("description")
                });
            } else {
                JOptionPane.showMessageDialog(Medical, "No record found");
            }
        } catch (SQLException e){
            JOptionPane.showMessageDialog(Medical, "DB view error" + e.getMessage());
        }
    }

    private void deleteMedical() {
        String id = medIdforView.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(Medical, "Please enter a valid id");
            return;
        }

        try (Connection con = DbConnection.getMyConnection()){
            String deleteQuery = "DELETE FROM medical WHERE med_id = ?";
            PreparedStatement ps = con.prepareStatement(deleteQuery);
            ps.setString(1, id);

            int deleted = ps.executeUpdate();
            if(deleted > 0){
                JOptionPane.showMessageDialog(Medical, "Deleted successfully");
                loadAllMedicals();
            } else {
                JOptionPane.showMessageDialog(Medical, "Delete failed");
            }
        } catch (SQLException e){
            JOptionPane.showMessageDialog(Medical, "DB delete error" + e.getMessage());
        }
    }

    public JPanel getRootPanel() {
        return Medical;
    }

    /*public static void main(String[] args) {
        JFrame frame = new JFrame("MedicalForm");
        frame.setContentPane(new MedicalForm().Medical);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }*/
}
