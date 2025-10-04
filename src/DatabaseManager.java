import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class DatabaseManager {
    
    private static final Logger LOGGER = Logger.getLogger("LogIQAppLogger");
    static {
        try {
            FileHandler fh = new FileHandler("logiq_app.log", true);
            fh.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fh);
            LOGGER.setUseParentHandlers(false);
        } catch (Exception e) {
            System.err.println("[LogIQ] Failed to initialize internal logger: " + e.getMessage());
        }
    }

    
    private static void handleError(String userMsg, Exception e) {
        System.out.println("\u26A0\uFE0F " + userMsg);
        if (e != null) LOGGER.severe(e.toString());
    }
    
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_RED = "\u001B[31m";

    
    private static String colorLevel(String level) {
        switch (level) {
            case "INFO": return ANSI_GREEN + level + ANSI_RESET;
            case "WARN": return ANSI_YELLOW + level + ANSI_RESET;
            case "ERROR": return ANSI_RED + level + ANSI_RESET;
            default: return level;
        }
    }

    
    private static void printLogEntry(ResultSet rs) throws SQLException {
    int id = rs.getInt("id");
    String timestamp = rs.getString("timestamp");
    String level = rs.getString("level");
    String message = rs.getString("message");
    String rootCause = rs.getString("root_cause");
    System.out.printf("ID: %d | Time: %s | Level: %s | Msg: %s | RootCause: %s%n",
        id, timestamp, colorLevel(level), message, rootCause);
    }

    
    public static int fetchLogsPaginated(int limit, int offset) {
        String selectSQL = "SELECT * FROM logs LIMIT ? OFFSET ?";
        int count = 0;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            pstmt.setInt(1, limit);
            pstmt.setInt(2, offset);
            ResultSet rs = pstmt.executeQuery();
            System.out.println("📜 Logs (page):");
            while (rs.next()) {
                printLogEntry(rs);
                count++;
            }
        } catch (SQLException e) {
            handleError("Could not fetch logs. Please try again or check your database connection.", e);
        }
        return count;
    }

    
    public static int searchLogsByLevelPaginated(String level, int limit, int offset) {
        String searchSQL = "SELECT * FROM logs WHERE level = ? LIMIT ? OFFSET ?";
        int count = 0;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(searchSQL)) {
            pstmt.setString(1, level);
            pstmt.setInt(2, limit);
            pstmt.setInt(3, offset);
            ResultSet rs = pstmt.executeQuery();
            System.out.println("📌 Logs with level = " + level + " (page):");
            while (rs.next()) {
                printLogEntry(rs);
                count++;
            }
        } catch (SQLException e) {
            handleError("Could not search logs by level. Please try again or check your database connection.", e);
        }
        return count;
    }

    
    public static int searchLogsByMessagePaginated(String keyword, int limit, int offset) {
        String searchSQL = "SELECT * FROM logs WHERE message LIKE ? LIMIT ? OFFSET ?";
        int count = 0;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(searchSQL)) {
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setInt(2, limit);
            pstmt.setInt(3, offset);
            ResultSet rs = pstmt.executeQuery();
            System.out.println("🔍 Logs with message containing '" + keyword + "' (page):");
            while (rs.next()) {
                printLogEntry(rs);
                count++;
            }
        } catch (SQLException e) {
            handleError("Could not search logs by message. Please try again or check your database connection.", e);
        }
        return count;
    }

    
    public static int searchLogsByRootCausePaginated(String rootCauseKeyword, int limit, int offset) {
        String searchSQL = "SELECT * FROM logs WHERE root_cause LIKE ? LIMIT ? OFFSET ?";
        int count = 0;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(searchSQL)) {
            pstmt.setString(1, "%" + rootCauseKeyword + "%");
            pstmt.setInt(2, limit);
            pstmt.setInt(3, offset);
            ResultSet rs = pstmt.executeQuery();
            System.out.println("🔍 Logs with root cause containing '" + rootCauseKeyword + "' (page):");
            while (rs.next()) {
                printLogEntry(rs);
                count++;
            }
        } catch (SQLException e) {
            handleError("Could not search logs by root cause. Please try again or check your database connection.", e);
        }
        return count;
    }

    
    public static int searchLogsByDateRangePaginated(String start, String end, int limit, int offset) {
        String searchSQL = "SELECT * FROM logs WHERE timestamp >= ? AND timestamp <= ? ORDER BY timestamp LIMIT ? OFFSET ?";
        int count = 0;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(searchSQL)) {
            pstmt.setString(1, start);
            pstmt.setString(2, end);
            pstmt.setInt(3, limit);
            pstmt.setInt(4, offset);
            ResultSet rs = pstmt.executeQuery();
            System.out.println("🔍 Logs from " + start + " to " + end + " (page):");
            while (rs.next()) {
                printLogEntry(rs);
                count++;
            }
        } catch (SQLException e) {
            handleError("Could not search logs by date range. Please try again or check your database connection.", e);
        }
        return count;
    }



    
    public static void searchLogsByMessage(String keyword) {
        String searchSQL = "SELECT * FROM logs WHERE message LIKE ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(searchSQL)) {
            pstmt.setString(1, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();
            System.out.println("🔍 Logs with message containing '" + keyword + "':");
            boolean found = false;
            while (rs.next()) {
                found = true;
                int id = rs.getInt("id");
                String timestamp = rs.getString("timestamp");
                String level = rs.getString("level");
                String message = rs.getString("message");
                String rootCause = rs.getString("root_cause");
        System.out.printf("ID: %d | Time: %s | Level: %s | Msg: %s | RootCause: %s%n",
            id, timestamp, colorLevel(level), message, rootCause);
            }
            if (!found) {
                System.out.println("⚠️ No logs found containing: " + keyword);
            }
        } catch (SQLException e) {
            System.err.println("Error searching logs by message: " + e.getMessage());
        }
    }

    
    public static void searchLogsByRootCause(String rootCauseKeyword) {
        String searchSQL = "SELECT * FROM logs WHERE root_cause LIKE ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(searchSQL)) {
            pstmt.setString(1, "%" + rootCauseKeyword + "%");
            ResultSet rs = pstmt.executeQuery();
            System.out.println("🔍 Logs with root cause containing '" + rootCauseKeyword + "':");
            boolean found = false;
            while (rs.next()) {
                found = true;
                int id = rs.getInt("id");
                String timestamp = rs.getString("timestamp");
                String level = rs.getString("level");
                String message = rs.getString("message");
                String rootCause = rs.getString("root_cause");
        System.out.printf("ID: %d | Time: %s | Level: %s | Msg: %s | RootCause: %s%n",
            id, timestamp, colorLevel(level), message, rootCause);
            }
            if (!found) {
                System.out.println("⚠️ No logs found for root cause: " + rootCauseKeyword);
            }
        } catch (SQLException e) {
            System.err.println("Error searching logs by root cause: " + e.getMessage());
        }
    }

    
    public static void searchLogsByDateRange(String start, String end) {
        String searchSQL = "SELECT * FROM logs WHERE timestamp >= ? AND timestamp <= ? ORDER BY timestamp";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(searchSQL)) {
            pstmt.setString(1, start);
            pstmt.setString(2, end);
            ResultSet rs = pstmt.executeQuery();
            System.out.println("🔍 Logs from " + start + " to " + end + ":");
            boolean found = false;
            while (rs.next()) {
                found = true;
                int id = rs.getInt("id");
                String timestamp = rs.getString("timestamp");
                String level = rs.getString("level");
                String message = rs.getString("message");
                String rootCause = rs.getString("root_cause");
        System.out.printf("ID: %d | Time: %s | Level: %s | Msg: %s | RootCause: %s%n",
            id, timestamp, colorLevel(level), message, rootCause);
            }
            if (!found) {
                System.out.println("⚠️ No logs found in the specified date range.");
            }
        } catch (SQLException e) {
            System.err.println("Error searching logs by date range: " + e.getMessage());
        }
    }

    private static final String DB_URL = "jdbc:sqlite:db/logiq.db";

    
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    
    public static void createLogsTable() {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS logs (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                timestamp TEXT NOT NULL,
                level TEXT NOT NULL,
                message TEXT NOT NULL,
                root_cause TEXT,
                UNIQUE(timestamp, level, message, root_cause)
            );
        """;

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("✅ logs table is ready.");
        } catch (SQLException e) {
            handleError("Could not create logs table. Please check your database setup and permissions.", e);
        }
    }

    
    public static void insertLog(LogEntry entry) {
        String insertSQL = "INSERT OR IGNORE INTO logs (timestamp, level, message, root_cause) VALUES (?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setString(1, entry.getTimestamp());
            pstmt.setString(2, entry.getLevel());
            pstmt.setString(3, entry.getMessage());
            pstmt.setString(4, entry.getRootCause());
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                System.out.println("✅ Log inserted successfully.");
            } else {
                System.out.println("⚠️ Duplicate log entry skipped.");
            }
        } catch (SQLException e) {
            handleError("Could not insert log entry. Please check your input and database connection.", e);
        }
    }

    
    public static void insertLogsBatch(java.util.List<LogEntry> entries) {
        String insertSQL = "INSERT OR IGNORE INTO logs (timestamp, level, message, root_cause) VALUES (?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            int batchSize = 0;
            for (LogEntry entry : entries) {
                pstmt.setString(1, entry.getTimestamp());
                pstmt.setString(2, entry.getLevel());
                pstmt.setString(3, entry.getMessage());
                pstmt.setString(4, entry.getRootCause());
                pstmt.addBatch();
                batchSize++;
                if (batchSize % 100 == 0) {
                    pstmt.executeBatch();
                }
            }
            pstmt.executeBatch();
            System.out.println("✅ Batch log insert completed (duplicates skipped)." );
        } catch (SQLException e) {
            handleError("Could not perform batch log insert. Please check your input and database connection.", e);
        }
    }

    
    public static void fetchLogs() {
        String selectSQL = "SELECT * FROM logs";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {

            System.out.println("📜 All Logs:");
            while (rs.next()) {
                int id = rs.getInt("id");
                String timestamp = rs.getString("timestamp");
                String level = rs.getString("level");
                String message = rs.getString("message");
                String rootCause = rs.getString("root_cause");

        System.out.printf("ID: %d | Time: %s | Level: %s | Msg: %s | RootCause: %s%n",
            id, timestamp, colorLevel(level), message, rootCause);
            }

        } catch (SQLException e) {
            handleError("Could not fetch logs. Please try again or check your database connection.", e);
        }
    }

    
    public static void searchLogsByLevel(String level) {
        String searchSQL = "SELECT * FROM logs WHERE level = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(searchSQL)) {

            pstmt.setString(1, level);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("📌 Logs with level = " + level + ":");
            boolean found = false;

            while (rs.next()) {
                found = true;
                int id = rs.getInt("id");
                String timestamp = rs.getString("timestamp");
                String message = rs.getString("message");
                String rootCause = rs.getString("root_cause");

        System.out.printf("ID: %d | Time: %s | Level: %s | Msg: %s | RootCause: %s%n",
            id, timestamp, colorLevel(level), message, rootCause);
            }

            if (!found) {
                System.out.println("⚠️ No logs found for level: " + level);
            }

        } catch (SQLException e) {
            handleError("Could not search logs by level. Please try again or check your database connection.", e);
        }
    }

    
    public static void main(String[] args) {
        createLogsTable();

        // Insert sample logs
    insertLog(new LogEntry("2025-10-04 15:45:00", "INFO", "System started", "N/A"));
    insertLog(new LogEntry("2025-10-04 15:46:10", "ERROR", "Null pointer exception", "Invalid object reference"));
    insertLog(new LogEntry("2025-10-04 15:47:30", "WARN", "Disk usage high", "Disk almost full"));

        // Fetch all logs
        fetchLogs();

        // Search only ERROR logs
        searchLogsByLevel("ERROR");

        // Search only INFO logs
        searchLogsByLevel("INFO");

        // Search for something that doesn't exist
        searchLogsByLevel("DEBUG");
    }
}
