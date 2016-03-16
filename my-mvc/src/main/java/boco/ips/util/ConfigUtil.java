package boco.ips.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * 配置文件工具类
 */
public class ConfigUtil {
	
	private static Logger logger = Logger.getLogger(ConfigUtil.class);
	
	/**
	 * 配置文件缓存，避免重复读取
	 */
	private static Map<String, Properties> cache = new HashMap<String, Properties>();


	/**
	 * 读取配置文件config.properties属性
	 * @param key 配置属性名
	 * @return
	 */
	public static String loadPropertyString(String key) {
		return loadProperty("/config.properties", key);
	}
	
	/**
	 * 读取配置文件config.properties属性
	 * @param key 配置属性名
	 * @return
	 */
	public static int loadPropertyInt(String key) {
		String str = loadProperty("/config.properties", key);
		return Integer.parseInt(str);
	}
	
	/**
	 * 读取配置文件config.properties属性
	 * @param key 配置属性名
	 * @return
	 */
	public static boolean loadPropertyBoolean(String key) {
		String str = loadProperty("/config.properties", key);
		return Boolean.parseBoolean(str);
	}
	
	/**
	 * 读取配置数据
	 * @param filename 配置文件名
	 * @param key 配置属性名
	 */
	public static String loadProperty(String filename, String key) {
		String val = null;
		if(cache.containsKey(filename)) {
			val = cache.get(filename).getProperty(key);
		}else {
			try {
				Properties prop = new Properties();
				prop.load(ConfigUtil.class.getResourceAsStream(filename));
				// 缓存配置文件
				cache.put(filename, prop);
				val = prop.getProperty(key);
			} catch (Exception e) {
				logger.error(String.format("读取配置文件(%s)异常", filename), e);
			}
		}
		return val;
	}
}
