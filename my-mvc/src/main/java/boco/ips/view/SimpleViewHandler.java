package boco.ips.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import boco.ips.db.DbInitializer;

public abstract class SimpleViewHandler implements ViewHandler {
	
	@SuppressWarnings("rawtypes")
	@Autowired
	protected RedisTemplate redisTemplateModel;

	@SuppressWarnings("rawtypes")
	@Autowired
	protected RedisTemplate redisTemplateView;
	
	@Autowired
	protected JdbcTemplate jdbcTemplate;
	
	@Autowired
	protected FreeMarkerConfigurer freemarkerConfigurer;
	
	@Autowired
	protected DbInitializer dbInitializer;
	
	@Autowired
	protected ViewInitializer viewInitailizer;

}
