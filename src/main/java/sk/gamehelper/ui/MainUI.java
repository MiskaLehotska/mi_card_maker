package sk.gamehelper.ui;

import javax.swing.SwingUtilities;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import sk.gamehelper.config.AppConfig;

public class MainUI {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			try {
				@SuppressWarnings("resource")
				AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
				context.registerShutdownHook();

				new MainWindow();

			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

}
