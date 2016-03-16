package boco.ips.db;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SQL语句
 */
public class SqlItem {
	
	private String id;
	
	private String sql;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}
	
	public String prepareSql(Map<String,Object> param) {
		Pattern p = Pattern.compile("\\$\\{(.*?)\\}"); // 匹配${param}参数
		String str = sql.replaceAll("\\-\\-[^\\n]*\n", " "); // 去除SQL注释
		Matcher m = p.matcher(str);
		StringBuffer sb = new StringBuffer(); 
		while(m.find()) {
			String key = m.group(1);
			Object val = param.get(key);
			if(val ==  null) {
				m.appendReplacement(sb, "0");
			}else {
				m.appendReplacement(sb, val.toString());
			}
		}
		m.appendTail(sb);
		return sb.toString();
	}
	
}
