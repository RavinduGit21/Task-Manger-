package com.example.taskmanager.db;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.Task.Priority;
import com.example.taskmanager.model.Task.Status;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {
    private static final String DB_FILE = "tasks.db";
    private static final String JDBC_URL = "jdbc:sqlite:" + DB_FILE;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite JDBC driver not found. Ensure sqlite-jdbc jar is on the classpath.", e);
        }
    }

    private static DatabaseHandler instance;

    private DatabaseHandler() {
        initializeDatabase();
    }

    public static synchronized DatabaseHandler getInstance() {
        if (instance == null) {
            instance = new DatabaseHandler();
        }
        return instance;
    }

    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             Statement stmt = conn.createStatement()) {
            String createTableSql = "CREATE TABLE IF NOT EXISTS tasks (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "title TEXT NOT NULL, " +
                    "description TEXT, " +
                    "priority TEXT NOT NULL, " +
                    "status TEXT NOT NULL, " +
                    "due_date TEXT" +
                    ")";
            stmt.execute(createTableSql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    public List<Task> getTasks(String searchTitle, String statusFilter) {
        List<Task> tasks = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT id, title, description, priority, status, due_date FROM tasks");
        List<Object> params = new ArrayList<>();

        boolean hasWhere = false;
        if (searchTitle != null && !searchTitle.trim().isEmpty()) {
            sql.append(" WHERE title LIKE ?");
            params.add("%" + searchTitle.trim() + "%");
            hasWhere = true;
        }
        if (statusFilter != null && !statusFilter.equalsIgnoreCase("All")) {
            sql.append(hasWhere ? " AND" : " WHERE");
            sql.append(" status = ?");
            params.add(statusFilter);
        }
        sql.append(" ORDER BY due_date IS NULL, due_date ASC, id DESC");

        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Integer id = rs.getInt("id");
                    String title = rs.getString("title");
                    String description = rs.getString("description");
                    Priority priority = Priority.valueOf(rs.getString("priority"));
                    Status status = Status.valueOf(rs.getString("status"));
                    String dueIso = rs.getString("due_date");
                    LocalDate dueDate = dueIso == null ? null : LocalDate.parse(dueIso);
                    tasks.add(new Task(id, title, description, priority, status, dueDate));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load tasks", e);
        }
        return tasks;
    }

    public int insertTask(Task task) {
        String sql = "INSERT INTO tasks(title, description, priority, status, due_date) VALUES(?,?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription());
            ps.setString(3, task.getPriority().name());
            ps.setString(4, task.getStatus().name());
            if (task.getDueDate() != null) {
                ps.setString(5, task.getDueDateIso());
            } else {
                ps.setNull(5, Types.VARCHAR);
            }
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            return -1;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert task", e);
        }
    }

    public void updateTask(Task task) {
        if (task.getId() == null) throw new IllegalArgumentException("Task ID is null");
        String sql = "UPDATE tasks SET title=?, description=?, priority=?, status=?, due_date=? WHERE id=?";
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription());
            ps.setString(3, task.getPriority().name());
            ps.setString(4, task.getStatus().name());
            if (task.getDueDate() != null) {
                ps.setString(5, task.getDueDateIso());
            } else {
                ps.setNull(5, Types.VARCHAR);
            }
            ps.setInt(6, task.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update task", e);
        }
    }

    public void deleteTask(int id) {
        String sql = "DELETE FROM tasks WHERE id=?";
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete task", e);
        }
    }

    public int countTotal() {
        return countBySql("SELECT COUNT(*) FROM tasks");
    }

    public int countCompleted() {
        return countBySql("SELECT COUNT(*) FROM tasks WHERE status='COMPLETED'");
    }

    private int countBySql(String sql) {
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to count tasks", e);
        }
        return 0;
    }
} 