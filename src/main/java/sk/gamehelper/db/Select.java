package sk.gamehelper.db;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

class Select {

	private JdbcTemplate jdbcTemplate;
	private RowMapper<DatabaseObject<?>> rowMapper; // ?
	private StringBuilder selectBuilder;
	private boolean isWhereClauseApplied;

	Select(JdbcTemplate jdbcTemplate, String... columns) {
		this.jdbcTemplate = jdbcTemplate;
		this.selectBuilder = new StringBuilder("SELECT ");
		select(columns);
	}

	private Select select(String... columns) {
		if (columns.length != 0) {
			selectBuilder.append(String.join(", ", columns));
		} else {
			selectBuilder.append("*");
		}
		return this;
	}

	public Select from(String tableName) {
		selectBuilder.append(" FROM ");
		selectBuilder.append(tableName);
		return this;
	}

	public Select where(String column, Object value) {
		if (isWhereClauseApplied) {
			selectBuilder.append(" AND ");
		} else {
			selectBuilder.append(" WHERE ");
			isWhereClauseApplied = true;
		}
		selectBuilder.append(column);
		selectBuilder.append(" = ");
		if (value instanceof CharSequence) {
			value = "'" + value + "'";
		}
		selectBuilder.append(value);
		return this;
	}

	public Map<String, Object> asMap() {
//		jdbcTemplate.queryForMap("");
		return Collections.emptyMap();
	}

	public List<Map<String, Object>> asList() {
//		jdbcTemplate.queryForList("");
		return null;
	}
}