package com.example.taskmanager.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    public enum Priority { LOW, MEDIUM, HIGH }
    public enum Status { TO_DO, IN_PROGRESS, COMPLETED }

    private Integer id;
    private String title;
    private String description;
    private Priority priority;
    private Status status;
    private LocalDate dueDate; // nullable

    public Task(Integer id, String title, String description, Priority priority, Status status, LocalDate dueDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.dueDate = dueDate;
    }

    public Task(String title, String description, Priority priority, Status status, LocalDate dueDate) {
        this(null, title, description, priority, status, dueDate);
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public String getDueDateIso() {
        return dueDate == null ? null : dueDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
} 