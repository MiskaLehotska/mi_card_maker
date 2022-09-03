package sk.gamehelper.dao;

import java.time.LocalDateTime;
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
	private LocalDateTime from;
	private LocalDateTime to;
	private LocalDateTime write;

	public MagicItem() {
		this.databaseTable = "card.t_magic_item";
		this.identifier = "n_id";
	}

	@Override
	public MagicItem setByData(CMap data) {
		this.id = data.getLong("id");
		this.uuid = data.getUUID("uid_id");
		this.title = data.getString("title");
		this.description = data.getString("description");
		this.categoryId = data.getLong("category_id");
		this.rarityId = data.getLong("rarity_id");
		this.price = data.getInteger("price");
		this.coinId = data.getLong("coin_id");
		this.attunement = data.getBoolean("attunement");
		this.from = data.getLocalDateTime("from");
		this.to = data.getLocalDateTime("to");
		this.write = data.getLocalDateTime("write");
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
		map.put("d_from", this.from);
		map.put("d_to", this.to);
		map.put("t_write", this.write);
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

	public LocalDateTime getFrom() {
		return from;
	}

	public void setFrom(LocalDateTime from) {
		this.from = from;
	}

	public LocalDateTime getTo() {
		return to;
	}

	public void setTo(LocalDateTime to) {
		this.to = to;
	}

	public LocalDateTime getWrite() {
		return write;
	}

	public void setWrite(LocalDateTime write) {
		this.write = write;
	}

	@Override
	public String toString() {
		return "MagicItem [id=" + id + ", uuid=" + uuid + ", title=" + title + ", description=" + description
				+ ", categoryId=" + categoryId + ", rarityId=" + rarityId + ", price=" + price + ", coinId=" + coinId
				+ ", attunement=" + attunement + "]";
	}

}
