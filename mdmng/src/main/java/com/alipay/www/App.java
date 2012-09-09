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
	/** Դ�ļ��� */
	private final static String SRCPATH = "C:\\Users\\ibm\\Desktop\\demo.csv";

	/** Ŀ���ļ��� */
	private final static String DESTPATH = "C:\\Users\\ibm\\Desktop\\result.csv";

	/** ���ö��ŷָ����� */
	private final static String COMM = ",";

	/** ���÷ָ���� */
	private final static String DOT = "\\.";

	/** ���÷ָ����� */
	private final static String SEMI = ":";

	public static void main(String[] args) {
		try {
			// �����ȡ�ļ�����ر�����
			String line = null;
			FileReader fileReader = new FileReader(SRCPATH);
			FileWriter fileWriter = new FileWriter(DESTPATH);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			PrintWriter printWriter = new PrintWriter(fileWriter);

			// ��¼ÿ�����Զ�Ӧ��Ƕ�ײ㼶��������attr1->0,attr2->0,attr3->1��
			Map<String, Integer> mapAttr2NestLevel = new HashMap<String, Integer>();
			// ��¼���Ե�������������Ķ�Ӧ��ϵ��
			Map<Integer, String> mapAttr2Order = new HashMap<Integer, String>();
			// ��¼Ƕ�ײ㼶��JSONObject��ӳ���ϵ��
			Map<Integer, JSONObject> mapNestLevel2JSONObject = new HashMap<Integer, JSONObject>();
			// ��¼Ƕ��json���Ե��������ơ�
			Map<Integer, String> mapNestLevel2AttrName = new HashMap<Integer, String>();

			// ����������Ϣ��
			parseNestAttr(mapNestLevel2AttrName, bufferedReader);

			// ����������Ϣ��
			parserAttrOrder(mapAttr2Order, mapAttr2NestLevel, bufferedReader);

			// ����������Ϣ��
			parseContent(mapAttr2NestLevel, mapAttr2Order,
					mapNestLevel2JSONObject, mapNestLevel2AttrName,
					bufferedReader);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��ÿһ��Ƕ��json��Ӧ�������������õ������map�С�������2��Ƕ�׹�ϵ,���1���key������key2,value��һ��Ƕ�׵�json;
	 * ��2���key������subKey3,value��һ��json��
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
	 *            �����õ�mapNestLevel2AttrName��
	 * @param bufferedReader
	 *            �ļ���ȡ����
	 * @throws IOException
	 * @throws NumberFormatException
	 */
	private final static void parseNestAttr(
			Map<Integer, String> mapNestLevel2AttrName,
			BufferedReader bufferedReader) throws NumberFormatException,
			IOException {
		// ������ʱ������
		String line = null;

		// ��ȡ������Ϣ����ȷ��Ƕ�����Ե����ơ�
		if ((line = bufferedReader.readLine()) != null) {
			// �����ݽ��зָ
			String[] keyValue = line.split(COMM);
			// ����"Ƕ�ײ㼶"��"Ƕ��������"֮���key-vlaue��ϵ��
			for (String pair : keyValue) {
				String[] hierarchyAndAttrName = pair.split(SEMI);
				mapNestLevel2AttrName.put(
						Integer.parseInt(hierarchyAndAttrName[0]),
						hierarchyAndAttrName[1]);
			}
			// ��ձ���
			line = null;
		}
	}

	/**
	 * ����ÿ�����Ե����,����¼ÿ���������ڵڼ���Ƕ�ײ㼶��
	 * 
	 * @param mapAttr2Order
	 *            ����ÿ�����Զ�Ӧ����š�
	 * @param bufferedReader
	 *            �ļ���ȡ����
	 * @param mapAttr2NestLevel
	 *            ������Ƕ�ײ㼶��ӳ���ϵ��
	 * @throws IOException
	 * @throws NumberFormatException
	 */
	private final static void parserAttrOrder(
			Map<Integer, String> mapAttr2Order,
			Map<String, Integer> mapAttr2NestLevel,
			BufferedReader bufferedReader) throws NumberFormatException,
			IOException {
		// ������ʱ�ļ�������
		String line = null;

		// ��ȡ������Ϣ�����������Ե�jsonǶ�ײ㼶��ϵ��
		if ((line = bufferedReader.readLine()) != null) {
			// �����ļ���Ӧ����š�
			int count = 0;
			// �����ݽ��зָ
			String[] attributes = line.split(COMM);
			// ������ֵ¼���Ӧ��Ƕ�ײ㼶��
			for (String attribute : attributes) {
				// ����Ƕ�ײ㼶���͡��������ơ����롣
				String[] serialNumAndAttr = attribute.split(DOT);
				// �����������ƺ�Ƕ�ײ㼶���ӳ���ϵ��
				mapAttr2NestLevel.put(serialNumAndAttr[1],
						Integer.parseInt(serialNumAndAttr[0]));
				// �����������ƺ���ŵĹ�ϵ����Ĭ����ʼ���Ϊ0����
				mapAttr2Order.put(count, serialNumAndAttr[1]);
				// ���������
				count++;
			}
			// ����ļ���صı�����
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

		// ��ȡ���ļ��о��������ֵ��Ϣ��
		while ((line = bufferedReader.readLine()) != null) {
			// ��¼�ļ���Ӧ�����
			int count = 0;
			// ���ļ���¼���зָ���
			String[] attributes = line.split(COMM);

			// ����ÿһ������ֵ��
			for (String attribute : attributes) {
				// ��ȡ��ǰ����ֵ��Ӧ���������ơ�
				String attrName = mapAttr2Order.get(count);
				// ��ȡ��ǰ���Ե�Ӧ��jsonǶ�ײ㼶����
				Integer hierarchyNum = mapAttr2NestLevel.get(attrName);
				// ��ȡ��ǰǶ�ײ㼶��Ӧ��JSONObject
				JSONObject jsonObject = mapNestLevel2JSONObject
						.get(hierarchyNum);
				if (jsonObject == null) {
					jsonObject = new JSONObject();
					mapNestLevel2JSONObject.put(hierarchyNum, jsonObject);
				}
				// ��jsonObject�������á�
				jsonObject.element(attrName, attribute);
				// ��������1��
				count++;
			}

			// ����mapHierarchyJSONObjectǶ�׹�ϵӳ���
			for (int i = mapNestLevel2JSONObject.size() - 1; i >= 0; i--) {
				// ����ǰJSONObject���л�
				String json = mapNestLevel2JSONObject.get(i).toString();

				// �����ǰ��Ϊ��㣬��json����Ϊ��һ�㼶�����ԡ�
				if (i != 0) {
					// ��õ�ǰjson����һ�㼶���������ơ�
					String attrName = mapNestLevel2AttrName.get(i);
					// ����������Ϊi-1���json���ԡ�
					mapNestLevel2JSONObject.get(i - 1).put(attrName, json);
				} else {

					// ��ӡ����
					System.out.println(json);
				}
			}
		}
	}
}
