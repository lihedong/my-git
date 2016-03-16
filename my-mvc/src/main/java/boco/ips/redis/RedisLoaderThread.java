package boco.ips.redis;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

import boco.ips.util.JdbcTemplateHelper;


/**
 * Redis装载线程
 */
public class RedisLoaderThread extends Thread {
	
	private static Logger logger = Logger.getLogger(RedisLoaderThread.class);

	/** SQL列表 */
	private List<Rqlment> sqlList;
	
	/** SQL列表索引 */
	private AtomicInteger sqlIndex;
	
	private JdbcTemplate jdbcTemplate;
	
	@SuppressWarnings("rawtypes")
	private RedisTemplate redisTemplate;
	
	
	public RedisLoaderThread(List<Rqlment> list, AtomicInteger index, JdbcTemplate jdbc, @SuppressWarnings("rawtypes") RedisTemplate redis) {
		this.sqlList = list;
		this.sqlIndex = index;
		this.jdbcTemplate = jdbc;
		this.redisTemplate = redis;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		// 循环遍历SQLMENT List，直到全部SQL执行完毕
		while(true) {
			Rqlment sqlment = null;
			
			// 获取SQL
			synchronized (sqlIndex) {
				int i = sqlIndex.getAndIncrement();
				if(i < sqlList.size()) {
					sqlment = sqlList.get(i);
				} else {
					break;
				}
			}
			
			try {
				// 查询SQL
				logger.info(String.format("%s load redis %s", this.getName(), sqlment.getId()));
				List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlment.getSelectSql());
				
				List<Map<String,Object>> resultList = JdbcTemplateHelper.linkedCaseInsensitiveMapToHashMap(list);
				// 保存Redis
				redisTemplate.opsForValue().set(sqlment.getId(), resultList);
				
			} catch(Exception e) {
				logger.error("load redis " + sqlment.getId() + " error:", e);
			}
			
		}
		logger.info(this.getName() + " load redis finished");
	}
	
	
}
