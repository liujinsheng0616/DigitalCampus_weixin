package com.cas.util;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cas.framework.utils.SystemProperties;

/*
'微信支付服务器签名支付请求请求类
'============================================================================
'api说明：
'init(app_id, app_secret, partner_key, app_key);
'初始化函数，默认给一些参数赋值，如cmdno,date等。
'setKey(key_)'设置商户密钥
'getLasterrCode(),获取最后错误号
'GetToken();获取Token
'getTokenReal();Token过期后实时获取Token
'createMd5Sign(signParams);生成Md5签名
'genPackage(packageParams);获取package包
'createSHA1Sign(signParams);创建签名SHA1
'sendPrepay(packageParams);提交预支付
'getDebugInfo(),获取debug信息
'============================================================================
'*/
public class RequestHandler {
	
	/** 预支付网关url地址 */
	private String gateUrl;
	
	/** 商户key*/
	private String key;
	
	/** 请求的参数 */
	private SortedMap<String, Object> parameters;
	
	/** debug��Ϣ */
	private String debugInfo;
	
	protected HttpServletRequest request;
	
	protected HttpServletResponse response;
	
	/**
	 * 初始构造函数。
	 * @param request
	 * @param response
	 */
	public RequestHandler(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		
		this.gateUrl = "https://gw.tenpay.com/gateway/pay.htm";
		this.key = "";
		this.parameters = new TreeMap<String, Object>();
		this.debugInfo = "";
	}
	
	/**
	*初始化函数。
	*/
	public void init() {
		//nothing to do
	}

	/**
	*获取入口地址,不包含参数值
	*/
	public String getGateUrl() {
		return gateUrl;
	}

	public void setGateUrl(String gateUrl) {
		this.gateUrl = gateUrl;
	}
	public String getKey() {
		return key;
	}

	//设置密钥
	public void setKey(String key) {
		this.key = key;
	}
	
	/**
	 * 拿到请求参数
	 * @param parameter 
	 * @return String 
	 */
	public String getParameter(String parameter) {
		String s = (String)this.parameters.get(parameter); 
		return (null == s) ? "" : s;
	}
	
	/**
	 * 设置请求参数
	 * @param parameter 
	 * @param parameterValue
	 */
	public void setParameter(String parameter, String parameterValue) {
		String v = "";
		if(null != parameterValue) {
			v = parameterValue.trim();
		}
		this.parameters.put(parameter, v);
	}
	
	/**
	 * 获得所有的请求参数
	 * @return SortedMap
	 */
	public SortedMap<String, Object> getAllParameters() {		
		return this.parameters;
	}

	/**
	*得到DBUG信息
	*/
	public String getDebugInfo() {
		return debugInfo;
	}
	
	/**
	 * 得到与支付请求地址
	 * @return String
	 * @throws UnsupportedEncodingException 
	 */
	public String getRequestURL() throws UnsupportedEncodingException {
		this.createSign();
		StringBuffer sb = new StringBuffer();
		Set<Entry<String, Object>> es = this.parameters.entrySet();
		Iterator<Entry<String, Object>> it = es.iterator();
		while(it.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry entry = (Map.Entry)it.next();
			String k = (String)entry.getKey();
			String v = (String)entry.getValue();
			if(!"spbill_create_ip".equals(k)) {
				sb.append(k + "=" + URLEncoder.encode(v, "UTF-8") + "&");
			} else {
				sb.append(k + "=" + v.replace("\\.", "%2E") + "&");
			}
		}
		String reqPars = sb.substring(0, sb.lastIndexOf("&"));
		return this.getGateUrl() + "?" + reqPars;
		
	}
	
	public void doSend() throws UnsupportedEncodingException, IOException {
		this.response.sendRedirect(this.getRequestURL());
	}
	
	/**
	 * 创建md5摘要,规则是:按参数名称a-z排序,遇到空值的参数不参加签名。
	 */
	protected void createSign() {
		StringBuffer sb = new StringBuffer();
		Set<Entry<String, Object>> es = this.parameters.entrySet();
		Iterator<Entry<String, Object>> it = es.iterator();
		while(it.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry entry = (Map.Entry)it.next();
			String k = (String)entry.getKey();
			String v = (String)entry.getValue();
			if(null != v && !"".equals(v) 
					&& !"sign".equals(k) && !"key".equals(k)) {
				sb.append(k + "=" + v + "&");
			}
		}
		sb.append("key=" + SystemProperties.getProperties("PARTNERID_KEY"));
		
		String sign = MD5Util.MD5Encode(sb.toString(), "UTF-8").toUpperCase();
		
		this.setParameter("sign", sign);
		
		//debug信息
		this.setDebugInfo(sb.toString() + " => sign:" + sign);
		
	}
	
	/**
	 * 设置debug信息
	 */
	protected void setDebugInfo(String debugInfo) {
		this.debugInfo = debugInfo;
	}
	
	protected HttpServletRequest getHttpServletRequest() {
		return this.request;
	}
	
	protected HttpServletResponse getHttpServletResponse() {
		return this.response;
	}
	
	/**
	 * 自定义函数，官方没有
	 * @param return_code
	 * @param return_msg
	 * @return
	 */
	public static String setXML(String return_code, String return_msg) {
		return "<xml><return_code><![CDATA[" + return_code + "]]></return_code><return_msg><![CDATA[" + return_msg
				+ "]]></return_msg></xml>";
	}
	
	/**
	 * 自定义函数，在官方文档中没有
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String getRequestXml() throws UnsupportedEncodingException {
		this.createSign();
		
		StringBuffer sb = new StringBuffer();
		sb.append("<xml>");
		Set<Entry<String, Object>> es = this.parameters.entrySet();
		Iterator<Entry<String, Object>> it = es.iterator();
		while (it.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry entry = (Map.Entry) it.next();
			String k = (String) entry.getKey();
			String v = (String) entry.getValue();
			if ("attach".equalsIgnoreCase(k) || "body".equalsIgnoreCase(k) || "sign".equalsIgnoreCase(k)) {
				sb.append("<" + k + ">" + "<![CDATA[" + v + "]]></" + k + ">");
			} else {
				sb.append("<" + k + ">" + v + "</" + k + ">");
			}
		}

		sb.append("</xml>");
		return sb.toString();
	}
}
