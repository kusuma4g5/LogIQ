# LogIQ

LogIQ is an automated log root cause classifier and analysis tool built in Java with SQLite. It provides advanced log search, summary, pagination, colored CLI output, error handling, and robust data integrity features.

## Features
- **Log Parsing & Insertion:** Parse log files and insert entries into a SQLite database.
- **Duplicate Prevention:** Prevents duplicate log entries using a unique constraint.
- **Batch Insert:** Optimized batch insert for large log files.
- **Pagination:** View logs page by page for large result sets.
- **Colored CLI Output:** Log levels are colorized (INFO=green, WARN=yellow, ERROR=red).
- **Advanced Search:** Search logs by level, message, root cause, or date range.
- **Summary:** Generate summaries (total logs, count by level, top root causes).
- **Error Handling:** User-friendly error messages and internal logging to `logiq_app.log`.
- **Configurable DB Location:** Database file is stored in the `db/` directory.

## Getting Started

### Prerequisites
- Java 17 or later
- [sqlite-jdbc](https://github.com/xerial/sqlite-jdbc) driver (already included in this repo)

### Build & Run
```bash
# Compile
javac -cp "src:sqlite-jdbc-3.50.3.0 (1).jar" src/Main.java src/DatabaseManager.java src/LogAnalyzer.java src/LogEntry.java

# Run
java -cp "src:sqlite-jdbc-3.50.3.0 (1).jar:src" Main
```

### Usage
- Follow the CLI menu to parse logs, search, view summaries, and more.
- Place your log files in the workspace and provide the path when prompted.
- The SQLite database will be created at `db/logiq.db`.

## Directory Structure
```
LogIQ/
├── db/                # SQLite database location
│   └── logiq.db
├── src/               # Java source files
│   ├── Main.java
│   ├── DatabaseManager.java
│   ├── LogAnalyzer.java
│   └── LogEntry.java
├── logiq_app.log      # Internal application log
├── README.md
└── ...
```

## Notes
- Duplicate logs are automatically skipped.
- All errors and key actions are logged to `logiq_app.log`.
- For best performance, use batch insert for large log files.

## License
MIT License