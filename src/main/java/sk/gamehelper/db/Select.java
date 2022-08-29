package sk.gamehelper.db;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.jdbc.core.JdbcTemplate;

// TODO: add documentation with hints
public final class Select {

	private static final Logger logger = Logger.getAnonymousLogger();

	private JdbcTemplate jdbcTemplate;
	private StringBuilder selectBuilder;
	private StringBuilder whereBuilder;
	private boolean isWhereClauseApplied;
	private boolean distinctAlreadyApplied;
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

	// TODO: how to apply distinct? on one column or on every column?
	public Select distinct() {
		if (distinctAlreadyApplied) {
			return this;
		}
		this.distinctAlreadyApplied = true;
		// TODO: apply distinct
		return this;
	}

	public Select from(String tableName) {
		selectBuilder.append(" FROM ");
		selectBuilder.append(tableName);
		return this;
	}

	public Select where(String column, Object value) {
		return where(column, QueryOperator.EQUALS, value);
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

	public Select where(String column, QueryOperator queryOperator, Object value) {
		appendWhereClauseConnector();
		whereBuilder.append(column);
		whereBuilder.append(queryOperator.applyOperationOnValue(value));
		return this;
	}

	// NOTE: na between treba pouzit where(String customWhere) nakolko sa to asi nebude pouzivat
	public <V> Select whereIn(String column, List<V> values) {
		if (values.isEmpty()) {
			// TODO: log that the method is not going to do anything on empty values
			return this;
		}
		if (values.size() < 2) {
			// TODO: log that the method is using basic equality where (or do something else?)
			return where(column, values.get(0));
		}

		appendWhereClauseConnector();
		whereBuilder.append(column);
		whereBuilder.append(" IN (");

		Stream<String> valuesJoiningStream = values.stream().map(String::valueOf);
		if (values.get(0) instanceof CharSequence) {
			valuesJoiningStream = valuesJoiningStream.map(val -> "'".concat(val).concat("'"));
		}
		whereBuilder.append(valuesJoiningStream.collect(Collectors.joining(",")));
		whereBuilder.append(")");
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
		return new Join(table);
	}

	public class Join {

		public Join(String table) {
			selectBuilder.append(" JOIN " + table);
		}

		public Select on(String leftColumn, String rightColumn) {
			selectBuilder.append(" ON ");
			selectBuilder.append(leftColumn);
			selectBuilder.append(" = ");
			selectBuilder.append(rightColumn);
			return Select.this;
		}
	}

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
	
	@Override
	public String toString() {
		return this.selectBuilder.append(this.whereBuilder).toString();
	}
}