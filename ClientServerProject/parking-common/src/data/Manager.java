package data;

import java.io.Serializable;

/**
 * Class for manager
 */
public class Manager extends Worker implements Serializable{

	private static final long serialVersionUID = 1L;

	/**constructor to create new manager with this values:
	 * @param username value for username
	 * @param password value for password
	 */
	public Manager(String username, String password) {
		super(username, password);
	}

}
