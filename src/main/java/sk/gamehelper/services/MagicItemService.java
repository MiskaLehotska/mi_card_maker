package sk.gamehelper.services;

import java.awt.Font;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.thoughtworks.xstream.XStream;

import net.sf.jett.transform.ExcelTransformer;
import sk.gamehelper.dao.MagicItem;
import sk.gamehelper.db.Database;
import sk.gamehelper.db.QueryOperator;
import sk.gamehelper.db.Select;
import sk.gamehelper.db.Table;
import sk.gamehelper.exceptions.RecordAlreadyExists;
import sk.gamehelper.helpers.CMap;
import sk.gamehelper.helpers.FormatType;
import sk.gamehelper.helpers.MagicItemDTO;
import sk.gamehelper.helpers.MagicItemXmlWrapper;
import sk.gamehelper.helpers.MessagesLoader;
import sk.gamehelper.helpers.QueryParams;

@Component
public class MagicItemService {

	private static final String EXPORT_TEMPLATE = "template.xlsx";

	@Autowired
	private Database db;

	@Autowired
	private Font cardFont;

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
		Long invalidId = db.select("n_id").from(Table.MAGIC_ITEM).where("n_id", id).asLong();

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
		Integer priceFrom = params.getAsInteger("from");
		params.removeParam("from");
		Integer priceTo = params.getAsInteger("to");
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

		return select.where(params).asList();
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
		if (from != null && to != null && from > to) {
			throw new IllegalArgumentException(MessagesLoader.resolveMessage("priceValidation", from, to));
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

		switch (formatType) {
		case JSON:
			exportToJsonFile(data, file);
			break;
		case CSV:
			exportToCsvFile(data, file);
			break;
		case EXCEL:
			exportToExcelFile(data, file);
			break;
		case XML:
			exportToXmlFile(data, file);
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
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		try {
			Writer fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			gson.toJson(data, fileWriter);
			fileWriter.flush();
		} catch (JsonIOException | IOException e) {
			throw new RuntimeException("error converting data to json");
		}
	}

	private void exportToCsvFile(List<CMap> data, File file) {
		String[] headers = new String[] { "Title", "Category", "Rarity", "Price", "Coin", "Attunement", "Description" };
		String csvSeparator = ";";

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			writer.write(Arrays.stream(headers).collect(Collectors.joining(";")));
			writer.newLine();
			writer.flush();

			for (CMap oneData : data) {
				StringBuilder oneLine = new StringBuilder();
				oneLine.append(oneData.getString("title")).append(csvSeparator);
				oneLine.append(oneData.getString("category_name")).append(csvSeparator);
				oneLine.append(oneData.getString("rarity_name")).append(csvSeparator);
				oneLine.append(oneData.getInteger("price")).append(csvSeparator);
				oneLine.append(oneData.getString("coin_name")).append(csvSeparator);
				oneLine.append(oneData.getBoolean("attunement")).append(csvSeparator);
				oneLine.append(oneData.getString("description")).append(csvSeparator);

				writer.write(oneLine.toString());
				writer.newLine();
				writer.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void exportToExcelFile(List<CMap> data, File file) {
		ExcelTransformer transformer = new ExcelTransformer();
		Map<String, Object> dataToExport = new HashMap<>();
		dataToExport.put("table", data);
		try {
			InputStream inputStream = MagicItemService.class.getClassLoader().getResourceAsStream(EXPORT_TEMPLATE);
			Workbook workbook = transformer.transform(inputStream, dataToExport);
			workbook.write(new BufferedOutputStream(new FileOutputStream(file)));
		} catch (InvalidFormatException | IOException e) {
			e.printStackTrace();
		}
	}

	private void exportToXmlFile(List<CMap> data, File file) {
		XStream xstream = new XStream();
		xstream.alias("magicItem", MagicItemDTO.class);
		xstream.alias("magicItems", MagicItemXmlWrapper.class);

		xstream.addImplicitCollection(MagicItemXmlWrapper.class, "data");
		String xml = xstream.toXML(new MagicItemXmlWrapper(data));

		try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
			writer.write(xml);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
