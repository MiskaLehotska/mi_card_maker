package sk.gamehelper.main;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import sk.gamehelper.config.AppConfig;

public class AppRunner {

	public static void main(String[] args) {

		// creates spring application context
		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {

		}
	}
}
