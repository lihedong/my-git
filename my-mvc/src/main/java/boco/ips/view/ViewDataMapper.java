package boco.ips.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 指标和视图对应关系
 */
public class ViewDataMapper {

	private static Map<String, List<String>> map = new HashMap<String, List<String>>();
	
	public static void clear() {
		map.clear();
	}
	
	/**
	 * 根据指标key，添加关联的视图key
	 * @param dataKey 指标key
	 * @param viewKey 视图key
	 */
	public static void put(String dataKey, String viewKey) {
		if(map.containsKey(dataKey)) {
			List<String> list = map.get(dataKey);
			if(!list.contains(viewKey)) {
				list.add(viewKey);
			}
		}else {
			List<String> list = new ArrayList<String>();
			list.add(viewKey);
			map.put(dataKey, list);
		}
	}
	
	/**
	 * 根据指标key，返回关联的所有视图
	 * @param dataKey 指标Key
	 * @return 视图Key集合
	 */
	public static List<String> get(String dataKey) {
		return map.get(dataKey);
	}
}
