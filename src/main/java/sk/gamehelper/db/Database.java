package sk.gamehelper.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class Database {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public Select select(String... columns) {
		return new Select(jdbcTemplate, columns);
	}

}