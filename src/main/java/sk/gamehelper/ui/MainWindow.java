package sk.gamehelper.ui;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import sk.gamehelper.config.AppConfig;

public class MainWindow extends JFrame {

	private JPanel contentPane;
	private MagicItemCreatePanel createPanel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			try {
				@SuppressWarnings("resource")
				AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
				context.registerShutdownHook();

				MainWindow frame = new MainWindow();
				frame.setVisible(true);

			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Create the frame.
	 * @throws IOException 
	 */
	public MainWindow() throws IOException {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1080, 600);

		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Options");
		JMenuItem create = new JMenuItem("Create magic item");
//		create.addActionListener(e -> System.out.println("I have chosen to create application"));
		create.addActionListener(e -> {
			JFrame f = new JFrame();
			f.setSize(555, 680);
			f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			f.setLocationRelativeTo(null);

			if (createPanel == null) {
				try {
					createPanel = new MagicItemCreatePanel(ImageIO.read(
							MainWindow.class.getClassLoader().getResourceAsStream("images/background_images/gladiator.jpg")));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} else {
				createPanel.clear();
			}
			f.add(createPanel);
			f.setVisible(true);
		});
		menu.add(create);
		menuBar.add(menu);

		setJMenuBar(menuBar);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
	}

}
