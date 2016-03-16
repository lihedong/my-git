package boco.ips.init;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import boco.ips.db.DbInitializer;
import boco.ips.redis.RedisInitializer;
import boco.ips.util.ConfigUtil;
import boco.ips.view.ViewInitializer;

/**
 * 系统初始化
 */
@Service
public class AppInitializer implements InitializingBean {
	
	private static Logger logger = Logger.getLogger(AppInitializer.class);

	@Autowired
	private DbInitializer db;
	
	@Autowired
	private RedisInitializer redis;
	
	@Autowired
	private ViewInitializer view;
	
	public void afterPropertiesSet() throws Exception {
		logger.info("<<<<< begin init data and view");
		
		// 装载SQL配置
		db.loadAll();
		
		// 装载指标
		if(ConfigUtil.loadPropertyBoolean("startup.init.redis")) {
			redis.loadAll();
		}
		
		// 装载视图
		if(ConfigUtil.loadPropertyBoolean("startup.init.view")) {
			view.loadAll();
		}
		
		logger.info(">>>>> end init data and view");
	}

}
