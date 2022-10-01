package sk.gamehelper.db;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import sk.gamehelper.helpers.CMap;
import sk.gamehelper.helpers.QueryParams;

// TODO: add documentation with hints
public final class Select {

	private static final Logger logger = Logger.getAnonymousLogger();

	private JdbcTemplate jdbcTemplate;
	private StringBuilder selectBuilder;
	private StringBuilder whereBuilder;
	private boolean isWhereClauseApplied;
	private boolean distinctAlreadyApplied;
	private int requestedColumnsCount;
	private int limit;
	private Object orderByColumn;
	private OrderByDirection direction;
	private RowMapper<CMap> cMapRowMapper;
	private ColumnNameTranslator fieldTranslator;

	Select(JdbcTemplate jdbcTemplate, RowMapper<CMap> rowMapper, String... columns) {
		this.jdbcTemplate = jdbcTemplate;
		this.selectBuilder = new StringBuilder("SELECT ");
		this.whereBuilder = new StringBuilder();
		this.requestedColumnsCount = columns.length;
		this.cMapRowMapper = rowMapper;
		this.direction = OrderByDirection.ASC;
		this.fieldTranslator = new ColumnNameTranslator();
		select(columns);
	}

	// TODO: test this
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

	public Select distinct() {
		if (distinctAlreadyApplied) {
			return this;
		}
		distinctAlreadyApplied = true;
		selectBuilder.insert(7, "DISTINCT ");
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

	public Select where(QueryParams queryParams) {
		String field;
		Object value;
		for (Map.Entry<String, Object> entry : queryParams.getQueryEntries()) {
			value = entry.getValue();
			field = fieldTranslator.translateToColumnName(entry.getKey(), value);

			where(field, value);
		}
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
			valuesJoiningStream = valuesJoiningStream
					.map(val -> val.replace('\n', ' '))
					.map(val -> val.replace("'", "\'"))
					.map(val -> val.replace('"', '\''))
					.map(val -> "\"".concat(val).concat("\""));
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

	public Join leftJoin(String table) {
		return new Join(table, Join.LEFT);
	}

	public Join leftOuterJoin(String table) {
		return new Join(table, Join.LEFT_OUTER);
	}

	public Join rightJoin(String table) {
		return new Join(table, Join.RIGHT);
	}

	public Join rightOuterJoin(String table) {
		return new Join(table, Join.RIGHT_OUTER);
	}

	public class Join {

		static final String JOIN = " JOIN ";
		static final String INNER = " INNER";
		static final String LEFT = " LEFT";
		static final String RIGHT = " RIGHT";
		static final String LEFT_OUTER = " LEFT OUTER";
		static final String RIGHT_OUTER = " RIGHT OUTER";

		public Join(String table) {
			this(table, ""); // INNER omitted for select logs clarity
		}

		public Join(String table, String joinType) {
			selectBuilder.append(joinType);
			selectBuilder.append(JOIN);
			selectBuilder.append(table);
		}

		public Select on(String leftColumn, String rightColumn) {
			selectBuilder.append(" ON ");
			selectBuilder.append(leftColumn);
			selectBuilder.append(" = ");
			selectBuilder.append(rightColumn);
			return Select.this;
		}

		public Select on(String leftColumn, String rightColumn, String... moreColumns) {
			on(leftColumn, rightColumn);

			int moreColumnsLength = moreColumns.length;
			if (moreColumnsLength != 0 && moreColumnsLength % 2 == 0) {
				for (int i = 0, j = 1; j < moreColumnsLength; i++, j++) {
					selectBuilder.append(" AND ");
					selectBuilder.append(moreColumns[i]);
					selectBuilder.append(" = ");
					selectBuilder.append(moreColumns[j]);
				}
			} 
			// add some exception if moreColumns is not empty?
			return Select.this;
		}
	}

	public Select limit(int howMany) {
		this.limit = howMany;
		return this;
	}
	
	public enum OrderByDirection {
		ASC(" ASC"),
		DESC(" DESC");

		private String direction;

		private OrderByDirection(String direction) {
			this.direction = direction;
		}

		public String getDirection() {
			return this.direction;
		}
	}

	public Select orderBy(Object column) {
		return orderBy(column, OrderByDirection.ASC);
	}

	// TODO: validate object column type against string or integer
	public Select orderBy(Object column, OrderByDirection direction) {
		this.orderByColumn = column;
		this.direction = direction;
		return this;
	}

	// TODO: add wherePreparedStatement() - dynamic values
	// TODO: if it if nothing returns, let it do it without throwing an exception but an empty map
	public CMap asMap() {
		appendRemainingParts();
		logSelect(selectBuilder);

		try {
			return jdbcTemplate.queryForObject(selectBuilder.toString(), cMapRowMapper);
		} catch (EmptyResultDataAccessException emptyResult) {
			return new CMap();
		}
	}

	// TODO: if it if nothing returns, let it do it without throwing an exception but an empty list
	public List<CMap> asList() {
		appendRemainingParts();
		logSelect(selectBuilder);

		try {
			return jdbcTemplate.query(selectBuilder.toString(), cMapRowMapper);
		} catch (DataAccessException dae) {
			return Collections.emptyList();
		}
	}

	public Boolean asBoolean() {
		appendRemainingParts();
		logSelect(selectBuilder);
		return queryForObject(Boolean.class);
	}

	public Long asLong() {
		appendRemainingParts();
		logSelect(selectBuilder);
		return queryForObject(Long.class);
	}

	public String asString() {
		appendRemainingParts();
		logSelect(selectBuilder);
		return queryForObject(String.class);
	}

	private void appendRemainingParts() {
		appendWhereStatements();

		if (orderByColumn != null) {
			appendOrderBy();
		}

		if (limit > 0) {
			appendLimit();
		}
	}

	private void appendWhereStatements() {
		selectBuilder.append(whereBuilder);
	}

	private void appendOrderBy() {
		selectBuilder.append(" ORDER BY ");
		selectBuilder.append(orderByColumn);
		selectBuilder.append(direction.getDirection());
	}

	private void appendLimit() {
		selectBuilder.append(" LIMIT ");
		selectBuilder.append(limit);
	}

	private <T> T queryForObject(Class<T> classObj) {
		// 0 means all and above 1 is also impossible to parse as one object
		if (requestedColumnsCount != 1) {
			throw new IllegalArgumentException("Exactly one column must appear in the"
				+ " select clause to directly ask for its value in this manner.");
		}
//		return jdbcTemplate.queryForObject(sql, rowMapper); // could use for StringMap with values converted
		try {
			return jdbcTemplate.queryForObject(selectBuilder.toString(), classObj);
		} catch (EmptyResultDataAccessException dae) {
			return null;
		}
	}

	private static void logSelect(CharSequence select) {
		logger.info("executing: " + select);
	}

	@Override
	public String toString() {
		appendRemainingParts();
		return this.selectBuilder.toString();
	}
}