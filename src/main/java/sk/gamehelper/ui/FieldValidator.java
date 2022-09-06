package sk.gamehelper.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import sk.gamehelper.exceptions.ExceedingFieldLength;
import sk.gamehelper.exceptions.RequiredFieldValidationError;

public class FieldValidator {

	private static final String REQUIRED_FIELDS_EMPTY_MESSAGE = "These fields are required: ";
	private static final String FIELD_TYPE_MISMATCH_MESSAGE = " field does not have required type of value!";
	private static final String FIELD_CONTENT_TOO_LARGE = " field content is too large, max allowed length is ";

	public void validateRequiredFields(JTextComponent... fields) {
		if (fields.length == 0) {
			return;
		}
		List<String> fieldNames = new ArrayList<>();
		for (JTextComponent field : fields) {
			if (field.getText().trim().isEmpty()) {
				fieldNames.add(field.getName());
			}
		}
		if (!fieldNames.isEmpty()) {
			throw new RequiredFieldValidationError(REQUIRED_FIELDS_EMPTY_MESSAGE + fieldNames);
		}
	}

	public void validateTextComponentValueLength(JTextComponent field, int numberOfCharacters) {
		if (field.getText().trim().length() > numberOfCharacters) {
			throw new ExceedingFieldLength(FIELD_CONTENT_TOO_LARGE + numberOfCharacters + " characters.");
		}
	}

	public void validateIntegerFieldValue(JTextField field, Predicate<Integer> condition, String conditionErrorMessage) {
		try {
			boolean conditionResult = condition.test(Integer.parseInt(field.getText()));
			if (!conditionResult) {
				throw new RequiredFieldValidationError(conditionErrorMessage);
			}
		} catch (NumberFormatException nfe) {
			throw new RequiredFieldValidationError(field.getName() + FIELD_TYPE_MISMATCH_MESSAGE);
		}
	}
}
