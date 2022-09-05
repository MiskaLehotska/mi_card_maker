package sk.gamehelper.db;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class FieldTranslator {

	public String getFieldPrefixByValueType(Object value) {
		if (value instanceof CharSequence) {
			return "s_";
		} else if (value instanceof Number) {
			return "n_";
		} else if (value instanceof LocalDateTime || value instanceof LocalDate) {
			return "d_";
		} else if(value instanceof Boolean) {
			return "b_";
		} else if (value instanceof UUID) {
			return "u_";
		}
		return "";
	}
}
