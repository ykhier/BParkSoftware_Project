import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import data.Car;
import data.Order;
import data.ResponseWrapper;
import data.Subscriber;
import data.UpdateOrderDetails;

/**
 * class that will control all reservation related methods
 */

public class reservationController {
	/** Singleton instance of the MySQL connection manager */
	private mysqlConnection instance = null;
	/** JDBC connection object */
	Connection conn = null;

	/**
	 * initialises the connection to the MySQL database.
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
	 * Cinstrcutors new controller and initialises connection
	 */
	public reservationController() {
		this.getDBConnection();
	}

	/**
	 * method for receiving all existing orders from the DB
	 * 
	 * @return ArrayList of orders
	 */
	public ArrayList<Order> getAllOrders() {
		ArrayList<Order> allOrders = null;
		try {
			allOrders = mysqlConnection.printOrders();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return allOrders;
	}

	/**
	 * method for updating specific order
	 * 
	 * @param order object with new values for the order
	 * @return string that will assign status of the operation
	 */
	public String updateOrder(UpdateOrderDetails order) {
		String returnMessageToClient = "No Operation was done";
		try {
			int updatedRows = mysqlConnection.updateParkingSpaceANDOrderDate(order.getOrder_number(),
					order.getParking_space(), order.getOrder_date());
			if (updatedRows == 1)
				returnMessageToClient = "Successfully updated";
			else if (updatedRows == -1)
				returnMessageToClient = "Invalid order number (there isn't any order with this order number)";
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return returnMessageToClient;
	}

	/**
	 * Method to get order details with ID
	 * @param orderID which D to look for
	 * @return Order object with all related data, null if not found.
	 */
	public Order getOrderByID(int orderID) {
		Order order = null;
		try {
			order = mysqlConnection.returnOrderByID(orderID);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return order;
	}

	
	/*
	 * public String handleParkingOrder(Order order) { try { if
	 * (!mysqlConnection.occupyFirstAvailableSpot(order)) { return null; } if
	 * (!mysqlConnection.carExists(order.getCar())) {
	 * mysqlConnection.insertCarToDatabase(order.getCar()); }
	 * 
	 * mysqlConnection.insertOrderToDatabase(order); } catch (Exception e) {
	 * System.out.println("Error on handleParkingOrder function"); } return
	 * "Order successfully processed and parking spot occupied"; }
	 */

}
