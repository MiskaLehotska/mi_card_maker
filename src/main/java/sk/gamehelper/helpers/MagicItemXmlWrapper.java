package sk.gamehelper.helpers;

import java.util.ArrayList;
import java.util.List;

public class MagicItemXmlWrapper {

	private List<MagicItemDTO> data;

	public MagicItemXmlWrapper() {
		data = new ArrayList<>();
	}

	public MagicItemXmlWrapper(List<CMap> list) {
		this();

		for (CMap map : list) {
			data.add(new MagicItemDTO(map));
		}
	}

	public void add(MagicItemDTO item) {
		data.add(item);
	}

	public void addMagicItems(List<MagicItemDTO> list) {
		data.addAll(list);
	}
}
