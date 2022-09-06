package sk.gamehelper.services;

import static sk.gamehelper.db.Table.CATEGORY_ENUM;
import static sk.gamehelper.db.Table.COIN_ENUM;
import static sk.gamehelper.db.Table.RARITY_ENUM;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sk.gamehelper.db.Database;
import sk.gamehelper.helpers.CMap;

@Component
public class EnumService {

	@Autowired
	private Database db;

	public List<CMap> getCategoryEnum(){
		return db.select()
			.from(CATEGORY_ENUM)
			.where("b_valid")
			.asList();
	}

	public List<CMap> getCoinEnum(){
		return db.select()
			.from(COIN_ENUM)
			.where("b_valid")
			.asList();
	}

	public List<CMap> getRarityEnum(){
		return db.select()
			.from(RARITY_ENUM)
			.where("b_valid")
			.asList();
	}
}
