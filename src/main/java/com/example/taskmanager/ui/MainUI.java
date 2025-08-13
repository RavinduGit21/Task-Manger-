package com.example.taskmanager.ui;

import com.example.taskmanager.db.DatabaseHandler;
import com.example.taskmanager.model.Task;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class MainUI extends JFrame {
    private final DatabaseHandler db = DatabaseHandler.getInstance();
    private static final String SETTINGS_FILE = "taskmanager_settings.properties";

    private JTextField searchField;
    private JComboBox<String> statusFilterCombo;
    private JTable taskTable;
    private TaskTableModel tableModel;
    private JLabel statusBarLabel;
    
    // Font settings
    private int currentFontSize = 12;
    private Color currentFontColor = Color.WHITE;

    public MainUI() {
        super("Task Manager");
        loadFontSettings(); // Load saved settings before applying theme
        applyDarkTheme();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 620);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        setJMenuBar(createMenuBar());
        add(createTopBar(), BorderLayout.NORTH);
        add(createCenterTable(), BorderLayout.CENTER);
        add(createStatusBar(), BorderLayout.SOUTH);

        refreshTasks();
        updateStatusBar();
        
        // Apply loaded font settings to the table
        applyFontSettings();
    }

    private JMenuBar createMenuBar() {
        JMenuBar bar = new JMenuBar();
        bar.setBackground(new Color(0x1e1e1e));
        bar.setBorder(BorderFactory.createMatteBorder(0,0,1,0,new Color(0x2a2a2a)));

        JMenu file = new JMenu("File");
        styleMenu(file);
        JMenuItem addItem = new JMenuItem("Add Task");
        JMenuItem exitItem = new JMenuItem("Exit");
        styleMenuItem(addItem);
        styleMenuItem(exitItem);

        addItem.addActionListener(e -> onAdd());
        exitItem.addActionListener(e -> System.exit(0));

        file.add(addItem);
        file.addSeparator();
        file.add(exitItem);

        bar.add(file);
        return bar;
    }

    private JPanel createTopBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0x1e1e1e));
        panel.setBorder(new EmptyBorder(8, 12, 8, 12));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setBackground(new Color(0x1e1e1e));
        searchField = createTextField(28);
        searchField.putClientProperty("JTextField.placeholderText", "Search by title...");
        searchField.getDocument().addDocumentListener((SimpleDocumentListener) e -> refreshTasks());
        left.add(searchField);

        statusFilterCombo = new JComboBox<>(new String[]{"All", "TO_DO", "IN_PROGRESS", "COMPLETED"});
        styleCombo(statusFilterCombo);
        statusFilterCombo.addActionListener(e -> refreshTasks());
        left.add(statusFilterCombo);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setBackground(new Color(0x1e1e1e));
        
        // Settings button (top right)
        JButton settingsBtn = createButton("⚙");
        settingsBtn.setToolTipText("Settings");
        settingsBtn.setPreferredSize(new Dimension(44, 32));
        settingsBtn.addActionListener(e -> onSettings());
        right.add(settingsBtn);
        
        right.add(Box.createHorizontalStrut(16)); // Spacer
        
        JButton addBtn = createButton("Add");
        JButton editBtn = createButton("Edit");
        JButton completeBtn = createButton("Complete");
        JButton delBtn = createButton("Delete");
        addBtn.addActionListener(e -> onAdd());
        editBtn.addActionListener(e -> onEdit());
        completeBtn.addActionListener(e -> onComplete());
        delBtn.addActionListener(e -> onDelete());
        right.add(addBtn); right.add(editBtn); right.add(completeBtn); right.add(delBtn);

        panel.add(left, BorderLayout.WEST);
        panel.add(right, BorderLayout.EAST);
        return panel;
    }

    private JScrollPane createCenterTable() {
        tableModel = new TaskTableModel();
        taskTable = new JTable(tableModel);
        taskTable.setFillsViewportHeight(true);
        taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskTable.setShowGrid(true);
        taskTable.setGridColor(new Color(0x2a2a2a));
        taskTable.setRowHeight(28);
        taskTable.setBackground(new Color(0x1e1e1e));
        taskTable.setForeground(Color.WHITE);
        taskTable.setSelectionBackground(new Color(0x2d2d2d));
        taskTable.setSelectionForeground(Color.WHITE);

        JTableHeader header = taskTable.getTableHeader();
        header.setBackground(new Color(0x2a2a2a));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createMatteBorder(0,0,1,0,new Color(0x3a3a3a)));

        TableColumn idCol = taskTable.getColumnModel().getColumn(0);
        idCol.setMinWidth(0);
        idCol.setMaxWidth(0);
        idCol.setPreferredWidth(0);

        TableColumn priorityCol = taskTable.getColumnModel().getColumn(3);
        priorityCol.setCellRenderer(new PriorityCellRenderer());

        taskTable.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && taskTable.getSelectedRow() != -1) {
                    onEdit();
                }
            }
        });

        JScrollPane sp = new JScrollPane(taskTable);
        sp.getViewport().setBackground(new Color(0x1e1e1e));
        sp.setBorder(BorderFactory.createMatteBorder(1,0,0,0,new Color(0x2a2a2a)));
        return sp;
    }

    private JPanel createStatusBar() {
        JPanel sb = new JPanel(new BorderLayout());
        sb.setBackground(new Color(0x1e1e1e));
        sb.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0x2a2a2a)));
        statusBarLabel = new JLabel();
        statusBarLabel.setForeground(new Color(0xcccccc));
        statusBarLabel.setBorder(new EmptyBorder(6, 12, 6, 12));
        sb.add(statusBarLabel, BorderLayout.WEST);
        return sb;
    }

    private void onSettings() {
        SettingsDialog dialog = new SettingsDialog(this, currentFontSize, currentFontColor);
        dialog.setVisible(true);
        
        // Apply settings if they were changed
        if (dialog.hasSettingsChanged()) {
            currentFontSize = dialog.getSelectedFontSize();
            currentFontColor = dialog.getSelectedFontColor();
            saveFontSettings(); // Save settings to file
            applyFontSettings();
        }
    }
    
    private void applyFontSettings() {
        // Update table font - this is the key to persistence
        Font newFont = new Font("Arial", Font.PLAIN, currentFontSize);
        taskTable.setFont(newFont);
        taskTable.setForeground(currentFontColor);
        
        // Update table header font
        taskTable.getTableHeader().setFont(newFont);
        taskTable.getTableHeader().setForeground(currentFontColor);
        
        // Update row height based on font size
        int newRowHeight = Math.max(28, currentFontSize + 8);
        taskTable.setRowHeight(newRowHeight);
        
        // Force table refresh to ensure font changes are applied
        taskTable.repaint();
        
        // Show confirmation
        JOptionPane.showMessageDialog(this, 
            "Font settings applied!\nSize: " + currentFontSize + "px\nColor: " + 
            String.format("#%02x%02x%02x", currentFontColor.getRed(), 
                currentFontColor.getGreen(), currentFontColor.getBlue()),
            "Settings Applied", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadFontSettings() {
        Properties props = new Properties();
        File settingsFile = new File(SETTINGS_FILE);
        
        if (settingsFile.exists()) {
            try (FileInputStream fis = new FileInputStream(settingsFile)) {
                props.load(fis);
                
                // Load font size
                String fontSizeStr = props.getProperty("font.size", "12");
                try {
                    currentFontSize = Integer.parseInt(fontSizeStr);
                } catch (NumberFormatException e) {
                    currentFontSize = 12; // Default if invalid
                }
                
                // Load font color
                String fontColorStr = props.getProperty("font.color", "#FFFFFF");
                try {
                    currentFontColor = Color.decode(fontColorStr);
                } catch (NumberFormatException e) {
                    currentFontColor = Color.WHITE; // Default if invalid
                }
            } catch (IOException e) {
                // Use defaults if file can't be read
                currentFontSize = 12;
                currentFontColor = Color.WHITE;
            }
        }
    }
    
    private void saveFontSettings() {
        Properties props = new Properties();
        
        // Save font size
        props.setProperty("font.size", String.valueOf(currentFontSize));
        
        // Save font color as hex string
        String colorHex = String.format("#%02x%02x%02x", 
            currentFontColor.getRed(), 
            currentFontColor.getGreen(), 
            currentFontColor.getBlue());
        props.setProperty("font.color", colorHex);
        
        try (FileOutputStream fos = new FileOutputStream(SETTINGS_FILE)) {
            props.store(fos, "Task Manager Font Settings");
        } catch (IOException e) {
            // Show error if settings can't be saved
            JOptionPane.showMessageDialog(this, 
                "Warning: Could not save font settings.\n" + e.getMessage(),
                "Settings Save Error", 
                JOptionPane.WARNING_MESSAGE);
        }
    }

    private void refreshTasks() {
        String search = searchField == null ? "" : searchField.getText();
        String statusFilter = statusFilterCombo == null ? "All" : (String) statusFilterCombo.getSelectedItem();
        List<Task> tasks = db.getTasks(search, statusFilter);
        tableModel.setTasks(tasks);
        updateStatusBar();
    }

    private void updateStatusBar() {
        int total = db.countTotal();
        int completed = db.countCompleted();
        statusBarLabel.setText("Total: " + total + " | Completed: " + completed);
    }

    private void onAdd() {
        TaskDialog dialog = new TaskDialog(this, null);
        dialog.setVisible(true);
        Task created = dialog.getTaskResult();
        if (created != null) {
            int id = db.insertTask(created);
            created.setId(id);
            refreshTasks();
        }
    }

    private void onEdit() {
        int row = taskTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a task to edit.");
            return;
        }
        Task selected = tableModel.getTaskAt(row);
        Task copy = new Task(
                selected.getId(),
                selected.getTitle(),
                selected.getDescription(),
                selected.getPriority(),
                selected.getStatus(),
                selected.getDueDate()
        );
        TaskDialog dialog = new TaskDialog(this, copy);
        dialog.setVisible(true);
        Task edited = dialog.getTaskResult();
        if (edited != null) {
            db.updateTask(edited);
            refreshTasks();
        }
    }

    private void onComplete() {
        int row = taskTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a task to complete.");
            return;
        }
        Task selected = tableModel.getTaskAt(row);
        if (selected.getStatus() == Task.Status.COMPLETED) {
            JOptionPane.showMessageDialog(this, "Task is already completed.");
            return;
        }
        selected.setStatus(Task.Status.COMPLETED);
        db.updateTask(selected);
        refreshTasks();
    }

    private void onDelete() {
        int row = taskTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a task to delete.");
            return;
        }
        Task selected = tableModel.getTaskAt(row);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete task '" + selected.getTitle() + "'?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            db.deleteTask(selected.getId());
            refreshTasks();
        }
    }

    private void applyDarkTheme() {
        UIManager.put("control", new Color(0x1e1e1e));
        UIManager.put("info", new Color(0x1e1e1e));
        UIManager.put("nimbusBase", new Color(0x121212));
        UIManager.put("nimbusAlertYellow", new Color(0xFFB84D));
        UIManager.put("nimbusDisabledText", new Color(0x777777));
        UIManager.put("nimbusFocus", new Color(0x3a3a3a));
        UIManager.put("nimbusGreen", new Color(0x5FBF5F));
        UIManager.put("nimbusInfoBlue", new Color(0x4DA3FF));
        UIManager.put("nimbusLightBackground", new Color(0x1e1e1e));
        UIManager.put("nimbusOrange", new Color(0xFF7F50));
        UIManager.put("nimbusRed", new Color(0xFF6B6B));
        UIManager.put("nimbusSelectedText", Color.WHITE);
        UIManager.put("nimbusSelectionBackground", new Color(0x2d2d2d));
        UIManager.put("text", Color.WHITE);
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}
    }

    private JTextField createTextField(int columns) {
        JTextField tf = new JTextField(columns);
        tf.setBackground(new Color(0x2a2a2a));
        tf.setForeground(Color.WHITE);
        tf.setCaretColor(Color.WHITE);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x3a3a3a)),
                new EmptyBorder(6, 8, 6, 8)
        ));
        return tf;
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
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { b.setBackground(hover); }
            @Override public void mouseExited(MouseEvent e) { b.setBackground(normal); }
        });
        return b;
    }

    private void styleCombo(JComboBox<?> combo) {
        combo.setBackground(new Color(0x2a2a2a));
        combo.setForeground(Color.WHITE);
        combo.setBorder(BorderFactory.createLineBorder(new Color(0x3a3a3a)));
        combo.setFocusable(false);
    }

    private void styleMenu(JMenu menu) {
        menu.setForeground(Color.WHITE);
        menu.getPopupMenu().setBorder(BorderFactory.createLineBorder(new Color(0x2a2a2a)));
        menu.getPopupMenu().setBackground(new Color(0x1e1e1e));
    }

    private void styleMenuItem(JMenuItem item) {
        item.setForeground(Color.WHITE);
        item.setBackground(new Color(0x1e1e1e));
    }

    @FunctionalInterface
    interface SimpleDocumentListener extends javax.swing.event.DocumentListener {
        void update(javax.swing.event.DocumentEvent e);
        @Override default void insertUpdate(javax.swing.event.DocumentEvent e) { update(e); }
        @Override default void removeUpdate(javax.swing.event.DocumentEvent e) { update(e); }
        @Override default void changedUpdate(javax.swing.event.DocumentEvent e) { update(e); }
    }
} 