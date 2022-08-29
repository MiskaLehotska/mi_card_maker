package sk.gamehelper.db;

public enum QueryOperator {

	EQUALS(" = "),
	LESS_THAN(" < "),
	GREATER_THAN(" > "),
	NOT_EQUAL(" <> "),
	LIKE(" LIKE '%"),
	NOT_LIKE(" NOT LIKE '%");

	private String operatorValue;

	private QueryOperator(String operatorValue) {
		this.operatorValue = operatorValue;
	}

	public String getOperatorValue() {
		return this.operatorValue;
	}

	public String applyOperationOnValue(Object value) {
		switch(name()) {
		case "LIKE":
		case "NOT LIKE":
			return this.operatorValue + value + "%'";
		default:
			if (value instanceof CharSequence) {
				value = "'" + value + "'";
			}
			return this.operatorValue + value;
		}
	}
}
