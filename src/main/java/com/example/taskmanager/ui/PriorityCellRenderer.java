package com.example.taskmanager.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class PriorityCellRenderer extends DefaultTableCellRenderer {
    private static final Color COLOR_HIGH = new Color(0xFF5C5C);      // Red for HIGH
    private static final Color COLOR_MEDIUM = new Color(0xFFD166);    // Yellow for MEDIUM  
    private static final Color COLOR_LOW = new Color(0x52D273);       // Green for LOW
    private static final Color BG_DARK = new Color(0x1e1e1e);
    private static final Color BG_SELECTED = new Color(0x2d2d2d);

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setBackground(isSelected ? BG_SELECTED : BG_DARK);
        setHorizontalAlignment(SwingConstants.LEFT);

        // Always inherit the table's font (for font size persistence)
        if (table != null) {
            setFont(table.getFont());
        }

        if (value != null) {
            String text = String.valueOf(value);
            setText(text);
            
            // Priority values ALWAYS use their specific colors (never change)
            if ("HIGH".equalsIgnoreCase(text)) {
                setForeground(COLOR_HIGH);        // Always RED
            } else if ("MEDIUM".equalsIgnoreCase(text)) {
                setForeground(COLOR_MEDIUM);      // Always YELLOW
            } else if ("LOW".equalsIgnoreCase(text)) {
                setForeground(COLOR_LOW);         // Always GREEN
            } else {
                // Non-priority values use the table's font color (your custom color)
                setForeground(table.getForeground());
            }
        } else {
            // For null values, use the table's font color
            setForeground(table.getForeground());
        }
        return this;
    }
}
