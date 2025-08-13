package com.example.taskmanager.ui;

import com.example.taskmanager.model.Task;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class TaskDialog extends JDialog {
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JComboBox<Task.Priority> priorityCombo;
    private JComboBox<Task.Status> statusCombo;
    private JTextField dueDateField; // ISO yyyy-MM-dd

    private Task taskResult;

    public TaskDialog(Window owner, Task existing) {
        super(owner, existing == null ? "Add Task" : "Edit Task", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(520, 480);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel content = new JPanel(new GridBagLayout());
        content.setBorder(new EmptyBorder(16, 16, 16, 16));
        content.setBackground(new Color(0x1e1e1e));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;

        JLabel titleLbl = createLabel("Title");
        content.add(titleLbl, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        titleField = createTextField();
        content.add(titleField, gbc);

        gbc.gridx = 0; gbc.gridy++; gbc.weightx = 0;
        JLabel descLbl = createLabel("Description");
        content.add(descLbl, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        descriptionArea = createTextArea();
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        styleScroll(descScroll);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        content.add(descScroll, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weighty = 0;

        gbc.gridx = 0; gbc.gridy++;
        JLabel priorityLbl = createLabel("Priority");
        content.add(priorityLbl, gbc);

        gbc.gridx = 1;
        priorityCombo = new JComboBox<>(Task.Priority.values());
        styleCombo(priorityCombo);
        content.add(priorityCombo, gbc);

        gbc.gridx = 0; gbc.gridy++;
        JLabel statusLbl = createLabel("Status");
        content.add(statusLbl, gbc);

        gbc.gridx = 1;
        statusCombo = new JComboBox<>(Task.Status.values());
        styleCombo(statusCombo);
        content.add(statusCombo, gbc);

        gbc.gridx = 0; gbc.gridy++;
        JLabel dueLbl = createLabel("Due Date (YYYY-MM-DD)");
        content.add(dueLbl, gbc);

        gbc.gridx = 1;
        JPanel duePanel = new JPanel(new BorderLayout(6, 0));
        duePanel.setBackground(new Color(0x1e1e1e));
        dueDateField = createTextField();
        JButton calBtn = createButton("📅");
        calBtn.setPreferredSize(new Dimension(44, dueDateField.getPreferredSize().height));
        duePanel.add(dueDateField, BorderLayout.CENTER);
        duePanel.add(calBtn, BorderLayout.EAST);
        content.add(duePanel, gbc);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setBackground(new Color(0x1e1e1e));
        JButton cancelBtn = createButton("Cancel");
        JButton okBtn = createButton("Save");
        actions.add(cancelBtn);
        actions.add(okBtn);

        add(content, BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);

        if (existing != null) {
            titleField.setText(existing.getTitle());
            descriptionArea.setText(existing.getDescription());
            priorityCombo.setSelectedItem(existing.getPriority());
            statusCombo.setSelectedItem(existing.getStatus());
            dueDateField.setText(existing.getDueDate() == null ? "" : existing.getDueDate().toString());
        } else {
            statusCombo.setSelectedItem(Task.Status.TO_DO);
            priorityCombo.setSelectedItem(Task.Priority.MEDIUM);
        }

        cancelBtn.addActionListener(e -> { taskResult = null; dispose(); });
        okBtn.addActionListener(e -> onSave(existing));

        calBtn.addActionListener(e -> onPickDate());
        dueDateField.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) onPickDate();
            }
        });
    }

    private void onPickDate() {
        LocalDate initial = null;
        String txt = dueDateField.getText().trim();
        if (!txt.isEmpty()) {
            try { initial = LocalDate.parse(txt); } catch (Exception ignore) {}
        }
        DatePickerPopup picker = new DatePickerPopup(initial, date -> dueDateField.setText(date.toString()));
        picker.showRelativeTo(dueDateField);
    }

    private void onSave(Task existing) {
        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title is required", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String desc = descriptionArea.getText();
        Task.Priority priority = (Task.Priority) priorityCombo.getSelectedItem();
        Task.Status status = (Task.Status) statusCombo.getSelectedItem();
        String due = dueDateField.getText().trim();
        LocalDate dueDate = null;
        if (!due.isEmpty()) {
            try {
                dueDate = LocalDate.parse(due);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        if (existing == null) {
            taskResult = new Task(title, desc, priority, status, dueDate);
        } else {
            existing.setTitle(title);
            existing.setDescription(desc);
            existing.setPriority(priority);
            existing.setStatus(status);
            existing.setDueDate(dueDate);
            taskResult = existing;
        }
        dispose();
    }

    public Task getTaskResult() {
        return taskResult;
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(Color.WHITE);
        return l;
    }

    private JTextField createTextField() {
        JTextField tf = new JTextField();
        tf.setBackground(new Color(0x2a2a2a));
        tf.setForeground(Color.WHITE);
        tf.setCaretColor(Color.WHITE);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x3a3a3a)),
                new EmptyBorder(6, 8, 6, 8)
        ));
        return tf;
    }

    private JTextArea createTextArea() {
        JTextArea ta = new JTextArea(6, 20);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setBackground(new Color(0x2a2a2a));
        ta.setForeground(Color.WHITE);
        ta.setCaretColor(Color.WHITE);
        ta.setBorder(new EmptyBorder(6, 8, 6, 8));
        return ta;
    }

    private void styleScroll(JScrollPane sp) {
        sp.getViewport().setBackground(new Color(0x2a2a2a));
        sp.setBorder(BorderFactory.createLineBorder(new Color(0x3a3a3a)));
    }

    private void styleCombo(JComboBox<?> combo) {
        combo.setBackground(new Color(0x2a2a2a));
        combo.setForeground(Color.WHITE);
        combo.setBorder(BorderFactory.createLineBorder(new Color(0x3a3a3a)));
        combo.setFocusable(false);
    }

    private JButton createButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setBackground(new Color(0x2f2f2f));
        b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x3a3a3a)),
                new EmptyBorder(6, 14, 6, 14)
        ));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        Color normal = b.getBackground();
        Color hover = new Color(0x3a3a3a);
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(hover); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { b.setBackground(normal); }
        });
        return b;
    }
} 