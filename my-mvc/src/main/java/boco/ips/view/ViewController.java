package boco.ips.view;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import boco.ips.model.ResultEntity;
import boco.ips.socket.ClientManager;
import boco.ips.util.SpringBeanUtil;

@RestController
@RequestMapping("/view")
public class ViewController {
	
	@Autowired
	private ViewInitializer viewInitailizer;
	
	@Autowired
	private ClientManager clientManager;
	
	@SuppressWarnings("rawtypes")
	@Autowired
	private RedisTemplate redisTemplateView;
	
	
	/**
	 * 订阅视图
	 */
	@MessageMapping("/view")
    @SendTo("/topic/view")
	public void subscribe(String key) {
		clientManager.subscribeView(key);
		clientManager.pushView(key);
	}
	
	/**
	 * 查询视图
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("query")
	public ResultEntity query(HttpServletRequest request) {
		ResultEntity result = ResultEntity.success();
		
		// 获取key
		String viewkey = request.getParameter("key");
		if(viewkey == null) {
			result.setCode(ResultEntity.CODE_VIEW_INVALID_KEY);
			result.setMessage("view key is null");
			return result;
		}

		// 获取参数
		Map<String, Object> param = new HashMap<String, Object>();
		Enumeration<String>  en = request.getParameterNames();
		while(en.hasMoreElements()) {
			String k = en.nextElement();
			String v = request.getParameter(k);
			param.put(k, v);
		}
		
		// 查询Redis
		Object redisFlag = param.get("redisFlag");
		boolean flag = redisFlag!=null && Boolean.getBoolean(redisFlag.toString());
		if(flag) {
			if(redisTemplateView.hasKey(viewkey)) {
				Object val = redisTemplateView.opsForValue().get(viewkey);
				result.setData(val);
				return result;
			}
		}
		
		// 生成视图
		String val = viewInitailizer.loadOne(viewkey, param);
		result.setData(val);
		
		return result;
	}
	
	/**
	 * 更新视图
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("update")
	public ResultEntity update(HttpServletRequest request) {
		ResultEntity result = ResultEntity.success();
		
		// 获取key
		String viewkey = request.getParameter("key");
		if(viewkey == null) {
			result.setCode(ResultEntity.CODE_VIEW_INVALID_KEY);
			result.setMessage("view key is null");
			return result;
		}
		
		// 生成视图
		String val = viewInitailizer.loadOne(viewkey, null);
		result.setData(val);
		
		// 保存REDIS
		redisTemplateView.opsForValue().set(viewkey, val);
		
		// 推送视图
		clientManager.pushView(viewkey);
		
		return result;
	}
	
	/**
	 * 根据指标查询所对应的所有视图
	 */
	@RequestMapping("viewkeys")
	public ResultEntity viewkeys(@RequestParam("key") String key) {
		ResultEntity result = ResultEntity.success();
		result.setData(ViewDataMapper.get(key));
		return result;
	}
	

	/**
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="handle")
	public ResultEntity handle(HttpServletRequest request) {
		ResultEntity result = ResultEntity.success();
		
		// 获取key
		String viewkey = request.getParameter("key");
		if(viewkey == null) {
			result.setCode(ResultEntity.CODE_VIEW_INVALID_KEY);
			result.setMessage("view handler is null");
			return result;
		}

		// 获取参数
		Map<String, Object> param = new HashMap<String, Object>();
		Enumeration<String>  en = request.getParameterNames();
		while(en.hasMoreElements()) {
			String k = en.nextElement();
			String v = request.getParameter(k);
			param.put(k, v);
		}
		
		String beanName = param.get("key").toString();
		ViewHandler handler = SpringBeanUtil.getBean(beanName, ViewHandler.class);
		Object val = handler.handle(param);
		result.setData(val);
				
		return result;
	}
}
