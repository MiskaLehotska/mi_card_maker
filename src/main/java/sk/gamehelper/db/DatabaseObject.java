package sk.gamehelper.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;

import sk.gamehelper.config.AccessibleContext;

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

	protected Long insert() {
		LocalDateTime write = LocalDateTime.now();
		jdbcTemplate.update(constructInsertStatement(write));

		return database.select(identifier)
			.from(databaseTable)
			.where("t_write", write)
			.asLong();
	}

	private String constructInsertStatement(LocalDateTime write) {
		Map<String, Object> data = getAsMap();
		data.remove(identifier);
		data.remove("d_from");
		data.remove("d_to");
		data.put("t_write", write);

		StringBuilder insert = new StringBuilder();
		insert.append("INSERT INTO ");
		insert.append(databaseTable);

		StringBuilder columns = new StringBuilder(" (");
		StringBuilder values  = new StringBuilder(" VALUES (");

		for (Map.Entry<String, Object> entry : data.entrySet()) {
			columns.append(entry.getKey());
			columns.append(",");

			values.append(entry.getValue());
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

	protected abstract T setByData(Map<String, Object> data);

	protected abstract Map<String, Object> getAsMap();
}
