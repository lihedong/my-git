package boco.ips.redis;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import boco.ips.util.JdbcTemplateHelper;

/**
 * Redis初始化
 */
@Service
public class RedisInitializer {
	
	private Logger logger = Logger.getLogger(RedisInitializer.class);
	
	@SuppressWarnings("rawtypes")
	@Autowired
	private RedisTemplate redisTemplateModel;

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	/**
	 * 装载全部指标
	 */
	public void loadAll() {
		logger.info("load redis");
		
		final List<Rqlment> sqlList = RqlReader.readPath();
		AtomicInteger sqlIndex = new AtomicInteger(0);
		int cpu = Runtime.getRuntime().availableProcessors();
		ExecutorService executorService = Executors.newFixedThreadPool(cpu);
		for(int i=0; i<cpu; i++) {
			executorService.execute(new RedisLoaderThread(sqlList, sqlIndex, jdbcTemplate, redisTemplateModel));
		}
		executorService.shutdown();
		try {
			executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		logger.info("load redis completed");		
	}
	
	/**
	 * 装载单个指标
	 * @param key
	 */
	@SuppressWarnings("unchecked")
	public Object loadOne(String key) {
		Rqlment sqlment = RqlReader.readFile(key);
		List<Map<String, Object>> list = null;
		if(sqlment != null) {
			try {
				// 查询SQL
				logger.info("load redis " + sqlment.getId());
				list = jdbcTemplate.queryForList(sqlment.getSelectSql());
				List<Map<String,Object>> resultList = JdbcTemplateHelper.linkedCaseInsensitiveMapToHashMap(list);
				// 保存Redis
				logger.info("save redis " + sqlment.getId());
				redisTemplateModel.opsForValue().set(key, resultList);
				
			} catch(Exception e) {
				logger.error("load redis " + sqlment.getId() + " error:", e);
			}
		}
		return list;
	}

}
