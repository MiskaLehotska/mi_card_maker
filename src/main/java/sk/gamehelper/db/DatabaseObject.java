package sk.gamehelper.db;

import java.util.Map;

import sk.gamehelper.config.AccessibleContext;

public abstract class DatabaseObject<T> {

	protected String databaseTable;
	protected String identifier;

	private Database database;

	public DatabaseObject() {
		this.database = AccessibleContext.getBean(Database.class);
	}

	public T selectById(Long id) {
		return setByData(database.select()
			.from(databaseTable)
			.where(identifier, id)
			.asMap());
	}

	protected abstract T setByData(Map<String, Object> data);

}
