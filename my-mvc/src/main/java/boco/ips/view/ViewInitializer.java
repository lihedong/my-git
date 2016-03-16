package boco.ips.view;

import java.io.File;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import boco.ips.db.DbInitializer;
import boco.ips.db.SqlItem;
import boco.ips.util.SystemUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;

@Service
public class ViewInitializer {
	
	private static Logger logger = Logger.getLogger(ViewInitializer.class);
	
	@SuppressWarnings("rawtypes")
	@Autowired
	private RedisTemplate redisTemplateModel;

	@SuppressWarnings("rawtypes")
	@Autowired
	private RedisTemplate redisTemplateView;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private FreeMarkerConfigurer freemarkerConfigurer;
	
	@Autowired
	private DbInitializer dbInitializer;
	
	/**
	 * 批量初始化视图，仅限redis相关的视图
	 */
	public void loadAll() {
		logger.info("load view");
		
		File dir = new File(SystemUtil.getClassPath() + SystemUtil.CFG_VIEW_PATH);
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.isFile()) {
				String filename = file.getName();
				String viewkey = filename.substring(0, filename.lastIndexOf('.'));
				loadOneByRedis(viewkey, true);
			}
		}
		
		logger.info("load view completed");
	}
	
	/**
	 * 从redis中视图并保存到Redis
	 * @param viewkey 视图ID
	 * @param data2view 是否记录指标与视图的对应关系
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String loadOneByRedis(String viewkey, boolean data2view) {
		logger.info("load view " + viewkey);
		String result = null;
		Configuration cfg = freemarkerConfigurer.getConfiguration();
		Map<String, Object> root = new HashMap<String,Object>();
		try {
			// 读取chart模板
			Template temp = cfg.getTemplate(viewkey+".ftl");
			
			// 查询chart配置的Redis结果
			Object redisObj = temp.getCustomAttribute("redis");
			if(redisObj !=null) {
				String[] arr = redisObj.toString().split(",");
				for (int i=0; i<arr.length; i++) {
					String key = arr[i];
					Object val = redisTemplateModel.opsForValue().get(key);
					root.put(key, val);
					if(data2view) {
						ViewDataMapper.put(key, viewkey);
					}
				}
			}
						
			// 填充模板
			StringWriter writer = new StringWriter();
			temp.process(root, writer);
			result = writer.toString();
			
			// 保存redis
			redisTemplateView.opsForValue().set(viewkey, result);
			
		} catch (Exception e) {
			logger.info("load view " + viewkey + ": " + e.getMessage());
			e.printStackTrace();
		}
		return result;	
	}
	
	@SuppressWarnings("unchecked")
	public String loadOne(String viewkey, Map<String,Object> param) {
		logger.info("load view " + viewkey);
		String result = null;
		Configuration cfg = freemarkerConfigurer.getConfiguration();
		Map<String, Object> root = new HashMap<String,Object>();
		try {
			// 读取chart模板
			Template temp = cfg.getTemplate(viewkey+".ftl");
			
			// 查询chart配置的Redis结果
			Object redisObj = temp.getCustomAttribute("redis");
			if(redisObj !=null) {
				String[] arr = redisObj.toString().split(",");
				for (int i=0; i<arr.length; i++) {
					String key = arr[i];
					Object val = redisTemplateModel.opsForValue().get(key);
					root.put(key, val);
					ViewDataMapper.put(key, viewkey);
				}
			}
			
			// 查询chart配置的SQL结果
			Object sqlObj = temp.getCustomAttribute("sql");
			if(sqlObj != null) {
				String[] arr = sqlObj.toString().split(",");
				for (int i=0; i<arr.length; i++) {
					String key = arr[i];
					SqlItem sqlitem = dbInitializer.loadOne(key);
					String sql = sqlitem.prepareSql(param);
					Object val = jdbcTemplate.queryForList(sql);
					root.put(key, val);
				}
			}
			
			// 将param传递到模板的上下文
			if(param != null) {
				root.put("param", param);
			}
						
			// 填充模板
			StringWriter writer = new StringWriter();
			temp.process(root, writer);
			result = writer.toString();
			
			// 保存redis
			if(redisObj != null) {
				redisTemplateView.opsForValue().set(viewkey, result);
			}
			
		} catch (Exception e) {
			logger.info("load view " + viewkey + ": " + e.getMessage());
			e.printStackTrace();
		}
		return result;	
	}
	
}
