package com.example.taskmanager;

import com.example.taskmanager.ui.MainUI;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainUI mainUI = new MainUI();
            mainUI.setVisible(true);
        });
    }
} 