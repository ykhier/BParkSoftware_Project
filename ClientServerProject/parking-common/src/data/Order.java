package data;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDateTime;

/**
 * Class for saving details for a new Order.
 */
public class Order implements Serializable {
	private static final long serialVersionUID = 1L;

	/** Parking space assigned to the order */
	private int parking_space;

	/** Unique number identifying the order */
	private int order_number;

	/** Date the parking order is for */
	private Date order_date;

	/**
	 * Confirmation code issued for the parking session
	 */
	private int confirmation_code;

	/** Subscriber ID associated with the order */
	private int subscriber_id;

	/** Date when the order was placed */
	private Date date_of_placing_an_order;

	/** Start time of the order */
	private LocalDateTime startTime;

	/** End time of the order */
	private LocalDateTime endTime;

	/** Car associated with the order */
	private Car car;

	/** Time the car was delivered */
	private LocalDateTime delivery_time;

	/** Time the car was received */
	private LocalDateTime recivingcartime;

	/** Delivery time (SQL Time format) */
	private Time del_Time;

	/** Receiving time (SQL Time format) */
	private Time rec_Time;

	/** Car number associated with the order */
	private String carNumber;

	/** Status of the parking session (e.g., "ACTIVE") */
	private String status;

	/** Number of times the parking session was extended */
	int numberofextend;

	/**
	 * constructor
	 * 
	 * @param parking_space            value of parking spot
	 * @param order_number             value of order number
	 * @param order_date               value of order date
	 * @param confirmation_code        value of confirmation code
	 * @param subscriber_id            value of subscriber id
	 * @param date_of_placing_an_order value of placing order date
	 */
	public Order(int parking_space, int order_number, Date order_date, int confirmation_code, int subscriber_id,
			Date date_of_placing_an_order) {
		super();
		this.parking_space = parking_space;
		this.order_number = order_number;
		this.order_date = order_date;
		this.confirmation_code = confirmation_code;
		this.subscriber_id = subscriber_id;
		this.date_of_placing_an_order = date_of_placing_an_order;
	}

	/**
	 * Constructor
	 * 
	 * @param confirmation_code confirmation for the parking
	 * @param subscriber_id     subscriber id
	 * @param startDate         start time of the order
	 * @param endTime           end time of the order
	 * @param car               place the order for the car
	 */
	public Order(int confirmation_code, int subscriber_id, LocalDateTime startDate, LocalDateTime endTime, Car car) {
		this.setCar(car);
		this.setStartTime(startDate);
		this.setEndTime(endTime);
		this.setConfirmation_code(confirmation_code);
		this.setSubscriber_id(subscriber_id);
	}

	/**
	 * constructor
	 * 
	 * @param confirmation_code confirmation code for the order
	 * @param parking_space     parking space for the order
	 */
	public Order(int confirmation_code, int parking_space) {
		this.parking_space = parking_space;
		this.confirmation_code = confirmation_code;
	}

	/**
	 * constructor with only subscriber id
	 * 
	 * @param subscriber_id value for subscriber id
	 */
	public Order(int subscriber_id) {
		this.subscriber_id = subscriber_id;
		// this.parking_space=parking_space;
	}

	/**
	 * Constructs an Order with full details including subscriber, car, and timing
	 * info.
	 *
	 * @param subscriber_id     the ID of the subscriber
	 * @param parking_space     the parking spot code
	 * @param order_date        the datetime the order was made
	 * @param numberofextend    number of parking extensions requested
	 * @param recivingcartime   the actual car receiving time
	 * @param car               the associated car object
	 * @param confirmation_code the unique order confirmation code
	 */
	public Order(int subscriber_id, int parking_space, LocalDateTime order_date, int numberofextend,
			LocalDateTime recivingcartime, Car car, int confirmation_code) {
		this.subscriber_id = subscriber_id;
		this.parking_space = parking_space;
		this.delivery_time = order_date;
		this.numberofextend = numberofextend;
		this.recivingcartime = recivingcartime;
		this.car = car;
		this.confirmation_code = confirmation_code;
	}

