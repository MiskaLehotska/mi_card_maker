package sk.gamehelper.config;

import java.util.UUID;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.transaction.TransactionManager;

import sk.gamehelper.db.TransactionTestComponent;

@TestInstance(Lifecycle.PER_CLASS)
class AppConfigTest {

	private AnnotationConfigApplicationContext context;

	@BeforeAll
	void initAppContext() {
		this.context = new AnnotationConfigApplicationContext(AppConfig.class);
		context.registerShutdownHook();
	}

	@DisplayName("DB DataSource initialization")
	@Test
	void datasourceTest() {
		SingleConnectionDataSource dataSource = (SingleConnectionDataSource) context.getBean(DataSource.class);
		Assertions.assertNotNull(dataSource, "DataSource bean should not be null");

		String actualUsername = dataSource.getUsername();
		String actualPassword = dataSource.getPassword();
		String actualURL = dataSource.getUrl();

		Assertions.assertEquals("postgres", actualUsername, "username should be postgres but was " + actualUsername);
		Assertions.assertEquals("postgres", actualPassword, "password should be pwd but was " + actualPassword);
		Assertions.assertEquals("jdbc:postgresql://localhost:5432/dd", actualURL);
	}

	@DisplayName("DB JdbcTemplate initialization")
	@Test
	void jdbcTemplateTest() {
		JdbcTemplate jdbcTemplate = context.getBean(JdbcTemplate.class);
		Assertions.assertNotNull(jdbcTemplate, "JdbcTemplate bean should not be null");
		Assertions.assertEquals(-1, jdbcTemplate.getMaxRows()); // -1 is the default value
		Assertions.assertInstanceOf(SingleConnectionDataSource.class, jdbcTemplate.getDataSource(), 
			"dataSource object used by jdbcTemplate should be of type SingleConnectionDataSource");
	}

	@DisplayName("DB TransactionManager initialization")
	@Test
	void txManagerTest() {
		DataSourceTransactionManager txManager = (DataSourceTransactionManager) context.getBean(TransactionManager.class);
		Assertions.assertNotNull(txManager, "TransactionManager bean should not be null");
		Assertions.assertInstanceOf(SingleConnectionDataSource.class, txManager.getDataSource(), 
				"dataSource object used by jdbcTemplate should be of type SingleConnectionDataSource");
	}

	@Disabled("until local database is available")
	@DisplayName("DB Connection test")
	@Test
	void connectionTest() {
		Assertions.assertDoesNotThrow(() -> context.getBean(DataSource.class).getConnection(),
			"If the database server is up and running - there should be no exception thrown");
	}

	@Disabled("until local database is available")
	@DisplayName("DB rollback from invalid transaction")
	@Test
	void txRollbackTest() {
		TransactionTestComponent txTest = context.getBean(TransactionTestComponent.class);
		UUID uuid = txTest.getUUID();
		try {
			txTest.insertWithException();
		} catch (Exception ex) {
			// do nothing
		}
		String uuidDb = txTest.getDbUUID();
		Assertions.assertFalse(uuid.toString().equals(uuidDb), 
			"The transaction should be rollbacked! Insert id: " + uuid + " Select id: " + uuidDb);
	}

	@AfterAll
	void closeContext() {
		context.close();
	}

}
