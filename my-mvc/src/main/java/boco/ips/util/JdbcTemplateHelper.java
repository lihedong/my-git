package boco.ips.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.LinkedCaseInsensitiveMap;

public class JdbcTemplateHelper {
	
	/**
	 * LinkedCaseInsensitiveMap 转 HashMap
	 * @param list
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<Map<String,Object>> linkedCaseInsensitiveMapToHashMap(List<Map<String, Object>> list){
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		Map<String,Object> noWrapMap = null;
		LinkedCaseInsensitiveMap springMap = null;
		String springMapKey = null;
		for(Object obj: list){
			if(obj instanceof LinkedCaseInsensitiveMap)
			{
				springMap = (LinkedCaseInsensitiveMap)obj;
				noWrapMap = new HashMap<String,Object>();
				Set<String> springMapKeys = springMap.keySet();
				Iterator<String> iter = springMapKeys.iterator();
				while(iter.hasNext())
				{
					springMapKey = iter.next();
					noWrapMap.put(springMapKey, springMap.get(springMapKey));
				}
				resultList.add(noWrapMap);
			}
			else if(obj instanceof HashMap){
				resultList.add((HashMap)obj);
			}
		}
		return resultList;
	}
}
