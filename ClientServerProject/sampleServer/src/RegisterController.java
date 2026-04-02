import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import data.Subscriber;

/**
 * Class to control registering new subscriber to the system
 */
public class RegisterController {
	/** Singleton instance of the MySQL connection manager */
	private mysqlConnection instance = null;
	/** JDBC connection object */
	Connection conn = null;

	/**
	 * Constructors new RegisterController and initialise the DB connection
	 */
	public RegisterController() {
		getDBConnection();
	}

	/**
	 * Method for initialising the DB connection
	 */
	private void getDBConnection() {
		try {
			instance = mysqlConnection.getInstance();
			conn = instance.getConnection();
		} catch (Exception e) {
			System.out.println("Error connecting to DB");
		}
	}

	/**
	 * Method that checks if specific email is related to any existing subscriber
	 * @param email email String to look for
	 * @return true if found, false otherwise
	 */
	private boolean isEmailExists(String email) {
		try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM subscribers WHERE email = ?")) {
			ps.setString(1, email);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt(1) > 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Method for inserting new subscriber to DB (if it doesnt exist - based on the email)
	 * @param subscriber which subscriber to insert
	 * @return string of result of insertion
	 */
	public String registerNewSubscriber(Subscriber subscriber) {
		try {
			if (isEmailExists(subscriber.getEmail())) {
				return "Registration failed: Email already exists.";
			}

			int code = mysqlConnection.generateUniqueCode();
			subscriber.setCode(code);

			boolean success = mysqlConnection.saveSubscriber(subscriber);
			return success ? "Registration successful. Your code is: " + code
					: "Registration failed: Could not insert subscriber.";
		} catch (Exception e) {
			e.printStackTrace(); // For debugging purposes
			return "Registration failed: Server error.";
		}
	}
}
