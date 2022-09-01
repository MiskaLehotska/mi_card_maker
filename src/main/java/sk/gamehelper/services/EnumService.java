package sk.gamehelper.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sk.gamehelper.db.Database;
import sk.gamehelper.db.Tables;
import sk.gamehelper.helpers.CMap;

@Component
public class EnumService {

	@Autowired
	private Database db;

	public List<CMap> getCategoryEnum(){
		return db.select()
			.from(Tables.CATEGORY_ENUM)
			.where("b_valid")
			.asList();
	}

	public List<CMap> getCoinEnum(){
		return db.select()
			.from(Tables.COIN_ENUM)
			.where("b_valid")
			.asList();
	}

	public List<CMap> getRarityEnum(){
		return db.select()
			.from(Tables.RARITY_ENUM)
			.where("b_valid")
			.asList();
	}
}
