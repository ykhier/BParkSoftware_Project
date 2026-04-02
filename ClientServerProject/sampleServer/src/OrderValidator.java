import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import data.ResponseWrapper;
import data.SubscriberSession;

/**
 * Utility class responsible for validating parking orders, checking
 * availability, and inserting new orders into the database.
 */
public class OrderValidator {

	/**
	 * Checks whether a specific parking spot is available on a given date and time.
	 *
	 * @param parkingCode    the parking spot code
	 * @param date           the date of interest
	 * @param requestedStart the requested start time
	 * @param requestedEnd   the requested end time
	 * @return true if the spot is free during that time range, false otherwise
	 */
	public static boolean isParkingSpotAvailable(int parkingCode, LocalDate date, LocalTime requestedStart,
			LocalTime requestedEnd) {
		String subscriberParkingSql = """
				    SELECT * FROM subscriberparking
				    WHERE parkingCode = ?
				      AND date = ?
				      AND status = 'ACTIVE'
				      AND (
				            time < ? AND
				            ADDTIME(time, SEC_TO_TIME((4 + numberOfExtends) * 3600)) > ?
				          )
				""";

		String orderSql = """
				    SELECT * FROM `order`
				    WHERE parking_space = ?
				      AND order_date = ?
				      AND (
				            startTime < ? AND
				            endTime > ?
				          )
				""";

		try (Connection conn = mysqlConnection.getInstance().getConnection()) {
			// Check subscriberparking conflicts
			try (PreparedStatement stmt1 = conn.prepareStatement(subscriberParkingSql)) {
				stmt1.setInt(1, parkingCode);
				stmt1.setDate(2, Date.valueOf(date));
				stmt1.setTime(3, Time.valueOf(requestedEnd));
				stmt1.setTime(4, Time.valueOf(requestedStart));

				if (stmt1.executeQuery().next()) {
					return false;
				}
			}

			// Check order conflicts
			try (PreparedStatement stmt2 = conn.prepareStatement(orderSql)) {
				stmt2.setInt(1, parkingCode);
				stmt2.setDate(2, Date.valueOf(date));
				stmt2.setTime(3, Time.valueOf(requestedEnd));
				stmt2.setTime(4, Time.valueOf(requestedStart));

				if (stmt2.executeQuery().next()) {
					return false;
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return true; // No conflicts
	}

	/**
	 * Checks whether at least 40% of the parking spots are available for a given
	 * time window.
	 *
	 * @param date  the requested date
	 * @param start the requested start time
	 * @param end   the requested end time
	 * @return true if the ratio of free spots is >= 40%, false otherwise
	 */
	public static boolean isOverallAvailabilitySufficient(LocalDate date, LocalTime start, LocalTime end) {
		String totalSpotsQuery = "SELECT COUNT(*) FROM parkingspot";
		String overlappingOrdersQuery = """
				    SELECT COUNT(*) FROM `order`
				    WHERE order_date = ?
				      AND (startTime < ? AND endTime > ?)
				""";
		String overlappingSubscribersSpotsQuery = """
				    SELECT COUNT(*) FROM subscriberparking
				    WHERE date = ?
				      AND status = 'ACTIVE'
				      AND (
				            time < ? AND
				            ADDTIME(time, SEC_TO_TIME((4 + numberOfExtends) * 3600)) > ?
				          )
				""";

		try (Connection conn = mysqlConnection.getInstance().getConnection();
				Statement totalStmt = conn.createStatement();
				ResultSet totalRS = totalStmt.executeQuery(totalSpotsQuery)) {

			if (totalRS.next()) {
				int totalSpots = totalRS.getInt(1);

				try (PreparedStatement overlapStmt = conn.prepareStatement(overlappingOrdersQuery)) {
					overlapStmt.setDate(1, Date.valueOf(date));
					overlapStmt.setTime(2, Time.valueOf(end));
					overlapStmt.setTime(3, Time.valueOf(start));

					ResultSet overlapRS = overlapStmt.executeQuery();
					if (overlapRS.next()) {
						int overlappingOrders = overlapRS.getInt(1);

						PreparedStatement overlapsubscriberSpotsStmt = conn
								.prepareStatement(overlappingSubscribersSpotsQuery);
						overlapsubscriberSpotsStmt.setDate(1, Date.valueOf(date));
						overlapsubscriberSpotsStmt.setTime(2, Time.valueOf(end));
						overlapsubscriberSpotsStmt.setTime(3, Time.valueOf(start));

						ResultSet overlapSP = overlapsubscriberSpotsStmt.executeQuery();
						if (overlapSP.next()) {
							int overlappingSPS = overlapSP.getInt(1);
							double freeRatio = (totalSpots - overlappingOrders - overlappingSPS) / (double) totalSpots;
							return freeRatio >= 0.4;
						}
					}
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Gets availability status of all parking spots for a specific time window.
	 *
	 * @param date  the date of interest
	 * @param start the start time
	 * @param end   the end time
	 * @return a map of parking spot codes to their availability status (true if
	 *         free)
	 */
	public static Map<Integer, Boolean> getAllSpotsStatus(LocalDate date, LocalTime start, LocalTime end) {
		Map<Integer, Boolean> statusMap = new LinkedHashMap<>();
		String queryAllSpots = "SELECT parkingCode FROM parkingspot";

		try (Connection conn = mysqlConnection.getInstance().getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(queryAllSpots)) {

			while (rs.next()) {
				int code = rs.getInt("parkingCode");
				boolean isAvailable = isParkingSpotAvailable(code, date, start, end);
				statusMap.put(code, isAvailable);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return statusMap;
	}

	/**
	 * Inserts a new order into the database and returns a confirmation code.
	 *
	 * @param rsp the ResponseWrapper containing order and time details
	 * @return a ResponseWrapper containing the generated confirmation code
	 */
	public static ResponseWrapper insertNewOrder(ResponseWrapper rsp) {
		ResponseWrapper answer = null;

		try {
			Connection conn = mysqlConnection.getInstance().getConnection();

			int spotId = (int) ((ResponseWrapper) rsp.getData()).getData();
			ResponseWrapper details = (ResponseWrapper) ((ResponseWrapper) rsp.getData()).getExtra();
			LocalDate date = (LocalDate) details.getData();
			ResponseWrapper time = (ResponseWrapper) details.getExtra();
			LocalTime startTime = (LocalTime) time.getData();
			LocalTime endTime = (LocalTime) time.getExtra();

			int confirmationCode = generateUniqueConfirmationCode();
			int subscriberCode = 17; // Hardcoded for now

			String query = """
					    INSERT INTO `order`
					    (parking_space, order_date, confirmation_code, subscriber_id,
					     date_of_placing_an_order, startTime, endTime)
					    VALUES (?, ?, ?, ?, ?, ?, ?)
					""";

			PreparedStatement insertStmt = conn.prepareStatement(query);
			insertStmt.setInt(1, spotId);
			insertStmt.setDate(2, Date.valueOf(date));
			insertStmt.setInt(3, confirmationCode);
			insertStmt.setInt(4, subscriberCode);
			insertStmt.setDate(5, Date.valueOf(LocalDate.now()));
			insertStmt.setTime(6, Time.valueOf(startTime));
			insertStmt.setTime(7, Time.valueOf(endTime));

			int rowsAffected = insertStmt.executeUpdate();
			answer = new ResponseWrapper("CONFIRMATION_CODE", null);

			if (rowsAffected > 0) {
				answer.setData(confirmationCode);
				System.out.println("Insert succeeded");
			} else {
				System.out.println("Insert failed");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return answer;
	}

	/**
	 * Generates a unique confirmation code between 1000 and 9999.
	 *
	 * @return a unique confirmation code
	 * @throws SQLException if a DB error occurs during uniqueness check
	 */
	public static int generateUniqueConfirmationCode() throws SQLException {
		Connection conn = mysqlConnection.getInstance().getConnection();
		Random random = new Random();
		int code;
		do {
			code = 1000 + random.nextInt(9000); // 1000–9999
		} while (!isCodeUnique(conn, code));
		return code;
	}

	/**
	 * Checks if a given confirmation code is unique in the `order` table.
	 *
	 * @param conn the DB connection
	 * @param code the confirmation code to check
	 * @return true if the code does not exist, false otherwise
	 * @throws SQLException if a DB error occurs
	 */
	public static boolean isCodeUnique(Connection conn, int code) throws SQLException {
		String sql = "SELECT 1 FROM `order` WHERE confirmation_code = ?";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, code);
			try (ResultSet rs = stmt.executeQuery()) {
				return !rs.next(); // true if not found
			}
		}
	}

	/**
	 * Example test main to validate the functionality. 
	 * @param args args for the main
	 */
	public static void main(String[] args) {
		int parkingCode = 12;
		LocalDate requestDate = LocalDate.of(2025, 5, 28);
		LocalTime start = LocalTime.of(13, 0);
		LocalTime end = LocalTime.of(15, 0);

		boolean available = isParkingSpotAvailable(parkingCode, requestDate, start, end);
		boolean hasCapacity = isOverallAvailabilitySufficient(requestDate, start, end);
		Map<Integer, Boolean> spotStatuses = getAllSpotsStatus(requestDate, start, end);

		System.out.println("Spot free? " + available);
		System.out.println("At least 40% spots free? " + hasCapacity);
		System.out.println("Spot statuses: " + spotStatuses);
	}
}
