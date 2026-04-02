package data;

import java.io.Serializable;
import java.sql.Date;

/**
 * Represents the data needed to update an existing parking order.
 * <p>
 * This class includes the order number, new parking space, and updated order date.
 * It implements {@link Serializable} for transfer between client and server.
 * </p>
 */
public class UpdateOrderDetails implements Serializable {

	private static final long serialVersionUID = 1L;

	/** The order number to be updated */
	private int order_number;

	/** The updated parking space value */
	private int parking_space;

	/** The updated order date */
	private Date order_date;

	/**
	 * Constructs an {@code UpdateOrderDetails} object with all fields.
	 *
	 * @param order_number  the ID of the order to update
	 * @param parking_space the new parking space assigned
	 * @param order_date    the updated order date
	 */
	public UpdateOrderDetails(int order_number, int parking_space, Date order_date) {
		this.order_number = order_number;
		this.parking_space = parking_space;
		this.order_date = order_date;
	}

	/**
	 * Returns the order number.
	 *
	 * @return the order number
	 */
	public int getOrder_number() {
		return order_number;
	}

	/**
	 * Sets a new value for the order number.
	 *
	 * @param order_number the new order number
	 */
	public void setOrder_number(int order_number) {
		this.order_number = order_number;
	}

	/**
	 * Returns the parking space.
	 *
	 * @return the parking space
	 */
	public int getParking_space() {
		return parking_space;
	}

	/**
	 * Sets a new value for the parking space.
	 *
	 * @param parking_space the new parking space
	 */
	public void setParking_space(int parking_space) {
		this.parking_space = parking_space;
	}

	/**
	 * Returns the order date.
	 *
	 * @return the updated order date
	 */
	public Date getOrder_date() {
		return order_date;
	}

	/**
	 * Sets a new value for the order date.
	 *
	 * @param order_date the new order date
	 */
	public void setOrder_date(Date order_date) {
		this.order_date = order_date;
	}

	/**
	 * Returns a string representation of the update order details.
	 *
	 * @return a string with order number, parking space, and order date
	 */
	@Override
	public String toString() {
		return "order_number=" + order_number +
			   ", parking_space=" + parking_space +
			   ", order_date=" + order_date;
	}
}
