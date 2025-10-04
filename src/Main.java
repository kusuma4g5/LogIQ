import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.out.println("===== Welcome to LogIQ — Automated Log Root Cause Classifier =====\n");

        // Ensure the logs table exists
        DatabaseManager.createLogsTable();

        try (Connection conn = DatabaseManager.connect();
             Scanner scanner = new Scanner(System.in)) {

            if (conn == null) {
                System.err.println("❌ Failed to connect to the database.");
                return;
            }

            LogAnalyzer analyzer = new LogAnalyzer();
            boolean exit = false;

            while (!exit) {
                // Display menu
                System.out.println("\nSelect an operation:");
                System.out.println("1. Parse log file and insert entries");
                System.out.println("2. Generate log summary");
                System.out.println("3. Fetch all logs");

                System.out.println("4. Search logs by level (INFO/WARN/ERROR/ALL)");
                System.out.println("5. Search logs by message content");
                System.out.println("6. Search logs by root cause");
                System.out.println("7. Search logs by date range");
                System.out.println("8. Exit");
                System.out.print("Enter your choice (1-8): ");

                String choice = scanner.nextLine();

                switch (choice) {
                    case "1":
                        System.out.print("Enter log file path: ");
                        String logFilePath = scanner.nextLine();
                        System.out.println("📂 Parsing and inserting logs...");
                        analyzer.parseAndInsertLogs(logFilePath, conn);
                        break;

                    case "2":
                        System.out.println("📊 Generating log summary:");
                        analyzer.generateSummary(conn);
                        break;

                    case "3":
                        System.out.print("Enter page size: ");
                        int pageSize3 = Integer.parseInt(scanner.nextLine());
                        int page3 = 0;
                        while (true) {
                            int count = DatabaseManager.fetchLogsPaginated(pageSize3, page3 * pageSize3);
                            if (count == 0 && page3 == 0) {
                                System.out.println("No logs found.");
                                break;
                            }
                            System.out.print("n=next, p=prev, q=quit: ");
                            String nav = scanner.nextLine();
                            if (nav.equalsIgnoreCase("n")) page3++;
                            else if (nav.equalsIgnoreCase("p") && page3 > 0) page3--;
                            else break;
                        }
                        break;

                    case "4":
                        System.out.print("Enter log level to search (INFO/WARN/ERROR/ALL): ");
                        String levelInput = scanner.nextLine().toUpperCase();
                        if (levelInput.equals("ALL")) {
                            System.out.print("Enter page size: ");
                            int pageSize4a = Integer.parseInt(scanner.nextLine());
                            int page4a = 0;
                            while (true) {
                                int count = DatabaseManager.fetchLogsPaginated(pageSize4a, page4a * pageSize4a);
                                if (count == 0 && page4a == 0) {
                                    System.out.println("No logs found.");
                                    break;
                                }
                                System.out.print("n=next, p=prev, q=quit: ");
                                String nav = scanner.nextLine();
                                if (nav.equalsIgnoreCase("n")) page4a++;
                                else if (nav.equalsIgnoreCase("p") && page4a > 0) page4a--;
                                else break;
                            }
                        } else if (levelInput.equals("INFO") || levelInput.equals("WARN") || levelInput.equals("ERROR")) {
                            System.out.print("Enter page size: ");
                            int pageSize4b = Integer.parseInt(scanner.nextLine());
                            int page4b = 0;
                            while (true) {
                                int count = DatabaseManager.searchLogsByLevelPaginated(levelInput, pageSize4b, page4b * pageSize4b);
                                if (count == 0 && page4b == 0) {
                                    System.out.println("No logs found.");
                                    break;
                                }
                                System.out.print("n=next, p=prev, q=quit: ");
                                String nav = scanner.nextLine();
                                if (nav.equalsIgnoreCase("n")) page4b++;
                                else if (nav.equalsIgnoreCase("p") && page4b > 0) page4b--;
                                else break;
                            }
                        } else {
                            System.out.println("⚠️ Invalid log level. Please enter INFO, WARN, ERROR, or ALL.");
                        }
                        break;

                    case "5":
                        System.out.print("Enter keyword to search in message: ");
                        String keyword = scanner.nextLine();
                        System.out.print("Enter page size: ");
                        int pageSize5 = Integer.parseInt(scanner.nextLine());
                        int page5 = 0;
                        while (true) {
                            int count = DatabaseManager.searchLogsByMessagePaginated(keyword, pageSize5, page5 * pageSize5);
                            if (count == 0 && page5 == 0) {
                                System.out.println("No logs found.");
                                break;
                            }
                            System.out.print("n=next, p=prev, q=quit: ");
                            String nav = scanner.nextLine();
                            if (nav.equalsIgnoreCase("n")) page5++;
                            else if (nav.equalsIgnoreCase("p") && page5 > 0) page5--;
                            else break;
                        }
                        break;

                    case "6":
                        System.out.print("Enter root cause keyword to search: ");
                        String rootCauseKeyword = scanner.nextLine();
                        System.out.print("Enter page size: ");
                        int pageSize6 = Integer.parseInt(scanner.nextLine());
                        int page6 = 0;
                        while (true) {
                            int count = DatabaseManager.searchLogsByRootCausePaginated(rootCauseKeyword, pageSize6, page6 * pageSize6);
                            if (count == 0 && page6 == 0) {
                                System.out.println("No logs found.");
                                break;
                            }
                            System.out.print("n=next, p=prev, q=quit: ");
                            String nav = scanner.nextLine();
                            if (nav.equalsIgnoreCase("n")) page6++;
                            else if (nav.equalsIgnoreCase("p") && page6 > 0) page6--;
                            else break;
                        }
                        break;

                    case "7":
                        System.out.print("Enter start timestamp (YYYY-MM-DD HH:MM:SS): ");
                        String start = scanner.nextLine();
                        System.out.print("Enter end timestamp (YYYY-MM-DD HH:MM:SS): ");
                        String end = scanner.nextLine();
                        System.out.print("Enter page size: ");
                        int pageSize7 = Integer.parseInt(scanner.nextLine());
                        int page7 = 0;
                        while (true) {
                            int count = DatabaseManager.searchLogsByDateRangePaginated(start, end, pageSize7, page7 * pageSize7);
                            if (count == 0 && page7 == 0) {
                                System.out.println("No logs found.");
                                break;
                            }
                            System.out.print("n=next, p=prev, q=quit: ");
                            String nav = scanner.nextLine();
                            if (nav.equalsIgnoreCase("n")) page7++;
                            else if (nav.equalsIgnoreCase("p") && page7 > 0) page7--;
                            else break;
                        }
                        break;

                    case "8":
                        exit = true;
                        System.out.println("👋 Exiting LogIQ. Goodbye!");
                        break;

                    default:
                        System.out.println("⚠️ Invalid choice. Please enter a number between 1-8.");
                }
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n===== End of LogIQ Session =====");
    }
}
