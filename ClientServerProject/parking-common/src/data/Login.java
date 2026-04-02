package data;

import java.io.Serializable;

/**
 * A simple data class representing login credentials.
 * Implements Serializable to allow object transfer (e.g., over a network).
 */
public class Login implements Serializable {

	private static final long serialVersionUID = 1L;

	// Fields for storing username and password
	/**
	 * username logged in with
	 */
	private String username;
	/**
	 * password logged in with
	 */
	private String passwrod; 

	/**
	 * Constructs a Login object with the given username and password.
	 *
	 * @param username the username
	 * @param passwrod the password (intended spelling may be "password")
	 */
	public Login(String username, String passwrod) {
		this.username = username;
		this.passwrod = passwrod;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return the password
	 */
	public String getPasswrod() {
		return passwrod;
	}
}
