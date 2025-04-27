import db.DbConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AttendanceForm extends JPanel{
    private JPanel attendance;
    private JPanel FormAddAttendance;
    private JTextField stuId;
    private JTextField courseCode;
    private JTextField date;
    private JRadioButton theoryRadioButton;
    private JRadioButton practicalRadioButton;
    private JRadioButton presentRadioButton;
    private JRadioButton absentRadioButton;
    private JRadioButton medicalRadioButton;
    private JButton addBtn;
    private JButton deleteBtn;
    private JButton viewAttBtn;
    private JTextField techId;
    private JPanel manageAttendance;
    private JTextField attId2;
    private JTable attendanceTable;

    private ButtonGroup sessionGroup;
    private ButtonGroup statusGroup;


    public AttendanceForm() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(attendance);

        sessionGroup = new ButtonGroup();
        sessionGroup.add(theoryRadioButton);
        sessionGroup.add(practicalRadioButton);

        statusGroup = new ButtonGroup();
        statusGroup.add(presentRadioButton);
        statusGroup.add(absentRadioButton);
        statusGroup.add(medicalRadioButton);

        addBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addAttendance();
            }
        });

        viewAttBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewSelectedAttendance();
            }
        });

        deleteBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteAttendance();
            }
        });

        viewAllAttendance();
    }

    private void addAttendance(){
        String tech = techId.getText().trim();
        String student = stuId.getText().trim();
        String course = courseCode.getText().trim();
        String dateStr = date.getText().trim();
        String session = theoryRadioButton.isSelected() ? "theory" : "practical";
        String status = presentRadioButton.isSelected() ? "present" : absentRadioButton.isSelected() ? "absent" : "medical";

        try(Connection con = DbConnection.getMyConnection()){
            String addAttQuery = "INSERT INTO attendance (techId, stuId, courseCode, attenDate, session_type, atten_status) VALUES (?,?,?,?,?,?)";
            PreparedStatement ps = con.prepareStatement(addAttQuery);
            ps.setString(1, tech);
            ps.setString(2, student);
            ps.setString(3, course);
            ps.setString(4, dateStr);
            ps.setString(5, session);
            ps.setString(6, status);

            int inserted = ps.executeUpdate();

            if(inserted > 0){
                JOptionPane.showMessageDialog(attendance, "Attendance added successfully");
                viewAllAttendance();
            }
        } catch (SQLException e){
            JOptionPane.showMessageDialog(attendance, "Error in adding attendance: " + e.getMessage());
        }
    }

    private void viewAllAttendance(){
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"ID", "Tech Id", "Student Id", "Course Code", "Date", "Session", "Status"});
        attendanceTable.setModel(model);

        try (Connection con = DbConnection.getMyConnection()){
            String viewAttQuery = "SELECT * FROM attendance";
            ResultSet rs = con.prepareStatement(viewAttQuery).executeQuery();

            while (rs.next()){
                model.addRow(new Object[]{
                        rs.getInt("atten_id"),
                        rs.getString("techId"),
                        rs.getString("stuId"),
                        rs.getString("courseCode"),
                        rs.getString("attenDate"),
                        rs.getString("session_type"),
                        rs.getString("atten_status")
                });
            }
        } catch (SQLException e){
            JOptionPane.showMessageDialog(attendance, "Error in loading attendance records: " + e.getMessage());
        }
    }

    private void viewSelectedAttendance(){
        String id = attId2.getText().trim();
        if(id.isEmpty()){
            JOptionPane.showMessageDialog(attendance, "Please select attendance id");
            return;
        }

        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"ID", "Tech Id", "Student Id", "Course Code", "Date", "Session", "Status"});
        attendanceTable.setModel(model);

        try (Connection con = DbConnection.getMyConnection()){
            String viewAttQuery = "SELECT * FROM attendance WHERE atten_id = ?";
            PreparedStatement ps = con.prepareStatement(viewAttQuery);
            ps.setInt(1, Integer.parseInt(id));
            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                model.addRow(new Object[]{
                        rs.getInt("atten_id"),
                        rs.getString("techId"),
                        rs.getString("stuId"),
                        rs.getString("courseCode"),
                        rs.getString("attenDate"),
                        rs.getString("session_type"),
                        rs.getString("atten_status")
                });
            }
        } catch (SQLException e){
            JOptionPane.showMessageDialog(attendance, "Error in loading attendance records for student: " + e.getMessage());
        }
    }

    private void deleteAttendance(){
        String id = attId2.getText().trim();

        if(id.isEmpty()){
            JOptionPane.showMessageDialog(attendance, "Please enter attendance id");
            return;
        }

        try (Connection con = DbConnection.getMyConnection()){
            String deleteAttQuery = "DELETE FROM attendance WHERE atten_id = ?";
            PreparedStatement ps = con.prepareStatement(deleteAttQuery);
            ps.setInt(1, Integer.parseInt(id));

            int deleted = ps.executeUpdate();
            if(deleted > 0){
                JOptionPane.showMessageDialog(attendance, "Attendance deleted successfully");
                viewAllAttendance();
            } else {
                JOptionPane.showMessageDialog(attendance, "Record could not be deleted");
            }
        } catch (SQLException e){
            JOptionPane.showMessageDialog(attendance, "Error in deleting attendance: " + e.getMessage());
        }
    }

    public JPanel getRootPanel() {
        return attendance;
    }

    /*public static void main(String[] args) {
        JFrame frame = new JFrame("AttendanceForm");
        frame.setContentPane(new AttendanceForm().attendance);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }*/

}
