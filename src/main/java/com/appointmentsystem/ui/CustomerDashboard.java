package com.appointmentsystem.ui;

import com.appointmentsystem.ai.ChatBot;
import com.appointmentsystem.ai.ChatResponse;
import com.appointmentsystem.ai.ProductRepository;
import com.appointmentsystem.ai.ProductSearchEngine;
import com.appointmentsystem.model.Appointment;
import com.appointmentsystem.model.Customer;
import com.appointmentsystem.model.Product;
import com.appointmentsystem.service.AppointmentService;
import com.appointmentsystem.service.AuthService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CustomerDashboard extends JFrame {

    private AuthService authService;
    private AppointmentService appointmentService;
    private Customer currentCustomer;
    private ProductRepository productRepository;
    private JTable availableTable;
    private JTable myBookingsTable;
    private DefaultTableModel availableModel;
    private DefaultTableModel myBookingsModel;
    private List<Appointment> currentAvailableAppointments = new ArrayList<>();
    private int highlightedSupplierId = -1;
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final Font UI_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 15);

    public CustomerDashboard(AuthService authService, AppointmentService appointmentService, Customer customer) {
        this(authService, appointmentService, customer, null);
    }

    public CustomerDashboard(AuthService authService, AppointmentService appointmentService, Customer customer,
            ProductRepository productRepository) {
        this.authService = authService;
        this.appointmentService = appointmentService;
        this.currentCustomer = customer;
        this.productRepository = productRepository;

        setTitle("Customer Dashboard - " + customer.getUsername());
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 245, 245));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        headerPanel.setBackground(new Color(245, 245, 245));

        JButton chatbotBtn = new JButton("AI Chatbot");
        styleButton(chatbotBtn, new Color(75, 0, 130));
        chatbotBtn.setEnabled(productRepository != null);

        JButton logoutBtn = new JButton("Logout");
        styleButton(logoutBtn, Color.RED);

        headerPanel.add(chatbotBtn);
        headerPanel.add(logoutBtn);
        add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        contentPanel.setBackground(new Color(245, 245, 245));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createTitledBorder("Available Appointments"));
        topPanel.setFont(HEADER_FONT);

        availableModel = new DefaultTableModel(
                new Object[] { "ID", "Date", "Time", "Supplier", "Spec", "Type", "Slots Left", "Duration" }, 0);
        availableTable = new JTable(availableModel);
        styleTable(availableTable);
        availableTable.setDefaultRenderer(Object.class, new SupplierHighlightRenderer());

        availableTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                highlightedSupplierId = -1;
                availableTable.repaint();
            }
        });

        topPanel.add(new JScrollPane(availableTable), BorderLayout.CENTER);

        JButton bookBtn = new JButton("Book Selected");
        styleButton(bookBtn, new Color(34, 139, 34));

        JPanel bookPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bookPanel.setBackground(new Color(245, 245, 245));
        bookPanel.add(bookBtn);
        topPanel.add(bookPanel, BorderLayout.SOUTH);

        contentPanel.add(topPanel);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createTitledBorder("My Bookings"));
        bottomPanel.setFont(HEADER_FONT);

        myBookingsModel = new DefaultTableModel(
                new Object[] { "ID", "Date", "Time", "Supplier", "Spec", "Type", "Duration", "Status" }, 0);
        myBookingsTable = new JTable(myBookingsModel);
        styleTable(myBookingsTable);
        myBookingsTable.getColumnModel().getColumn(myBookingsModel.getColumnCount() - 1)
                .setCellRenderer(TableUtils.createStatusRenderer());

        bottomPanel.add(new JScrollPane(myBookingsTable), BorderLayout.CENTER);

        JButton cancelBtn = new JButton("Cancel My Booking");
        styleButton(cancelBtn, Color.ORANGE);

        JPanel cancelPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        cancelPanel.setBackground(new Color(245, 245, 245));
        cancelPanel.add(cancelBtn);
        bottomPanel.add(cancelPanel, BorderLayout.SOUTH);

        contentPanel.add(bottomPanel);
        add(contentPanel, BorderLayout.CENTER);

        bookBtn.addActionListener(e -> book());
        cancelBtn.addActionListener(e -> cancel());
        logoutBtn.addActionListener(e -> logout());
        chatbotBtn.addActionListener(e -> openChatBotWindow());

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

    private void openChatBotWindow() {
        this.setVisible(false);
        new ChatBotFrame(this).setVisible(true);
    }

    public void highlightSupplierAppointments(int supplierId) {
        this.highlightedSupplierId = supplierId;
        loadData();

        availableTable.clearSelection();
        boolean firstFound = false;

        for (int i = 0; i < currentAvailableAppointments.size(); i++) {
            if (currentAvailableAppointments.get(i).getSupplier().getId() == supplierId) {
                availableTable.addRowSelectionInterval(i, i);
                if (!firstFound) {
                    availableTable.scrollRectToVisible(availableTable.getCellRect(i, 0, true));
                    firstFound = true;
                }
            }
        }
    }

    private void loadData() {
        appointmentService.updateCompletedAppointments();
        availableModel.setRowCount(0);
        myBookingsModel.setRowCount(0);
        currentAvailableAppointments.clear();

        List<Appointment> available = appointmentService.getAvailableAppointments();
        for (Appointment a : available) {
            currentAvailableAppointments.add(a);
            long duration = Duration.between(a.getStartTime(), a.getEndTime()).toMinutes();
            int slotsLeft = a.getMaxParticipants() - a.getBookedCount();
            String dateStr = a.getStartTime().format(dateFormatter);
            String timeStr = a.getStartTime().format(timeFormatter);
            availableModel.addRow(new Object[] { a.getId(), dateStr, timeStr, a.getSupplier().getUsername(),
                    a.getSupplier().getSpecialization(), a.getType(), slotsLeft, duration });
        }

        List<Appointment> bookings = appointmentService.getBookingsByCustomer(currentCustomer);
        for (Appointment a : bookings) {
            long duration = Duration.between(a.getStartTime(), a.getEndTime()).toMinutes();
            String dateStr = a.getStartTime().format(dateFormatter);
            String timeStr = a.getStartTime().format(timeFormatter);
            myBookingsModel.addRow(new Object[] { a.getId(), dateStr, timeStr, a.getSupplier().getUsername(),
                    a.getSupplier().getSpecialization(), a.getType(), duration, a.getStatus().toString() });
        }
    }

    private class SupplierHighlightRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(JLabel.CENTER);
            if (!isSelected) {
                if (highlightedSupplierId != -1 && row < currentAvailableAppointments.size()) {
                    Appointment a = currentAvailableAppointments.get(row);
                    if (a.getSupplier().getId() == highlightedSupplierId) {
                        c.setBackground(new Color(173, 216, 230));
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                    } else {
                        c.setBackground(Color.WHITE);
                        c.setFont(UI_FONT);
                    }
                } else {
                    c.setBackground(Color.WHITE);
                    c.setFont(UI_FONT);
                }
            }
            return c;
        }
    }

    private void book() {
        int row = availableTable.getSelectedRow();
        if (row >= 0) {
            int id = (int) availableModel.getValueAt(row, 0);
            String error = appointmentService.bookAppointment(id, currentCustomer);
            if (error == null) {
                JOptionPane.showMessageDialog(this, "Booked Successfully!");
                highlightedSupplierId = -1;
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, error, "Booking Failed", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an appointment.");
        }
    }

    private void cancel() {
        int row = myBookingsTable.getSelectedRow();
        if (row >= 0) {
            int id = (int) myBookingsModel.getValueAt(row, 0);
            String error = appointmentService.cancelAppointment(id, currentCustomer);
            if (error == null) {
                JOptionPane.showMessageDialog(this, "Cancelled Successfully!");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, error, "Cancel Failed", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a booking to cancel.");
        }
    }

    private void logout() {
        authService.logout();
        new LoginFrame(authService, appointmentService, productRepository).setVisible(true);
        this.dispose();
    }

    private class ChatBotFrame extends JFrame {

        private JTable resultsTable;
        private DefaultTableModel resultsModel;
        private JTextField queryField;
        private JTextArea historyArea;
        private JPanel filterPanel;
        private JComboBox<String> priceFilterCombo;
        private CustomerDashboard parentDashboard;
        private List<Product> currentUnfilteredResults = new ArrayList<>();

        public ChatBotFrame(CustomerDashboard parent) {
            this.parentDashboard = parent;
            setTitle("AI Product Search");
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

            JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            splitPane.setResizeWeight(0.4);

            JPanel searchContainer = new JPanel(new BorderLayout(5, 5));
            searchContainer.setBackground(Color.WHITE);
            searchContainer.setBorder(BorderFactory.createTitledBorder("Search & History"));

            JPanel searchFieldPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
            searchFieldPanel.setBackground(Color.WHITE);

            queryField = new JTextField(35);
            queryField.setFont(UI_FONT);
            queryField.addActionListener(e -> searchProducts());

            JButton sendBtn = new JButton("Search");
            styleButton(sendBtn, new Color(75, 0, 130));
            sendBtn.addActionListener(e -> searchProducts());

            searchFieldPanel.add(new JLabel("Query:"));
            searchFieldPanel.add(queryField);
            searchFieldPanel.add(sendBtn);
            searchContainer.add(searchFieldPanel, BorderLayout.NORTH);

            historyArea = new JTextArea(3, 10);
            historyArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            historyArea.setEditable(false);
            historyArea.setText("--- Search History ---\n");
            searchContainer.add(new JScrollPane(historyArea), BorderLayout.CENTER);

            splitPane.setTopComponent(searchContainer);

            JPanel resultsContainer = new JPanel(new BorderLayout(5, 5));
            resultsContainer.setBackground(Color.WHITE);
            resultsContainer.setBorder(BorderFactory.createTitledBorder("Search Results (Sorted by Best Match)"));

            resultsModel = new DefaultTableModel(
                    new Object[] { "Name", "Supplier ID", "Category", "Price ($)", "Description" }, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            resultsTable = new JTable(resultsModel);
            styleTable(resultsTable);
            resultsTable.getColumnModel().getColumn(4).setCellRenderer(new DescriptionTooltipRenderer());

            resultsContainer.add(new JScrollPane(resultsTable), BorderLayout.CENTER);

            filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            filterPanel.setBackground(Color.WHITE);

            priceFilterCombo = new JComboBox<>(new String[] { "No Filter", "Under $100", "Under $500", "Under $1000",
                    "Under $5000", "Under $10000" });
            priceFilterCombo.setFont(UI_FONT);
            priceFilterCombo.addActionListener(e -> applyPriceFilter());

            filterPanel.add(new JLabel("Filter by Max Price:"));
            filterPanel.add(priceFilterCombo);
            resultsContainer.add(filterPanel, BorderLayout.SOUTH);

            JPanel bookActionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            bookActionPanel.setBackground(Color.WHITE);
            JButton bookProductBtn = new JButton("Book Appointment for Selected Product");
            styleButton(bookProductBtn, new Color(34, 139, 34));
            bookProductBtn.addActionListener(e -> bookSelectedProduct());
            bookActionPanel.add(bookProductBtn);

            JPanel bottomWrapper = new JPanel(new BorderLayout());
            bottomWrapper.setBackground(Color.WHITE);
            bottomWrapper.add(resultsContainer, BorderLayout.CENTER);
            bottomWrapper.add(bookActionPanel, BorderLayout.SOUTH);

            splitPane.setBottomComponent(bottomWrapper);
            add(splitPane, BorderLayout.CENTER);

            addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    goBack();
                }
            });
        }

        private void searchProducts() {
            String query = queryField.getText().trim();
            if (query.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a search query.");
                return;
            }

            ProductSearchEngine searchEngine = new ProductSearchEngine(productRepository);
            ChatBot bot = new ChatBot(searchEngine);
            ChatResponse response = bot.getResponse(query, 10);
            currentUnfilteredResults = response.getRecommendedProducts();

            historyArea.append("[" + java.time.LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                    + "] Searched: '" + query + "' -> Found " + currentUnfilteredResults.size() + " results.\n");

            filterPanel.setVisible(currentUnfilteredResults.size() > 1);
            priceFilterCombo.setSelectedIndex(0);
            applyPriceFilter();

            if (currentUnfilteredResults.isEmpty()) {
                JOptionPane.showMessageDialog(this, response.getResponseText(), "No Results",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }

        private void applyPriceFilter() {
            resultsModel.setRowCount(0);
            String selectedFilter = (String) priceFilterCombo.getSelectedItem();
            double maxPrice = Double.MAX_VALUE;
            if (!"No Filter".equals(selectedFilter)) {
                try {
                    maxPrice = Double.parseDouble(selectedFilter.replaceAll("[^0-9.]", ""));
                } catch (NumberFormatException e) {
                    maxPrice = Double.MAX_VALUE;
                }
            }
            for (Product p : currentUnfilteredResults) {
                if (p.getPrice() <= maxPrice) {
                    resultsModel.addRow(new Object[] { p.getName(), p.getSupplierId(), p.getCategory(),
                            String.format("%.2f", p.getPrice()), p.getDescription() });
                }
            }
        }

        private class DescriptionTooltipRenderer extends DefaultTableCellRenderer {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value != null) {
                    String htmlDesc = value.toString().replace("\n", "<br>");
                    setToolTipText(
                            "<html><div style='padding: 5px; border: 1px solid #aaaaaa; background-color: #ffffcc; font-size: 12px; font-family: Segoe UI; max-width: 300px;'>"
                                    + htmlDesc + "</div></html>");
                }
                return c;
            }
        }

        private void bookSelectedProduct() {
            int row = resultsTable.getSelectedRow();
            if (row >= 0) {
                int supplierId = (int) resultsModel.getValueAt(row, 1);
                parentDashboard.setVisible(true);
                parentDashboard.highlightSupplierAppointments(supplierId);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a product from the results table first.");
            }
        }

        private void goBack() {
            parentDashboard.setVisible(true);
            this.dispose();
        }
    }
}