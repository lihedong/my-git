package boco.ips.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;


/**
 * 数据库SQL配置初始化
 */
@Service
public class DbInitializer {
	
	private Logger logger = Logger.getLogger(DbInitializer.class);

	private Map<String, SqlItem> cache = new HashMap<String, SqlItem>();
	
	/**
	 * 装载全部SQL配置
	 */
	public void loadAll() {
		logger.info("load sql");
		
		final List<SqlItem> sqlList = SqlItemReader.readPath();
		for(SqlItem sqlitem : sqlList) {
			cache.put(sqlitem.getId(), sqlitem);
		}
		logger.info("load sql completed");		
	}
	
	/**
	 * 装载单个SQL配置
	 * @param key
	 */
	public SqlItem loadOne(String key) {
		SqlItem sqlitem = null;
		if(cache.containsKey(key)) {
			sqlitem = cache.get(key);
		} else {
			sqlitem = SqlItemReader.readFile(key);
		}
		return sqlitem;
	}

}
