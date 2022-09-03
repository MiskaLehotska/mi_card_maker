package sk.gamehelper.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

import sk.gamehelper.config.AccessibleContext;
import sk.gamehelper.helpers.CMap;

public abstract class DatabaseObject<T> {

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

	public void insert() {
		String write = Timestamp.valueOf(LocalDateTime.now()).toString();
		jdbcTemplate.update(constructInsertStatement(write));

		// TODO: figure out how to get last valid record and directly set it into the model
		setByData(
		database.select()
			.from(databaseTable)
			.where("t_write", write)
//			.limit(1)
			.asMap());
	}

	private String constructInsertStatement(String write) {
		CMap data = getAsDbRow();
		data.remove(identifier);
		data.remove("u_uid_id");
		data.put("d_to", "infinity");
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
				value = "'" + value + "'";
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

	
	protected abstract T setByData(CMap data);

	protected abstract CMap getAsDbRow();
}
