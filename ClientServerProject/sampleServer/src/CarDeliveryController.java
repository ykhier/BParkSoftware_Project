import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import data.Subscriber;

/**
 * Class to control the functionality  of delivering the car on the server side
 */
public class CarDeliveryController {
    /** JDBC connection object */
	private mysqlConnection instance = null;
	Connection conn = null;
	
    /**
     * Initializes the connection to the database.
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
     * @return array list that contains all existing subscribers
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
}
