import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;

import data.Login;
import data.ResponseWrapper;

/**
 * This controller handles operations related to parking spot management, such
 * as validating subscribers, verifying confirmation codes, and updating parking
 * spot statuses.
 */
public class parkingSpotController {
	/**
	 * mesqlconnecton instance
	 */
	private mysqlConnection instance = null;
	/**
	 * DB connection field
	 */
	Connection conn = null;

	/**
	 * Initializes the database connection.
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
	 * Constructor that initializes the DB connection on object creation.
	 */
	public parkingSpotController() {
		this.getDBConnection();
	}

	/**
	 * Verifies subscriber login credentials using a case-sensitive (BINARY) match.
	 * 
	 * @param response A ResponseWrapper containing Login data (username and
	 *                 password).
	 * @return true if subscriber exists, false otherwise.
	 */
	public boolean validSubscriber(ResponseWrapper response) {
		Login loginDetails = (Login) response.getData();
		boolean exists = false;
		try {
			System.out.println("Server " + loginDetails.getUsername() + "  " + loginDetails.getPasswrod());
			PreparedStatement ps = conn
					.prepareStatement("SELECT * FROM `subscriber` WHERE BINARY username=(?) AND BINARY password=(?)");
			ps.setString(1, loginDetails.getUsername());
			ps.setString(2, loginDetails.getPasswrod());
			ResultSet rs = ps.executeQuery();
			exists = rs.next(); // true if user found
			System.out.println("Exists? " + exists);
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return exists;
	}

	/**
	 * Checks if a valid parking session exists for the given confirmation and
	 * subscriber code. If found, it attempts to release (empty) the parking spot.
	 *
	 * @param response A ResponseWrapper with confirmation code (data) and
	 *                 subscriber code (extra).
	 * @return true if a valid session exists and was successfully released, false
	 *         otherwise.
	 */
	public boolean findParkingCode(ResponseWrapper response) {
		int confirmationCode = Integer.parseInt(response.getData().toString());
		int subscriberCode = Integer.parseInt(response.getExtra().toString());
		boolean exists = false, result = false;
		try {
			// Select parking session within ±15 min window of current time
			PreparedStatement ps = conn.prepareStatement("""
						SELECT * FROM subscriberparking
						WHERE confirmation_code = ?
						  AND subscriberCode = ?
						  AND status = 'ACTIVE'
						  AND DATE(time) = CURRENT_DATE
						  AND TIME(time) <= (CURRENT_TIME + INTERVAL 15 MINUTE)
					""");
			ps.setInt(1, confirmationCode);
			ps.setInt(2, subscriberCode);
			ResultSet rs = ps.executeQuery();
			exists = rs.next();

			// If found, try to update DB to mark parking spot as free
			if (exists) {
				System.out.println("yes1");
				int parkingCode = rs.getInt("parkingCode");
				result = emptyRelevantParkingSpot(confirmationCode, subscriberCode, parkingCode);
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return exists && result;
	}

	/**
	 * Marks the subscriber parking as inactive and frees the related parking spot.
	 *
	 * @param confirmationCode The confirmation code of the session.
	 * @param subscriberCode   The subscriber who made the reservation.
	 * @param parkingCode      The parking spot code to be freed.
	 * @return true if both updates succeed, false otherwise.
	 */
	public boolean emptyRelevantParkingSpot(int confirmationCode, int subscriberCode, int parkingCode) {
		boolean success1 = false, success2 = false;
		System.out.println("yes2 " + confirmationCode + " " + subscriberCode + " " + parkingCode);
		try {
			LocalDate today = LocalDate.now();
			LocalTime now = LocalTime.now().withSecond(0).withNano(0); // Normalized current time

			// 1. Mark subscriber parking session as NOT ACTIVE and record pickup time
			PreparedStatement ps1 = conn.prepareStatement("""
						UPDATE subscriberparking
						SET status = 'NOT ACTIVE', receivingCarTime = ?
						WHERE confirmation_code = ?
						  AND subscriberCode = ?
						  AND status = 'ACTIVE'
						  AND DATE(time) = CURRENT_DATE
						  AND TIME(time) <= (CURRENT_TIME + INTERVAL 15 MINUTE)
					""");
			ps1.setTime(1, java.sql.Time.valueOf(now));
			ps1.setInt(2, confirmationCode);
			ps1.setInt(3, subscriberCode);
			int updated1 = ps1.executeUpdate();
			success1 = updated1 > 0;
			ps1.close();

			// 2. Set the parking spot to "empty" and remove the subscriber association
			PreparedStatement ps2 = conn.prepareStatement("""
						UPDATE parkingspot
						SET status = 'empty', subscriber_code = NULL
						WHERE subscriber_code = ? AND parkingCode = ?
					""");
			ps2.setInt(1, subscriberCode);
			ps2.setInt(2, parkingCode);
			int updated2 = ps2.executeUpdate();
			success2 = updated2 > 0;
			ps2.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("server sql " + success1 + " " + success2);
		return success1 && success2;
	}
}
