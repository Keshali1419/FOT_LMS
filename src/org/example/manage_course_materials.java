package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.sql.*;

public class manage_course_materials extends JFrame {
    private JPanel panel1;
    private JButton button1;
    private JComboBox<String> comboBox1;
    private JTextField textField1;
    private JButton browseButton;
    private JTextField textField2;
    private JButton updateButton;
    private JButton resetButton;
    private JButton removeButton;
    private JTable table1;
    private JTextArea textArea1;

    String driver = "com.mysql.cj.jdbc.Driver";
    String url = "jdbc:mysql://localhost:3306/tecmis";
    String user = "root";
    String password = "1234";

    public manage_course_materials() {
        setTitle("Lecture Materials");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setContentPane(panel1);
        setVisible(true);

        loadCourses();
        loadTable();

        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                int result = chooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = chooser.getSelectedFile();
                    textField1.setText(selectedFile.getAbsolutePath());
                }
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedCourse = comboBox1.getSelectedItem().toString();
                String filePath = textField1.getText();

                if (filePath.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please select a file first.");
                    return;
                }

                File file = new File(filePath);
                try {
                    Class.forName(driver);
                    Connection con = DriverManager.getConnection(url, user, password);

                    PreparedStatement ps = con.prepareStatement(
                            "UPDATE course SET material_content = ? WHERE Course_code = ?");

                    FileInputStream fis = new FileInputStream(file);
                    ps.setBinaryStream(1, fis, (int) file.length());
                    ps.setString(2, selectedCourse);

                    int result = ps.executeUpdate();

                    if (result > 0) {
                        JOptionPane.showMessageDialog(null, "Material updated successfully!");

                        // ✅ Now update JTable row directly
                        DefaultTableModel model = (DefaultTableModel) table1.getModel();
                        for (int i = 0; i < model.getRowCount(); i++) {
                            String courseId = model.getValueAt(i, 0).toString();
                            if (courseId.equals(selectedCourse)) {
                                model.setValueAt(file.getName(), i, 1);
                                break;
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Course not found.");
                    }

                    ps.close();
                    con.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Upload failed.");
                }
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                comboBox1.setSelectedIndex(0);
                textField1.setText("");
                textField2.setText("");
                textArea1.setText("");
            }
        });

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = comboBox1.getSelectedItem().toString();
                try {
                    Class.forName(driver);
                    Connection con = DriverManager.getConnection(url, user, password);

                    PreparedStatement ps = con.prepareStatement(
                            "UPDATE course SET material_content = NULL WHERE Course_code = ?");
                    ps.setString(1, selected);

                    int result = ps.executeUpdate();

                    if (result > 0) {
                        JOptionPane.showMessageDialog(null, "Material removed.");


                        DefaultTableModel model = (DefaultTableModel) table1.getModel();
                        for (int i = 0; i < model.getRowCount(); i++) {
                            String courseId = model.getValueAt(i, 0).toString();
                            if (courseId.equals(selected)) {
                                model.setValueAt("No material available", i, 1);
                                break;
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Course not found.");
                    }

                    ps.close();
                    con.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Removal failed.");
                }
            }
        });

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadTable();
            }
        });

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new newlec_dash().setVisible(true);
                dispose();
            }
        });
    }

    private void loadCourses() {
        try {
            Class.forName(driver);
            Connection con = DriverManager.getConnection(url, user, password);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT Course_code FROM course");

            comboBox1.removeAllItems();
            while (rs.next()) {
                comboBox1.addItem(rs.getString("Course_code"));
            }

            rs.close();
            st.close();
            con.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to load courses.");
        }
    }

    private void loadTable() {
        try {
            Class.forName(driver);
            Connection con = DriverManager.getConnection(url, user, password);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT Course_code, material_content FROM course");

            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Course ID");
            model.addColumn("Material");

            while (rs.next()) {
                String id = rs.getString("Course_code");
                Blob materialBlob = rs.getBlob("material_content");

                String materialName;
                if (materialBlob == null) {
                    materialName = "No material available";
                } else {
                    materialName = "Material Uploaded"; // Just a simple label
                }

                model.addRow(new Object[]{id, materialName});
            }

            table1.setModel(model);

            rs.close();
            st.close();
            con.close();
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to load table. Error: " + sqlEx.getMessage());
        } catch (ClassNotFoundException cnfEx) {
            cnfEx.printStackTrace();
            JOptionPane.showMessageDialog(null, "JDBC Driver not found. Error: " + cnfEx.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to load table. Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new manage_course_materials();
    }
}
