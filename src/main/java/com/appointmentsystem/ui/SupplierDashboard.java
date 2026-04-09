package com.appointmentsystem.ui;

import com.appointmentsystem.ai.ProductRepository;
import com.appointmentsystem.model.*;
import com.appointmentsystem.service.AppointmentService;
import com.appointmentsystem.service.AuthService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class SupplierDashboard extends JFrame {

    private AuthService authService;
    private AppointmentService appointmentService;
    private Supplier currentSupplier;
    private ProductRepository productRepository;

    private JTable table;
    private DefaultTableModel model;
    private JSpinner dateSpinner;
    private JTextField timeField;
    private JTextField durationField;
    private JTextField capacityField;
    private JComboBox<String> typeCombo;
    private JButton createBtn;
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private boolean isModifying = false;
    private int modifyingId = -1;
    private final Font UI_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 15);

    public SupplierDashboard(AuthService authService, AppointmentService appointmentService, Supplier supplier) {
        this(authService, appointmentService, supplier, null);
    }

    public SupplierDashboard(AuthService authService, AppointmentService appointmentService, Supplier supplier,
            ProductRepository productRepository) {
        this.authService = authService;
        this.appointmentService = appointmentService;
        this.currentSupplier = supplier;
        this.productRepository = productRepository;

        setTitle("Supplier Dashboard - " + supplier.getUsername());
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 245, 245));

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createTitledBorder("My Appointments"));
        tablePanel.setFont(HEADER_FONT);

        model = new DefaultTableModel(new Object[] { "ID", "Date", "Time", "Type", "Booked/Max", "Duration", "Status" }, 0);
        table = new JTable(model);
        table.setFont(UI_FONT);
        table.getTableHeader().setFont(HEADER_FONT);
        table.setRowHeight(25);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        table.getColumnModel().getColumn(model.getColumnCount() - 1).setCellRenderer(TableUtils.createStatusRenderer());
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel tableActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        tableActions.setBackground(new Color(245, 245, 245));

        JButton addProductBtn = new JButton("Manage Products");
        styleButton(addProductBtn, new Color(60, 179, 113));

        JButton modifyBtn = new JButton("Modify Selected Appointment");
        styleButton(modifyBtn, new Color(70, 130, 180));

        JButton cancelBtn = new JButton("Cancel Selected Appointment");
        styleButton(cancelBtn, Color.ORANGE);

        tableActions.add(addProductBtn);
        tableActions.add(modifyBtn);
        tableActions.add(cancelBtn);
        tablePanel.add(tableActions, BorderLayout.SOUTH);

        add(tablePanel, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Create New Appointment"));
        formPanel.setFont(HEADER_FONT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        styleFormLabel(gbc, 0, 0, formPanel, "Date:");
        gbc.gridx = 1;
        dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        dateSpinner.setValue(new Date());
        dateSpinner.setFont(UI_FONT);
        formPanel.add(dateSpinner, gbc);

        styleFormLabel(gbc, 2, 0, formPanel, "Time (HH:MM):");
        gbc.gridx = 3;
        timeField = new JTextField("10:00");
        styleTextField(timeField);
        formPanel.add(timeField, gbc);

        styleFormLabel(gbc, 0, 1, formPanel, "Duration (Min):");
        gbc.gridx = 1;
        durationField = new JTextField("30");
        styleTextField(durationField);
        formPanel.add(durationField, gbc);

        styleFormLabel(gbc, 2, 1, formPanel, "Capacity:");
        gbc.gridx = 3;
        capacityField = new JTextField("1");
        styleTextField(capacityField);
        formPanel.add(capacityField, gbc);

        styleFormLabel(gbc, 0, 2, formPanel, "Type:");
        gbc.gridx = 1;
        typeCombo = new JComboBox<>(new String[] { "IN_PERSON", "VIRTUAL", "URGENT", "FOLLOW_UP", "GROUP", "INDIVIDUAL", "ASSESSMENT" });
        typeCombo.setFont(UI_FONT);
        formPanel.add(typeCombo, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        btnPanel.setBackground(Color.WHITE);
        
        createBtn = new JButton("Submit Proposal");
        styleButton(createBtn, new Color(70, 130, 180));

        JButton logoutBtn = new JButton("Logout");
        styleButton(logoutBtn, Color.RED);

        btnPanel.add(createBtn);
        btnPanel.add(logoutBtn);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        formPanel.add(btnPanel, gbc);
        add(formPanel, BorderLayout.SOUTH);

        createBtn.addActionListener(e -> createOrUpdateAppointment());
        logoutBtn.addActionListener(e -> logout());
        cancelBtn.addActionListener(e -> cancelAppointment());
        modifyBtn.addActionListener(e -> populateFormForModification());
        addProductBtn.addActionListener(e -> openProductManagementWindow());

        loadData();
    }

    private void styleButton(JButton btn, Color bgColor) {
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(UI_FONT);
        btn.setFocusPainted(false);
    }

    private void styleTextField(JTextField field) {
        field.setFont(UI_FONT);
    }

    private void styleFormLabel(GridBagConstraints gbc, int x, int y, JPanel panel, String text) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.weightx = 0;
        JLabel label = new JLabel(text);
        label.setFont(UI_FONT);
        panel.add(label, gbc);
    }

    private void openProductManagementWindow() {
        this.setVisible(false);
        new ProductManagementFrame(this).setVisible(true);
    }

    private void loadData() {
        appointmentService.updateCompletedAppointments();
        model.setRowCount(0);
        List<Appointment> all = appointmentService.getAllAppointments();
        for (Appointment a : all) {
            if (a.getSupplier().getId() == currentSupplier.getId()) {
                long duration = Duration.between(a.getStartTime(), a.getEndTime()).toMinutes();
                String dateStr = a.getStartTime().format(dateFormatter);
                String timeStr = a.getStartTime().format(timeFormatter);
                String bookedInfo = a.getBookedCount() + "/" + a.getMaxParticipants();
                model.addRow(new Object[] { a.getId(), dateStr, timeStr, a.getType(), bookedInfo, duration, a.getStatus().toString() });
            }
        }
    }

    private void populateFormForModification() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            modifyingId = (int) model.getValueAt(row, 0);
            Appointment appt = null;
            for (Appointment a : appointmentService.getAllAppointments()) {
                if (a.getId() == modifyingId) { appt = a; break; }
            }
            if (appt != null) {
                Date date = Date.from(appt.getStartTime().atZone(ZoneId.systemDefault()).toInstant());
                dateSpinner.setValue(date);
                timeField.setText(appt.getStartTime().format(timeFormatter));
                durationField.setText(String.valueOf(Duration.between(appt.getStartTime(), appt.getEndTime()).toMinutes()));
                capacityField.setText(String.valueOf(appt.getMaxParticipants()));
                typeCombo.setSelectedItem(appt.getType().name());
                isModifying = true;
                createBtn.setText("Save Modifications");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an appointment to modify.");
        }
    }

    private void createOrUpdateAppointment() {
        try {
            Date date = (Date) dateSpinner.getValue();
            LocalDateTime localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            String[] timeParts = timeField.getText().split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);
            LocalDateTime start = localDate.withHour(hour).withMinute(minute);
            int duration = Integer.parseInt(durationField.getText());
            LocalDateTime end = start.plusMinutes(duration);
            AppointmentType type = AppointmentType.valueOf((String) typeCombo.getSelectedItem());
            int capacity = Integer.parseInt(capacityField.getText());

            if (isModifying) {
                String error = appointmentService.modifyAppointmentBySupplier(modifyingId, currentSupplier, start, end, type, capacity);
                if (error == null) {
                    JOptionPane.showMessageDialog(this, "Appointment modified successfully!\n(It will revert to PENDING if it was APPROVED).");
                    resetFormMode();
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this, error, "Modification Failed", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                Appointment appt = new Appointment((int) (Math.random() * 10000), start, end, type, capacity, currentSupplier);
                if (appointmentService.proposeAppointment(appt)) {
                    JOptionPane.showMessageDialog(this, "Proposal Sent!");
                    loadData();
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetFormMode() {
        isModifying = false;
        modifyingId = -1;
        createBtn.setText("Submit Proposal");
    }

    private void cancelAppointment() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            int id = (int) model.getValueAt(row, 0);
            String result = appointmentService.cancelAppointmentBySupplier(id, currentSupplier);
            if (result == null) { JOptionPane.showMessageDialog(this, "Appointment cancelled successfully."); loadData(); }
            else { JOptionPane.showMessageDialog(this, result, "Cancel Failed", JOptionPane.ERROR_MESSAGE); }
        } else { JOptionPane.showMessageDialog(this, "Please select an appointment to cancel."); }
    }

    private void logout() {
        authService.logout();
        new LoginFrame(authService, appointmentService, productRepository).setVisible(true);
        this.dispose();
    }

    private class ProductManagementFrame extends JFrame {

        private JTable productTable;
        private DefaultTableModel productModel;
        private JTextField nameField;
        private JTextField priceField;
        private JTextField categoryField;
        private JTextArea descArea;
        private JButton saveUpdateBtn;
        private JButton clearFormBtn;
        private boolean isUpdating = false;
        private int updatingProductId = -1;
        private SupplierDashboard parentDashboard;

        public ProductManagementFrame(SupplierDashboard parent) {
            this.parentDashboard = parent;
            setTitle("Manage My Products - " + currentSupplier.getUsername());
            setSize(900, 700);
            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            setLocationRelativeTo(null);
            setResizable(false);
            setLayout(new BorderLayout(10, 10));
            getContentPane().setBackground(new Color(245, 245, 245));

            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            topPanel.setBackground(new Color(245, 245, 245));
            JButton backBtn = new JButton("<< Back to Dashboard");
            styleButton(backBtn, new Color(200, 180, 140));
            backBtn.addActionListener(e -> goBack());
            topPanel.add(backBtn);
            add(topPanel, BorderLayout.NORTH);

            JPanel tablePanel = new JPanel(new BorderLayout());
            tablePanel.setBackground(Color.WHITE);
            tablePanel.setBorder(BorderFactory.createTitledBorder("My Products"));
            tablePanel.setFont(HEADER_FONT);

            productModel = new DefaultTableModel(new Object[] { "ID", "Name", "Category", "Price ($)", "Description" }, 0) {
                @Override
                public boolean isCellEditable(int row, int column) { return false; }
            };
            productTable = new JTable(productModel);
            productTable.setFont(UI_FONT);
            productTable.getTableHeader().setFont(HEADER_FONT);
            productTable.setRowHeight(25);
            
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            for (int i = 0; i < productTable.getColumnCount(); i++) {
                productTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }

            productTable.getColumnModel().getColumn(4).setCellRenderer(new DescriptionTooltipRenderer());

            tablePanel.add(new JScrollPane(productTable), BorderLayout.CENTER);

            JPanel tableActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            tableActions.setBackground(new Color(245, 245, 245));
            JButton updateBtn = new JButton("Update Selected");
            styleButton(updateBtn, new Color(70, 130, 180));
            updateBtn.addActionListener(e -> populateFormForUpdate());
            JButton deleteBtn = new JButton("Delete Selected");
            styleButton(deleteBtn, Color.RED);
            deleteBtn.addActionListener(e -> deleteProduct());
            tableActions.add(updateBtn);
            tableActions.add(deleteBtn);
            tablePanel.add(tableActions, BorderLayout.SOUTH);
            add(tablePanel, BorderLayout.CENTER);

            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBackground(Color.WHITE);
            formPanel.setBorder(BorderFactory.createTitledBorder("Product Details"));
            formPanel.setFont(HEADER_FONT);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            styleFormLabel(gbc, 0, 0, formPanel, "Name:");
            gbc.gridx = 1; gbc.weightx = 0.5;
            nameField = new JTextField(15); styleTextField(nameField);
            formPanel.add(nameField, gbc);

            styleFormLabel(gbc, 2, 0, formPanel, "Price ($):");
            gbc.gridx = 3; gbc.weightx = 0.5;
            priceField = new JTextField(15); styleTextField(priceField);
            formPanel.add(priceField, gbc);

            styleFormLabel(gbc, 0, 1, formPanel, "Category:");
            gbc.gridx = 1; gbc.weightx = 0.5;
            categoryField = new JTextField(15); styleTextField(categoryField);
            formPanel.add(categoryField, gbc);

            styleFormLabel(gbc, 2, 1, formPanel, "Description:");
            gbc.gridx = 3; gbc.weightx = 0.5;
            descArea = new JTextArea(2, 15);
            descArea.setFont(UI_FONT);
            descArea.setLineWrap(true);
            descArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            formPanel.add(new JScrollPane(descArea), gbc);

            JPanel formBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
            formBtnPanel.setBackground(Color.WHITE);
            saveUpdateBtn = new JButton("Add Product");
            styleButton(saveUpdateBtn, new Color(60, 179, 113));
            saveUpdateBtn.addActionListener(e -> saveOrUpdateProduct());
            clearFormBtn = new JButton("Clear Form");
            styleButton(clearFormBtn, Color.GRAY);
            clearFormBtn.addActionListener(e -> clearForm());
            formBtnPanel.add(saveUpdateBtn);
            formBtnPanel.add(clearFormBtn);
            gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4; gbc.weightx = 0;
            formPanel.add(formBtnPanel, gbc);
            add(formPanel, BorderLayout.SOUTH);

            addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) { goBack(); }
            });
            loadProductData();
        }

        private void loadProductData() {
            productModel.setRowCount(0);
            for (Product p : currentSupplier.getProducts()) {
                productModel.addRow(new Object[] { p.getId(), p.getName(), p.getCategory(), String.format("%.2f", p.getPrice()), p.getDescription() });
            }
        }

        private void populateFormForUpdate() {
            int row = productTable.getSelectedRow();
            if (row >= 0) {
                updatingProductId = (int) productModel.getValueAt(row, 0);
                nameField.setText(productModel.getValueAt(row, 1).toString());
                categoryField.setText(productModel.getValueAt(row, 2).toString());
                priceField.setText(productModel.getValueAt(row, 3).toString());
                descArea.setText(productModel.getValueAt(row, 4).toString());
                isUpdating = true;
                saveUpdateBtn.setText("Save Changes");
            } else { JOptionPane.showMessageDialog(this, "Please select a product to update."); }
        }

        private void deleteProduct() {
            int row = productTable.getSelectedRow();
            if (row >= 0) {
                int id = (int) productModel.getValueAt(row, 0);
                Product toRemove = null;
                for (Product p : currentSupplier.getProducts()) { if (p.getId() == id) { toRemove = p; break; } }
                if (toRemove != null) {
                    currentSupplier.removeProduct(toRemove);
                    if (productRepository != null) { productRepository.removeProduct(toRemove); }
                    loadProductData(); clearForm();
                    JOptionPane.showMessageDialog(this, "Product deleted successfully.");
                }
            } else { JOptionPane.showMessageDialog(this, "Please select a product to delete."); }
        }

        private void saveOrUpdateProduct() {
            try {
                String name = nameField.getText().trim(); String desc = descArea.getText().trim();
                String priceText = priceField.getText().trim(); String category = categoryField.getText().trim();
                if (name.isEmpty() || desc.isEmpty() || priceText.isEmpty() || category.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields are required!", "Validation Error", JOptionPane.ERROR_MESSAGE); return;
                }
                double price = Double.parseDouble(priceText);
                if (price < 0) { JOptionPane.showMessageDialog(this, "Price cannot be negative.", "Validation Error", JOptionPane.ERROR_MESSAGE); return; }
                if (isUpdating) {
                    currentSupplier.updateProduct(updatingProductId, name, desc, price, category);
                    JOptionPane.showMessageDialog(this, "Product updated successfully!");
                } else {
                    int newId = (int) (Math.random() * 10000);
                    Product newProduct = new Product(newId, name, desc, price, category, currentSupplier.getId());
                    currentSupplier.addProduct(newProduct);
                    if (productRepository != null) { productRepository.addProduct(newProduct); }
                    JOptionPane.showMessageDialog(this, "Product added successfully!");
                }
                loadProductData(); clearForm();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number for Price.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void clearForm() {
            nameField.setText(""); priceField.setText(""); categoryField.setText(""); descArea.setText("");
            isUpdating = false; updatingProductId = -1; saveUpdateBtn.setText("Add Product");
        }

        private void goBack() { parentDashboard.setVisible(true); this.dispose(); }
        
        private class DescriptionTooltipRenderer extends DefaultTableCellRenderer {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value != null) {
                    String htmlDesc = value.toString().replace("\n", "<br>");
                    setToolTipText("<html><div style='padding: 5px; border: 1px solid #aaaaaa; background-color: #ffffcc; font-size: 12px; font-family: Segoe UI; max-width: 300px;'>" + htmlDesc + "</div></html>");
                }
                return c;
            }
        }
    }
}