package sk.gamehelper.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@ComponentScan(basePackages = "sk.gamehelper")
@EnableTransactionManagement
@PropertySource("classpath:/db/connection.properties")
public class AppConfig {

	@Bean
	@Autowired
	public PlatformTransactionManager getTransactionManager(DataSource dataSource) {
		return new JdbcTransactionManager(dataSource);
	}

	@Bean
	@Autowired
	public JdbcTemplate getJdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	@Bean
	public DataSource getDatabaseDataSource(
			@Value("${db.url}") String url, 
			@Value("${db.username}") String username, 
			@Value("${db.password}") String password) {

		SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);

		return dataSource;
	}
}
