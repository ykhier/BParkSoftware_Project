import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import data.MonthlyParkingEntry;

/**
 * Responsible for fetching monthly parking report entries from the database.
 */
public class ReportFetcher {

    /** Database connection */
    private Connection conn;

    /**
     * Constructs a new ReportFetcher and initializes the database connection.
     */
    public ReportFetcher() {
        try {
            this.conn = mysqlConnection.getInstance().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves all parking sessions from the summary table for the given month.
     *
     * @param monthYear The month in format "YYYY-MM" (e.g. "2025-06")
     * @return A list of MonthlyParkingEntry objects representing the report
     */
    public ArrayList<MonthlyParkingEntry> getMonthlyReport(String monthYear) {
        ArrayList<MonthlyParkingEntry> report = new ArrayList<>();

        // SQL query to fetch relevant report data
        String query = """
            SELECT subscriberCode, carNumber, parkingDate, startTime, endTime, 
                   durationMinutes, numberOfExtends, delayWarnings
            FROM monthly_parking_summary
            WHERE month_year = ?
            ORDER BY parkingDate ASC;
        """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, monthYear);  // Example: "2025-06"
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                MonthlyParkingEntry entry = new MonthlyParkingEntry(
                        rs.getInt("subscriberCode"),
                        rs.getString("carNumber"),
                        rs.getDate("parkingDate").toString(),
                        rs.getTime("startTime").toString(),
                        rs.getTime("endTime").toString(),
                        rs.getInt("durationMinutes"),
                        rs.getInt("numberOfExtends"),
                        rs.getInt("delayWarnings")
                );
                report.add(entry);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return report;
    }
}
