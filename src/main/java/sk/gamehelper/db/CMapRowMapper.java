package sk.gamehelper.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import sk.gamehelper.helpers.CMap;

@Component
public class CMapRowMapper implements RowMapper<CMap> {

	@Override
	public CMap mapRow(ResultSet rs, int rowNum) throws SQLException {
		ResultSetMetaData rsMeta = rs.getMetaData();
		int columnCount = rsMeta.getColumnCount();
		CMap mapOfColumnValues = new CMap();
		for (int i = 1; i < columnCount; i++) {
			Object value = rs.getObject(i);
			mapOfColumnValues.put(removeColumnPrefix(rsMeta.getColumnName(i)), value);
		}
		return mapOfColumnValues;
	}

	public String removeColumnPrefix(String name) {
		return name.substring(2);
	}
}
