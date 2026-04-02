import java.sql.*;
import java.time.*;
import data.ResponseWrapper;

/**
 * Controller that handles the logic for extending parking time and retrieving
 * parking info for subscribers who want to receive (pick up) their car.
 */
public class ReceivingCarController {
	/** Singleton instance of the MySQL connection manager */
	private mysqlConnection instance = null;
	/** JDBC connection object */
	Connection conn = null;

	/**
	 * Initializes the connection to the MySQL database.
	 */
	public void getDBConnection() {
		try {
			instance = mysqlConnection.getInstance();
			conn = instance.getConnection();
		} catch (Exception e) {
			System.out.println("Error connecting to DB");
		}
	}

	/**
	 * Constructor – connects to the DB immediately.
	 */
	public ReceivingCarController() {
		this.getDBConnection();
	}

	/**
	 * Retrieves the active confirmation code (parking session) for a given
	 * subscriber. Only returns sessions that are still within allowed time
	 * (+extensions).
	 *
	 * @param response A wrapper containing the subscriber code
	 * @return The confirmation code of the current parking session, or -1 if not
	 *         found
	 */
	public int getParkingCodeForSubscriber(ResponseWrapper response) {
		int subscriberCode = Integer.parseInt(response.getData().toString());
		int parkingCode = -1;

		String query = """
					SELECT confirmation_code
					FROM subscriberparking
					WHERE subscriberCode = ?
					  AND status = 'ACTIVE'
					  AND DATE(time) = CURRENT_DATE
					  AND (CURRENT_TIME) <= TIME(DATE_ADD(time, INTERVAL numberOfExtends HOUR))
				""";

		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setInt(1, subscriberCode);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				parkingCode = rs.getInt("confirmation_code");
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return parkingCode;
	}

	/**
	 * Attempts to extend the parking time for a subscriber based on their request.
	 *
	 * @param response A wrapper with subscriberCode as data, and requested extra
	 *                 hours as extra
	 * @return Message indicating success or failure of the extension request
	 */
	public String extendParkingTime(ResponseWrapper response) {
		int subscriberCode = Integer.parseInt(response.getData().toString()); // subscriberCode - 123
		int selectedTime = Integer.parseInt(response.getExtra().toString()); // 2 hours
		int confirmationCode = getParkingCodeForSubscriber(response); // code - 7557

		return handleExtensionRequest(subscriberCode, confirmationCode, selectedTime);
	}

	/**
	 * Checks if an extension can be granted and updates the database accordingly.
	 *
	 * @param subscriberCode          ID of the subscriber requesting the extension
	 * @param confirmationCode        Active parking confirmation code
	 * @param requestedExtensionHours Requested number of extension hours
	 * @return Status message indicating the outcome of the extension
	 */
	public String handleExtensionRequest(int subscriberCode, int confirmationCode, int requestedExtensionHours) {
		String selectQuery = """
					SELECT time, date
					FROM subscriberparking
					WHERE subscriberCode = ? AND confirmation_code = ? AND status = 'ACTIVE'
				""";

		String conflictQuery = """
					SELECT COUNT(*)
					FROM subscriberparking
					WHERE subscriberCode != ? AND confirmation_code = ? AND status = 'ACTIVE'
					  AND (time < ? AND ADDTIME(time, SEC_TO_TIME(4 * 3600)) > ?)
				""";

		String updateQuery = """
					UPDATE subscriberparking
					SET numberOfExtends = numberOfExtends + ?
					WHERE subscriberCode = ? AND confirmation_code = ?
				""";

		try (Connection conn = mysqlConnection.getInstance().getConnection();
				PreparedStatement ps1 = conn.prepareStatement(selectQuery)) {

			ps1.setInt(1, subscriberCode);
			ps1.setInt(2, confirmationCode);
			ResultSet rs = ps1.executeQuery();

			if (!rs.next())
				return "No active parking found.";

			// Parse time values from DB
			LocalDate date = rs.getDate("date").toLocalDate();
			LocalTime entryTime = rs.getTime("time").toLocalTime();
			LocalDateTime entry = LocalDateTime.of(date, entryTime);
			LocalDateTime defaultEnd = entry.plusHours(4); // default duration

			// Try granting the requested extension or the next smaller valid duration
			for (int tryHours = requestedExtensionHours; tryHours >= 1; tryHours--) {
				LocalDateTime proposedEnd = defaultEnd.plusHours(tryHours);

				// Check for time conflicts with others
				try (PreparedStatement ps2 = conn.prepareStatement(conflictQuery)) {
					ps2.setInt(1, subscriberCode);
					ps2.setInt(2, confirmationCode);
					ps2.setTime(3, Time.valueOf(proposedEnd.toLocalTime()));
					ps2.setTime(4, Time.valueOf(defaultEnd.toLocalTime()));

					ResultSet check = ps2.executeQuery();
					if (check.next() && check.getInt(1) == 0) {
						// If no conflict → approve this extension
						try (PreparedStatement ps3 = conn.prepareStatement(updateQuery)) {
							ps3.setInt(1, tryHours);
							ps3.setInt(2, subscriberCode);
							ps3.setInt(3, confirmationCode);
							ps3.executeUpdate();
						}
						return "Extension approved for " + tryHours + " hour(s).";
					}
				}
			}

			// No valid time slot found
			return "Extension denied: no available time slot.";

		} catch (SQLException e) {
			e.printStackTrace();
			return "Error processing extension request.";
		}
	}
}
