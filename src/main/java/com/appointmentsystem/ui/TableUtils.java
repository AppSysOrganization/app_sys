package com.appointmentsystem.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class TableUtils {
	
    public static DefaultTableCellRenderer createStatusRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                String status = (String) value;
                setHorizontalAlignment(SwingConstants.CENTER);

                if ("APPROVED".equals(status) || "BOOKED".equals(status)) {
                    c.setBackground(new Color(34, 139, 34)); 
                    c.setForeground(Color.WHITE);
                } else if ("REJECTED".equals(status) || "CANCELLED".equals(status)) {
                    c.setBackground(Color.RED);
                    c.setForeground(Color.WHITE);
                } else if ("EXPIRED".equals(status)) {
                    c.setBackground(new Color(180, 180, 180)); 
                    c.setForeground(Color.BLACK);
                } else if ("COMPLETED".equals(status)) {
                    c.setBackground(new Color(70, 130, 180)); 
                    c.setForeground(Color.WHITE);
                } else if ("PENDING".equals(status)) {
                    c.setBackground(Color.YELLOW);
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        };
    }
}