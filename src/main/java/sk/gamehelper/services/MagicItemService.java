package sk.gamehelper.services;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;

import sk.gamehelper.dao.MagicItem;
import sk.gamehelper.db.Database;
import sk.gamehelper.db.QueryOperator;
import sk.gamehelper.db.Select;
import sk.gamehelper.db.Table;
import sk.gamehelper.exceptions.RecordAlreadyExists;
import sk.gamehelper.helpers.CMap;
import sk.gamehelper.helpers.FormatType;
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
		validateTitle(mi.getTitle());

		mi.insert();
	}

	private void validateTitle(String title) {
		List<CMap> itemsWithSameTitle = db.select("s_title")
			.from(Table.MAGIC_ITEM)
			.where("s_title", title)
			.asList();

		if (!itemsWithSameTitle.isEmpty()) {
			throw new RecordAlreadyExists(MessagesLoader.resolveMessage("magicItemTitleValidation", title));
		}
	}

	@Transactional
	public void updateMagicItem(CMap data) {
		MagicItem itemToUpdate = new MagicItem().setByData(data);
		String newTitle = itemToUpdate.getTitle();
		Long id = itemToUpdate.getId();

		validateIdPresence(id);
		checkForInvalidId(id);
		validateTitleForUpdate(newTitle, id);

		itemToUpdate.update();
	}

	private void validateIdPresence(Long id) {
		if (id == null) {
			throw new IllegalStateException(MessagesLoader.resolveMessage("missingIdParameter"));
		}
	}

	private void checkForInvalidId(Long id) {
		Long invalidId = db.select("n_id")
			.from(Table.MAGIC_ITEM)
			.where("n_id", id)
			.asLong();

		if (invalidId == null) {
			throw new IllegalStateException(MessagesLoader.resolveMessage("updateInvalidRecord"));
		}
	}

	private void validateTitleForUpdate(String currentTitle, Long id) {
		String oldTitle = db.select("s_title")
			.from(Table.MAGIC_ITEM)
			.where("n_id", id)
			.asString();

		if (!currentTitle.equals(oldTitle)) {
			validateTitle(currentTitle);
		}
	}

	public List<CMap> searchMagicItem(QueryParams params) {
//		we are testing large amount of data 
//		validateParamSize(params);

		Integer priceFrom = params.getAsInteger("from");
		params.removeParam("from");
		Integer priceTo =  params.getAsInteger("to");
		params.removeParam("to");
		validatePriceRange(priceFrom, priceTo);

		Select select = db.select(
				"A.n_id",
				"A.s_title",
				"A.s_description",
				"A.n_price",
				"A.b_attunement",
				"A.d_from",
				"A.d_to",
				"A.t_write",
				"B.s_category_name",
				"C.s_rarity_name",
				"D.s_coin_name")
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

	@Transactional
	public void deleteMagicItem(CMap data) {
		MagicItem item = new MagicItem().setByData(data);
		validateIdPresence(item.getId());

		item.delete();
	}

	public void exportToFile(QueryParams queryParams, String absolutePath, FormatType formatType) {
		validateFormatType(formatType);		
		List<CMap> data = searchMagicItem(queryParams);
		File file = createFile(absolutePath, formatType);

		switch(formatType) {
		case JSON: 
			exportToJsonFile(data, file);
			break;
		default:
			break;
		}
	}

	private void validateFormatType(FormatType formatType) {
		switch (formatType) {
		case EXCEL:
		case JSON:
		case CSV:
		case XML:
			return;
		default:
			throw new IllegalArgumentException("Illegal format type");
		}
	}

	private File createFile(String absolutePath, FormatType formatType) {
		StringBuilder fullName = new StringBuilder();
		fullName.append("magic_items_export_");
		fullName.append(LocalDate.now());
		fullName.append(formatType.getSuffix());

		return new File(absolutePath, fullName.toString());
	}

	private void exportToJsonFile(List<CMap> data, File file) {
		Gson gson = new GsonBuilder()
			.setPrettyPrinting()
			.create();

		try {
			Writer fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			gson.toJson(data, fileWriter);
			fileWriter.flush();
		} catch (JsonIOException | IOException e) {
			throw new RuntimeException("error converting data to json");
		}
	}
}
