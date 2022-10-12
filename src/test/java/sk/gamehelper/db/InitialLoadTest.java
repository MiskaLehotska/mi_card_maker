package sk.gamehelper.db;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import sk.gamehelper.config.AppConfig;
import sk.gamehelper.helpers.CMap;

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
		//mam db
		Database db = context.getBean(Database.class);
		
		LocalDateTime before = LocalDateTime.of(2022, 10, 11, 00, 00);
		
		List<CMap> list = db.select("t_write").
				from("t_magic_item")
				.asList();
	
		long count = list.stream()
				.map(e -> e.getLocalDateTime("write"))
				.filter(time -> time.isBefore(before))
				.count();
		
		Assertions.assertEquals(0, count);
	
	}
}
