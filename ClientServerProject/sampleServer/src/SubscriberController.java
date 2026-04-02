import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import data.Subscriber;

/**
 * Controller to manage functionality related to the subscriber
 */
public class SubscriberController {
	/** Singleton instance of the MySQL connection manager */
	private mysqlConnection instance = null;
	/** JDBC connection object */
	Connection conn = null;
	
    /**
     * Initialises the connection to the database.
     * Should be called before performing any queries.
     */
    public void getDBConnection() {
        try {
            instance = mysqlConnection.getInstance();
            conn = instance.getConnection();
        } catch (Exception e) {
            System.out.println("Error connecting to DB: " + e.getMessage());
        }
    }
    /**
     * this function for return all the subscribers
     * @return array list containing details of all existing susbcribers
     */
	public ArrayList<Subscriber> getAllSubscribers() {
	    ArrayList<Subscriber> allSubscribers = null;
        instance = mysqlConnection.getInstance();
	    try {
	    	conn = instance.getConnection();
	        allSubscribers = mysqlConnection.printSubscribers(conn);
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	    return allSubscribers;
	}
	
	/**
	 * method to update subscriber according to it's code
	 * @param subscriber new vales for the subscriber
	 * @return result true/false if update successes
	 */
	public static boolean updateSubscriber(Subscriber subscriber) {
	    String sql = "UPDATE subscribers SET username = ?, password = ?, phoneNumber = ?, email = ? WHERE code = ?";

	    Connection conn = null;
	    PreparedStatement ps = null;

	    try {
	        conn = mysqlConnection.getInstance().getConnection();
	        ps = conn.prepareStatement(sql);

	        ps.setString(1, subscriber.getUsername());
	        ps.setString(2, subscriber.getPassword());
	        ps.setString(3, subscriber.getPhoneNumber());
	        ps.setString(4, subscriber.getEmail());
	        ps.setInt(5, subscriber.getCode());

	        return ps.executeUpdate() == 1;

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;

	    } finally {
	        try {
	            if (ps != null) ps.close();
	            if (conn != null) conn.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	}
}
