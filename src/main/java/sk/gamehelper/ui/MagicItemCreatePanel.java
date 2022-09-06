package sk.gamehelper.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
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
	private JTextField txtFeMessageStones;
	private JTextField txtFe;
	private Image image;

	private FieldValidator validator;
	private MagicItemService magicItemService;

	private static List<CMap> categoryEnum;
	private static List<CMap> rarityEnum;
	private static List<CMap> coinEnum;

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

		JLabel lblNewLabel = new JLabel("MAGIC ITEM NAME");
		lblNewLabel.setForeground(new Color(238, 238, 236));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(172, 12, 214, 37);
		add(lblNewLabel);
		
		txtFeMessageStones = new JTextField();
		txtFeMessageStones.setHorizontalAlignment(SwingConstants.CENTER);
		txtFeMessageStones.setFont(new Font("Dialog", Font.PLAIN, 14));
		txtFeMessageStones.setBounds(50, 56, 460, 37);
//		txtFeMessageStones.setOpaque(false);
		add(txtFeMessageStones);
		txtFeMessageStones.setColumns(10);
		
		JLabel lblCategory = new JLabel("CATEGORY");
		lblCategory.setForeground(new Color(238, 238, 236));
		lblCategory.setBounds(50, 133, 99, 15);
		add(lblCategory);
		
		JLabel lblRarity = new JLabel("RARITY");
		lblRarity.setForeground(new Color(238, 238, 236));
		lblRarity.setBounds(50, 170, 70, 15);
		add(lblRarity);
		
		JLabel lblPrice = new JLabel("PRICE");
		lblPrice.setForeground(new Color(238, 238, 236));
		lblPrice.setBounds(287, 170, 70, 15);
		add(lblPrice);
		
		JLabel lblDescription = new JLabel("DESCRIPTION");
		lblDescription.setForeground(new Color(238, 238, 236));
		lblDescription.setBounds(51, 218, 121, 15);
		add(lblDescription);
		
		JTextArea textArea = new JTextArea();
		textArea.setBounds(46, 245, 464, 331);
		textArea.setFont(new Font("Dialog", Font.PLAIN, 14));
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		add(textArea);

		JComboBox comboBox = new JComboBox();
		Object[] categoryOptions = categoryEnum.stream().map(c -> c.getString("name")).toArray();
		comboBox.setModel(new DefaultComboBoxModel(categoryOptions));
		comboBox.setBounds(137, 128, 121, 24);
		add(comboBox);

		JComboBox comboBox_1 = new JComboBox();
		Object[] rarityOptions = rarityEnum.stream().map(c -> c.getString("name")).toArray();
		comboBox_1.setModel(new DefaultComboBoxModel(rarityOptions));
		comboBox_1.setBounds(137, 165, 121, 24);
		add(comboBox_1);
		
		txtFe = new JTextField();
		txtFe.setFont(new Font("Dialog", Font.PLAIN, 14));
		txtFe.setBounds(336, 165, 70, 25);
		txtFe.setHorizontalAlignment(JTextField.RIGHT);
		add(txtFe);
		txtFe.setColumns(10);
		
		JComboBox comboBox_2 = new JComboBox();
		Object[] coinOptions = coinEnum.stream().map(c -> c.getString("name")).toArray();
		comboBox_2.setModel(new DefaultComboBoxModel(coinOptions));
		comboBox_2.setBounds(410, 165, 100, 24);
		add(comboBox_2);
		
		JLabel lblRequiresAttunement = new JLabel("REQ. ATTUNEMENT ?");
		lblRequiresAttunement.setForeground(new Color(238, 238, 236));
		lblRequiresAttunement.setBounds(287, 133, 156, 15);
		add(lblRequiresAttunement);
		
		JComboBox comboBox_3 = new JComboBox();
		comboBox_3.setModel(new DefaultComboBoxModel(new String[] {"No", "Yes"}));
		comboBox_3.setBounds(448, 128, 63, 25);
		add(comboBox_3);

		JButton btnCreate = new JButton("CREATE");
		btnCreate.setBounds(61, 598, 88, 25);
		btnCreate.addActionListener(al -> {
			// validate text fields
			validator.validateRequiredFields(txtFeMessageStones, txtFe, textArea);

			// gather values, resolve and prepare insert data
			CMap data = new CMap(

				"title", txtFeMessageStones.getText(),
				"description", textArea.getText(),
				"attunement", "Yes".equals(comboBox_3.getSelectedItem()) ? true : false,
				"price", Double.valueOf(txtFe.getText()).intValue(),

				"category_id", categoryEnum.stream()
					.filter(e -> e.getString("name").equals(comboBox.getSelectedItem()))
					.map(e -> e.getLong("id"))
					.findFirst().get(),

				"rarity_id", rarityEnum.stream()
					.filter(e -> e.getString("name").equals(comboBox_1.getSelectedItem()))
					.map(e -> e.getLong("id"))
					.findFirst().get(),

				"coin_id", coinEnum.stream()
					.filter(e -> e.getString("name").equals(comboBox_2.getSelectedItem()))
					.map(e -> e.getLong("id"))
					.findFirst().get());

			// check data
			System.out.println(data);

			try {
				magicItemService.createMagicItem(data);
			} catch (RuntimeException e) {
				// if insert doesnt go well
			}
		});
		add(btnCreate);

		JButton btnExport = new JButton("EXPORT");
		btnExport.setBounds(240, 598, 88, 25);
		add(btnExport);

		JButton btnReset = new JButton("RESET");
		btnReset.setBounds(410, 598, 88, 25);
		btnReset.addActionListener(al -> {
			// clear all the fields and reset options
			txtFeMessageStones.setText("");
			txtFe.setText("");
			textArea.setText("");
			comboBox.setSelectedIndex(0);
			comboBox_1.setSelectedIndex(0);
			comboBox_2.setSelectedIndex(0);
			comboBox_3.setSelectedIndex(0);
		});
		add(btnReset);

	}

	@Override
	  protected void paintComponent(Graphics g) {

	    super.paintComponent(g);
	    // image, x-os, y-os
	       g.drawImage(image, -20, -52, null);
	}

	private static void loadEnums() {
		EnumService enumService = AccessibleContext.getBean(EnumService.class);
		categoryEnum = enumService.getCategoryEnum();
		rarityEnum = enumService.getRarityEnum();
		coinEnum = enumService.getCoinEnum();
	}
}
