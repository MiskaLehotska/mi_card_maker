package sk.gamehelper.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.springframework.lang.Nullable;

/**
 * Creates basic swing component like JTextArea or JButton.
 * Additional properties not required by methods such as bounds
 * or others, are not set here and should be set explicitely
 * from outside of this factory class.
 */
public class SimpleComponentCreator {
	
	private SimpleComponentCreator() {
		throw new IllegalArgumentException("This class was not designed to be instantiated");
	}

	public static JLabel createBasicLabel(String labelName, String text, Color foreground) {
		return createBasicLabel(labelName, text, foreground, null);
	}

	public static JLabel createBasicLabel(String labelName, String text, Color foreground, @Nullable Integer horizontalAlignment) {
		JLabel label = new JLabel(text);
		label.setName(labelName);
		label.setForeground(foreground);
		if (horizontalAlignment != null) {
			label.setHorizontalAlignment(horizontalAlignment);
		}
		return label;
	}

	public static JTextField createBasicTextField(String fieldName, Font font, int horizontalAlignment, int columns) {
		JTextField textField = new JTextField();
		textField.setName(fieldName);
		textField.setFont(font);
		textField.setHorizontalAlignment(horizontalAlignment);
		textField.setColumns(columns);

		return textField;
	}

	public static JTextArea createBasicTextArea(String areaName, Font font, boolean lineWrap, boolean wrapStyleWord) {
		JTextArea textArea = new JTextArea();
		textArea.setName(areaName);
		textArea.setFont(font);
		textArea.setLineWrap(lineWrap);
		textArea.setWrapStyleWord(wrapStyleWord);

		return textArea;
	}

	@SuppressWarnings("unchecked")
	public static <T> JComboBox<T> createBasicComboBox(String comboBoxName, List<T> options) {
		return createBasicComboBox(comboBoxName, (T[]) options.toArray());
	}

	@SafeVarargs
	public static <T> JComboBox<T> createBasicComboBox(String comboBoxName, T... options) {
		JComboBox<T> comboBox = new JComboBox<>();
		comboBox.setName(comboBoxName);
		comboBox.setModel(new DefaultComboBoxModel<>(options));

		return comboBox;
	}

	public static JButton createBasicButton(String buttonName, String text, ActionListener action) {
		JButton button = new JButton(text);
		button.setName(buttonName);
		if (action != null) {
			button.addActionListener(action);
		}

		return button;
	}
}
