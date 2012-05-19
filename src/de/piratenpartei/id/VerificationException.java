package de.piratenpartei.id;

public class VerificationException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public VerificationException(String message) {
		super(message);
	}
	
	public VerificationException(String message, Throwable cause) {
		super(message, cause);
	}
}
