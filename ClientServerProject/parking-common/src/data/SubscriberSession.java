package data;

/**
 * class to save the subscriber user along its subscription
 */
public class SubscriberSession {
	private static Subscriber currentSubscriber;
	/* a variable to understand if the car deliver with session or from outside */
	private static String connectionWay = "inetrnal";
	private static String subscriberid;

	/**
	 * method to set the new subscriber
	 * 
	 * @param subscriber who is the new subscriber
	 */
	public static void setSubscriber(Subscriber subscriber) {
		currentSubscriber = subscriber;
	}

	/**
	 * method to get the current subscriber
	 * 
	 * @return the current subscribed user
	 */
	public static Subscriber getSubscriber() {
		return currentSubscriber;
	}

	/**
	 * method to get the connectionWay variable
	 * 
	 * @return connectionWay value
	 */
	public static String getconway() {
		return connectionWay;
	}

	/**
	 * method to set the connectionWay variable
	 * 
	 * @param conway2 new connectionWay value
	 */
	public static void setconway(String conway2) {
		connectionWay = conway2;
	}

	/**
	 * method to set subscriber id
	 * 
	 * @param subscriberid2 new value for id
	 */
	public static void setsubscriberid(String subscriberid2) {
		subscriberid = subscriberid2;
	}

	/**
	 * method to get subscriber id
	 * 
	 * @return subscriber id
	 */
	public static String getsubscriberid() {
		return subscriberid;
	}

}
