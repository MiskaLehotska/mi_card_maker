package sk.gamehelper.config;

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

@TestInstance(Lifecycle.PER_CLASS) // toto je na to, aby sme mohli pouzivat v tejto metode nestaticku instancnu premennu a aby sa drzala pocas behu vsetkych testov
class AppConfigTest {

	private AnnotationConfigApplicationContext context;

	// @BeforeAll logicky - co sa ma vykonat pred tym ako zacnu unit testy oznacene @Test anotaciou
	@BeforeAll
	void initAppContext() {
		this.context = new AnnotationConfigApplicationContext(AppConfig.class);
		context.registerShutdownHook();
	}

	// @Test hovori, ze ide o unit test, ktory ma testovaci framework JUnit 5 (jupiter) spustit
	// @Display hovori, ze ako sa ma v tom lavom panely, ked to bude zelene zobrazit popis testu (nemusi to tu vobec byt a ostane tam nazov metody ako popis)
	@DisplayName("DB DataSource initialization")
	@Test
	void datasourceTest() {
		SingleConnectionDataSource dataSource = (SingleConnectionDataSource) context.getBean(DataSource.class);
		Assertions.assertNotNull(dataSource, "DataSource bean should not be null");

		String actualUsername = dataSource.getUsername();
		String actualPassword = dataSource.getPassword();
		String actualURL = dataSource.getUrl();

		Assertions.assertAll(
			() -> "postgres".equals(actualUsername),
			() -> "postgres".equals(actualPassword),
			() -> "jdbc:postgresql://localhost:5432/dd".equals(actualURL));
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

	@Disabled("until the database is available for this project")
	@DisplayName("DB Connection test")
	@Test
	void connectionTest() {
		Assertions.assertDoesNotThrow(() -> context.getBean(DataSource.class).getConnection(),
			"If the database server is up and running - there should be no exception thrown");
	}

	// nakoniec sa vykona tato metoda po vsetkych testoch...uzavrie sa kontext springu pre tieto testy
	@AfterAll
	void closeContext() {
		context.close();
	}

}
