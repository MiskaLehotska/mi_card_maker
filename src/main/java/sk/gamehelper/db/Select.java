package sk.gamehelper.db;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.jdbc.core.JdbcTemplate;

// TODO: add documentation with hints
public class Select {

	private static final Logger logger = Logger.getAnonymousLogger();

	private JdbcTemplate jdbcTemplate;
	private StringBuilder selectBuilder;
	private boolean isWhereClauseApplied;
	private int requestedColumnsCount;

	Select(JdbcTemplate jdbcTemplate, String... columns) {
		this.jdbcTemplate = jdbcTemplate;
		this.selectBuilder = new StringBuilder("SELECT ");
		requestedColumnsCount = columns.length;
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

	// TODO: these wheres should be appended at the total end because of joins !!
	public Select where(String column, Object value) {
		appendWhereClauseConnector();
		selectBuilder.append(column);
		selectBuilder.append(" = ");
		if (value instanceof CharSequence) {
			value = "'" + value + "'";
		}
		selectBuilder.append(value);
		return this;
	}

	private void appendWhereClauseConnector() {
		if (isWhereClauseApplied) {
			selectBuilder.append(" AND ");
		} else {
			selectBuilder.append(" WHERE ");
			isWhereClauseApplied = true;
		}
	}

	public Select where(String column, Select innerSelect) {
		appendWhereClauseConnector();
		selectBuilder.append(column);
		selectBuilder.append(" = (");
		selectBuilder.append(innerSelect.selectBuilder);
		selectBuilder.append(")");
		return this;
	}

	public Join join(String table) {
		return new Join(this, table);
	}

	class Join {
		private Select select;

		public Join(Select select, String table) {
			this.select = select;
			this.select.selectBuilder.append(" JOIN " + table);
		}

		public Select on(String leftColumn, String rightColumn) {
			select.selectBuilder.append(" ON ");
			select.selectBuilder.append(leftColumn);
			select.selectBuilder.append(" = ");
			select.selectBuilder.append(rightColumn);
			return this.select;
		}
	}

	// TODO: add joins
	// TODO: add QueryOperators for where clause
	// TODO: add custom where
	// TODO: add whereIn
	// TODO: add whereBetween
	// TODO: add like support (QueryOperators)
	// TODO: never implement OR
	// TODO: add wherePreparedStatement() - dynamic values
	// TODO: if it if nothing returns, let it do it without throwing an exception

	// when getting rows as map.. it would be convenient to have some map that supports getting value as required type
	public Map<String, Object> asMap() {
		logSelect(selectBuilder);
		return jdbcTemplate.queryForMap(selectBuilder.toString());
	}

	public List<Map<String, Object>> asList() {
		logSelect(selectBuilder);
		return jdbcTemplate.queryForList(selectBuilder.toString());
	}

	public Boolean asBoolean() {
		logSelect(selectBuilder);
		return queryForObject(Boolean.class);
	}

	public Long asLong() {
		logSelect(selectBuilder);
		return queryForObject(Long.class);
	}

	public String asString() {
		logSelect(selectBuilder);
		return queryForObject(String.class);
	}

	private <T> T queryForObject(Class<T> classObj) {
		// 0 means all and above 1 is also impossible to parse as one object
		if (requestedColumnsCount != 1) {
			throw new IllegalArgumentException("Exactly one column must appear in the"
				+ " select clause to directly ask for its value in this manner.");
		}
		return jdbcTemplate.queryForObject(selectBuilder.toString(), classObj);
	}

	private static void logSelect(CharSequence select) {
		logger.info("executing: " + select);
	}
}