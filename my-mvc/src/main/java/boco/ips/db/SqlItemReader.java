package boco.ips.db;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import boco.ips.util.SystemUtil;

public class SqlItemReader {

	private SqlItemReader() {

	}

	/**
	 * 读取redis目录下的所有SQL文件
	 */
	public static List<SqlItem> readPath() {
		File dir = new File(SystemUtil.getClassPath() + SystemUtil.CFG_SQL_PATH);
		File[] files = dir.listFiles();
		List<SqlItem> list = new ArrayList<SqlItem>();
		
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file != null && file.isFile()) {
				List<SqlItem> sublist = readFile(file);
				list.addAll(sublist);
			}
		}
		return list;
	}
	
	/**
	 * 读取单个SQL文件
	 */
	public static SqlItem readFile(String key) {
		String prefix = key.substring(0, key.indexOf('.'));
		String suffix = key.substring(key.indexOf('.'));
		
		File file = new File(SystemUtil.getClassPath() + SystemUtil.CFG_REDIS_PATH +  prefix + ".xml");
		if(file.exists()) {
			try {
				SAXReader saxReader = new SAXReader();
				Document document = saxReader.read(file);
				Element rootElt = document.getRootElement();

				// sql
				@SuppressWarnings("rawtypes")
				Iterator iter = rootElt.elementIterator("item");
				if (iter != null) {
					while (iter.hasNext()) {
						Element elt = (Element) iter.next();
						
						String id = elt.attributeValue("id");
						if(id.equals(suffix)) {
							SqlItem item = new SqlItem();
							item.setId(prefix + "_" + id);
							String body = elt.getTextTrim();
							item.setSql(body);
							return item;
						}
						
					}
				}
			} catch (DocumentException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 读取单个SQL文件
	 */
	public static List<SqlItem> readFile(File file) {
		List<SqlItem> list = new ArrayList<SqlItem>();
		try {
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(file);
			Element rootElt = document.getRootElement();

			// 以文件名作为ID前缀
			String fileName = file.getName();
			String prefix = fileName.substring(0, file.getName().indexOf(".")); 

			// sql
			@SuppressWarnings("rawtypes")
			Iterator iter = rootElt.elementIterator("item");
			if (iter != null) {
				while (iter.hasNext()) {
					Element elt = (Element) iter.next();
					SqlItem item = new SqlItem();
					
					String id = elt.attributeValue("id");
					item.setId(prefix + "_" + id);
					
					String body = elt.getTextTrim();
					item.setSql(body);
					
					list.add(item);
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return list;
	}
}
