package sk.gamehelper.ui;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import sk.gamehelper.config.AccessibleContext;
import sk.gamehelper.helpers.CMap;
import sk.gamehelper.services.EnumService;

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
	private JComboBox<String> comboBox;
	private JComboBox<String> comboBox_1;
	private JComboBox<String> comboBox_2;
	private JComboBox<String> comboBox_3;

	private JTable table;
	
	static {
		loadEnums();
	}

	/**
	 * Create the frame.
	 * @throws IOException 
	 * @throws UnsupportedLookAndFeelException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	public MainWindow() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 1183, 719);

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
			f.setResizable(false);
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
		
//		JButton btnOpenForUpdate = new JButton("update");
//		btnOpenForUpdate.addActionListener(e -> {
//				JDialog f = new JDialog(frame, "Update Magic Item", true);
////				JFrame f = new JFrame();
//				f.setSize(555, 680);
//				f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//				f.setLocationRelativeTo(null);
//
//				if (updatePanel == null) {
//					try {
//						updatePanel = new MagicItemCreatePanel(ImageIO.read(
//								MainWindow.class.getClassLoader().getResourceAsStream("images/background_images/gladiator.jpg")), WindowType.UPDATE);
//					} catch (IOException e1) {
//						e1.printStackTrace();
//					}
//				}
////				CMap data = new CMap();
////				updatePanel.initializeValues(data);
//				f.add(updatePanel);
//				f.setVisible(true);
//		});
//		btnOpenForUpdate.setBounds(22, 505, 92, 25);
//		contentPane.add(btnOpenForUpdate);
		
		textField = new JTextField();
		textField.setBounds(22, 87, 124, 25);
		contentPane.add(textField);
		textField.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setBounds(22, 39, 124, 25);
		contentPane.add(textField_1);
		textField_1.setColumns(10);
		
		//ZVALIDOVAT ABY NEBOLO FROM VACSIE AKO TO
		textField_2 = SimpleComponentCreator.createBasicTextField("price_from", new Font("Arial", Font.PLAIN, 12), SwingConstants.CENTER, 10);
		textField_2.setBounds(22, 138, 124, 25);
		textField_2.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent keyEvent) {
				int keyChar = keyEvent.getKeyChar();
				if ((keyChar == KeyEvent.VK_BACK_SPACE || keyChar == KeyEvent.VK_DELETE) || (keyChar <= '9' && keyChar >= '0')) {
					textField_2.setEditable(true);
				} else {
					textField_2.setEditable(false);
				}
			}
		});
		contentPane.add(textField_2);
		
		textField_3 = SimpleComponentCreator.createBasicTextField("price_to", new Font("Arial", Font.PLAIN, 12), SwingConstants.CENTER, 10);
		textField_3.setBounds(22, 188, 124, 25);
		textField_3.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent keyEvent) {
				int keyChar = keyEvent.getKeyChar();
				if ((keyChar == KeyEvent.VK_BACK_SPACE || keyChar == KeyEvent.VK_DELETE) || (keyChar <= '9' && keyChar >= '0')) {
					textField_3.setEditable(true);
				} else {
					textField_3.setEditable(false);
				}
			}
		});
		contentPane.add(textField_3);

		List<String> currencyEnumOptions = getEnumNames(coinEnum);
		currencyEnumOptions.add(0, "");
		comboBox = SimpleComponentCreator.createBasicComboBox("currency_combo", currencyEnumOptions);
		comboBox.setBounds(22, 241, 124, 24);
		contentPane.add(comboBox);

		List<String> categoryEnumOptions = getEnumNames(categoryEnum);
		categoryEnumOptions.add(0, "");
		comboBox_1 = SimpleComponentCreator.createBasicComboBox("category_combo", categoryEnumOptions);
		comboBox_1.setBounds(22, 294, 124, 24);
		contentPane.add(comboBox_1);
		
		List<String> rarityEnumOptions = getEnumNames(rarityEnum);
		rarityEnumOptions.add(0, "");
		comboBox_2 = SimpleComponentCreator.createBasicComboBox("rarity_combo", rarityEnumOptions);
		comboBox_2.setBounds(22, 344, 124, 24);
		contentPane.add(comboBox_2);
		
		comboBox_3 = SimpleComponentCreator.createBasicComboBox("attunement_combo", "", "No", "Yes");
		comboBox_3.setBounds(22, 395, 124, 24);
		contentPane.add(comboBox_3);
		
		JButton btnNewButton = SimpleComponentCreator.createBasicButton("resetButton", "RESET", this::resetFieldsAction);
		btnNewButton.setBounds(22, 468, 92, 25);
		contentPane.add(btnNewButton);
		
		JButton btnSearch = SimpleComponentCreator.createBasicButton("searchButton", "SEARCH", this::searchDataAction);
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
		panel.setBounds(158, 0, 1025, 661);
		contentPane.add(panel);
		panel.setLayout(new GridLayout(1, 0, 0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		panel.add(scrollPane);

		Object[][] data = new Object[][] {
//			{null, null, null, null, null, null, null}
		};
		table = new JTable(data, new Object[] {
				"Title", "Description", "Category", "Rarity", "Price", "Coin", "Attunement"
		});
		// set title and description column lengths
		table.getColumnModel().getColumn(0).setPreferredWidth(200);
		table.getColumnModel().getColumn(1).setPreferredWidth(270);

		table.setFillsViewportHeight(true);
		scrollPane.setViewportView(table);

//		System.out.println(table.getColumnModel().getColumn(0).getWidth());
		
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
	
	public static List<String> getEnumNames(List<CMap> enumList) {
		return enumList.stream()
			.map(e -> e.getString("name"))
			.collect(Collectors.toList());
	}

	public static Long getEnumIdBySelectedComboBoxValue(List<CMap> enumList, JComboBox<String> comboBox) {
		return enumList.stream()
			.filter(e -> e.getString("name").equals(comboBox.getSelectedItem()))
			.map(e -> e.getLong("id"))
			.findFirst()
			.get();
	}
	
	private void resetFieldsAction(ActionEvent actionEvent) {
		// clear all the fields and reset options
		textField.setText("");
		textField_1.setText("");
		textField_2.setText("");
		textField_3.setText("");
		comboBox.setSelectedIndex(0);
		comboBox_1.setSelectedIndex(0);
		comboBox_2.setSelectedIndex(0);
		comboBox_3.setSelectedIndex(0);
	}
	
	private void searchDataAction(ActionEvent actionEvent) {
		System.out.println("searching");
	}

}