	/**
	 * Constructs an Order with basic details including car number and times.
	 *
	 * @param carNumber         the car number
	 * @param confirmation_code the unique confirmation code
	 * @param del_time          delivery time of the order
	 * @param rec_time          receiving time of the car
	 * @param subscriber_id     the subscriber's ID
	 * @param parking_space     the parking space number
	 */
	public Order(String carNumber, int confirmation_code, Time del_time, Time rec_time, int subscriber_id,
			int parking_space) {
		this.setCarNumber(carNumber);
		this.confirmation_code = confirmation_code;
		this.setDel_Time(del_time);
		this.setRec_Time(rec_time);
		this.subscriber_id = subscriber_id;
		this.parking_space = parking_space;
	}

	/**
	 * Constructs an Order for historical/log view, without time info.
	 *
	 * @param order_number             the order number (primary key)
	 * @param order_date               the date the parking order is for
	 * @param date_of_placing_an_order the date the order was placed
	 * @param parking_space            the parking space code
	 * @param carNumber                the number of the car
	 */
	public Order(int order_number, Date order_date, Date date_of_placing_an_order, int parking_space,
			String carNumber) {
		this.order_number = order_number;
		this.order_date = order_date;
		this.date_of_placing_an_order = date_of_placing_an_order;
		this.parking_space = parking_space;
		this.carNumber = carNumber;
	}

	/**
	 * Constructs an Order with status and timing for use in subscriberparking
	 * reporting.
	 *
	 * @param parking_space  the parking space code
	 * @param order_date     the date of the order
	 * @param del_Time       the time the parking started
	 * @param status         current status of the parking (e.g., "ACTIVE")
	 * @param numberofextend how many times parking was extended
	 * @param rec_Time       time the car was received (if available)
	 */
	public Order(int parking_space, Date order_date, Time del_Time, String status, int numberofextend, Time rec_Time) {
		this.parking_space = parking_space;
		this.order_date = order_date;
		this.del_Time = del_Time;
		this.status = status;
		this.numberofextend = numberofextend;
		this.rec_Time = rec_Time;
	}

	/**
	 * Gets the parking space.
	 * 
	 * @return parking space number
	 */
	public int getParking_space() {
		return parking_space;
	}

	/**
	 * Sets the parking space.
	 * 
	 * @param parking_space the parking space to set
	 */
	public void setParking_space(int parking_space) {
		this.parking_space = parking_space;
	}

	/**
	 * Gets the order number.
	 * 
	 * @return order number
	 */
	public int getOrder_number() {
		return order_number;
	}

	/**
	 * Sets the order number.
	 * 
	 * @param order_number the order number to set
	 */
	public void setOrder_number(int order_number) {
		this.order_number = order_number;
	}

	/**
	 * Gets the date of the order.
	 * 
	 * @return order date
	 */
	public Date getOrder_date() {
		return order_date;
	}

	/**
	 * Sets the order date.
	 * 
	 * @param order_date the order date to set
	 */
	public void setOrder_date(Date order_date) {
		this.order_date = order_date;
	}

	/**
	 * Gets the confirmation code.
	 * 
	 * @return confirmation code
	 */
	public int getConfirmation_code() {
		return confirmation_code;
	}

	/**
	 * Sets the confirmation code.
	 * 
	 * @param confirmation_code the confirmation code to set
	 */
	public void setConfirmation_code(int confirmation_code) {
		this.confirmation_code = confirmation_code;
	}

	/**
	 * Gets the subscriber ID.
	 * 
	 * @return subscriber ID
	 */
	public int getSubscriber_id() {
		return subscriber_id;
	}

	/**
	 * Sets the subscriber ID.
	 * 
	 * @param subscriber_id the subscriber ID to set
	 */
	public void setSubscriber_id(int subscriber_id) {
		this.subscriber_id = subscriber_id;
	}

