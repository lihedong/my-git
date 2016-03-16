package boco.ips.db;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import boco.ips.model.ResultEntity;
import boco.ips.util.SpringBeanUtil;

/**
 * 数据库SQL服务
 */
@RestController
@RequestMapping("/db")
public class DbController {

	private static Logger logger = Logger.getLogger(DbController.class);
	
	@Autowired
	private DbInitializer dbInitializer;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	/**
	 * 查询数据
	 */
	@RequestMapping("query")
	public ResultEntity query(HttpServletRequest request) {
		ResultEntity result = ResultEntity.success();
		
		// 获取key
		String sqlkey = request.getParameter("key");
		if(sqlkey == null) {
			result.setCode(ResultEntity.CODE_DB_INVALID_KEY);
			result.setMessage("sql key is null");
			return result;
		}
		
		SqlItem sqlitem = dbInitializer.loadOne(sqlkey);
		if(sqlitem == null) {
			result.setCode(ResultEntity.CODE_DB_INVALID_KEY);
			result.setMessage("not found " + "/sql/" + sqlkey + ".xml");
			return result;
		}
		
		
		// 获取参数
		Map<String, Object> param = new HashMap<String, Object>();
		@SuppressWarnings("unchecked")
		Enumeration<String>  en = request.getParameterNames();
		while(en.hasMoreElements()) {
			String k = en.nextElement();
			String v = request.getParameter(k);
			param.put(k, v);
		}
		
		// 准备参数
		String sql = sqlitem.prepareSql(param);
		
		// 执行SQL SQLException
		try {
			List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString());
			result.setData(list);
		}
		catch (Exception e) {
			String msg = "query sql " + sqlkey + " error: " + e.getMessage();
			logger.error(msg, e);
			result.setCode(ResultEntity.CODE_DB_SQL_ERROR);
			result.setMessage(msg);
		}
		return result;
	}
	
	/**
	 * 查询数据
	 */
	@RequestMapping("update")
	public ResultEntity update(HttpServletRequest request) {
		ResultEntity result = ResultEntity.success();
		
		// 获取key
		String sqlkey = request.getParameter("key");
		if(sqlkey == null) {
			result.setCode(ResultEntity.CODE_DB_INVALID_KEY);
			result.setMessage("sql key is null");
			return result;
		}
		
		SqlItem sqlitem = dbInitializer.loadOne(sqlkey);
		if(sqlitem == null) {
			result.setCode(ResultEntity.CODE_DB_INVALID_KEY);
			result.setMessage("not found " + "/sql/" + sqlkey + ".xml");
			return result;
		}
		
		
		// 获取参数
		Map<String, Object> param = new HashMap<String, Object>();
		@SuppressWarnings("unchecked")
		Enumeration<String>  en = request.getParameterNames();
		while(en.hasMoreElements()) {
			String k = en.nextElement();
			String v = request.getParameter(k);
			param.put(k, v);
		}
		
		// 准备参数
		String sql = sqlitem.prepareSql(param);
		
		// 执行SQL SQLException
		try {
			int n = jdbcTemplate.update(sql);
			result.setData(n);
		}
		catch (Exception e) {
			String msg = "udpate sql " + sqlkey + " error: " + e.getMessage();
			logger.error(msg, e);
			result.setCode(ResultEntity.CODE_DB_SQL_ERROR);
			result.setMessage(msg);
		}
		return result;
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value="handle")
	public ResultEntity handle(HttpServletRequest request) {
		ResultEntity result = ResultEntity.success();
		
		// 获取key
		String sqlkey = request.getParameter("key");
		if(sqlkey == null) {
			result.setCode(ResultEntity.CODE_DB_INVALID_KEY);
			result.setMessage("data handler is null");
			return result;
		}
		
		// 获取参数
		Map<String, Object> param = new HashMap<String, Object>();
		@SuppressWarnings("unchecked")
		Enumeration<String>  en = request.getParameterNames();
		while(en.hasMoreElements()) {
			String k = en.nextElement();
			String v = request.getParameter(k);
			param.put(k, v);
		}
		
		String beanName = param.get("key").toString();
		DataHandler handler = SpringBeanUtil.getBean(beanName, DataHandler.class);
		Object val = handler.handle(param);
		result.setData(val);
				
		return result;
	}
	
}
