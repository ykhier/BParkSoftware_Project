import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Controller responsible for generating the monthly parking report.
 * It collects and stores summary data about subscriber parking activity,
 * delays, and metadata in the database.
 */
public class MonthlyReportController {

    /** Database connection instance */
    private Connection conn;

    /**
     * Constructs a new MonthlyReportController and establishes a connection to the database.
     */
    public MonthlyReportController() {
        try {
            this.conn = mysqlConnection.getInstance().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates the monthly parking report for the current month.
     * <p>
     * This includes:
     * <ul>
     *   <li>Deleting existing data for the current month</li>
     *   <li>Inserting updated summary session data from subscriber parking</li>
     *   <li>Generating a JSON summary and storing it in the report table</li>
     * </ul>
     */
    public void generateMonthlyParkingReport() {
        try {
            LocalDate now = LocalDate.now();
            String monthYear = now.getYear() + "-" + String.format("%02d", now.getMonthValue());
            // Step 1: Delete old data
            String deleteSummary = "DELETE FROM monthly_parking_summary WHERE month_year = ?";
            String deleteReport = "DELETE FROM parking_report WHERE month_year = ?";
            try (PreparedStatement delStmt1 = conn.prepareStatement(deleteSummary);
                 PreparedStatement delStmt2 = conn.prepareStatement(deleteReport)) {
                delStmt1.setString(1, monthYear);
                delStmt1.executeUpdate();

                delStmt2.setString(1, monthYear);
                delStmt2.executeUpdate();
            }

            // Step 2: Insert detailed session data
            String insertSummary = """
                INSERT INTO monthly_parking_summary
                (subscriberCode, carNumber, parkingDate, startTime, endTime, durationMinutes, numberOfExtends, delayWarnings, month_year)
                SELECT
                    sp.subscriberCode,
                    sp.carNumber,
                    sp.date AS parkingDate,
                    sp.time AS startTime,
                    sp.receivingCarTime AS endTime,
                    TIMESTAMPDIFF(MINUTE, sp.time, sp.receivingCarTime) AS durationMinutes,
                    sp.numberOfExtends,
                    COALESCE(w.numberOfWarnings, 0) AS delayWarnings,
                    ?
                FROM subscriberparking sp
                LEFT JOIN (
                    SELECT subscriberCode, parkingCode, COUNT(*) AS numberOfWarnings
                    FROM latemessage
                    GROUP BY subscriberCode, parkingCode
                ) w ON sp.subscriberCode = w.subscriberCode AND sp.parkingCode = w.parkingCode
                WHERE sp.status IN ('ACTIVE', 'NOT ACTIVE')
                  AND sp.time IS NOT NULL
                  AND sp.receivingCarTime IS NOT NULL;
            """;

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSummary)) {
                insertStmt.setString(1, monthYear);
                insertStmt.executeUpdate();
            }

            // Step 3: Insert report metadata (JSON blob)
            String json = buildReportJson(monthYear);
            String insertReport = "INSERT INTO parking_report (month_year, generated_at, data) VALUES (?, NOW(), ?)";
            try (PreparedStatement reportStmt = conn.prepareStatement(insertReport)) {
                reportStmt.setString(1, monthYear);
                reportStmt.setString(2, json);
                reportStmt.executeUpdate();
            }

            System.out.println("✅ Report for " + monthYear + " generated.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Builds a JSON summary of the monthly report containing total entries and delay warnings.
     *
     * @param monthYear The year and month string (like "2025-07") to summarize.
     * @return A JSON string representing the report summary.
     * @throws SQLException If a database error occurs during summary calculation.
     */
    private String buildReportJson(String monthYear) throws SQLException {
        String query = "SELECT COUNT(*) AS totalEntries, SUM(delayWarnings) AS totalWarnings FROM monthly_parking_summary WHERE month_year = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, monthYear);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int total = rs.getInt("totalEntries");
                int warnings = rs.getInt("totalWarnings");
                return String.format("{\"month\":\"%s\",\"totalEntries\":%d,\"totalWarnings\":%d}", monthYear, total, warnings);
            }
        }
        return "{}";
    }
}
