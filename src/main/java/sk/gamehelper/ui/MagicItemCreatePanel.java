package sk.gamehelper.ui;

import static sk.gamehelper.ui.SimpleComponentCreator.createBasicButton;
import static sk.gamehelper.ui.SimpleComponentCreator.createBasicComboBox;
import static sk.gamehelper.ui.SimpleComponentCreator.createBasicLabel;
import static sk.gamehelper.ui.SimpleComponentCreator.createBasicTextArea;
import static sk.gamehelper.ui.SimpleComponentCreator.createBasicTextField;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import sk.gamehelper.config.AccessibleContext;
import sk.gamehelper.helpers.CMap;
import sk.gamehelper.services.EnumService;
import sk.gamehelper.services.MagicItemService;

public class MagicItemCreatePanel extends JPanel {

	private static final long serialVersionUID = 7797469423579293767L;
	private static final Color WHITE = new Color(238, 238, 236);
	private static final Font DIALOG_PLAIN_14 = new Font("Dialog", Font.PLAIN, 14);

	private static List<CMap> categoryEnum;
	private static List<CMap> rarityEnum;
	private static List<CMap> coinEnum;

	private MagicItemService magicItemService;
	private FieldValidator validator;
	private JLabel titleLabel;
	private JLabel categoryLabel;
	private JLabel rarityLabel;
	private JLabel priceLabel;
	private JLabel descriptionLabel;
	private JLabel attunementLabel;
	private JTextField titleField;
	private JTextField priceField;
	private JTextArea descriptionArea;
	private JComboBox<String> categoryComboBox;
	private JComboBox<String> rarityComboBox;
	private JComboBox<String> coinComboBox;
	private JComboBox<String> attunementComboBox;
	private JButton createButton;
	private JButton exportButton;
	private JButton resetButton;

	private Image image;

	// init enums into the cache
	static {
		loadEnums();
	}

	/**
	 * Create the panel.
	 */
	public MagicItemCreatePanel(Image image) {		
		this.magicItemService = AccessibleContext.getBean(MagicItemService.class);
		this.image = image;
		this.validator = new FieldValidator();

		setLayout(null);
		initializeComponents();
	}

	@Override
	  protected void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    // image, x-os, y-os
	    g.drawImage(image, -20, -52, null);
	}

	private void initializeComponents() {
		initializeLabels();
		initializeTextFields();
		initializeTextAreas();
		initializeComboBoxes();
		initializeButtons();
	}

	private void initializeLabels() {
		// labels
		titleLabel = createBasicLabel("title_label", "MAGIC ITEM NAME", WHITE);
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel.setBounds(172, 12, 214, 37);
		add(titleLabel);

		categoryLabel = createBasicLabel("category_label", "CATEGORY", WHITE);
		categoryLabel.setBounds(50, 133, 99, 15);
		add(categoryLabel);
		
		rarityLabel = createBasicLabel("rarity_label", "RARITY", WHITE);
		rarityLabel.setBounds(50, 170, 70, 15);
		add(rarityLabel);

		attunementLabel = createBasicLabel("attunement_label", "REQ. ATTUNEMENT ?", WHITE);
		attunementLabel.setBounds(287, 133, 156, 15);
		add(attunementLabel);

		priceLabel = createBasicLabel("price_label", "PRICE", WHITE);
		priceLabel.setBounds(287, 170, 70, 15);
		add(priceLabel);
		
		descriptionLabel = createBasicLabel("description_label", "DESCRIPTION", WHITE);
		descriptionLabel.setBounds(51, 218, 121, 15);
		add(descriptionLabel);
	}

	private void initializeTextFields() {
		titleField = createBasicTextField("title", DIALOG_PLAIN_14, SwingConstants.CENTER, 10);
		titleField.setBounds(50, 56, 460, 37);
		add(titleField);

		priceField = createBasicTextField("price", DIALOG_PLAIN_14, JTextField.RIGHT, 10);
		priceField.setBounds(336, 165, 70, 25);
		add(priceField);
	}

	private void initializeTextAreas() {
		descriptionArea = createBasicTextArea("description", DIALOG_PLAIN_14, true, true);
		descriptionArea.setBounds(46, 245, 464, 331);
		add(descriptionArea);
	}
	
	private void initializeComboBoxes() {
		categoryComboBox = createBasicComboBox("category_options", getEnumNames(categoryEnum));
		categoryComboBox.setBounds(137, 128, 121, 24);
		add(categoryComboBox);

		rarityComboBox = createBasicComboBox("rarity_options", getEnumNames(rarityEnum));
		rarityComboBox.setBounds(137, 165, 121, 24);
		add(rarityComboBox);

		coinComboBox = createBasicComboBox("coin_optinos", getEnumNames(coinEnum));
		coinComboBox.setBounds(410, 165, 100, 24);
		add(coinComboBox);

		attunementComboBox = createBasicComboBox("attunement_options", "No", "Yes");
		attunementComboBox.setBounds(448, 128, 63, 25);
		add(attunementComboBox);
	}

	private List<String> getEnumNames(List<CMap> enumList) {
		return enumList.stream()
			.map(e -> e.getString("name"))
			.collect(Collectors.toList());
	}

	private void initializeButtons() {
		createButton = createBasicButton("create_button", "CREATE", this::createMagicItemAction);
		createButton.setBounds(61, 598, 88, 25);
		add(createButton);

		exportButton = createBasicButton("export_button", "EXPORT", null);
		exportButton.setBounds(240, 598, 88, 25);
		add(exportButton);

		resetButton = createBasicButton("reset_button", "RESET", this::resetFieldsAction);
		resetButton.setBounds(410, 598, 88, 25);
		add(resetButton);
	}

	private void createMagicItemAction(ActionEvent actionEvent) {
		// validate text fields
		validator.validateRequiredFields(titleField, priceField, descriptionArea);

		// gather values, resolve and prepare insert data
		CMap data = new CMap(
			"title", titleField.getText(),
			"description", descriptionArea.getText(),
			"attunement", "Yes".equals(attunementComboBox.getSelectedItem()) ? true : false,
			"price", Double.valueOf(priceField.getText()).intValue(),
			"category_id", getEnumIdBySelectedComboBoxValue(categoryEnum, categoryComboBox),
			"rarity_id", getEnumIdBySelectedComboBoxValue(rarityEnum, rarityComboBox),
			"coin_id", getEnumIdBySelectedComboBoxValue(coinEnum, coinComboBox)
		);

		// check data
		System.out.println(data);

		try {
			magicItemService.createMagicItem(data);
		} catch (RuntimeException e) {
			// create dialog
		}
	}

	private Long getEnumIdBySelectedComboBoxValue(List<CMap> enumList, JComboBox<String> comboBox) {
		return enumList.stream()
			.filter(e -> e.getString("name").equals(comboBox.getSelectedItem()))
			.map(e -> e.getLong("id"))
			.findFirst()
			.get();
	}

	private void resetFieldsAction(ActionEvent actionEvent) {
		// clear all the fields and reset options
		titleField.setText("");
		priceField.setText("");
		descriptionArea.setText("");
		categoryComboBox.setSelectedIndex(0);
		rarityComboBox.setSelectedIndex(0);
		coinComboBox.setSelectedIndex(0);
		attunementComboBox.setSelectedIndex(0);
	}

	// this will be cached on higher level
	private static void loadEnums() {
		EnumService enumService = AccessibleContext.getBean(EnumService.class);
		categoryEnum = enumService.getCategoryEnum();
		rarityEnum = enumService.getRarityEnum();
		coinEnum = enumService.getCoinEnum();
	}
}
