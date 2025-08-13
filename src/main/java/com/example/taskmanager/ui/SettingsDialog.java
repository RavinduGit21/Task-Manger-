package com.example.taskmanager.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SettingsDialog extends JDialog {
    private JSpinner fontSizeSpinner;
    private JButton fontColorButton;
    private Color selectedFontColor;
    private int selectedFontSize;
    private boolean settingsChanged = false;

    public SettingsDialog(JFrame parent) {
        this(parent, 12, Color.WHITE); // Default values
    }
    
    public SettingsDialog(JFrame parent, int currentFontSize, Color currentFontColor) {
        super(parent, "Settings", true);
        this.selectedFontSize = currentFontSize;
        this.selectedFontColor = currentFontColor;
        
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        
        // Apply dark theme
        getContentPane().setBackground(new Color(0x1e1e1e));
        
        createUI();
        pack();
    }

    private void createUI() {
        // Title
        JLabel titleLabel = new JLabel("🎨 Appearance & Theme", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(titleLabel, BorderLayout.NORTH);

        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(0x1e1e1e));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Font Size Section
        JPanel fontSizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fontSizePanel.setBackground(new Color(0x1e1e1e));
        
        JLabel fontSizeLabel = new JLabel("Font Size:");
        fontSizeLabel.setForeground(Color.WHITE);
        fontSizePanel.add(fontSizeLabel);
        
        fontSizeSpinner = new JSpinner(new SpinnerNumberModel(selectedFontSize, 8, 24, 1));
        styleSpinner(fontSizeSpinner);
        fontSizePanel.add(fontSizeSpinner);
        
        fontSizePanel.add(Box.createHorizontalStrut(20));
        
        JLabel pxLabel = new JLabel("px");
        pxLabel.setForeground(Color.WHITE);
        fontSizePanel.add(pxLabel);
        
        contentPanel.add(fontSizePanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Font Color Section
        JPanel fontColorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fontColorPanel.setBackground(new Color(0x1e1e1e));
        
        JLabel fontColorLabel = new JLabel("Font Color:");
        fontColorLabel.setForeground(Color.WHITE);
        fontColorPanel.add(fontColorLabel);
        
        fontColorButton = new JButton("Choose Color");
        styleButton(fontColorButton);
        fontColorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color newColor = JColorChooser.showDialog(SettingsDialog.this, 
                    "Choose Font Color", selectedFontColor);
                if (newColor != null) {
                    selectedFontColor = newColor;
                    updateColorButton();
                }
            }
        });
        fontColorPanel.add(fontColorButton);
        
        contentPanel.add(fontColorPanel);
        contentPanel.add(Box.createVerticalStrut(30));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(0x1e1e1e));
        
        JButton resetButton = new JButton("Reset to Default");
        styleButton(resetButton);
        resetButton.addActionListener(e -> {
            selectedFontSize = 12;
            selectedFontColor = Color.WHITE;
            fontSizeSpinner.setValue(selectedFontSize);
            updateColorButton();
        });
        
        JButton applyButton = new JButton("Apply");
        styleButton(applyButton);
        applyButton.addActionListener(e -> {
            selectedFontSize = (Integer) fontSizeSpinner.getValue();
            settingsChanged = true;
            dispose();
        });
        
        JButton cancelButton = new JButton("Cancel");
        styleButton(cancelButton);
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(resetButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(applyButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(cancelButton);
        
        contentPanel.add(buttonPanel);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Initialize color button display
        updateColorButton();
    }

    private void updateColorButton() {
        fontColorButton.setBackground(selectedFontColor);
        fontColorButton.setForeground(getContrastColor(selectedFontColor));
        fontColorButton.setText(String.format("#%02x%02x%02x", 
            selectedFontColor.getRed(), 
            selectedFontColor.getGreen(), 
            selectedFontColor.getBlue()));
    }

    private Color getContrastColor(Color color) {
        double luminance = (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255;
        return luminance > 0.5 ? Color.BLACK : Color.WHITE;
    }

    private void styleSpinner(JSpinner spinner) {
        // Simple styling for Java 8 compatibility
        try {
            JComponent editor = spinner.getEditor();
            if (editor instanceof JSpinner.DefaultEditor) {
                JTextField textField = ((JSpinner.DefaultEditor) editor).getTextField();
                textField.setBackground(new Color(0x2a2a2a));
                textField.setForeground(Color.WHITE);
                textField.setBorder(BorderFactory.createLineBorder(new Color(0x3a3a3a)));
                textField.setCaretColor(Color.WHITE);
            }
        } catch (Exception e) {
            // Fallback styling
        }
    }

    private void styleButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(new Color(0x2f2f2f));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x3a3a3a)),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public boolean hasSettingsChanged() {
        return settingsChanged;
    }

    public int getSelectedFontSize() {
        return selectedFontSize;
    }

    public Color getSelectedFontColor() {
        return selectedFontColor;
    }
}
