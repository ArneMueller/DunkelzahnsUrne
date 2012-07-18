package de.piratenpartei.id;

/**
 * This Exception is thrown if a Key is not of the correct type, or if the key doesn't match to its associated hash value.
 * @author arne
 *
 */
public class KeyException extends Exception {
	private static final long serialVersionUID = 1L;

	public KeyException(String message) {
		super(message);
	}
	
	public KeyException(String message, Throwable cause) {
		super(message, cause);
	}
}
