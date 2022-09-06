package sk.gamehelper.helpers;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import org.springframework.stereotype.Component;

@Component
public class LoadProperties {
	
	private static final Logger logger = Logger.getAnonymousLogger();
	
	private static Properties property = new Properties();
	
	static {
		try {
			property.load(LoadProperties.class.getClassLoader().getResourceAsStream("messages.properties"));
			logger.info("Property was loaded.");
		} catch(IOException e){
			logger.info("EXCEPTION: " + e.toString());;
		}
	}
	
	
	
	
	
	public static String resolveMessage(String propertyName, Object ...objects) {
		String resolvedPropertyName = property.getProperty(propertyName);
		for(Object object: objects) {
			int indexOfOpeningBracket = resolvedPropertyName.indexOf("{");
			int indexOfClosingBracket = resolvedPropertyName.indexOf("}");
			resolvedPropertyName = resolvedPropertyName.substring(0, indexOfOpeningBracket).concat(object.toString()).concat(resolvedPropertyName.substring(indexOfClosingBracket +1));
		}
		return resolvedPropertyName;
	}

	
	
	
	
}
