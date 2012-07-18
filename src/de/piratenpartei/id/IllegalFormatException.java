package de.piratenpartei.id;

public class IllegalFormatException extends Exception {
	private static final long serialVersionUID = 1L;

	public IllegalFormatException(String message) {
		super(message);
	}
	public IllegalFormatException(String message, Throwable t) {
		super(message, t);
	}
}

