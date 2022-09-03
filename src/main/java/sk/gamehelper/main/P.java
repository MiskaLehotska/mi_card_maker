package sk.gamehelper.main;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import sk.gamehelper.dao.MagicItem;

public class P extends JPanel {
	private JTextField textField;
	private JTextField textField_1;

	
	/**
	 * Create the panel.
	 */
	public P() {
		setLayout(null);
		
		JLabel lblTitle = new JLabel("Title");
		lblTitle.setBounds(12, 12, 70, 15);
		add(lblTitle);
		
		textField = new JTextField();
		textField.setBounds(83, 10, 114, 19);
		add(textField);
		textField.setColumns(10);
		
		JLabel lblRarity = new JLabel("Rarity");
		lblRarity.setBounds(12, 56, 70, 15);
		add(lblRarity);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"Common", "Uncommon", "Rare", "Very rare", "Legendary"}));
		comboBox.setBounds(83, 51, 114, 24);
		add(comboBox);
		
		JTextArea textArea = new JTextArea();
		textArea.setBounds(12, 185, 202, 134);
		add(textArea);
		
		JLabel lblDescription = new JLabel("Description");
		lblDescription.setBounds(12, 158, 81, 15);
		add(lblDescription);
		
		JLabel lblCategory = new JLabel("Category");
		lblCategory.setBounds(232, 56, 70, 15);
		add(lblCategory);
		
		JComboBox comboBox_1 = new JComboBox();
		comboBox_1.setModel(new DefaultComboBoxModel(new String[] {"Armor", "Staff", "Ring", "Scroll", "Weapon", "Wonderous item"}));
		comboBox_1.setBounds(324, 51, 114, 24);
		add(comboBox_1);
		
		JLabel lblPrice = new JLabel("Price");
		lblPrice.setBounds(12, 102, 70, 15);
		add(lblPrice);
		
		textField_1 = new JTextField();
		textField_1.setBounds(83, 100, 114, 19);
		add(textField_1);
		textField_1.setColumns(10);
		
		JComboBox comboBox_2 = new JComboBox();
		comboBox_2.setModel(new DefaultComboBoxModel(new String[] {"Gold", "Silver", "Copper", "Electrum", "Platinum"}));
		comboBox_2.setBounds(209, 97, 93, 24);
		add(comboBox_2);
		
		JLabel lblAttunement = new JLabel("Attunement");
		lblAttunement.setBounds(232, 12, 93, 15);
		add(lblAttunement);
		
		JComboBox comboBox_3 = new JComboBox();
		comboBox_3.setModel(new DefaultComboBoxModel(new String[] {"false", "true"}));
		comboBox_3.setBounds(324, 7, 114, 24);
		add(comboBox_3);

		JButton btnSave = new JButton("Save");
		btnSave.setBounds(279, 230, 117, 25);
		btnSave.addActionListener(e -> {
			MagicItem item = new MagicItem();
			item.setTitle(textField.getText());
			item.setAttunement(Boolean.parseBoolean(comboBox_3.getSelectedItem().toString()));
			item.setCategoryId(1L);
			item.setCoinId(1L);
			item.setRarityId(3L);
			item.setPrice(Integer.parseInt(textField_1.getText()));
			item.setDescription(textArea.getText());
			item.insert();
		});
		
		add(btnSave);

	}
}
