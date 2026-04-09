package com.appointmentsystem.ui;

import com.appointmentsystem.model.Appointment;
import com.appointmentsystem.model.AppointmentStatus;
import com.appointmentsystem.service.AppointmentService;
import com.appointmentsystem.service.AuthService;
import com.appointmentsystem.ai.ProductRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class AdminDashboard extends JFrame {
    
    private AppointmentService appointmentService;
    private AuthService authService;
    private JTable pendingTable;
    private DefaultTableModel pendingModel;
    private JTable historyTable;
    private DefaultTableModel historyModel;
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final Font UI_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 15);
    private ProductRepository productRepository;

    public AdminDashboard(AuthService authService, AppointmentService appointmentService, ProductRepository productRepository) {
        this.authService = authService;
        this.appointmentService = appointmentService;
        this.productRepository = productRepository;

        setTitle("Admin Dashboard");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 245, 245));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        headerPanel.setBackground(new Color(245, 245, 245));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JButton refreshBtn = new JButton("Refresh Data");
        styleButton(refreshBtn, new Color(70, 130, 180));
        
        JButton logoutBtn = new JButton("Logout");
        styleButton(logoutBtn, Color.RED);
        
        headerPanel.add(refreshBtn);
        headerPanel.add(logoutBtn);
        add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        contentPanel.setBackground(new Color(245, 245, 245));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel pendingPanel = new JPanel(new BorderLayout());
        pendingPanel.setBackground(Color.WHITE);
        pendingPanel.setBorder(BorderFactory.createTitledBorder("Pending Approvals"));
        pendingPanel.setFont(HEADER_FONT);
        
        pendingModel = new DefaultTableModel(new Object[]{"ID", "Date", "Time", "Supplier", "Spec", "Type", "Capacity", "Duration"}, 0);
        pendingTable = new JTable(pendingModel);
        styleTable(pendingTable);
        pendingPanel.add(new JScrollPane(pendingTable), BorderLayout.CENTER);

        JPanel pendingBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pendingBtnPanel.setBackground(new Color(245, 245, 245));
        
        JButton approveBtn = new JButton("Approve Selected");
        styleButton(approveBtn, new Color(0, 100, 0));
        
        JButton rejectBtn = new JButton("Reject Selected");
        styleButton(rejectBtn, new Color(255, 69, 0));
        
        pendingBtnPanel.add(approveBtn);
        pendingBtnPanel.add(rejectBtn);
        pendingPanel.add(pendingBtnPanel, BorderLayout.SOUTH);

        contentPanel.add(pendingPanel);

        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBackground(Color.WHITE);
        historyPanel.setBorder(BorderFactory.createTitledBorder("Processed Appointments"));
        historyPanel.setFont(HEADER_FONT);
        
        historyModel = new DefaultTableModel(new Object[]{"ID", "Date", "Time", "Supplier", "Spec", "Type", "Booked/Max", "Duration", "Customers", "Status"}, 0);
        historyTable = new JTable(historyModel);
        styleTable(historyTable);
        historyTable.getColumnModel().getColumn(historyModel.getColumnCount() - 1).setCellRenderer(TableUtils.createStatusRenderer());
        historyPanel.add(new JScrollPane(historyTable), BorderLayout.CENTER);

        contentPanel.add(historyPanel);
        add(contentPanel, BorderLayout.CENTER);

        refreshBtn.addActionListener(e -> loadData());
        approveBtn.addActionListener(e -> handleApprove());
        rejectBtn.addActionListener(e -> handleReject());
        logoutBtn.addActionListener(e -> logout());
        
        loadData();
    }

    private void styleButton(JButton btn, Color bgColor) {
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(UI_FONT);
        btn.setFocusPainted(false);
    }

    private void styleTable(JTable table) {
        table.setFont(UI_FONT);
        table.getTableHeader().setFont(HEADER_FONT);
        table.setRowHeight(25);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private void loadData() {
        appointmentService.updateCompletedAppointments();
        
        pendingModel.setRowCount(0);
        historyModel.setRowCount(0);

        List<Appointment> all = appointmentService.getAllAppointments();
        for (Appointment a : all) {
            long duration = Duration.between(a.getStartTime(), a.getEndTime()).toMinutes();
            String dateStr = a.getStartTime().format(dateFormatter);
            String timeStr = a.getStartTime().format(timeFormatter);
            String bookedInfo = a.getBookedCount() + "/" + a.getMaxParticipants();
            
            String customersList = a.getCustomers().stream()
                    .map(c -> c.getUsername())
                    .collect(Collectors.joining(", "));
            if (customersList.isEmpty()) customersList = "-";

            if (a.getStatus() == AppointmentStatus.PENDING) {
                pendingModel.addRow(new Object[]{
                    a.getId(), dateStr, timeStr, 
                    a.getSupplier().getUsername(), 
                    a.getSupplier().getSpecialization(), 
                    a.getType(), a.getMaxParticipants(), duration
                });
            } else {
                historyModel.addRow(new Object[]{
                    a.getId(), dateStr, timeStr, 
                    a.getSupplier().getUsername(), 
                    a.getSupplier().getSpecialization(), 
                    a.getType(), bookedInfo, duration, customersList, a.getStatus().toString()
                });
            }
        }
    }

    private void handleApprove() {
        int row = pendingTable.getSelectedRow();
        if (row >= 0) {
            int id = (int) pendingModel.getValueAt(row, 0);
            Appointment appt = appointmentService.getAllAppointments().stream()
                    .filter(a -> a.getId() == id).findFirst().orElse(null);
            
            if (appt != null) {
                String error = appointmentService.validateRules(appt);
                
                if (!error.isEmpty()) {
                    int confirm = JOptionPane.showConfirmDialog(this, 
                            error + "\nThis appointment violates business rules. Are you sure you want to approve it?", 
                            "Rule Violation Warning", 
                            JOptionPane.YES_NO_OPTION);
                    
                    if (confirm == JOptionPane.YES_OPTION) {
                        appointmentService.approveAppointment(id, null);
                        JOptionPane.showMessageDialog(this, "Approved with violations.");
                        loadData();
                    } else {
                        appointmentService.rejectAppointment(id);
                        JOptionPane.showMessageDialog(this, "Appointment Rejected.");
                        loadData();
                    }
                } else {
                    appointmentService.approveAppointment(id, null);
                    JOptionPane.showMessageDialog(this, "Approved Successfully.");
                    loadData();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a row.");
        }
    }
    
    private void handleReject() {
        int row = pendingTable.getSelectedRow();
        if (row >= 0) {
            int id = (int) pendingModel.getValueAt(row, 0);
            if (appointmentService.rejectAppointment(id)) {
                JOptionPane.showMessageDialog(this, "Rejected.");
                loadData();
            }
        }
    }

    private void logout() {
        authService.logout();
        new LoginFrame(authService, appointmentService, productRepository).setVisible(true);
        this.dispose();
    }
}