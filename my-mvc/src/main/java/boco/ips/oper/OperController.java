package boco.ips.oper;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import boco.ips.model.ResultEntity;
import boco.ips.socket.ClientManager;

@RestController
@RequestMapping("/oper")
public class OperController {
	
	@Autowired
	private ClientManager client;
	
	/**
	 * 发送动作到大屏
	 * @param request
	 * @return
	 */
	@RequestMapping(value="send", method={RequestMethod.POST})
	public ResultEntity send(@RequestBody Map<String, Object> param) {
		ResultEntity result = ResultEntity.success();
		
		// 推送到客户端
		client.pushOper(param);
				
		return result;
	}
}
