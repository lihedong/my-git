package boco.ips.util;

/**
 * 系统相关工具方法
 */
public class SystemUtil {

	/** database sql 配置路径 */
	public static final String CFG_SQL_PATH = "/cfg_sql";
	
	/** redis sql 配置路径 */
	public static final String CFG_REDIS_PATH = "/cfg_redis";
	
	/** view配置路径 */
	public static final String CFG_VIEW_PATH = "/cfg_view";
	
	/** java class路径 */
	private static String classPath = null;
	
	/** tomcat webapp路径 */
	private static String webRoot = null;
	
	/** java class路径 */
	public static String getClassPath() {
		if(classPath == null) {
			classPath =  SystemUtil.class.getResource("/").getPath();
		}
		return classPath;
	}
	
	/** tomcat webapp路径 */
	public static String getWebRoot() {
		if(webRoot == null) {
			String classpath = getClassPath();
			webRoot = classpath.substring(0, classpath.indexOf("WEB-INF"));
		}
		return webRoot;
	}
}
