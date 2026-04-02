package data;

import java.io.Serializable;
import java.time.Year;

/**
 * Represents a car associated with a {@link Subscriber}.
 * <p>
 * Each car has a license plate number, model, year of manufacture,
 * and a reference to the subscriber (owner). This class is used for parking system records
 * and is serializable for network transmission between client and server.
 * </p>
 */
public class Car implements Serializable {
	private static final long serialVersionUID = 1L;

	/** The license plate number of the car */
	private String carNumber;

	/** The model name of the car */
	private String model;

	/** The year the car was manufactured */
	private int year;

	/** The subscriber (owner) associated with the car */
	private Subscriber subscriber;

	/**
	 * Constructs a Car object.
	 *
	 * @param carNumber the license plate number of the car
	 * @param model     the model of the car
	 * @param year      the manufacturing year of the car
	 */
	public Car(String carNumber, String model, int year) {
		this.carNumber = carNumber;
		this.model = model;
		this.year = year;
	}

	/**
	 * Sets the car number (license plate).
	 *
	 * @param carNumber the car's license plate number
	 */
	public void setCarNumber(String carNumber) {
		this.carNumber = carNumber;
	}

	/**
	 * Sets the model of the car.
	 *
	 * @param model the car's model name
	 */
	public void setModel(String model) {
		this.model = model;
	}

	/**
	 * Sets the year the car was manufactured.
	 *
	 * @param year the car's manufacturing year
	 */
	public void setYear(int year) {
		this.year = year;
	}

	/**
	 * Associates a subscriber (owner) with this car.
	 *
	 * @param subscriber the car's owner
	 */
	public void setSubscriber(Subscriber subscriber) {
		this.subscriber = subscriber;
	}

	/**
	 * Validates and formats an Israeli car number.
	 * <p>
	 * Accepts both hyphenated and non-hyphenated input (e.g., "1234567", "12-345-67").
	 * </p>
	 *
	 * @param carNumber the input car number (with or without hyphens)
	 * @return the formatted car number (e.g., "12-345-67" or "123-45-678") if valid,
	 *         or {@code null} if invalid
	 */
	public static String formatIsraeliCarNumber(String carNumber) {
	    if (carNumber == null) return null;

	    String cleaned = carNumber.replaceAll("-", "");

	    // Old format: 7 digits → XX-XXX-XX
	    if (cleaned.matches("\\d{7}")) {
	        return cleaned.substring(0, 2) + "-" + cleaned.substring(2, 5) + "-" + cleaned.substring(5);
	    }

	    // New format: 8 digits → XXX-XX-XXX
	    else if (cleaned.matches("\\d{8}")) {
	        return cleaned.substring(0, 3) + "-" + cleaned.substring(3, 5) + "-" + cleaned.substring(5);
	    }

	    return null;
	}

	/**
	 * Validates that a car model contains only alphabetic characters and spaces.
	 *
	 * @param model the car model string
	 * @return {@code true} if valid, {@code false} otherwise
	 */
	public static boolean isValidCarModel(String model) {
	    return model != null && model.matches("[a-zA-Z ]+");
	}

	/**
	 * Validates that the car year is within a logical range (1900 to next year).
	 *
	 * @param year the year to validate
	 * @return {@code true} if valid, {@code false} otherwise
	 */
	public static boolean isValidCarYear(int year) {
	    int currentYear = Year.now().getValue();
	    return year >= 1900 && year <= currentYear + 1;
	}

	/**
	 * Returns the car's license plate number.
	 *
	 * @return the car number
	 */
	public String getCarNumber() {
		return carNumber;
	}

	/**
	 * Returns the car model.
	 *
	 * @return the car's model
	 */
	public String getModel() {
		return model;
	}

	/**
	 * Returns the year the car was manufactured.
	 *
	 * @return the car's year
	 */
	public int getYear() {
		return year;
	}

	/**
	 * Returns the subscriber associated with the car.
	 *
	 * @return the car's owner
	 */
	public Subscriber getSubscriber() {
		return subscriber;
	}

	/**
	 * Returns a string representation of the car.
	 *
	 * @return a string in the format: carNumber model year
	 */
	@Override
	public String toString() {
	    return carNumber + " " + model + " " + year;
	}
}
