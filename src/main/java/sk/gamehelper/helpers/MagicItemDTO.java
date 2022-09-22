package sk.gamehelper.helpers;

/**
 * Helper container for MagicItem for successful XStream reflective parse.
 * Original MagicItem class cannot be reflectively accessed due to Spring
 * proxy object.
 * 
 * @author Miška
 */
public class MagicItemDTO {

	private String title;
	private String description;
	private Long categoryId;
	private Long rarityId;
	private Integer price;
	private Long coinId;
	private Boolean attunement;

	public MagicItemDTO(CMap data) {
		this.title = data.getString("title");
		this.description = data.getString("description");
		this.categoryId = data.getLong("category_id");
		this.rarityId = data.getLong("rarity_id");
		this.price = data.getInteger("price");
		this.coinId = data.getLong("coin_id");
		this.attunement = data.getBoolean("attunement");
	}
}