	/**
	 * Gets the date when the order was placed.
	 * 
	 * @return date of placing the order
	 */
	public Date getDate_of_placing_an_order() {
		return date_of_placing_an_order;
	}

	/**
	 * Sets the date of placing the order.
	 * 
	 * @param date_of_placing_an_order the placing date to set
	 */
	public void setDate_of_placing_an_order(Date date_of_placing_an_order) {
		this.date_of_placing_an_order = date_of_placing_an_order;
	}

	/**
	 * Returns a string with order summary details.
	 * 
	 * @return order summary string
	 */
	@Override
	public String toString() {
		return parking_space + " " + order_number + " " + confirmation_code + " " + subscriber_id + " " + startTime
				+ " " + endTime + " " + car;
	}

	/**
	 * Gets the start time of the order.
	 * 
	 * @return start time
	 */
	public LocalDateTime getStartTime() {
		return startTime;
	}

	/**
	 * Sets the start time of the order.
	 * 
	 * @param startTime the start time to set
	 */
	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	/**
	 * Gets the end time of the order.
	 * 
	 * @return end time
	 */
	public LocalDateTime getEndTime() {
		return endTime;
	}

	/**
	 * Sets the end time of the order.
	 * 
	 * @param endTime the end time to set
	 */
	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	/**
	 * Gets the car object associated with the order.
	 * 
	 * @return car object
	 */
	public Car getCar() {
		return car;
	}

	/**
	 * Sets the car for the order.
	 * 
	 * @param car the car to assign
	 */
	public void setCar(Car car) {
		this.car = car;
	}

	/**
	 * Sets the delivery time.
	 * 
	 * @param delivery_time the delivery time to set
	 */
	public void setDelivery_time(LocalDateTime delivery_time) {
		this.delivery_time = delivery_time;
	}

	/**
	 * Sets the car receiving time.
	 * 
	 * @param recivingcartime the receiving time to set
	 */
	public void setRecivingcartime(LocalDateTime recivingcartime) {
		this.recivingcartime = recivingcartime;
	}

	/**
	 * Sets the delivery time as java.sql.Time.
	 * 
	 * @param del_Time the time to set
	 */
	public void setDel_Time(Time del_Time) {
		this.del_Time = del_Time;
	}

	/**
	 * Sets the receiving time as java.sql.Time.
	 * 
	 * @param rec_Time the time to set
	 */
	public void setRec_Time(Time rec_Time) {
		this.rec_Time = rec_Time;
	}

	/**
	 * Sets the car number.
	 * 
	 * @param carNumber the car number to set
	 */
	public void setCarNumber(String carNumber) {
		this.carNumber = carNumber;
	}

	/**
	 * Sets the number of extensions.
	 * 
	 * @param numberofextend the count of extensions
	 */
	public void setNumberofextend(int numberofextend) {
		this.numberofextend = numberofextend;
	}

	/**
	 * Gets the delivery time.
	 * 
	 * @return delivery time
	 */
	public LocalDateTime getDelivery_time() {
		return delivery_time;
	}

	/**
	 * Gets the receiving car time.
	 * 
	 * @return receiving time
	 */
	public LocalDateTime getRecivingcartime() {
		return recivingcartime;
	}

	/**
	 * Gets the delivery time (SQL).
	 * 
	 * @return delivery time in SQL format
	 */
	public Time getDelTime() {
		return del_Time;
	}

	/**
	 * Gets the receiving time (SQL).
	 * 
	 * @return receiving time in SQL format
	 */
	public Time getRecTime() {
		return rec_Time;
	}

	/**
	 * Gets the car number.
	 * 
	 * @return car number
	 */
	public String getCarNumber() {
		return carNumber;
	}

	/**
	 * Gets the number of extensions for the order.
	 * 
	 * @return number of extensions
	 */
	public int getNumberofextend() {
		return numberofextend;
	}

	/**
	 * Gets the parking status.
	 * 
	 * @return status (e.g., "ACTIVE", "NOT ACTIVE")
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Sets the parking status.
	 * 
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
}
