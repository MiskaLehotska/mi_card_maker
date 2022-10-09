package sk.gamehelper.db;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import sk.gamehelper.config.AppConfig;

@TestInstance(Lifecycle.PER_CLASS)
class InitialLoadTest {

	private AnnotationConfigApplicationContext context;

	@BeforeAll
	void initAppContext() {
		this.context = new AnnotationConfigApplicationContext(AppConfig.class);
		context.registerShutdownHook();
	}
	
	@Test
	@DisplayName("Test for valid data after initial load based on timestamp")
	void loadTest() {
		// TODO: dorobit test na to, ci v den zapisu novych dat do databazy sa tam 
		// nachadzaju naozaj len tie, ktorych t_write nie je skor ako den tohto noveho loadu
	}
}
