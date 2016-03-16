package boco.ips.redis;

import java.util.List;

public class Rqlment
{
	private String id;
	
	private String label;
	
	private String dataSource;
	
	private String selectSql;
	
	private List<RqlField> fieldList;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String datasource) {
		this.dataSource = datasource;
	}

	public String getSelectSql() {
		return selectSql.replaceAll("\\-\\-[^\\n]*\n", " "); // 去除SQL注释;
	}

	public void setSelectSql(String selectSql) {
		this.selectSql =selectSql;
	}

	public List<RqlField> getFieldList() {
		return fieldList;
	}

	public void setFieldList(List<RqlField> fieldList) {
		this.fieldList = fieldList;
	}

}
