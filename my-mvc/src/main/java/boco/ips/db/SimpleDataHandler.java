package boco.ips.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

import boco.ips.redis.RedisInitializer;

public abstract class SimpleDataHandler implements DataHandler {

	@Autowired
	protected DbInitializer dbInitializer;
	
	@Autowired
	protected RedisInitializer redisInitializer;
	
	@Autowired
	protected JdbcTemplate jdbcTemplate;
	
	@SuppressWarnings("rawtypes")
	@Autowired
	protected RedisTemplate redisTemplateModel;
}
