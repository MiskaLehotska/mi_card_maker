package sk.gamehelper.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@ComponentScan(basePackages = "sk.gamehelper")
@PropertySource("classpath:/db/connection.properties")
public class AppConfig {

	@Bean
	public DataSource getDatabaseDataSource(
			@Value("${url}") String url, 
			@Value("${username}") String username, 
			@Value("${password}") String password) {

		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);

		return dataSource;
	}

}
