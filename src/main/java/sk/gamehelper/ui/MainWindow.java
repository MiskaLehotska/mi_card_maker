package sk.gamehelper.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import sk.gamehelper.config.AccessibleContext;
import sk.gamehelper.helpers.CMap;
import sk.gamehelper.helpers.FormatType;
import sk.gamehelper.helpers.QueryParams;
import sk.gamehelper.services.EnumService;
import sk.gamehelper.services.MagicItemService;

/**
 * Dufam, ze uz v zivote nanapisem takyto sajrajt
 * Dufam, ze uz v zivote nebudem musiet pisat GUI
 * @author martin
 */
public class MainWindow {
	private static final Color WHITE = new Color(238, 238, 236);
	private static List<CMap> categoryEnum;
	private static List<CMap> rarityEnum;
	private static List<CMap> coinEnum;

	private List<String> currencyEnumOptions;
	private List<String> categoryEnumOptions;
	private List<String> rarityEnumOptions;

	private JPanel contentPane;
	private MagicItemPanel createPanel;
	private MagicItemPanel updatePanel;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JComboBox<String> comboBox;
	private JComboBox<String> comboBox_1;
	private JComboBox<String> comboBox_2;
	private JComboBox<String> comboBox_3;
	private JComboBox<String> exportComboBox;
	private JButton btnNewButton;
	private JButton btnSearch;
	private JButton exportButton;
	private JLabel lblTitle;
	private JLabel lblDescription;
	private JLabel lblPriceFrom;
	private JLabel lblPriceTo;
	private JLabel lblCurrency;
	private JLabel lblCategory;
	private JLabel lblRarity;
	private JLabel lblAttunement;
	private JLabel recordCounter;
	private JPanel panel;
	private JScrollPane scrollPane;
	private MagicItemService magicItemService;
	private final JPopupMenu popupMenu = new JPopupMenu();
    private JMenuItem updateItem;
	private JTable table;
	private JMenuItem deleteItem;

	private JFrame frame;

	private final String[] exportOptions = {"excel", "csv", "json", "xml"};
	
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
		this.magicItemService = AccessibleContext.getBean(MagicItemService.class);

		frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 1183, 719);

		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Options");
		JMenuItem create = new JMenuItem("Create magic item");
		create.addActionListener(this::windowedCreateAction);
		menu.add(create);
		menuBar.add(menu);

		frame.setJMenuBar(menuBar);
		contentPane = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			  protected void paintComponent(Graphics g) {
			    super.paintComponent(g);
			    Image image = null;
				try {
					image = ImageIO.read(
							MainWindow.class.getClassLoader().getResourceAsStream(
									"images/background_images/dnd-forest-queen-and-forest-giant-apyjbqbu2g3tn821-apyjbqbu2g3tn821.jpg"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			    // image, x-os, y-os
			    g.drawImage(image, -130, -160, null);
			}
		};
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		initializeComponents();

		frame.setContentPane(contentPane);

		panel = new JPanel();
		panel.setBounds(158, 0, 1025, 661);
		panel.setLayout(new GridLayout(1, 0, 0, 0));
		scrollPane = new JScrollPane();
		panel.add(scrollPane);

		contentPane.add(panel);

        updateItem = new JMenuItem("Update magic item");
        updateItem.addActionListener(this::windowedUpdateAction);

        deleteItem = new JMenuItem("Delete magic item");
        deleteItem.addActionListener(this::deleteAction);

        initializeScrollableTableWithMenu();

        popupMenu.add(updateItem);
        popupMenu.add(deleteItem);

		frame.setVisible(true);
	}

	private void windowedCreateAction(ActionEvent event) {
		JDialog f = new JDialog(frame, "Create Magic Item", true);
		f.setSize(555, 680);
		f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		f.setLocationRelativeTo(null);

		if (createPanel == null) {
			try {
				createPanel = new MagicItemPanel(ImageIO.read(
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
	}

	private void windowedUpdateAction(ActionEvent event) {
		JDialog f = new JDialog(frame, "Update Magic Item", true);
		f.setSize(555, 680);
		f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		f.setLocationRelativeTo(null);

		if (updatePanel == null) {
			try {
				updatePanel = new MagicItemPanel(ImageIO.read(
						MainWindow.class.getClassLoader().getResourceAsStream("images/background_images/gladiator.jpg")), WindowType.UPDATE);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		CMap tableData = getSelectedRowData();
		updatePanel.initializeValues(tableData);
		f.setResizable(false);
		f.add(updatePanel);
		f.setVisible(true);
	}

	private void deleteAction(ActionEvent event) {
		// throw confirmation dialog before delete
		CMap deletedMagicItem = getSelectedRowData();
		int selection = JOptionPane.showConfirmDialog(frame, "Delete \"" + deletedMagicItem.getString("title") + "\" ?", "Delete magic item", JOptionPane.YES_NO_OPTION);
		switch (selection) {
		case JOptionPane.YES_OPTION:
			magicItemService.deleteMagicItem(deletedMagicItem);
			break;
		default:
			break;
		}
	}

	private void initializeComponents() {
		initializeLabels();
		initializeTextFields();
		initializeComboBoxes();
		initializeButtons();
	}

	private void initializeLabels() {
		lblTitle = SimpleComponentCreator.createBasicLabel("titleLabel", "Title", WHITE);
		lblTitle.setBounds(22, 23, 70, 15);
		contentPane.add(lblTitle);

		lblDescription = SimpleComponentCreator.createBasicLabel("descLabel", "Description", WHITE);
		lblDescription.setBounds(22, 71, 114, 15);
		contentPane.add(lblDescription);

		lblPriceFrom = SimpleComponentCreator.createBasicLabel("priceFromLabel", "Price from", WHITE);
		lblPriceFrom.setBounds(22, 124, 114, 15);
		contentPane.add(lblPriceFrom);

		lblPriceTo = SimpleComponentCreator.createBasicLabel("priceToLabel", "Price to", WHITE);
		lblPriceTo.setBounds(22, 174, 70, 15);
		contentPane.add(lblPriceTo);

		lblCurrency = SimpleComponentCreator.createBasicLabel("currencyLabel", "Currency", WHITE);
		lblCurrency.setBounds(22, 225, 70, 15);
		contentPane.add(lblCurrency);

		lblCategory = SimpleComponentCreator.createBasicLabel("categoryLabel", "Category", WHITE);
		lblCategory.setBounds(22, 277, 70, 15);
		contentPane.add(lblCategory);

		lblRarity = SimpleComponentCreator.createBasicLabel("rarityLabel", "Rarity", WHITE);
		lblRarity.setBounds(22, 328, 70, 15);
		contentPane.add(lblRarity);

		lblAttunement = SimpleComponentCreator.createBasicLabel("attunementLabel", "Attunement", WHITE);
		lblAttunement.setBounds(22, 380, 92, 15);
		contentPane.add(lblAttunement);

		recordCounter = SimpleComponentCreator.createBasicLabel("recordCounterLabel", "Records: 0", WHITE);
		recordCounter.setBounds(30, 640, 110, 20);
		contentPane.add(recordCounter);
	}

	public void initializeTextFields() {
		textField = new JTextField();
		textField.setBounds(22, 87, 124, 25);
		contentPane.add(textField);
		textField.setColumns(10);

		textField_1 = new JTextField();
		textField_1.setBounds(22, 39, 124, 25);
		contentPane.add(textField_1);
		textField_1.setColumns(10);

		textField_2 = SimpleComponentCreator.createBasicTextField("price_from", new Font("Arial", Font.PLAIN, 12), SwingConstants.CENTER, 10);
		textField_2.setBounds(22, 138, 124, 25);
		textField_2.addKeyListener(new KeyAdapter() {
			@Override
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
			@Override
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
	}

	private void initializeComboBoxes() {
		currencyEnumOptions = getEnumNames(coinEnum, "coin");
		currencyEnumOptions.add(0, "");
		comboBox = SimpleComponentCreator.createBasicComboBox("currency_combo", currencyEnumOptions);
		comboBox.setBounds(22, 241, 124, 24);
		contentPane.add(comboBox);

		categoryEnumOptions = getEnumNames(categoryEnum, "category");
		categoryEnumOptions.add(0, "");
		comboBox_1 = SimpleComponentCreator.createBasicComboBox("category_combo", categoryEnumOptions);
		comboBox_1.setBounds(22, 294, 124, 24);
		contentPane.add(comboBox_1);
		
		rarityEnumOptions = getEnumNames(rarityEnum, "rarity");
		rarityEnumOptions.add(0, "");
		comboBox_2 = SimpleComponentCreator.createBasicComboBox("rarity_combo", rarityEnumOptions);
		comboBox_2.setBounds(22, 344, 124, 24);
		contentPane.add(comboBox_2);
		
		comboBox_3 = SimpleComponentCreator.createBasicComboBox("attunement_combo", "", "No", "Yes");
		comboBox_3.setBounds(22, 395, 124, 24);
		contentPane.add(comboBox_3);

		exportComboBox = SimpleComponentCreator.createBasicComboBox("exportComboBox", exportOptions);
	}

	private void initializeButtons() {
		btnNewButton = SimpleComponentCreator.createBasicButton("resetButton", "RESET", this::resetFieldsAction);
		btnNewButton.setBounds(22, 468, 92, 25);
		contentPane.add(btnNewButton);
		
		btnSearch = SimpleComponentCreator.createBasicButton("searchButton", "SEARCH", this::searchDataAction);
		btnSearch.setBounds(22, 431, 92, 25);
		contentPane.add(btnSearch);

		exportButton = SimpleComponentCreator.createBasicButton("exportButton", "Export table", this::exportAction);
		exportButton.setBounds(16, 615, 120, 25);
		contentPane.add(exportButton);
	}
	
	private void initializeScrollableTableWithMenu() {
		table = new JTable(new DefaultTableModel(new Object[] {
				"ID", "Title", "Description", "Category", "Rarity", "Price", "Coin", "Attunement" }, 0)) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		// set title and description column lengths
		table.getColumnModel().getColumn(1).setPreferredWidth(200);
		table.getColumnModel().getColumn(2).setPreferredWidth(340);
		table.getColumnModel().getColumn(3).setPreferredWidth(80);
		table.getColumnModel().getColumn(4).setPreferredWidth(60);
		table.getColumnModel().getColumn(5).setPreferredWidth(50);
		table.getColumnModel().getColumn(6).setPreferredWidth(60);
		table.getColumnModel().getColumn(7).setPreferredWidth(60);

		table.setFillsViewportHeight(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// https://stackoverflow.com/questions/3878411/jtable-how-to-add-data-without-displaying
		// column id can be selected only from model not from table:
		table.getColumnModel().removeColumn(table.getColumn("ID"));

        table.setComponentPopupMenu(popupMenu);
		scrollPane.setViewportView(table);
	}

	private CMap getSelectedRowData() {
		CMap data = new CMap();
		// if -1, no row is selected
		int row = table.getSelectedRow();
		TableModel model = table.getModel();

		data.put("id", Long.valueOf(model.getValueAt(row, 0).toString()));
		data.put("title", model.getValueAt(row, 1));
		data.put("description", model.getValueAt(row, 2));
		data.put("category", categoryEnumOptions.indexOf(model.getValueAt(row, 3)) - 1);
		data.put("rarity", rarityEnumOptions.indexOf(model.getValueAt(row, 4)) - 1);
		data.put("price", model.getValueAt(row, 5));//integer?
		data.put("coin", currencyEnumOptions.indexOf(model.getValueAt(row, 6)) - 1);
		data.put("attunement", "true".equals(model.getValueAt(row, 7).toString()) ? 1 : 0);

		return data;
	}

	private static void loadEnums() {
		EnumService enumService = AccessibleContext.getBean(EnumService.class);
		categoryEnum = enumService.getCategoryEnum();
		rarityEnum = enumService.getRarityEnum();
		coinEnum = enumService.getCoinEnum();
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

	public static List<String> getEnumNames(List<CMap> enumList, String enumName) {
		return enumList.stream()
			.map(e -> e.getString(enumName + "_name"))
			.collect(Collectors.toList());
	}

	public static Long getEnumIdBySelectedComboBoxValue(List<CMap> enumList, String enumName, JComboBox<String> comboBox) {
		return enumList.stream()
			.filter(e -> e.getString(enumName + "_name").equals(comboBox.getSelectedItem()))
			.map(e -> e.getLong("id"))
			.findFirst()
			.orElse(null);
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
		QueryParams queryParams = getValuesAsQueryParams();

		try {
			List<CMap> data = magicItemService.searchMagicItem(queryParams);
			setTableData(data);
			recordCounter.setText("Records: " + data.size());
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(frame, ex.getMessage(), "Search error", JOptionPane.WARNING_MESSAGE);
		}
	}

	private QueryParams getValuesAsQueryParams() {
		QueryParams queryParams = new QueryParams();
		if (!textField.getText().isEmpty()) {
			queryParams.addParam("description", textField.getText());
		}
		if (!textField_1.getText().isEmpty()) {
			queryParams.addParam("title", textField_1.getText());
		}
		if (!textField_2.getText().isEmpty()) {
			queryParams.addParam("from", textField_2.getText());
		}
		if (!textField_3.getText().isEmpty()) {
			queryParams.addParam("to", textField_3.getText());
		}
		if (!((String) comboBox.getSelectedItem()).isEmpty()) {
			queryParams.addParam("coin_id", getEnumIdBySelectedComboBoxValue(coinEnum, "coin", comboBox));
		}
		if (!((String) comboBox_1.getSelectedItem()).isEmpty()) {
			queryParams.addParam("category_id", getEnumIdBySelectedComboBoxValue(categoryEnum, "category", comboBox_1));
		}
		if (!((String) comboBox_2.getSelectedItem()).isEmpty()) {
			queryParams.addParam("rarity_id", getEnumIdBySelectedComboBoxValue(rarityEnum, "rarity", comboBox_2));
		}
		if (!((String) comboBox_3.getSelectedItem()).isEmpty()) {
			Boolean value = "Yes".equals(comboBox_3.getSelectedItem());
			queryParams.addParam("attunement", value);
		}
		return queryParams;
	}

	private void setTableData(List<CMap> data) {
		DefaultTableModel dataModel = (DefaultTableModel) table.getModel();
		// delete all the records
		dataModel.setRowCount(0);
		// display new records
		data.forEach(e -> dataModel.addRow(getAsTableRow(e)));
		table.setModel(dataModel);
	}

	private Object[] getAsTableRow(CMap data) {
		return new Object[] {
			data.getLong("id"),
			data.getString("title"),
			data.getString("description"),
			data.getString("category_name"),
			data.getString("rarity_name"),
			data.getInteger("price"),
			data.getString("coin_name"),
			data.getBoolean("attunement")
		};
	}

	private void exportAction(ActionEvent actionEvent) {
		int option = JOptionPane.showConfirmDialog(frame, exportComboBox, "Choose output format", JOptionPane.DEFAULT_OPTION);
		// if one closes the first window there is no point of opening the second one..
		if ((option == JOptionPane.CLOSED_OPTION)) {
			return;
		}
		int format = exportComboBox.getSelectedIndex();
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		option = fileChooser.showOpenDialog(frame);
		if (option == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			magicItemService.exportToFile(getValuesAsQueryParams(), file.getAbsolutePath(), resolveFormat(format));
		}
	}

	private FormatType resolveFormat(int format) {
		switch (format) {
		case 0:
			return FormatType.EXCEL;
		case 1:
			return FormatType.CSV;
		case 2:
			return FormatType.JSON;
		case 3:
			return FormatType.XML;
		default:
			return null;
		}
	}
}
