package boco.ips.socket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import boco.ips.view.ViewDataMapper;
import boco.ips.view.ViewInitializer;

/**
 * 客户端管理类
 */
@Service
public class ClientManager {

	private List<String> dataKeys = new ArrayList<String>();

	private List<String> viewKeys = new ArrayList<String>();

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@SuppressWarnings("rawtypes")
	@Autowired
	private RedisTemplate redisTemplateModel;

	@SuppressWarnings("rawtypes")
	@Autowired
	private RedisTemplate redisTemplateView;

	@Autowired
	private ViewInitializer viewInitializer;

	/**
	 * 订阅数据
	 */
	public void subscribeData(String key) {
		if (!dataKeys.contains(key)) {
			dataKeys.add(key);
		}
	}

	/**
	 * 订阅视图
	 */
	public void subscribeView(String key) {
		if (!viewKeys.contains(key)) {
			viewKeys.add(key);
		}
	}

	/**
	 * 推送数据
	 */
	public void pushData(String key) {
		if (dataKeys.contains(key)) {
			HashMap<String, Object> header = new HashMap<String, Object>();
			header.put("ips-key", key);
			Object body = redisTemplateModel.opsForValue().get(key);
			if (body == null) {
				return;
			}
			messagingTemplate.convertAndSend("/topic/data", body, header);
		}
	}

	/**
	 * 推送数据和视图
	 */
	public void pushDataAndView(final String key) {
		//// 推送数据
		if (dataKeys.contains(key)) {
			Object dataBody = redisTemplateModel.opsForValue().get(key);
			HashMap<String, Object> dataHeader = new HashMap<String, Object>();
			dataHeader.put("ips-key", key);
			messagingTemplate.convertAndSend("/topic/data", dataBody, dataHeader);
		}
		// 推送关联视图
		List<String> views = ViewDataMapper.get(key);
		if (views != null && !views.isEmpty()) {
			for (String viewkey : views) {
				// 重新生成视图
				String viewBody = viewInitializer.loadOneByRedis(viewkey, false);

				// 推送视图
				HashMap<String, Object> viewHeader = new HashMap<String, Object>();
				viewHeader.put("ips-key", viewkey);
				messagingTemplate.convertAndSend("/topic/view", viewBody, viewHeader);
			}
		}
	}

	/**
	 * 推送视图
	 */
	public void pushView(String key) {
		if (viewKeys.contains(key)) {
			HashMap<String, Object> header = new HashMap<String, Object>();
			header.put("ips-key", key);
			Object body = redisTemplateView.opsForValue().get(key);
			messagingTemplate.convertAndSend("/topic/view", body, header);
		}
	}

	/**
	 * 推送操作
	 */
	public void pushOper(Map<String, Object> param) {
		messagingTemplate.convertAndSend("/topic/oper", param);
	}

}
