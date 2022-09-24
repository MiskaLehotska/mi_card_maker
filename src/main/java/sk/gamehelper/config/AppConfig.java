package sk.gamehelper.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;

@ComponentScan(basePackages = "sk.gamehelper")
@PropertySource("classpath:/db/connection.properties")
@EnableTransactionManagement
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

//		ComboPooledDataSource dataSource = new ComboPooledDataSource();
//		dataSource.setUser(username);
//		dataSource.setPassword(password);
//		dataSource.setJdbcUrl(url);
//		dataSource.setMaxPoolSize(2);
//		dataSource.setMinPoolSize(1);
//		
//		System.out.println(dataSource.getMaxConnectionAge());
//		System.out.println(dataSource.getMaxIdleTime());
//		System.out.println(dataSource.getMaxStatements());
//		System.out.println(dataSource.getMaxStatementsPerConnection());
		
		MysqlConnectionPoolDataSource dataSource = new MysqlConnectionPoolDataSource();
		dataSource.setUrl(url);
		dataSource.setUser(username);
		dataSource.setPassword(password);

		return dataSource;
//		return connectionPool;
	}
}
