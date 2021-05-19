package com.guohua.north_bulb.util;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * 自定义一个用于从首选项中保存/获取数据的工具类
 * 
 */
public class SharedPreferencesUtils {

	public static final String SP_NAME = "config";
	public static SharedPreferences sp;

	/**
	 * 保存字符串到首选项中
	 * 
	 * @param ct
	 *            上下文
	 * @param SP_NAME
	 *            首选项文件的名称
	 * @param key
	 *            保存的数据名称
	 * @param value
	 *            保存的数据的值
	 */
	public static void saveString(Context ct, String key, String value) {

		if (sp == null)
			sp = ct.getSharedPreferences(SP_NAME, 0);
		sp.edit().putString(key, value).commit();
	}

	/**
	 * 从首选项中获取字符串数据
	 * 
	 * @param ct
	 *            上下文
	 * @param SP_NAME
	 *            首选项文件的名称
	 * @param key
	 *            将要获取的数据的名称
	 * @param defValue
	 *            将要获取的数据的默认值
	 * @return 获取的字符串数据的值
	 */
	public static String getString(Context ct, String key, String defValue) {
		if (sp == null)
			sp = ct.getSharedPreferences(SP_NAME, 0);
		return sp.getString(key, defValue);
	}

	/**
	 * 从首选项中获取已经学习过的数据
	 * 
	 * @return 按钮数据组成的集合
	 */
	public static Map<String, String> getRemoteControlData(Context ct,
			String remote_control_name, String defValue, String[] btn_names) {
		/**
		 * 从首选项中获取当前已经学习过的遥控器的名称和已经学习过的按钮，将已经学习过的按钮的名称和对应的数据保存到集合中
		 */
		Map<String, String> tv_button_data = new LinkedHashMap<String, String>();
		String remote_control_name_value = SharedPreferencesUtils.getString(ct,
				remote_control_name, "");
		if (!TextUtils.isEmpty(remote_control_name_value)) {// 表示已经有学习过
			for (String tv_btn_name : btn_names) {// 循环取出所有的按钮的名字和值
				String btn_value = SharedPreferencesUtils.getString(ct,
						tv_btn_name, "");
				tv_button_data.put(tv_btn_name, btn_value);
			}
		}
		return tv_button_data;
	}

}
