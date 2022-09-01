package sk.gamehelper.helpers;

public class InconvertableTypeException extends RuntimeException{
	
	public InconvertableTypeException(Object value, Class<?> type) {
		super( value +  " cannot be converted to " + type);
	}

}
