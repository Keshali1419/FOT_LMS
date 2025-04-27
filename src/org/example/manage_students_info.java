package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class manage_students_info extends JFrame {
    private JPanel panel1;
    private JButton button2;
    private JButton button3;
    private JButton button4;
    private JButton button6;
    private JButton button1;
    private JButton viewButton;

    public manage_students_info() {

        setTitle("Student Information");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 600, 500);
        setContentPane(panel1);
        setVisible(true);

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newlec_dash b1 = new newlec_dash();
                b1.setVisible(true);
                dispose();


            }
        });
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                students_info b2 = new students_info();
                b2.setVisible(true);
                dispose();

            }
        });
        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                students_eligibility b3 = new students_eligibility();
                b3.setVisible(true);
                dispose();

            }
        });
        button4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manage_course_marks1 b4 = new manage_course_marks1();
                b4.setVisible(true);
                dispose();

            }
        });
        button6.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Attendence_medical b6 = new Attendence_medical();
                b6.setVisible(true);
                dispose();

            }
        });
        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                students_gpa b7 = new students_gpa();
                b7.setVisible(true);
                dispose();

            }
        });
    }

    public static void main(String[] args) {
        new manage_students_info().setVisible(true);
    }
}
