package sk.gamehelper.ui;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import sk.gamehelper.config.AppConfig;

public class MainUI {

	public static void main(String... strings) {
		SwingUtilities.invokeLater(() -> {
			try {
				@SuppressWarnings("resource")
				AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
				context.registerShutdownHook();

				JFrame f = new JFrame();
				f.setSize(555, 680);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setLocationRelativeTo(null);
				f.add(new MagicItemCreatePanel(ImageIO.read(
						MainUI.class.getClassLoader().getResourceAsStream("images/background_images/gladiator.jpg")), WindowType.CREATE));
				f.setVisible(true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

}
