package project.clup.exceptions;

public class BadReservationException extends Exception {
	private static final long serialVersionUID = 1L;

	public BadReservationException(String message) {
		super(message);
	}
}