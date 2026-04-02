package data;

/**
 * Enum representing different roles of staff in the system.
 * <p>
 * This is used to distinguish between users with administrative privileges and
 * regular workers during login or authorization checks.
 * </p>
 */
public enum Role {
	/** Administrator with elevated permissions */
	ADMIN,

	/** Regular staff member or worker */
	WORKER
}
