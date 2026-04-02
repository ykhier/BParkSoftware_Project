package data;

/**
 * Enum representing constant system status messages used across the application.
 * <p>
 * These values are typically used to describe the result of operations related to parking,
 * car delivery, and car lookup, and can be sent between server and client to simplify
 * status handling.
 * </p>
 */
public enum SystemStatus {

    /** Indicates that a parking spot is available for assignment */
    PARKING_SPOT_AVAILABLE,

    /** Indicates that no parking spot is currently available */
    NO_PARKING_SPOT,

    /** Indicates that the car already exists in the system (duplicate attempt) */
    CAR_EXISTS,

    /** Indicates that the requested car was not found */
    CAR_NOT_FOUND,

    /** Indicates that the car delivery was completed successfully */
    SUCCESS_DELIVERY,

    /** Indicates that the car has already been delivered previously */
    ALREADY_DELIVERED
}
