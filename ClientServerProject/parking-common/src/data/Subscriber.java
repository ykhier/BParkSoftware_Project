package data;

import java.io.Serializable;

/**
 * Represents a subscriber with personal login details and a unique code.
 * <p>
 * This class is used for transferring subscriber data between client and server.
 * It implements {@link Serializable} to allow network transmission.
 * </p>
 */
public class Subscriber implements Serializable {

	private static final long serialVersionUID = 1L;

	/** The subscriber's username */
	private String username;

	/** The subscriber's password */
	private String password;

	/** The subscriber's phone number */
	private String phoneNumber;

	/** The subscriber's email address */
	private String email;

	/** The subscriber's unique identifier/code */
	private int code;

	/**
	 * Constructs a {@code Subscriber} with all fields.
	 *
	 * @param usrname     the subscriber's username
	 * @param password    the subscriber's password
	 * @param phoneNumber the subscriber's phone number
	 * @param email       the subscriber's email address
	 * @param code        the subscriber's unique code
	 */
	public Subscriber(String usrname, String password, String phoneNumber, String email, int code) {
		this.username = usrname;
		this.password = password;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.code = code;
	}

	/**
	 * Constructs a {@code Subscriber} with only the unique code.
	 *
	 * @param code the subscriber's unique code
	 */
	public Subscriber(int code) {
		this.code = code;
	}

	/**
	 * Returns the subscriber's username.
	 *
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the subscriber's username.
	 *
	 * @param usrname the new username
	 */
	public void setUsername(String usrname) {
		this.username = usrname;
	}

	/**
	 * Returns the subscriber's password.
	 *
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the subscriber's password.
	 *
	 * @param password the new password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Returns the subscriber's phone number.
	 *
	 * @return the phone number
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * Sets the subscriber's phone number.
	 *
	 * @param phoneNumber the new phone number
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * Returns the subscriber's email address.
	 *
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the subscriber's email address.
	 *
	 * @param email the new email address
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Returns the subscriber's unique code.
	 *
	 * @return the subscriber code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * Sets the subscriber's unique code.
	 *
	 * @param code the new subscriber code
	 */
	public void setCode(int code) {
		this.code = code;
	}

	/**
	 * Returns a string representation of the subscriber for logging/debugging.
	 *
	 * @return a formatted string with username, password, and code
	 */
	@Override
	public String toString() {
		return "username " + username + " password " + password + " code " + code;
	}
}
