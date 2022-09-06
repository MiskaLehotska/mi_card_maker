package sk.gamehelper.db;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class ColumnNameTranslator {

	public String translateToColumnName(String field, Object value) {
		if (value instanceof CharSequence) {
			return "s_" + field;
		} else if (value instanceof Number) {
			return "n_" + field;
		} else if (value instanceof LocalDateTime || value instanceof LocalDate) {
			if ("write".equals(field)) {
				return "t_" + field;
			}
			return "d_" + field;
		} else if(value instanceof Boolean) {
			return "b_" + field;
		} else if (value instanceof UUID) {
			return "u_" + field;
		}
		return field;
	}

	public String removeColumnPrefix(String columnName) {
		return columnName.substring(2);
	}
}
