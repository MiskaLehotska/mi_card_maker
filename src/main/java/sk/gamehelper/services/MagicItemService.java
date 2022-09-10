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

	@Transactional
	public void updateMagicItem(CMap data) {
		MagicItem itemToUpdate = new MagicItem();
		itemToUpdate.setByData(data);

		itemToUpdate.update();
	}
	
	public List<CMap> searchMagicItem(QueryParams params) {
//		we are testing large amounth of data 
//		validateParamSize(params);
		
		Object priceFrom = params.removeParam("from");
		Object priceTo =  params.removeParam("to");
		validatePrice(priceFrom, priceTo);
		
		Select select = db.select().from(Table.MAGIC_ITEM);

		resolveStringParam(params, "title", select);
		resolveStringParam(params, "description", select);
		resolveIntegerParam(priceFrom, QueryOperator.GREATER_THAN_EQUAL, select);
		resolveIntegerParam(priceTo, QueryOperator.LESS_THAN_EQUAL, select);

		select.where(params);
		
		return select.asList();
	}
	
	private void validateParamSize(QueryParams params) {
		if(params.isEmpty()) {
			throw new IllegalArgumentException(MessagesLoader.resolveMessage("missingParam"));
		}
	}
	
	private void resolveStringParam(QueryParams params, String paramName, Select select) {
		Object param = params.removeParam(paramName);
		if (param != null) {
			select.where("s_" + paramName, QueryOperator.LIKE, param);
		}
	}
	
	private void resolveIntegerParam(Object paramName, QueryOperator operator, Select select) {
		if(paramName != null) {
			select.where((paramName.toString()), operator, paramName.toString());
		}
	}
	
	private void validatePrice(Object from, Object to) {
		Integer valueFrom = (Integer) from;
		Integer valueTo = (Integer) to;

		if ((valueFrom != null && valueTo != null) && (valueFrom < valueTo && valueFrom < 0)) {
			throw new IllegalArgumentException(MessagesLoader.resolveMessage("priceValidation", from, to));
		}
	}
	
}
