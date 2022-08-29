package sk.gamehelper.main;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import sk.gamehelper.config.AppConfig;
import sk.gamehelper.dao.MagicItem;
import sk.gamehelper.db.Database;
import sk.gamehelper.db.QueryOperator;

public class AppRunner {

	public static void main(String[] args) {

		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {

			// i need database object here to perform explicit selects
			Database db = context.getBean(Database.class);

			// give me coin id by coin acronym (gp)
			Long coinId = db.select("n_id")
				.from("card_enum.e_coin")
				.where("s_acronym", QueryOperator.LIKE, "g")
				.asLong();

			// give me category id 1
			Long categoryId = db.select("n_id")
				.from("card_enum.e_category")
				.where("n_id", QueryOperator.LESS_THAN, 2)
				.asLong();
			
			// give me rarity id by rarity name (Uncommon)
			Long rarityId = db.select("n_id")
				.from("card_enum.e_rarity")
				.where("s_name", "Uncommon") // QueryOperator.EQUALS je default
				.asLong();

			// create magic item and set the desired values
			MagicItem item = new MagicItem();
			item.setAttunement(true);
			item.setTitle("Test item");
			item.setDescription("this is a test description");
			item.setPrice(100);
			item.setCoinId(coinId);
			item.setCategoryId(categoryId);
			item.setRarityId(rarityId);

			// insert item object to database and retrieve the ID of the new record
			Long newId = item.insert();
			
			// select magic item record by ID just retrieved from the insert operation
			System.out.println(new MagicItem().selectById(newId));
		}
	}
}
