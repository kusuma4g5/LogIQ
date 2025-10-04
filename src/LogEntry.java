public class LogEntry {
    private String timestamp;
    private String level;
    private String message;
    private String rootCause;

    
    public LogEntry(String timestamp, String level, String message, String rootCause) {
        this.timestamp = timestamp;
        this.level = level;
        this.message = message;
        this.rootCause = rootCause;
    }

    
    public String getTimestamp() {
        return timestamp;
    }

    public String getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }

    public String getRootCause() {
        return rootCause;
    }

    
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setRootCause(String rootCause) {
        this.rootCause = rootCause;
    }

    
    @Override
    public String toString() {
        return String.format(
            "[%s] %-5s | %s | Root Cause: %s",
            timestamp, level, message, (rootCause != null ? rootCause : "N/A")
        );
    }
}
