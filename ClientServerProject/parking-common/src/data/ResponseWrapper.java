package data;

import java.io.Serializable;

/**
 * A serializable wrapper for sending general messages between client and server.
 * <p>
 * Each message includes a type (string identifier), main data (usually a payload object),
 * and optionally extra data for additional context or parameters.
 * </p>
 */
public class ResponseWrapper implements Serializable {
	private static final long serialVersionUID = 1L;

	/** Type identifier for the message (used to route on the server/client side) */
	private String type;

	/** Main payload data to be transferred */
	private Object data;

	/** Optional extra data to include with the message */
	private Object extra;

	/**
	 * Constructs a ResponseWrapper with a type and data.
	 *
	 * @param type the type of the message (used to recognize it in the handler)
	 * @param data the main payload of the message
	 */
	public ResponseWrapper(String type, Object data) {
		this.type = type;
		this.data = data;
	}

	/**
	 * Constructs a ResponseWrapper with type, data, and extra information.
	 *
	 * @param type  the type of the message
	 * @param data  the main payload
	 * @param extra additional optional data
	 */
	public ResponseWrapper(String type, Object data, Object extra) {
		this.type = type;
		this.data = data;
		this.extra = extra;
	}

	/**
	 * Returns the extra data object, if any.
	 *
	 * @return the extra data
	 */
	public Object getExtra() {
		return extra;
	}

	/**
	 * Returns the message type.
	 *
	 * @return the message type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Returns the main data payload.
	 *
	 * @return the data
	 */
	public Object getData() {
		return data;
	}

	/**
	 * Sets the message type.
	 *
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Sets the data payload.
	 *
	 * @param data the data to set
	 */
	public void setData(Object data) {
		this.data = data;
	}

	/**
	 * Sets the extra data.
	 *
	 * @param extra the extra data to set
	 */
	public void setExtra(Object extra) {
		this.extra = extra;
	}

	/**
	 * Returns a string representation of this response wrapper.
	 *
	 * @return a string with the type and data
	 */
	@Override
	public String toString() {
		return "ResponseWrapper [type=" + type + ", data=" + data + "]";
	}
}
