package sk.gamehelper.db;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Lazy
@Component
public class TransactionTestComponent {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private UUID uuid;

	public TransactionTestComponent() {
		this.uuid = UUID.randomUUID();
	}

	public UUID getUUID() {
		return this.uuid;
	}

	public String getDbUUID() {
		List<Map<String, Object>> result = jdbcTemplate.queryForList("SELECT u_uid_id FROM test.t_test ORDER BY 1 DESC");
		return String.valueOf(result.get(0).get("u_uid_id"));
	}

	@Transactional
	public void insertWithException() {
		jdbcTemplate.update("INSERT INTO test.t_test (u_uid_id, s_text) VALUES (?,?)",
			this.uuid, "Unit test - FAILED");

		throw new RuntimeException("transaction failure");
	}
}
