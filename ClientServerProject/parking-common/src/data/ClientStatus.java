package data;

/**
 * Represents the status of a connected client.
 * Holds IP address, host name, and connection status.
 */
public class ClientStatus {
	private final String ip;
	private final String hostName;
	private String status;

	/**
	 * Constructs a ClientStatus object.
	 * 
	 * @param ip        the client's IP address
	 * @param hostName  the client's host name
	 * @param status    the connection status (e.g., "Connected", "Disconnected")
	 */
	public ClientStatus(String ip, String hostName, String status) {
		this.ip = ip;
		this.hostName = hostName;
		this.status = status;
	}

	/**
	 * Gets the IP address of the client.
	 * 
	 * @return the client's IP address
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * Gets the host name of the client.
	 * 
	 * @return the client's host name
	 */
	public String getHostName() {
		return hostName;
	}

	/**
	 * Gets the current connection status of the client.
	 * 
	 * @return the client's connection status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Sets a new connection status for the client.
	 * 
	 * @param status the new status (e.g., "Connected", "Disconnected")
	 */
	public void setStatus(String status) {
		this.status = status;
	}
}
