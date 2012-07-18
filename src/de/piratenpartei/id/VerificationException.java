package de.piratenpartei.id;

/**
 * This exception is thrown, if a message failed to verify.
 * @author arne
 *
 */
public class VerificationException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public VerificationException(String message) {
		super(message);
	}
	
	public VerificationException(String message, Throwable cause) {
		super(message, cause);
	}
}
