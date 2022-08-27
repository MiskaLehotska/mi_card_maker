package sk.gamehelper.db;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SelectApiExamples {

	@Autowired
	private Database db;
	
	public String getCoinNameById(Integer coinId) {
		return db.select("s_name")
			.from("card_enum.e_coin")
			.where("n_id", coinId)
			.asString();
	}

	public boolean isCoinValid(Integer coinId) {
		return db.select("b_valid")
			.from("card_enum.e_coin")
			.where("n_id", coinId)
			.asBoolean();
	}

	public List<Map<String, Object>> getCoins() {
		return db.select()
			.from("card_enum.e_coin")
			.asList();
	}

	public Map<String, Object> getCoinById(Integer coinId) {
		return db.select("n_id", "s_name", "s_acronym")
			.from("card_enum.e_coin")
			.where("n_id", coinId)
			.asMap();
	}

	public Long getCoinIdByCategoryName(String category) {		
		Select innerSelect = db.select("n_id")
			.from("card_enum.e_category")
			.where("s_name", category);

		return db.select("n_id")
			.from("card_enum.e_coin")
			.where("n_id", innerSelect)
			.asLong();
	}

	public Map<String, Object> getRarityWithResolvedAcronymByRarityName(String rarity) {
		return db.select("A.n_id", "A.s_name", "A.n_coin_id", "B.s_acronym")
			.from("card_enum.e_rarity A")
			.join("card_enum.e_coin B")
			.on("A.n_coin_id", "B.n_id")
			.where("A.s_name", rarity)
			.asMap();
	}
}
