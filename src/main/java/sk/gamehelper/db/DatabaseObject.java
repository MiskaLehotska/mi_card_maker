package sk.gamehelper.db;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import org.springframework.jdbc.core.JdbcTemplate;

import sk.gamehelper.config.AccessibleContext;
import sk.gamehelper.db.Select.OrderByDirection;
import sk.gamehelper.helpers.CMap;

public abstract class DatabaseObject<T> {

	private static final Logger logger = Logger.getAnonymousLogger();

	private static final String INFINITY = Timestamp.valueOf(LocalDateTime.of(9999, 12, 31, 11, 59, 59)).toString();

	protected String databaseTable;
	protected String identifier;

	private Database database;
	private JdbcTemplate jdbcTemplate;

	protected DatabaseObject() {
		this.database = AccessibleContext.getBean(Database.class);
		this.jdbcTemplate = AccessibleContext.getBean(JdbcTemplate.class);
	}

	public T selectById(Long id) {
		return setByData(database.select()
			.from(databaseTable)
			.where(identifier, id)
			.asMap());
	}

	public void insert() {
		String insertStatement = constructInsertStatement();
		logger.info(insertStatement);
		jdbcTemplate.update(insertStatement);

		// get this record from db and set generated fields
		setByData(database.select()
			.from(databaseTable)
			.orderBy(identifier, OrderByDirection.DESC)
			.limit(1)
			.asMap());
	}

	private String constructInsertStatement() {
		String write = getCurrentTimestamp().toString();

		CMap data = getAsDbRow();
		data.remove(identifier);
		data.put("u_uid_id", UUID.randomUUID().toString());
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
			// TODO: centralize this transformation
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
		// invalidate existing record
		delete();
		// insert new record with new data and ID
		insert();
	}

	private String constructUpdateStatement() {
		String write = getCurrentTimestamp().toString();
		CMap data = getAsDbRow();

		StringBuilder update = new StringBuilder("UPDATE ");
		update.append(databaseTable);
		update.append(" SET d_to = '");
		update.append(write);
		update.append("', t_write = '");
		update.append(write);
		update.append("' WHERE ");
		update.append(identifier);
		update.append(" = ");
		update.append(data.getLong(identifier));

		return update.toString();
	}

	public void delete() {
		String updateStatement = constructUpdateStatement();
		logger.info(updateStatement);
		jdbcTemplate.update(updateStatement);
	}

	private Timestamp getCurrentTimestamp() {
		return Timestamp.valueOf(LocalDateTime.now());
	}

	protected abstract T setByData(CMap data);

	protected abstract CMap getAsDbRow();
}
