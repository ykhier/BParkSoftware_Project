import javafx.fxml.FXML;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import data.Order;

/**
 * Controller class for subscriber main page operations. Handles fetching past
 * orders and parking history for a given subscriber.
 */
public class SubscribermainPageController {

	/**
	 * Retrieves all regular orders placed by a given subscriber from the `order`
	 * table.
	 *
	 * @param subscriberId The ID of the subscriber.
	 * @return A list of Order objects representing the subscriber's regular orders.
	 */
	public static ArrayList<Order> getOrdersBySubscriber(int subscriberId) {
		ArrayList<Order> list = new ArrayList<>();

		String sql = """
				    SELECT order_number,
				           order_date,
				           date_of_placing_an_order,
				           parking_space,
				           car_number
				      FROM `order`
				     WHERE subscriber_id = ?
				     ORDER BY order_date DESC
				""";

		try (Connection conn = mysqlConnection.getInstance().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, subscriberId);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				int orderNumber = rs.getInt("order_number");
				Date orderDate = rs.getDate("order_date");
				Date placingDate = rs.getDate("date_of_placing_an_order");
				int parkingSpace = rs.getInt("parking_space");
				String carNumber = rs.getString("car_number");

				// Create and add order to the list
				list.add(new Order(orderNumber, orderDate, placingDate, parkingSpace, carNumber));
			}

			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	/**
	 * Retrieves all subscriber parking sessions from the `subscriberparking` table.
	 *
	 * @param subscriberId The subscriber's unique ID.
	 * @return A list of Order objects with session information (parking space,
	 *         time, extensions, etc.)
	 */
	public static ArrayList<Order> getOrdersFromSubscriberParking(int subscriberId) {
		ArrayList<Order> list = new ArrayList<>();

		String sql = """
				    SELECT parkingCode, date, time, status, numberOfExtends, receivingCarTime
				    FROM subscriberparking
				    WHERE subscriberCode = ?
				""";

		try (Connection conn = mysqlConnection.getInstance().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, subscriberId);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				int parkingSpace = rs.getInt("parkingCode");
				Date date = rs.getDate("date");
				Time time = rs.getTime("time");
				String status = rs.getString("status");
				int numberOfExtends = rs.getInt("numberOfExtends");
				Time receivingCarTime = rs.getTime("receivingCarTime");

				// Create and add parking session to the list
				list.add(new Order(parkingSpace, date, time, status, numberOfExtends, receivingCarTime));
			}

			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}

}
