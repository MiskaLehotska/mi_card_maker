package sk.gamehelper.db;

import static java.util.stream.Collectors.toList;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.jdbc.core.JdbcTemplate;

import sk.gamehelper.config.AccessibleContext;
import sk.gamehelper.db.Select.OrderByDirection;
import sk.gamehelper.helpers.CMap;
import sk.gamehelper.helpers.QueryParams;

public abstract class DatabaseObject<T> {

	private static final Logger logger = Logger.getAnonymousLogger();

	private static final String INFINITY = Timestamp.valueOf(LocalDateTime.of(9999, 12, 31, 11, 59, 59)).toString();

	protected String databaseTable;
	protected String identifier;

	private Database database;
	private JdbcTemplate jdbcTemplate;

	public DatabaseObject() {
		this.database = AccessibleContext.getBean(Database.class);
		this.jdbcTemplate = AccessibleContext.getBean(JdbcTemplate.class);
	}

	public T selectById(Long id) {
		return setByData(database.select()
			.from(databaseTable)
			.where(identifier, id)
			.asMap());
	}

	public List<T> selectByQuery(String column, Object value) {
		return selectByQuery(column, QueryOperator.EQUALS, value);
	}

	public List<T> selectByQuery(String column, QueryOperator queryOperator, Object value) {
		return selectBySelect(database.select()
			.from(databaseTable)
			.where(column, queryOperator, value));
	}

	public List<T> selectByQuery(QueryParams queryParams) {
		return selectBySelect(database.select()
			.from(databaseTable)
			.where(queryParams));
	}

	private List<T> selectBySelect(Select select) {
		return select.asList()
			.stream()
			.map(this::setByData)
			.collect(toList());
	}

	public void insert() {
		String write = Timestamp.valueOf(LocalDateTime.now()).toString();
		String insertStatement = constructInsertStatement(write);
		logger.info(insertStatement);
		jdbcTemplate.update(insertStatement);

		// get this record from db and set generated fields
		setByData(database.select()
			.from(databaseTable)
			.orderBy("n_id", OrderByDirection.DESC)
			.limit(1)
			.asMap());
	}

	private String constructInsertStatement(String write) {
		CMap data = getAsDbRow();
		data.remove(identifier);
		data.remove("u_uid_id");
		data.put("d_to", INFINITY);
		data.put("d_from", write);
		data.put("t_write", write);

		StringBuilder insert = new StringBuilder();
		insert.append("INSERT INTO ");
		insert.append(databaseTable);

		StringBuilder columns = new StringBuilder(" (");
		StringBuilder values  = new StringBuilder(" VALUES (");

		for (Map.Entry<String, Object> entry : data.entrySet()) {
			columns.append(entry.getKey());
			columns.append(",");

			Object value = entry.getValue();
			if (value instanceof CharSequence) {
				value = value.toString()
					.replace('\n', ' ')
					.replace("'", "\'")
					.replace('"', '\'');

				value = "\"" + value + "\"";
			}
			values.append(value);
			values.append(",");
		}
		columns.deleteCharAt(columns.length() - 1);
		columns.append(")");

		values.deleteCharAt(values.length() - 1);
		values.append(")");

		return insert.append(columns)
				.append(values)
				.toString();
	}

	public void update() {
		jdbcTemplate.update("UPDATE " + databaseTable + " SET "); // construct this update statement
		// UPDATE databaseTable SET column = value, column = value, column = value
		// WHERE identifier = id;
		// + do not forget to change validity dates
		// insert() -> new Item
	}

	protected abstract T setByData(CMap data);

	protected abstract CMap getAsDbRow();
}
