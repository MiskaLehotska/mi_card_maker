package sk.gamehelper.exceptions;

public class ExceedingFieldLength extends RuntimeException {

	private static final long serialVersionUID = -6939959242006157844L;

	public ExceedingFieldLength(String message) {
		super(message);
	}

}
