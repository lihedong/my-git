package boco.ips.redis;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import boco.ips.model.ResultEntity;
import boco.ips.socket.ClientManager;

@RestController
@RequestMapping("/redis")
public class RedisController {
	
	private final Logger logger = Logger.getLogger(RedisController.class);

	@Autowired
	private RedisInitializer redisInitializer;
	
	@Autowired
	private ClientManager clientManager;
	
	@SuppressWarnings("rawtypes")
	@Autowired
	private RedisTemplate redisTemplateModel;
	
	/**
	 * 订阅数据
	 */
	@MessageMapping("/data")
    @SendTo("/topic/data")
	public void subscribe(String key) {
		clientManager.subscribeData(key);
		clientManager.pushData(key);
	}
	
	/**
	 * 查询数据
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("query")
	public ResultEntity query(@RequestParam("key") String key) {
		ResultEntity result = ResultEntity.success();
		
		if(key == null) {
			result.setCode(ResultEntity.CODE_REDIS_INVALID_KEY);
			result.setMessage("redis key is null");
			return result;
		}
		
		if(!redisTemplateModel.hasKey(key)) {
			result.setCode(ResultEntity.CODE_REDIS_INVALID_KEY);
			result.setMessage("redis key is not exist");
			return result;
		}
		
		Object val = redisTemplateModel.opsForValue().get(key);
		result.setData(val);
		
		return result;
	}
	
	/**
	 * 更新数据
	 */
	@RequestMapping("update")
	public ResultEntity update(@RequestParam("key") String key) {
		ResultEntity result = ResultEntity.success();
		
		if(key == null) {
			result.setCode(ResultEntity.CODE_REDIS_INVALID_KEY);
			result.setMessage("redis key is null");
			return result;
		}
		
		// 重新加载数据
		Object val = redisInitializer.loadOne(key);
		
		// 保存数据
		result.setData(val);
		
		// 推送数据和关联的视图
		clientManager.pushDataAndView(key);
		
		return result;
	}
	
	
	/**
	 * 更新数据
	 */
	@RequestMapping("trigger")
	public ResultEntity trigger(@RequestParam("key") String key) {
		logger.info(String.format("trigger invoke key [%s]", key));
		ResultEntity result = ResultEntity.success();
		if(key == null) {
			result.setCode(ResultEntity.CODE_REDIS_INVALID_KEY);
			result.setMessage("redis key is null");
			return result;
		}
		clientManager.pushDataAndView(key);// 推送数据和关联的视图
		return result;
	}
}
