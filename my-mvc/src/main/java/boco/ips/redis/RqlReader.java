package boco.ips.redis;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import boco.ips.util.SystemUtil;

public class RqlReader {

	private RqlReader() {

	}

	/**
	 * 读取redis目录下的所有SQL文件
	 */
	public static List<Rqlment> readPath() {
		File dir = new File(SystemUtil.getClassPath() + SystemUtil.CFG_REDIS_PATH);
		File[] files = dir.listFiles();
		List<Rqlment> list = new ArrayList<Rqlment>();

		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file != null && file.isFile()) {
				Rqlment sqlment = readFile(file);
				list.add(sqlment);
			}
		}
		return list;
	}

	/**
	 * 读取单个SQL文件
	 */
	public static Rqlment readFile(String key) {
		File file = new File(SystemUtil.getClassPath() + SystemUtil.CFG_REDIS_PATH + File.separator + key + ".xml");
		if (file.exists()) {
			return readFile(file);
		}
		return null;
	}

	/**
	 * 读取单个SQL文件
	 */
	public static Rqlment readFile(File file) {
		Rqlment sqlment = new Rqlment();
		try {
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(file);
			Element rootElt = document.getRootElement();

			// 以文件名作为ID
			String fileName = file.getName();
			sqlment.setId(fileName.substring(0, file.getName().indexOf(".")));
			sqlment.setLabel(rootElt.attributeValue("label"));
			sqlment.setDataSource(rootElt.attributeValue("datasource"));
			sqlment.setSelectSql(rootElt.elementTextTrim("select"));

			// fields
			Element filedsElement = rootElt.element("fields");
			if (filedsElement != null) {
				@SuppressWarnings("rawtypes")
				Iterator fieldsIterator = filedsElement.elementIterator("field");
				Element fieldElement = null;
				List<RqlField> fieldList = null;
				if (fieldsIterator != null) {
					fieldList = new ArrayList<RqlField>();
					RqlField sqlFiled = null;
					while (fieldsIterator.hasNext()) {
						fieldElement = (Element) fieldsIterator.next();
						sqlFiled = new RqlField();
						sqlFiled.setName(fieldElement.attributeValue("name"));
						sqlFiled.setLabel(fieldElement.attributeValue("label"));
						fieldList.add(sqlFiled);
					}
					sqlment.setFieldList(fieldList);
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return sqlment;
	}
}
