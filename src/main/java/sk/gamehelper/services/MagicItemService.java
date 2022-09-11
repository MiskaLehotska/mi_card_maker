package sk.gamehelper.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import sk.gamehelper.dao.MagicItem;
import sk.gamehelper.db.Database;
import sk.gamehelper.db.QueryOperator;
import sk.gamehelper.db.Select;
import sk.gamehelper.db.Table;
import sk.gamehelper.exceptions.RecordAlreadyExists;
import sk.gamehelper.helpers.CMap;
import sk.gamehelper.helpers.MessagesLoader;
import sk.gamehelper.helpers.QueryParams;

@Component
public class MagicItemService {

	@Autowired
	private Database db;

	@Transactional
	public void createMagicItem(CMap data) {
		MagicItem mi = new MagicItem();
		mi.setByData(data);
		validateTitle(mi);

		mi.insert();
	}

	// Tato metoda moze zobrat nejaky
	private void validateTitle(MagicItem item) {
		String title = item.getTitle();
		List<CMap> itemsWithSameTitle = db.select("s_title")
			.from(Table.MAGIC_ITEM)
			.where("s_title", title)
			.asList();

		if (!itemsWithSameTitle.isEmpty()) {
			throw new RecordAlreadyExists(MessagesLoader.resolveMessage("magicItemTitleValidation", title));
		}
	}

	// TODO:
	// ak robim update na rovnakom zazname na UI, lebo som si nedal refresh, tak mi to dokaze urobit update
	// 2 krat na tom istom uz teraz neplatnom zazname... to je neziaduce.. na UI to opravim, ale tu treba
	// dorobit to, ze ked idem updatnut tento zaznam, musi byt podla idcka validny...ak nie je, znamena to,
	// ze nemozem updatnut stary, neplatny zaznam.. a to by malo vyhodit chybu..

	// tak isto by som skontroloval pred tym, ze ci mi vobec dojde ID-cko ...po setByData() mozes zavoat getId()
	// ak ti to da null, tak hod chybu, ze je to povinne pole pre update
	// na taketo chyby co je tu alebo vyssie mozu byt kludne aj nejake spravy tahane z messages.properties, to
	// je ale na tebe ako to poriesis..

	// dalej, tu by som tiez pouzil uz existujucu validaciu na titulok, pokial aktualny titulok, co chcem zmenit 
	// je iny ako ten pred tym... 
	// stary titulok, co bude zmeneny si vies getnut z databazy podla ID zaznamu co ti dojde na vstupe a novy si vies po
	// volani setByData() uz priamo vypytat z modelu metodou getTitle() ...to je titulok, na ktory to chceme
	// zmenit... a len v tom pripade, ze je tento titulok iny ako to co je tam povodne, validujem tento novy
	// titulok.. inak nie, pretoze stary titulok moze ostat aj pri update (mozem menit len description), no ak
	// zmenim titulok, nesmiem pridat titulok, ktory uz existuje
	@Transactional
	public void updateMagicItem(CMap data) {
		MagicItem itemToUpdate = new MagicItem();
		itemToUpdate.setByData(data);

		itemToUpdate.update();
	}

	public List<CMap> searchMagicItem(QueryParams params) {
//		we are testing large amount of data 
//		validateParamSize(params);

		Integer priceFrom = params.getAsInteger("from");
		params.removeParam("from");
		Integer priceTo =  params.getAsInteger("to");
		params.removeParam("to");
		validatePriceRange(priceFrom, priceTo);

		Select select = db.select("A.n_id, A.s_title", "A.s_description", "A.n_price", 
				"A.b_attunement", "A.d_from", "A.d_to", "A.t_write", "B.s_category_name",
				"C.s_rarity_name", "D.s_coin_name")
			.from("v_magic_item A")
			.join("ve_category B")
			.on("A.n_category_id", "B.n_id")
			.join("ve_rarity C")
			.on("A.n_rarity_id", "C.n_id")
			.join("ve_coin D")
			.on("A.n_coin_id", "D.n_id");

		applyWhereStatement("A.s_title", null, params.removeParam("title"), select);
		applyWhereStatement("A.s_description", null, params.removeParam("description"), select);
		applyWhereStatement("A.n_coin_id", null, params.removeParam("coin_id"), select);
		applyWhereStatement("A.n_price", QueryOperator.GREATER_THAN_EQUAL, priceFrom, select);
		applyWhereStatement("A.n_price", QueryOperator.LESS_THAN_EQUAL, priceTo, select);

		return select.where(params)
			.asList();
	}

	private void validateParamSize(QueryParams params) {
		if(params.isEmpty()) {
			throw new IllegalArgumentException(MessagesLoader.resolveMessage("missingParam"));
		}
	}

	private void applyWhereStatement(String columnName, QueryOperator operator, Object value, Select select) {
		if (value != null) {
			QueryOperator op = operator;
			if (value instanceof CharSequence) {
				op = op != null ? op : QueryOperator.LIKE;
				select.where(columnName, op, value);
			} else if (value instanceof Number) {
				op = op != null ? op : QueryOperator.EQUALS;
				select.where(columnName, op, value);
			}
		}
	}

	private void validatePriceRange(Integer from, Integer to) {
		if (from != null && to != null) {
			if (from > to) {
				throw new IllegalArgumentException(MessagesLoader.resolveMessage("priceValidation", from, to));
			}
		}
	}	
}
