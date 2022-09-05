package sk.gamehelper.main;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import sk.gamehelper.config.AppConfig;
import sk.gamehelper.dao.MagicItem;
import sk.gamehelper.db.Database;
import sk.gamehelper.db.QueryOperator;
import sk.gamehelper.db.Select.OrderByDirection;
import sk.gamehelper.helpers.CMap;
import sk.gamehelper.helpers.QueryParams;

public class AppRunner {

	public static void main(String[] args) {

		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {

			// i need database object here to perform explicit selects
			Database db = context.getBean(Database.class);

			// give me coin id by coin acronym (gp)
			Long coinId = db.select("n_id")
				.from("e_coin")
				.where("s_acronym", QueryOperator.LIKE, "g")
				.asLong();

			// give me category id 1
			Long categoryId = db.select("n_id")
				.from("e_category")
				.where("n_id", QueryOperator.LESS_THAN, 2)
				.asLong();
			
			// give me rarity id by rarity name (Uncommon)
			Long rarityId = db.select("n_id")
				.from("e_rarity")
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
			item.insert();
			Long newId = item.getId();
			System.out.println(newId);

			// select magic item record by ID just retrieved from the insert operation
			System.out.println(new MagicItem().selectById(newId));
			
			// print magic item record with resolved category and rarity
			List<CMap> magicItems = 
				db.select("A.n_id", "A.s_title", "B.s_name AS category", "A.n_price", "C.s_acronym AS currency")
					.distinct()
					.from("t_magic_item A")
					.leftJoin("e_category B")
					.on("A.n_category_id", "B.n_id")
					.join("e_coin C")
					.on("A.n_coin_id", "C.n_id")
					.where("A.n_id", QueryOperator.LESS_THAN, 3)
					.asList();

			magicItems.forEach(System.out::println);

			// select magic items where its id is one of 3, 4, or 7
			magicItems = db.select()
				.from("t_magic_item")
				.whereIn("n_id", Arrays.asList(3, 4, 7))
				.asList();
			
			// multiple columns ON clause
			List<CMap> data = db.select("A.*")
				.from("t_magic_item A")
				.join("e_coin B")
				.on("A.n_coin_id", "B.n_id", "A.n_category_id", "B.n_id")
				.asList();

			System.out.println(data.size());

			// order by and limit test - order by supports integers or strings
			System.out.println(db.select()
				.from("e_rarity")
				.where("n_id", QueryOperator.LESS_THAN, 100)
				.orderBy(1, OrderByDirection.DESC)
				.limit(3)
				.asList());
			
			// using queryParams
			QueryParams queryParams = new QueryParams();
			queryParams.addParam("rarity_id", 2);
			queryParams.addParam("coin_id", 2);

			queryParams.removeParam("coin_id");

			CMap miData = db.select()
				.from("t_magic_item")
				.where(queryParams)
				.limit(1)
				.asMap();
			
			MagicItem magicItem = new MagicItem().setByData(miData);
			System.out.println(magicItem);
		}
	}
}
