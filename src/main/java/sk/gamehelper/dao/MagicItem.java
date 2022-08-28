package sk.gamehelper.dao;

import java.util.Map;
import java.util.UUID;

import sk.gamehelper.db.DatabaseObject;

public class MagicItem extends DatabaseObject<MagicItem> {

	private Long id;
	private UUID uuid;
	private String title;
	private String description;
	private Long categoryId;
	private Long rarityId;
	private Integer price;
	private Long coinId;
	private Boolean attunement;
	//TODO: add last three paramethers - d_from, d_to, t_write

	public MagicItem() {
		this.databaseTable = "card.t_magic_item";
		this.identifier = "n_id";
	}

	@Override
	protected MagicItem setByData(Map<String, Object> data) {
		this.id = (Long) data.get("n_id");
		this.uuid = (UUID) data.get("u_uid_id");
		this.title = (String) data.get("s_title");
		this.description = (String) data.get("s_description");
		this.categoryId = (Long) data.get("n_category_id");
		this.rarityId = (Long) data.get("n_rarity_id");
		this.price = (Integer) data.get("n_price");
		this.coinId = (Long) data.get("n_coin_id");
		this.attunement = (Boolean) data.get("b_attunement");
		
		return this;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public Long getRarityId() {
		return rarityId;
	}

	public void setRarityId(Long rarityId) {
		this.rarityId = rarityId;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public Long getCoinId() {
		return coinId;
	}

	public void setCoinId(Long coinId) {
		this.coinId = coinId;
	}

	public Boolean getAttunement() {
		return attunement;
	}

	public void setAttunement(Boolean attunement) {
		this.attunement = attunement;
	}

}
