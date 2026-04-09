package com.appointmentsystem.ui;

import com.appointmentsystem.ai.ProductRepository;
import com.appointmentsystem.model.Admin;
import com.appointmentsystem.model.Customer;
import com.appointmentsystem.model.Supplier;
import com.appointmentsystem.model.User;
import com.appointmentsystem.service.AppointmentService;
import com.appointmentsystem.service.AuthService;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private AuthService authService;
    private AppointmentService appointmentService;
    private ProductRepository productRepository;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame(AuthService authService, AppointmentService appointmentService, ProductRepository productRepository) {
        this.authService = authService;
        this.appointmentService = appointmentService;
        this.productRepository = productRepository;
        
        setTitle("Appointment System - Login");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Welcome to Appointment System", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 18));
        mainPanel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0;
        mainPanel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 18));
        mainPanel.add(passwordField, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        
        JButton loginBtn = new JButton("Login");
        loginBtn.setPreferredSize(new Dimension(150, 50));
        loginBtn.setBackground(new Color(34, 139, 34));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Arial", Font.BOLD, 16));
        loginBtn.setFocusPainted(false);
        
        JButton exitBtn = new JButton("Exit");
        exitBtn.setPreferredSize(new Dimension(150, 50));
        exitBtn.setBackground(new Color(220, 20, 60));
        exitBtn.setForeground(Color.WHITE);
        exitBtn.setFont(new Font("Arial", Font.BOLD, 16));
        exitBtn.setFocusPainted(false);

        btnPanel.add(loginBtn);
        btnPanel.add(exitBtn);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        mainPanel.add(btnPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);

        loginBtn.addActionListener(e -> performLogin());
        exitBtn.addActionListener(e -> System.exit(0));
    }

    private void performLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        User user = authService.login(username, password);

        if (user != null) {
            this.dispose();
            if (user instanceof Admin) {
            	new AdminDashboard(authService, appointmentService, productRepository).setVisible(true);
            } else if (user instanceof Supplier) {
                new SupplierDashboard(authService, appointmentService, (Supplier) user, productRepository).setVisible(true);
            } else if (user instanceof Customer) {
                new CustomerDashboard(authService, appointmentService, (Customer) user, productRepository).setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Username or Password", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}