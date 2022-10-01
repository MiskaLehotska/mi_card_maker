package sk.gamehelper.helpers;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

public class MessagesLoader {

	private static final Logger logger = Logger.getAnonymousLogger();	
	private static final Properties property = new Properties();
	
	private MessagesLoader() {
		throw new IllegalStateException("This class was not designed to be instantiated");
	}

	static {
		try {
			property.load(MessagesLoader.class.getClassLoader().getResourceAsStream("messages.properties"));
			logger.info("Messages have been loaded.");
		} catch(IOException e){
			logger.severe(e.getMessage());
		}
	}

	public static String resolveMessage(String propertyName, Object ...objects) {
		String message = property.getProperty(propertyName);
		if (message == null) {
			return propertyName;
		}
		if (objects.length != 0) {
			for (int i = 0; i < objects.length; i++) {
				message = message.replace("{" + i + "}", objects[i].toString());
			}
		}
		return message;
	}
}
