package com.example.taskmanager.ui;

import com.example.taskmanager.model.Task;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TaskTableModel extends AbstractTableModel {
    private static final String[] COLUMN_NAMES = {
            "ID", "Title", "Description", "Priority", "Status", "Due Date"
    };

    private final List<Task> tasks = new ArrayList<>();

    public void setTasks(List<Task> newTasks) {
        tasks.clear();
        if (newTasks != null) tasks.addAll(newTasks);
        fireTableDataChanged();
    }

    public Task getTaskAt(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= tasks.size()) return null;
        return tasks.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return tasks.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Task task = tasks.get(rowIndex);
        switch (columnIndex) {
            case 0: return task.getId();
            case 1: return task.getTitle();
            case 2: return task.getDescription();
            case 3: return task.getPriority().name();
            case 4: return task.getStatus().name();
            case 5: return task.getDueDate() == null ? "" : task.getDueDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
            default: return "";
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0: return Integer.class;
            default: return String.class;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
} 