package sk.gamehelper.config;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

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
		DriverManagerDataSource dataSource = (DriverManagerDataSource) context.getBean(DataSource.class);
		Assertions.assertNotNull(dataSource, "DataSource bean should not be null");

		String actualUsername = dataSource.getUsername();
		String actualPassword = dataSource.getPassword();
		String actualURL = dataSource.getUrl();

		Assertions.assertAll(
			() -> "postgres".equals(actualUsername),
			() -> "postgres".equals(actualPassword),
			() -> "jdbc:postgresql://localhost:5432/dd".equals(actualURL));
	}

	@AfterAll
	void closeContext() {
		context.close();
	}

}
