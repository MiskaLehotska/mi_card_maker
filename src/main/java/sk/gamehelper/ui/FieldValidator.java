package sk.gamehelper.ui;

import java.util.function.Predicate;

import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

public class FieldValidator {

	private static final String REQUIRED_FIELD_EMPTY_MESSAGE = " field is required and it cannot be empty!";
	private static final String FIELD_TYPE_MISMATCH_MESSAGE = " field does not have required type of value!";

	public void validateRequiredField(JTextComponent field) {
		validateRequiredField(field, REQUIRED_FIELD_EMPTY_MESSAGE);
	}

	public void validateRequiredField(JTextComponent field, String errorMessage) {
		if (field.getText().trim().isEmpty()) {
			String throwMessage;
			switch (errorMessage) {
			case REQUIRED_FIELD_EMPTY_MESSAGE:
				throwMessage = field.getName() + REQUIRED_FIELD_EMPTY_MESSAGE;
				break;
			default:
				throwMessage = errorMessage;
			}
			throw new RequiredFieldValidationError(throwMessage);
		}
	}

	public void validateTextComponentValueLength(JTextComponent field, int numberOfCharacters) {
		
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
