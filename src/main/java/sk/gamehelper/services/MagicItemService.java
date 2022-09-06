package sk.gamehelper.services;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import sk.gamehelper.config.AccessibleContext;
import sk.gamehelper.dao.MagicItem;
import sk.gamehelper.db.Database;
import sk.gamehelper.db.Table;
import sk.gamehelper.exceptions.ExistingMagicItemTitleException;
import sk.gamehelper.helpers.CMap;

@Component
public class MagicItemService {

	Database db = AccessibleContext.getBean(Database.class);

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

		if (itemsWithSameTitle.isEmpty()) {
			item.insert();
		} else {
			throw new ExistingMagicItemTitleException();
		}
	}

}
