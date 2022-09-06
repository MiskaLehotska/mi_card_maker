package sk.gamehelper.exceptions;

public class ExistingMagicItemTitleException extends RuntimeException {

	public ExistingMagicItemTitleException() {
		super("Magic Item with the same title already exists");
	}

}
