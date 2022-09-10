package sk.gamehelper.helpers;

import java.util.Map;
import java.util.Set;

public class QueryParams {

	private CMap params;

	public QueryParams() {
		this.params = new CMap();
	}

	public Object getParam(String name) {
		return params.get(name);
	}

	public Set<String> getParamNames() {
		return params.keySet();
	}

	public void addParam(String name, Object value) {
		params.put(name, value);
	}

	public Object removeParam(String name) {
		return params.remove(name);
	}

	public Set<Map.Entry<String, Object>> getQueryEntries() {
		return this.params.entrySet();
	}
	
	public String getAsString(String paramName) {
		return params.getString(paramName);
	}
	
	public Integer getAsInteger(String paramName) {
		return params.getInteger(paramName);
	}
}
