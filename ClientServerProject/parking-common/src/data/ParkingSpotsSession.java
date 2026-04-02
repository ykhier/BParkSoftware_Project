package data;

import java.util.Map;

/**
 * A utility class that holds the current session state of parking spot
 * availability. This class uses a static map to track whether each parking spot
 * is occupied or not.
 * 
 * Key: parking spot ID (Integer) Value: availability (Boolean) — true if
 * available, false if occupied
 */
public class ParkingSpotsSession {

	/**
	 * A map representing the availability status of parking spots. True =
	 * available, False = occupied.
	 */
	private static Map<Integer, Boolean> spotMap;

	/**
	 * Sets the current parking spot availability map.
	 *
	 * @param map a map of parking spot IDs to availability booleans
	 */
	public static void setMap(Map<Integer, Boolean> map) {
		spotMap = map;
	}

	/**
	 * Retrieves the current parking spot availability map.
	 *
	 * @return a map of parking spot IDs to availability booleans
	 */
	public static Map<Integer, Boolean> getMap() {
		return spotMap;
	}
}
