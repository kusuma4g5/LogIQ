import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;




import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogAnalyzer {

    // Regex pattern to match log format: [timestamp] LEVEL message
    // Example: 2025-10-04 10:15:30 ERROR NullPointer occurred in process
    private static final String LOG_REGEX =
            "(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2})\\s+(INFO|WARN|ERROR)\\s+(.*)";

    private static final Pattern logPattern = Pattern.compile(LOG_REGEX);

    // Reads a log file and inserts parsed entries into DB using batch insert
    public void parseAndInsertLogs(String filePath, Connection conn) {
        java.util.List<LogEntry> batch = new java.util.ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                Matcher matcher = logPattern.matcher(line);
                if (matcher.matches()) {
                    String timestamp = matcher.group(1);
                    String level = matcher.group(2);
                    String message = matcher.group(3);
                    String rootCause = extractRootCause(message);
                    LogEntry entry = new LogEntry(timestamp, level, message, rootCause);
                    batch.add(entry);
                }
            }
            if (!batch.isEmpty()) {
                DatabaseManager.insertLogsBatch(batch);
            }
            System.out.println("Logs inserted successfully from file: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Extracts root cause keyword from a log message
    private String extractRootCause(String message) {
        if (message.contains("NullPointer")) {
            return "NullPointer";
        } else if (message.contains("Timeout")) {
            return "Timeout";
        } else if (message.contains("IndexOutOfBounds")) {
            return "IndexOutOfBounds";
        }
        return null; // No known root cause
    }

    // Generate log summary: total count, count by level, top 3 root causes
    public void generateSummary(Connection conn) {
        try (Statement stmt = conn.createStatement()) {

            // Total logs
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM logs");
            if (rs.next()) {
                System.out.println("Total log entries: " + rs.getInt(1));
            }

            // Count by level
            rs = stmt.executeQuery("SELECT level, COUNT(*) FROM logs GROUP BY level");
            System.out.println("\nLog count by level:");
            while (rs.next()) {
                System.out.println(rs.getString(1) + ": " + rs.getInt(2));
            }

            // Top 3 root causes
            rs = stmt.executeQuery(
                    "SELECT root_cause, COUNT(*) as cnt FROM logs " +
                    "WHERE root_cause IS NOT NULL " +
                    "GROUP BY root_cause ORDER BY cnt DESC LIMIT 3"
            );

            System.out.println("\nTop 3 root causes:");
            while (rs.next()) {
                System.out.println(rs.getString("root_cause") + " → " + rs.getInt("cnt"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Main method for testing
    public static void main(String[] args) {
        String logFilePath = "application.log"; // replace with your log file path

        // Ensure logs table exists
        DatabaseManager.createLogsTable();

        try (Connection conn = DatabaseManager.connect()) {
            LogAnalyzer analyzer = new LogAnalyzer();

            // Parse logs and insert into DB
            analyzer.parseAndInsertLogs(logFilePath, conn);

            // Generate summary
            analyzer.generateSummary(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
