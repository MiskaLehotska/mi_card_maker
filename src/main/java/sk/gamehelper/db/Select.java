package sk.gamehelper.db;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.jdbc.core.JdbcTemplate;

// TODO: add documentation with hints
public final class Select {

	private static final Logger logger = Logger.getAnonymousLogger();

	private JdbcTemplate jdbcTemplate;
	private StringBuilder selectBuilder;
	private StringBuilder whereBuilder;
	private boolean isWhereClauseApplied;
	private int requestedColumnsCount;

	Select(JdbcTemplate jdbcTemplate, String... columns) {
		this.jdbcTemplate = jdbcTemplate;
		this.selectBuilder = new StringBuilder("SELECT ");
		this.whereBuilder = new StringBuilder();
		this.requestedColumnsCount = columns.length;
		select(columns);
	}

	// toto mi vobec nepomoze pri Join triede
	public Select(Select otherSelect) {
		this.jdbcTemplate = otherSelect.jdbcTemplate;
		this.selectBuilder = new StringBuilder(otherSelect.selectBuilder);
		this.whereBuilder = new StringBuilder(otherSelect.whereBuilder);
		this.isWhereClauseApplied = otherSelect.isWhereClauseApplied;
		this.requestedColumnsCount = otherSelect.requestedColumnsCount;
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

	// TODO: these wheres should be appended at the total end because of joins
	public Select where(String column, Object value) {
		appendWhereClauseConnector();
		whereBuilder.append(column);
		whereBuilder.append(" = ");
		if (value instanceof CharSequence) {
			value = "'" + value + "'";
		}
		whereBuilder.append(value);
		return this;
	}

	public Select where(String column, Select innerSelect) {
		appendWhereClauseConnector();
		whereBuilder.append(column);
		whereBuilder.append(" = (");
		whereBuilder.append(innerSelect.selectBuilder);
		whereBuilder.append(")");
		return this;
	}

	public Select where(String customWhere) {
		appendWhereClauseConnector();
		if (customWhere.contains("WHERE ")) {
			customWhere = customWhere.replace("WHERE ", "");
		}
		whereBuilder.append(customWhere);
		return this;
	}

	private void appendWhereClauseConnector() {
		if (isWhereClauseApplied) {
			whereBuilder.append(" AND ");
		} else {
			whereBuilder.append(" WHERE ");
			isWhereClauseApplied = true;
		}
	}

	public Join join(String table) {
		return new Join(this, table);
	}

	public class Join {

		private Select select;

		public Join(Select select, String table) {
			this.select = select;
			selectBuilder.append(" JOIN " + table);
		}

		public Select on(String leftColumn, String rightColumn) {
			selectBuilder.append(" ON ");
			selectBuilder.append(leftColumn);
			selectBuilder.append(" = ");
			selectBuilder.append(rightColumn);
			return this.select;
		}
	}

	// TODO: add QueryOperators for where clause
	// TODO: add whereIn
	// TODO: add whereBetween
	// TODO: add like support (QueryOperators)
	// TODO: never implement OR
	// TODO: add wherePreparedStatement() - dynamic values
	// TODO: if it if nothing returns, let it do it without throwing an exception

	// when getting rows as map.. it would be convenient to have some map that supports getting value as required type
	public Map<String, Object> asMap() {
		appendWhereStatements();
		logSelect(selectBuilder);
		return jdbcTemplate.queryForMap(selectBuilder.toString());
	}

	public List<Map<String, Object>> asList() {
		appendWhereStatements();
		logSelect(selectBuilder);
		return jdbcTemplate.queryForList(selectBuilder.toString());
	}

	public Boolean asBoolean() {
		appendWhereStatements();
		logSelect(selectBuilder);
		return queryForObject(Boolean.class);
	}

	public Long asLong() {
		appendWhereStatements();
		logSelect(selectBuilder);
		return queryForObject(Long.class);
	}

	public String asString() {
		appendWhereStatements();
		logSelect(selectBuilder);
		return queryForObject(String.class);
	}

	private void appendWhereStatements() {
		this.selectBuilder.append(whereBuilder);
	}

	private <T> T queryForObject(Class<T> classObj) {
		// 0 means all and above 1 is also impossible to parse as one object
		if (requestedColumnsCount != 1) {
			throw new IllegalArgumentException("Exactly one column must appear in the"
				+ " select clause to directly ask for its value in this manner.");
		}
//		return jdbcTemplate.queryForObject(sql, rowMapper); // could use for StringMap with values converted
		return jdbcTemplate.queryForObject(selectBuilder.toString(), classObj);
	}

	private static void logSelect(CharSequence select) {
		logger.info("executing: " + select);
	}
}