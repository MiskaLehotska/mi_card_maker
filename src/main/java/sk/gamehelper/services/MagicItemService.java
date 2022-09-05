package sk.gamehelper.services;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import sk.gamehelper.dao.MagicItem;
import sk.gamehelper.helpers.CMap;

@Component
public class MagicItemService {

	@Transactional
	public void createMagicItem(CMap data) {
		MagicItem mi = new MagicItem();
		mi.setByData(data);

		mi.insert();
	}
}
