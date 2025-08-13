package com.example.taskmanager.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.function.Consumer;

public class DatePickerPopup extends JPopupMenu {
    private final Consumer<LocalDate> onDateSelected;
    private YearMonth visibleMonth;
    private LocalDate selectedDate;
    private final LocalDate today = LocalDate.now();

    private final JPanel daysPanel;
    private final JLabel monthLabel;

    private static final Color BG_DARK = new Color(0x1e1e1e);
    private static final Color BG_DARKER = new Color(0x161616);
    private static final Color FG_TEXT = Color.WHITE;
    private static final Color FG_MUTED = new Color(0xBBBBBB);
    private static final Color BTN_NORMAL = new Color(0x2f2f2f);
    private static final Color BTN_HOVER = new Color(0x3a3a3a);
    private static final Color BTN_SELECTED = new Color(0x4a4a4a);
    private static final Color TODAY_BORDER = new Color(0x4DA3FF);

    public DatePickerPopup(LocalDate initialDate, Consumer<LocalDate> onDateSelected) {
        this.onDateSelected = onDateSelected;
        this.selectedDate = initialDate != null ? initialDate : LocalDate.now();
        this.visibleMonth = YearMonth.from(this.selectedDate);

        setBorder(BorderFactory.createLineBorder(new Color(0x3a3a3a)));
        setBackground(BG_DARK);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_DARK);
        root.setBorder(new EmptyBorder(8, 8, 8, 8));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_DARK);
        JButton prevBtn = createNavButton("◀");
        JButton nextBtn = createNavButton("▶");
        monthLabel = new JLabel();
        monthLabel.setForeground(FG_TEXT);
        monthLabel.setHorizontalAlignment(SwingConstants.CENTER);
        monthLabel.setBorder(new EmptyBorder(0, 8, 0, 8));
        header.add(prevBtn, BorderLayout.WEST);
        header.add(monthLabel, BorderLayout.CENTER);
        header.add(nextBtn, BorderLayout.EAST);

        prevBtn.addActionListener(e -> {
            visibleMonth = visibleMonth.minusMonths(1);
            rebuildDays();
        });
        nextBtn.addActionListener(e -> {
            visibleMonth = visibleMonth.plusMonths(1);
            rebuildDays();
        });

        JPanel weekHeader = new JPanel(new GridLayout(1, 7, 4, 4));
        weekHeader.setBackground(BG_DARK);
        for (DayOfWeek dow : DayOfWeek.values()) {
            JLabel l = new JLabel(dow.toString().substring(0, 3), SwingConstants.CENTER);
            l.setForeground(FG_MUTED);
            l.setBorder(new EmptyBorder(2, 2, 2, 2));
            weekHeader.add(l);
        }

        daysPanel = new JPanel(new GridLayout(6, 7, 4, 4));
        daysPanel.setBackground(BG_DARK);

        root.add(header, BorderLayout.NORTH);
        root.add(weekHeader, BorderLayout.CENTER);
        root.add(daysPanel, BorderLayout.SOUTH);

        add(root);
        rebuildDays();
    }

    private JButton createNavButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setBackground(BTN_NORMAL);
        b.setForeground(FG_TEXT);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x3a3a3a)),
                new EmptyBorder(4, 8, 4, 8)
        ));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(BTN_HOVER); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { b.setBackground(BTN_NORMAL); }
        });
        return b;
    }

    private JButton createDayButton(LocalDate date, boolean inMonth) {
        String label = Integer.toString(date.getDayOfMonth());
        JButton b = new JButton(label);
        b.setFocusPainted(false);
        b.setBackground(BTN_NORMAL);
        b.setForeground(inMonth ? FG_TEXT : FG_MUTED);
        b.setBorder(BorderFactory.createLineBorder(new Color(0x3a3a3a)));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (date.equals(selectedDate)) {
            b.setBackground(BTN_SELECTED);
        } else {
            b.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(BTN_HOVER); }
                @Override public void mouseExited(java.awt.event.MouseEvent e) { b.setBackground(BTN_NORMAL); }
            });
        }
        if (date.equals(today)) {
            b.setBorder(new MatteBorder(2, 2, 2, 2, TODAY_BORDER));
            b.setToolTipText("Today");
        }
        b.setEnabled(inMonth);
        if (inMonth) {
            b.addActionListener(e -> {
                selectedDate = date;
                if (onDateSelected != null) {
                    onDateSelected.accept(selectedDate);
                }
                setVisible(false);
            });
        }
        return b;
    }

    private void rebuildDays() {
        monthLabel.setText(visibleMonth.getMonth().toString() + " " + visibleMonth.getYear());
        daysPanel.removeAll();

        LocalDate firstOfMonth = visibleMonth.atDay(1);
        DayOfWeek firstDow = firstOfMonth.getDayOfWeek();
        int shift = firstDow.getValue() % 7;
        LocalDate gridStart = firstOfMonth.minusDays(shift);

        for (int i = 0; i < 42; i++) {
            LocalDate day = gridStart.plusDays(i);
            boolean inMonth = YearMonth.from(day).equals(visibleMonth);
            daysPanel.add(createDayButton(day, inMonth));
        }
        daysPanel.revalidate();
        daysPanel.repaint();
        packPopup();
    }

    private void packPopup() {
        this.invalidate();
        this.validate();
    }

    public void showRelativeTo(Component invoker) {
        this.show(invoker, 0, invoker.getHeight());
    }
}
