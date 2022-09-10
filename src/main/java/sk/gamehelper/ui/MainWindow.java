package sk.gamehelper.ui;

import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import sk.gamehelper.config.AccessibleContext;
import sk.gamehelper.config.AppConfig;
import sk.gamehelper.helpers.CMap;
import sk.gamehelper.services.EnumService;
import javax.swing.JScrollPane;
import java.awt.GridLayout;

public class MainWindow {

	private static List<CMap> categoryEnum;
	private static List<CMap> rarityEnum;
	private static List<CMap> coinEnum;

	private JPanel contentPane;
	private MagicItemCreatePanel createPanel;
	private MagicItemCreatePanel updatePanel;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	
	static {
		loadEnums();
	}

	/**
	 * Create the frame.
	 * @throws IOException 
	 */
	public MainWindow() throws IOException {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 1080, 600);

		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Options");
		JMenuItem create = new JMenuItem("Create magic item");
//		create.addActionListener(e -> System.out.println("I have chosen to create application"));
		create.addActionListener(e -> {
			JDialog f = new JDialog(frame, "Create Magic Item", true);
//			JFrame f = new JFrame();
			f.setSize(555, 680);
			f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			f.setLocationRelativeTo(null);

			if (createPanel == null) {
				try {
					createPanel = new MagicItemCreatePanel(ImageIO.read(
							MainWindow.class.getClassLoader().getResourceAsStream("images/background_images/gladiator.jpg")), WindowType.CREATE);
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

		frame.setJMenuBar(menuBar);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		frame.setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnOpenForUpdate = new JButton("update");
		btnOpenForUpdate.addActionListener(e -> {
				JDialog f = new JDialog(frame, "Update Magic Item", true);
//				JFrame f = new JFrame();
				f.setSize(555, 680);
				f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				f.setLocationRelativeTo(null);

				if (updatePanel == null) {
					try {
						updatePanel = new MagicItemCreatePanel(ImageIO.read(
								MainWindow.class.getClassLoader().getResourceAsStream("images/background_images/gladiator.jpg")), WindowType.UPDATE);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
//				CMap data = new CMap();
//				updatePanel.initializeValues(data);
				f.add(updatePanel);
				f.setVisible(true);
		});
		btnOpenForUpdate.setBounds(22, 505, 92, 25);
		contentPane.add(btnOpenForUpdate);
		
		textField = new JTextField();
		textField.setBounds(22, 87, 124, 25);
		contentPane.add(textField);
		textField.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setBounds(22, 39, 124, 25);
		contentPane.add(textField_1);
		textField_1.setColumns(10);
		
		textField_2 = new JTextField();
		textField_2.setBounds(22, 138, 124, 25);
		contentPane.add(textField_2);
		textField_2.setColumns(10);
		
		textField_3 = new JTextField();
		textField_3.setBounds(22, 188, 124, 25);
		contentPane.add(textField_3);
		textField_3.setColumns(10);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setBounds(22, 241, 124, 24);
		contentPane.add(comboBox);
		
		JComboBox comboBox_1 = new JComboBox();
		comboBox_1.setBounds(22, 294, 124, 24);
		contentPane.add(comboBox_1);
		
		JComboBox comboBox_2 = new JComboBox();
		comboBox_2.setBounds(22, 344, 124, 24);
		contentPane.add(comboBox_2);
		
		JComboBox comboBox_3 = new JComboBox();
		comboBox_3.setBounds(22, 395, 124, 24);
		contentPane.add(comboBox_3);
		
		JButton btnNewButton = new JButton("Reset");
		btnNewButton.setBounds(22, 468, 92, 25);
		contentPane.add(btnNewButton);
		
		JButton btnSearch = new JButton("Search");
		btnSearch.setBounds(22, 431, 92, 25);
		contentPane.add(btnSearch);
		
		JLabel lblTitle = new JLabel("Title");
		lblTitle.setBounds(22, 23, 70, 15);
		contentPane.add(lblTitle);
		
		JLabel lblDescription = new JLabel("Description");
		lblDescription.setBounds(22, 71, 114, 15);
		contentPane.add(lblDescription);
		
		JLabel lblPriceFrom = new JLabel("Price from");
		lblPriceFrom.setBounds(22, 124, 114, 15);
		contentPane.add(lblPriceFrom);
		
		JLabel lblPriceTo = new JLabel("Price to");
		lblPriceTo.setBounds(22, 174, 70, 15);
		contentPane.add(lblPriceTo);
		
		JLabel lblCurrency = new JLabel("Currency");
		lblCurrency.setBounds(22, 225, 70, 15);
		contentPane.add(lblCurrency);
		
		JLabel lblCategory = new JLabel("Category");
		lblCategory.setBounds(22, 277, 70, 15);
		contentPane.add(lblCategory);
		
		JLabel lblRarity = new JLabel("Rarity");
		lblRarity.setBounds(22, 328, 70, 15);
		contentPane.add(lblRarity);
		
		JLabel lblAttunement = new JLabel("Attunement");
		lblAttunement.setBounds(22, 380, 92, 15);
		contentPane.add(lblAttunement);
		
		JPanel panel = new JPanel();
		panel.setBounds(158, 0, 922, 542);
		contentPane.add(panel);
		panel.setLayout(new GridLayout(1, 0, 0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		panel.add(scrollPane);
		
		frame.setVisible(true);
	}

	private static void loadEnums() {
		EnumService enumService = AccessibleContext.getBean(EnumService.class);
		categoryEnum = enumService.getCategoryEnum();
		rarityEnum = enumService.getRarityEnum();
		coinEnum = enumService.getCoinEnum();
//		categoryEnum = Collections.emptyList();
//		rarityEnum = Collections.emptyList();
//		coinEnum = Collections.emptyList();
	}
	
	public static List<CMap> getCategoryEnum() {
		return categoryEnum;
	}
	
	public static List<CMap> getRarityEnum() {
		return rarityEnum;
	}

	public static List<CMap> getCoinEnum() {
		return coinEnum;
	}
}
