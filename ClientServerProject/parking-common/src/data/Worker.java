package data;

import java.io.Serializable;

/**
 * Class for saving worker details
 */
public class Worker implements Serializable{

	private static final long serialVersionUID = 1L;
	/**
	 * username for worker
	 */
	private String username;
	/**
	 * passwrod for worker
	 */
	private String password;
	
	/**Constructor
	 * @param username value for username
	 * @param password value for password
	 */
	public Worker(String username,String password) {
		this.setUsername(username);
		this.password = password;
	}

	/**
	 * Method to get worker's username
	 * @return worker's username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Set new value for username
	 * @param username value for worker's username
	 */
	public void setUsername(String username) {
		this.username = username;
	}
}
