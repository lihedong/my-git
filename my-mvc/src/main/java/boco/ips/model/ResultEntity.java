package boco.ips.model;

/**
 * 返回结果实例类
 * 
 * 返回码分配规则
 *           0: 成功
 *     100-199: 数据库错误 
 *     200-299: REDIS错误
 *     300-399: 视图错误
 */
public class ResultEntity {
	
	/** 正确码 */
	public static final int CODE_SUCCESS = 0;
	
	/** 错误码：数据库SQL异常 */
	public static final int CODE_DB_SQL_ERROR = 101;
	
	/** 错误码：数据库SQL KEY错误 */
	public static final int CODE_DB_INVALID_KEY = 102;
	
	/** 错误码：REDIS KEY不合法 */
	public static final int CODE_REDIS_INVALID_KEY = 201;
	
	/** 错误码：视图KEY不合法 */
	public static final int CODE_VIEW_INVALID_KEY = 301;
	
	/** 结果编码 */
	private Integer code;
	
	/** 结果描述 */
	private String message;
	
	/** 结果数据ID */
	private String dataId;
	
	/** 结果数据 */
	private Object data;

	
	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDataId() {
		return dataId;
	}

	public void setDataId(String dataId) {
		this.dataId = dataId;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
	public static ResultEntity success() {
		ResultEntity result = new ResultEntity();
		result.code = CODE_SUCCESS;
		result.message = "success";
		return result;
	}
}
