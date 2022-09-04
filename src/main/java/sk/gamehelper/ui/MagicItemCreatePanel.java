package sk.gamehelper.ui;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Color;

public class MagicItemCreatePanel extends JPanel {
	private JTextField txtFeMessageStones;
	private JTextField txtFe;
	private Image image;
	/**
	 * Create the panel.
	 */
	public MagicItemCreatePanel(Image image) {
		this.image = image;
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
		add(textArea);
		
		JButton btnCreate = new JButton("CREATE");
		btnCreate.setBounds(61, 598, 88, 25);
		add(btnCreate);
		
		JButton btnExport = new JButton("EXPORT");
		btnExport.setBounds(240, 598, 88, 25);
		add(btnExport);
		
		JButton btnReset = new JButton("RESET");
		btnReset.setBounds(410, 598, 88, 25);
		add(btnReset);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"Armor", "Staff", "Weapon", "Scroll"}));
		comboBox.setBounds(137, 128, 121, 24);
		add(comboBox);
		
		JComboBox comboBox_1 = new JComboBox();
		comboBox_1.setModel(new DefaultComboBoxModel(new String[] {"Common", "Uncommon", "Rare", "Very rare", "Legendary"}));
		comboBox_1.setBounds(137, 165, 121, 24);
		add(comboBox_1);
		
		txtFe = new JTextField();
		txtFe.setFont(new Font("Dialog", Font.PLAIN, 14));
		txtFe.setBounds(336, 165, 70, 25);
		add(txtFe);
		txtFe.setColumns(10);
		
		JComboBox comboBox_2 = new JComboBox();
		comboBox_2.setModel(new DefaultComboBoxModel(new String[] {"Gold", "Silver", "Platinum", "Copper", "Electrum"}));
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

	}

	@Override
	  protected void paintComponent(Graphics g) {

	    super.paintComponent(g);
	    // image, x-os, y-os
	       g.drawImage(image, -20, -52, null);
	}
}
