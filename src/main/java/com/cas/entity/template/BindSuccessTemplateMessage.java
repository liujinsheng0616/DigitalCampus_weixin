package com.cas.entity.template;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.cas.framework.utils.SerializeUtil;
import com.cas.framework.utils.SystemProperties;

/**
 * 家长绑定成功消息
 * 
 * @author zhuzhen
 *
 */
@SuppressWarnings("unused")
public class BindSuccessTemplateMessage extends TemplateMessage{
	// 顶部边界颜色
	private final static String TOP_COLOR = "#FF0000";
	// 数据颜色
	private final static String COLOR = "#173177";
	// 正式模板id 
	private final static String TEMPLATE_ID = SystemProperties.getProperties("template.id1");//"g0tMInIJO2vR9taqY-WupmADTsPNuTBlyW1tNNGe1vg";
	private final static String FIRST = SystemProperties.getProperties("template.bindsuccess.first");//"恭喜您已经成功绑定孩子信息!";
	private final static String REMARK = SystemProperties.getProperties("template.bindsuccess.remark");//"现在您可以查看孩子学习成绩，帮孩子打印错题，联系孩子老师啦。";
	/**
	 * 生成绑定成功的模板消息Map
	 * 
	 * @param openIdWX	
	 * @param studentName	 
	 * @param familyName
	 * @param loginPhone
	 * @param post
	 * @param studentId
	 * 
	 * @return
	 */
	public static Map<String, Object> getTemplateMessageMap(String openIdWX, String studentName, String familyName, String loginPhone, Integer post, Long studentId) {
		Map<String, Object> bindSuccessTemplateMap = new HashMap<String, Object>();
		bindSuccessTemplateMap.put("touser", openIdWX);
		bindSuccessTemplateMap.put("template_id", TEMPLATE_ID);
		// 点击模板消息跳转首页
		bindSuccessTemplateMap.put("url", SystemProperties.getProperties("family_path") + SystemProperties.getProperties("template.bindsuccess.url")
				+ "?openIdWX=" + openIdWX + "&studentId=" + studentId);
		bindSuccessTemplateMap.put("topcolor", TOP_COLOR);
		
		// data属性
		Map<String, Map<String, String>> data = new HashMap<String, Map<String, String>>();
		bindSuccessTemplateMap.put("data", data);
		
		// 标题first
		Map<String, String> first = new HashMap<String, String>();
		first.put("value", FIRST);
		first.put("color", COLOR);
		data.put("first", first);
		
		// 学生姓名keyword1
		Map<String, String> studentNameMap = new HashMap<String, String>();
		studentNameMap.put("value", studentName);
		studentNameMap.put("color", COLOR);
		data.put("keyword1", studentNameMap);
		
		// 家长身份keyword2
		String postName = "";
		switch (post) {
			case 11:
				postName = SystemProperties.getProperties("template.bindsuccess.father");//"爸爸";
				break;
			case 12:
				postName = SystemProperties.getProperties("template.bindsuccess.mother");//"妈妈";
				break;
			case 13:
				postName = SystemProperties.getProperties("template.bindsuccess.other");//"其他长辈";
				break;
		}
		Map<String, String> postMap = new HashMap<String, String>();
		postMap.put("value", postName);
		postMap.put("color", COLOR);
		data.put("keyword2", postMap);
		
		// 家长姓名keyword3
		Map<String, String> familyNameMap = new HashMap<String, String>();
		String grdOrClssStr = "";
		familyNameMap.put("value", familyName);
		familyNameMap.put("color", COLOR);
		data.put("keyword3", familyNameMap);
		
		// 联系电话keyword4
		Map<String, String> loginPhoneMap = new HashMap<String, String>();
		loginPhoneMap.put("value", loginPhone);
		loginPhoneMap.put("color", COLOR);
		data.put("keyword4", loginPhoneMap);
		
		// 绑定时间keyword5
		Map<String, String> bindTimeMap = new HashMap<String, String>();
		bindTimeMap.put("value", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		bindTimeMap.put("color", COLOR);
		data.put("keyword5", bindTimeMap);
		
		// 末尾remark
		Map<String, String> remark = new HashMap<String, String>();
		remark.put("value", REMARK);
		remark.put("color", COLOR);
		data.put("remark", remark);
		
		return bindSuccessTemplateMap;
	}
	
}
