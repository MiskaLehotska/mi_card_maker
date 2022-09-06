package sk.gamehelper.exceptions;

public class RecordAlreadyExists extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public RecordAlreadyExists(String message) {
		super(message);
	}
}
