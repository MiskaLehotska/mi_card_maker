package sk.gamehelper.helpers;

public enum FormatType {

	EXCEL(".xlsx"), JSON(".json"), CSV(".csv"), XML(".xml");

	public final String suffix;

	private FormatType(String suffix) {
		this.suffix = suffix;

	}

	public String getSuffix() {
		return this.suffix;
	}

}
