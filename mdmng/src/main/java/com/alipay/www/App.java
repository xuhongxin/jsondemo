package com.alipay.www;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

/**
 * Hello world!
 * 
 */
public class App {
	/** 源文件。 */
	private final static String SRCPATH = "C:\\Users\\ibm\\Desktop\\demo.csv";

	/** 目标文件。 */
	private final static String DESTPATH = "C:\\Users\\ibm\\Desktop\\result.csv";

	/** 设置逗号分隔符。 */
	private final static String COMM = ",";

	/** 设置分割符。 */
	private final static String DOT = "\\.";

	/** 设置分隔符。 */
	private final static String SEMI = ":";

	public static void main(String[] args) {
		try {
			// 定义读取文件的相关变量。
			String line = null;
			FileReader fileReader = new FileReader(SRCPATH);
			FileWriter fileWriter = new FileWriter(DESTPATH);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			PrintWriter printWriter = new PrintWriter(fileWriter);

			// 记录每个属性对应的嵌套层级数。例如attr1->0,attr2->0,attr3->1。
			Map<String, Integer> mapAttr2NestLevel = new HashMap<String, Integer>();
			// 记录属性的序号与属性名的对应关系。
			Map<Integer, String> mapAttr2Order = new HashMap<Integer, String>();
			// 记录嵌套层级与JSONObject的映射关系。
			Map<Integer, JSONObject> mapNestLevel2JSONObject = new HashMap<Integer, JSONObject>();
			// 记录嵌套json属性的属性名称。
			Map<Integer, String> mapNestLevel2AttrName = new HashMap<Integer, String>();

			// 解析首行信息。
			parseNestAttr(mapNestLevel2AttrName, bufferedReader);

			// 解析属性信息。
			parserAttrOrder(mapAttr2Order, mapAttr2NestLevel, bufferedReader);

			// 解析内容信息。
			parseContent(mapAttr2NestLevel, mapAttr2Order,
					mapNestLevel2JSONObject, mapNestLevel2AttrName,
					bufferedReader);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将每一个嵌套json对应的属性名称配置到输入的map中。假设有2层嵌套关系,则第1层的key属性是key2,value是一个嵌套的json;
	 * 第2层的key属性是subKey3,value是一个json。
	 * 
	 * <pre>
	 * 		{
	 * 			key1:value1,
	 * 			key2:{
	 * 				subKey1:subValue1,
	 * 				subKey2:subValue2,
	 * 				subKey3:{
	 * 					subSubKey1:subSubValue1
	 * 				}
	 * 			}
	 * 		}
	 * </pre>
	 * 
	 * @param mapNestLevel2AttrName
	 *            待配置的mapNestLevel2AttrName。
	 * @param bufferedReader
	 *            文件读取器。
	 * @throws IOException
	 * @throws NumberFormatException
	 */
	private final static void parseNestAttr(
			Map<Integer, String> mapNestLevel2AttrName,
			BufferedReader bufferedReader) throws NumberFormatException,
			IOException {
		// 设置临时变量。
		String line = null;

		// 读取首行信息，并确认嵌套属性的名称。
		if ((line = bufferedReader.readLine()) != null) {
			// 对数据进行分割。
			String[] keyValue = line.split(COMM);
			// 变量"嵌套层级"与"嵌套属性名"之间的key-vlaue关系。
			for (String pair : keyValue) {
				String[] hierarchyAndAttrName = pair.split(SEMI);
				mapNestLevel2AttrName.put(
						Integer.parseInt(hierarchyAndAttrName[0]),
						hierarchyAndAttrName[1]);
			}
			// 清空变量
			line = null;
		}
	}

	/**
	 * 解析每个属性的序号,并记录每个属性属于第几个嵌套层级。
	 * 
	 * @param mapAttr2Order
	 *            解析每个属性对应的序号。
	 * @param bufferedReader
	 *            文件读取器。
	 * @param mapAttr2NestLevel
	 *            属性与嵌套层级的映射关系。
	 * @throws IOException
	 * @throws NumberFormatException
	 */
	private final static void parserAttrOrder(
			Map<Integer, String> mapAttr2Order,
			Map<String, Integer> mapAttr2NestLevel,
			BufferedReader bufferedReader) throws NumberFormatException,
			IOException {
		// 保存临时文件变量。
		String line = null;

		// 读取次行信息，并设置属性的json嵌套层级关系。
		if ((line = bufferedReader.readLine()) != null) {
			// 定义文件对应的序号。
			int count = 0;
			// 对数据进行分割。
			String[] attributes = line.split(COMM);
			// 将属性值录入对应的嵌套层级中
			for (String attribute : attributes) {
				// 将‘嵌套层级’和‘属性名称’分离。
				String[] serialNumAndAttr = attribute.split(DOT);
				// 设置属性名称和嵌套层级间的映射关系。
				mapAttr2NestLevel.put(serialNumAndAttr[1],
						Integer.parseInt(serialNumAndAttr[0]));
				// 设置属性名称和序号的关系，（默认起始序号为0）。
				mapAttr2Order.put(count, serialNumAndAttr[1]);
				// 序号自增。
				count++;
			}
			// 清空文件相关的变量。
			line = null;
		}
	}

	private final static void parseContent(
			Map<String, Integer> mapAttr2NestLevel,
			Map<Integer, String> mapAttr2Order,
			Map<Integer, JSONObject> mapNestLevel2JSONObject,
			Map<Integer, String> mapNestLevel2AttrName,
			BufferedReader bufferedReader) throws IOException {
		// line
		String line = null;

		// 读取其文件中具体的属性值信息。
		while ((line = bufferedReader.readLine()) != null) {
			// 记录文件对应的序号
			int count = 0;
			// 将文件记录进行分隔。
			String[] attributes = line.split(COMM);

			// 遍历每一个属性值。
			for (String attribute : attributes) {
				// 获取当前属性值对应的属性名称。
				String attrName = mapAttr2Order.get(count);
				// 获取当前属性的应对json嵌套层级数。
				Integer hierarchyNum = mapAttr2NestLevel.get(attrName);
				// 获取当前嵌套层级对应的JSONObject
				JSONObject jsonObject = mapNestLevel2JSONObject
						.get(hierarchyNum);
				if (jsonObject == null) {
					jsonObject = new JSONObject();
					mapNestLevel2JSONObject.put(hierarchyNum, jsonObject);
				}
				// 对jsonObject进行配置。
				jsonObject.element(attrName, attribute);
				// 计数器加1。
				count++;
			}

			// 遍历mapHierarchyJSONObject嵌套关系映射表。
			for (int i = mapNestLevel2JSONObject.size() - 1; i >= 0; i--) {
				// 将当前JSONObject序列化
				String json = mapNestLevel2JSONObject.get(i).toString();

				// 如果当前不为最顶层，则将json设置为上一层级的属性。
				if (i != 0) {
					// 获得当前json在上一层级的属性名称。
					String attrName = mapNestLevel2AttrName.get(i);
					// 将属性设置为i-1层的json属性。
					mapNestLevel2JSONObject.get(i - 1).put(attrName, json);
				} else {

					// 打印变量
					System.out.println(json);
				}
			}
		}
	}
}
