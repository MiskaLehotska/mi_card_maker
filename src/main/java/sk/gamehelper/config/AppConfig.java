package sk.gamehelper.config;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

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

import sk.gamehelper.helpers.MessagesLoader;

@ComponentScan(basePackages = "sk.gamehelper")
@PropertySource("classpath:/db/connection.properties")
@EnableTransactionManagement
public class AppConfig {

	private static final Logger LOGGER = Logger.getAnonymousLogger();

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

		
		MysqlConnectionPoolDataSource dataSource = new MysqlConnectionPoolDataSource();
		dataSource.setUrl(url);
		dataSource.setUser(username);
		dataSource.setPassword(password);

		return dataSource;
	}

	@Bean
	public Font getCardFont() {
		InputStream inputStream = AppConfig.class.getClassLoader().getResourceAsStream("fonts/calligraphy/CalligraphyFLF.ttf");
		try {
			Font font = Font.createFont(Font.PLAIN, inputStream);
			GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
			boolean isRegistered = environment.registerFont(font);

			if (isRegistered) {
				LOGGER.info(() -> MessagesLoader.resolveMessage("fontRegistration", font.getFontName()));
			} else {
				LOGGER.warning("The font: " + font.getFontName() + " could not be registered within graphics environment.");
			}
			return font;
		} catch (FontFormatException | IOException e) {
			LOGGER.severe(e.getMessage());
			throw new RuntimeException("Unable to load card font");
		}
	}
}
