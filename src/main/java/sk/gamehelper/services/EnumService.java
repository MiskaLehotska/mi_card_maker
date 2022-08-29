package sk.gamehelper.services;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sk.gamehelper.db.Database;
import sk.gamehelper.db.Tables;

@Component
public class EnumService {

	
	@Autowired
	private Database db;
	
	public List<Map<String, Object>> getCategoryEnum(){
		return db.select()
				.from(Tables.CATEGORY_ENUM)
				.where("b_valid")
				.asList();
	}
	
	public List<Map<String, Object>> getCoinEnum(){
		return db.select()
				.from(Tables.COIN_ENUM)
				.where("b_valid")
				.asList();
	}
	
	public List<Map<String, Object>> getRarityEnum(){
		return db.select()
				.from(Tables.RARITY_ENUM)
				.where("b_valid")
				.asList();
	}
}
