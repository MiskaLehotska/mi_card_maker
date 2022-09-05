package sk.gamehelper.ui;

public class RequiredFieldValidationError extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public RequiredFieldValidationError(String message) {
		super(message);
	}

}
