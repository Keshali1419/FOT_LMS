import db.DbConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TimetableForm extends JPanel{
    private JPanel timetableForm;
    private JTable timetable;

    private String techId;
    private String department;

    public TimetableForm(String techId) {
        this.techId = techId;
        this.department = departmentById(techId);

        loadTimetable();
    }

    private void loadTimetable() {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"TT_id", "Course Code", "Session Type", "Lecturer", "Day", "Start time", "End time"});
        timetable.setModel(model);

        try (Connection con = DbConnection.getMyConnection()){
            String timetableQuery = "SELECT * FROM timetable WHERE department = ?";
            PreparedStatement ps = con.prepareStatement(timetableQuery);
            ps.setString(1, department);
            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                model.addRow(new Object[]{
                    rs.getString("ttId"),
                    rs.getString("courseCode"),
                    rs.getString("sessionType"),
                    rs.getString("lecId"),
                    rs.getString("day"),
                    rs.getString("startTime"),
                    rs.getString("endTime")
                });
            }
        } catch (SQLException e){
            JOptionPane.showMessageDialog(timetableForm, "Error loading timetable: " + e.getMessage());
        }
    }

    private String departmentById(String techId) {
        try (Connection con = DbConnection.getMyConnection()){
            String depQuery = "SELECT deptName FROM technical_officer WHERE techId = ?";
            PreparedStatement ps = con.prepareStatement(depQuery);
            ps.setString(1, techId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()){
                return rs.getString("deptName");
            }
        } catch (SQLException e){
            JOptionPane.showMessageDialog(timetableForm, "Error when loading department: " + e.getMessage());
        }
        return "Unknown";
    }

    public JPanel getRootPanel(){
        return timetableForm;
    }

    /*public static void main(String[] args) {
        JFrame frame = new JFrame("TimetableForm");
        frame.setContentPane(new TimetableForm("TO001").timetableForm);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }*/
}
