package sk.gamehelper.helpers;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CMapTest {

	@DisplayName("Instantiation test with even number of args")
	@Test
	void initTest() {
		Assertions.assertDoesNotThrow(() -> new CMap("Miska", 0));
		Assertions.assertDoesNotThrow(() -> new CMap("Miska", 0, "Mato", 1, 12, 12));

		CMap map2 = new CMap("Miska", 0, "Mato", 1, 12, 12);
		int expected = 3;
		int actual = map2.keySet().size();
		Assertions.assertEquals(expected, actual);

		Assertions.assertEquals(12, map2.get("12"));

		Set<String> set = new HashSet<>();
		set.add("Miska");
		set.add("Mato");
		set.add("12");
		Assertions.assertEquals(set, map2.keySet());
	}

	@DisplayName("Instantiation test with odd number of args")
	@Test
	void initTest2() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> new CMap("Miska", 2, 4) , "Should throw incompatible exception");
	}

	@Test
	void conversionMethodsTest() {
		CMap map = new CMap(
				"Miska", 50, 
				"student", 0, 
				"vek", 59, 
				null, "false",
				56, null,
				"today", LocalDate.now(),
				"date", Date.valueOf(LocalDate.now()));

		Assertions.assertEquals("false",map.getString("null"));
		Assertions.assertEquals(false, map.getBoolean("student"));
		Assertions.assertEquals(0, map.getInteger("student"));
		Assertions.assertEquals(false, map.getBoolean("null"));
		Assertions.assertNull(map.getString("56"));
		Assertions.assertEquals(0, map.getLong("student"));
		Assertions.assertEquals(LocalDate.now(), map.getLocalDate("today"));
		Assertions.assertEquals(LocalDate.now(), map.getLocalDate("date"));
	}
}
