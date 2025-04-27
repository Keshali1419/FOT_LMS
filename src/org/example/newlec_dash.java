package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class newlec_dash extends JFrame {
    private JPanel mainp;
    private JButton manageButton;
    private JButton manageButton1;
    private JButton manageButton2;
    private JButton manageButton3;
    private JButton manageButton4;

    public newlec_dash() {
        setTitle("Lecture Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 600, 600);
        setContentPane(mainp);
        setVisible(true);

        manageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Manage_profile b1 = new Manage_profile();
                b1.setVisible(true);
                dispose();

            }
        });
        manageButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manage_course_materials b2 = new manage_course_materials();
                b2.setVisible(true);
                dispose();

            }
        });
        manageButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manage_course_marks1 b3 = new manage_course_marks1();
                b3.setVisible(true);
                dispose();

            }
        });
        manageButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manage_students_info b4 = new manage_students_info();
                b4.setVisible(true);
                dispose();

            }
        });
        manageButton4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                university_notice b5 = new university_notice();
                b5.setVisible(true);
                dispose();

            }
        });

        manageButton4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                university_notice b5 = new university_notice();
                b5.setVisible(true);
                dispose();
            }
        });
    }

    public static void main(String[] args) {
        new newlec_dash();
    }
}
