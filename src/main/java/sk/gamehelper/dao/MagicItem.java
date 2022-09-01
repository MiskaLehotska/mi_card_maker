package sk.gamehelper.dao;

import java.util.UUID;

import sk.gamehelper.db.DatabaseObject;
import sk.gamehelper.helpers.CMap;

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
	public MagicItem setByData(CMap data) {
		this.id = data.getLong("n_id");
		this.uuid = data.getUUID("u_uid_id");
		this.title = data.getString("s_title");
		this.description = data.getString("s_description");
		this.categoryId = data.getLong("n_category_id");
		this.rarityId = data.getLong("n_rarity_id");
		this.price = data.getInteger("n_price");
		this.coinId = data.getLong("n_coin_id");
		this.attunement = data.getBoolean("b_attunement");

		return this;
	}

	@Override
	protected CMap getAsDbRow() {
		CMap map = new CMap();
		map.put("n_id", this.id);
		map.put("u_uid_id", this.uuid);
		map.put("s_title", this.title);
		map.put("s_description", this.description);
		map.put("n_category_id", this.categoryId);
		map.put("n_rarity_id", this.rarityId);
		map.put("n_price", this.price);
		map.put("n_coin_id", this.coinId);
		map.put("b_attunement", this.attunement);

		return map;
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

	@Override
	public String toString() {
		return "MagicItem [id=" + id + ", uuid=" + uuid + ", title=" + title + ", description=" + description
				+ ", categoryId=" + categoryId + ", rarityId=" + rarityId + ", price=" + price + ", coinId=" + coinId
				+ ", attunement=" + attunement + "]";
	}

}
