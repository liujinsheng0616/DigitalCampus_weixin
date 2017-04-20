package com.cas.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.cas.framework.utils.HttpClientUtil;
import com.cas.framework.utils.HttpClientUtil.ResponseCallback;
import com.cas.framework.utils.SerializeUtil;
import com.cas.framework.utils.StringUtil;
import com.cas.framework.utils.SystemProperties;

/**
 * @Creat 2017年04月8日
 * @Author:kingson·liu
 * 微信主业务逻辑处理全局类，单例模式
 */
@SuppressWarnings({"unchecked", "unused"})
public final class WxApiConfig {
	
	private static final Logger log = LoggerFactory.getLogger(WxApiConfig.class);
	private String appId;

	private WxApiParam wxApiParam=new WxApiParam();
	
	public static final String MsgType_TEXT = "text"; // 消息类型：文本
	public static final String MsgType_IMAGE = "image";
	public static final String MsgType_LINK = "link";
	public static final String MsgType_LOCATION = "location";
	public static final String MsgType_VOICE = "voice"; // 消息类型：语音
	public static final String MsgType_VIDEO = "video"; // 消息类型：视频
	public static final String MsgType_MUSIC = "music"; // 消息类型：音乐
	public static final String MsgType_EVENT = "event";
	public static final String MsgType_NEWS = "news"; // 消息类型：图文
	public static final String MsgType_TRANSFER_CUSTOMER = "transfer_customer_service"; // 消息类型：转发到客服
	public static final String EventType_SUBSCRIBE = "subscribe";
	public static final String EventType_UNSUBSCRIBE = "unsubscribe";
	public static final String EventType_CLICK = "CLICK";
	public static final String EventType_VIEW = "VIEW";
	public static final String EventType_LOCATION = "LOCATION";
	public static final String EventType_SCAN = "SCAN";
	public static final String EventType_KF_CREATE_SESSION = "kf_create_session";// 事件类型：客服会话状态事件
	public static final String EventType_KF_CLOSE_SESSION = "kf_close_session";
	public static final String EventType_KF_SWITCH_SESSION = "kf_switch_session";
	public static final String MERCHANT_ORDER = "merchant_order";
	public static final String TEMPLATESENDJOBFINISH = "TEMPLATESENDJOBFINISH";
	public static final String GET_token = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={appid}&secret={secret}";
	public static final String GET_ticket = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token={access_token}&type=jsapi";
	public static final String SEND_custom = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token={access_token}";
	public static final String UPLOAD_news = "https://api.weixin.qq.com/cgi-bin/media/uploadnews?access_token={access_token}";
	public static final String SEND_news_all = "https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token={access_token}";
	public static final String SEND_news_openid ="https://api.weixin.qq.com/cgi-bin/message/mass/send?access_token={access_token}";
	public static final String GET_UserInfo ="https://api.weixin.qq.com/cgi-bin/user/info?access_token={access_token}&openid={openid}&lang=zh_CN";
	// 菜单接口
	public static final String CREATE_menu ="https://api.weixin.qq.com/cgi-bin/menu/create?access_token={access_token}";
	public static final String GET_menu ="https://api.weixin.qq.com/cgi-bin/menu/get?access_token={access_token}";
	public static final String DELETE_menu ="https://api.weixin.qq.com/cgi-bin/menu/delete?access_token={access_token}";
	// 网页授权token接口
	private static final String GET_AUTH_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
	private static final String REFRESH_AUTH_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=%s&grant_type=refresh_token&refresh_token=%s";
	// 网页授权拿取用户信息
	private static final String GET_AUTH_USERINFO = "https://api.weixin.qq.com/sns/userinfo?access_token={access_token}&openid={openid}";
	// 模板消息接口
	private static final String SEND_TEMPLATE = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s";
	private static final String SET_INDUSTRY = "https://api.weixin.qq.com/cgi-bin/template/api_set_industry?access_token=%s";
	private static final String GET_TEMPLATE_ID = "https://api.weixin.qq.com/cgi-bin/template/api_add_template?access_token=%s";
	// 获取客户的会话状态
	private static final String GET_CUSTOM_SESSION_STATUS = "https://api.weixin.qq.com/customservice/kfsession/getsession?access_token=%s&openid=%s";
	// 获取在线客服的接待信息
	private static final String GET_ONLINE_KF_LIST = "https://api.weixin.qq.com/cgi-bin/customservice/getonlinekflist?access_token=%s";
	// 获取客服基本信息
	private static final String GET_KF_INFO = "https://api.weixin.qq.com/cgi-bin/customservice/getkflist?access_token=%s";
	// 关闭会话
	private static final String CLOSE_KF_SESSION = "https://api.weixin.qq.com/customservice/kfsession/close?access_token=%s";
	// 获取未接入会话列表
	private static final String GET_WAIT_CASE = "https://api.weixin.qq.com/customservice/kfsession/getwaitcase?access_token=%s";
	// 微信分组
    public static final String CREATE_group ="https://api.weixin.qq.com/cgi-bin/groups/create?access_token={access_token}";
    public static final String GET_groups ="https://api.weixin.qq.com/cgi-bin/groups/get?access_token={access_token}";
    public static final String GET_group_byid ="https://api.weixin.qq.com/cgi-bin/groups/getid?access_token={access_token}";//用户所在分组
    public static final String UPDATE_group="https://api.weixin.qq.com/cgi-bin/groups/update?access_token={access_token}";
    public static final String CHANGE_group="https://api.weixin.qq.com/cgi-bin/members/update?access_token={access_token}";
    public static final String BATCH_group="https://api.weixin.qq.com/cgi-bin/members/batchupdate?access_token={access_token}";
    public static final String DELETE_group="https://api.weixin.qq.com/cgi-bin/groups/update?access_token={access_token}";
	
	/**
	 * 获取用户基本信息
	 * 
	 * @param accessToken
	 * @param openId
	 * @return
	 */
	public Map<String, Object> getUserInfo(String accessToken,final String openId){
		final String json[]={""};
		HttpClientUtil.get(WxApiConfig.GET_UserInfo.replace("{access_token}", accessToken).replace("{openid}", openId), null, null, new HttpClientUtil.ResponseCallback() {
			@Override
			public void onResponse(int resultCode, String resultJson) {
				if(resultCode==0||resultCode==200){
					json[0]=resultJson;
				}else{
					refreshAccessTokenFromDB();
					//再次获取getUserInfo
					getUserInfo(getWxApiParam().getAccessToken(), openId);
				}
			}
		});
		if(json!=null&&json.length>0){
			return SerializeUtil.serializeJsonToObject(json[0], HashMap.class);
		}
		return null;
	}
	
	/**
     * 判断是否来自微信
     * @param request
     * @return
     */
    public static boolean isWeiXin(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (StringUtils.isNotBlank(userAgent)) {
            Pattern p = Pattern.compile("MicroMessenger/(\\d+).+");
            Matcher m = p.matcher(userAgent);
            String version = null;
            if (m.find()) {
                version = m.group(1);
            }
            return (null != version && NumberUtils.toInt(version) >= 5);
        }
        return false;
    }
    
	/**
	 * 无参构造器
	 */
    public WxApiConfig() {
    	log.debug("初始化WxApiConfig参数");
    }
    
	/**
	 * 解析从微信服务器来的请求，将消息或者事件返回出去
	 * 
	 * @param request
	 * @return 微信消息或者事件Map
	 */
	public static Map<String, String> parseXml(HttpServletRequest request) {
		Map<String, String> map = new HashMap<String, String>();
		InputStream inputStream = null;
		try {
			inputStream = request.getInputStream();
			if (null != inputStream) {
				SAXReader reader = new SAXReader();
				Document document = reader.read(inputStream);
				Element root = document.getRootElement();
				List<Element> elementList = root.elements();
				for (Element e : elementList) {
					map.put(e.getName(), e.getText());
				}
				map.put("xml", document.asXML());
			}
		} catch (Exception e) {
			log.error("解析xml失败！");
		} finally {
			if (null != inputStream) {
				try {
					inputStream.close();
				} catch (IOException e) {
					log.error("关闭inputStream失败！");
				}
			}
		}
		return map;
	}

	/**
	 * 认证微信，可以参见微信开发者文档
	 * 
	 * @param token
	 *            我们自己设定的token值
	 * @param signature
	 *            微信传来的变量
	 * @param timestamp
	 *            微信传来的变量
	 * @param nonce
	 *            微信传来的变量
	 * @return 是否合法
	 */
	public static boolean checkSignature(String appToken,String signature, String timestamp, String nonce) {
		if (StringUtil.hasBlank(appToken, signature, timestamp, nonce)) {
			return false;
		}
		String[] arr = new String[] {appToken, timestamp, nonce };
		Arrays.sort(arr);
		StringBuilder content = new StringBuilder();
		for (String anArr : arr) {
			content.append(anArr);
		}
		MessageDigest md;
		String tmpStr = null;

		try {
			md = MessageDigest.getInstance("SHA-1");
			byte[] digest = md.digest(content.toString().getBytes("UTF-8"));
			tmpStr = byteToStr(digest);
		} catch (NoSuchAlgorithmException e) {
			log.error("加密方式异常", e);
		} catch (UnsupportedEncodingException e) {
			log.error("编码格式不支持", e);
		}
		return tmpStr != null && tmpStr.equalsIgnoreCase(signature);
	}
	
	/**
	 * 将byte数组变为16进制对应的字符串
	 * 
	 * @param byteArray
	 *            byte数组
	 * @return 转换结果
	 */
	private static String byteToStr(byte[] byteArray) {
		int len = byteArray.length;
		StringBuilder strDigest = new StringBuilder(len * 2);
		for (byte aByteArray : byteArray) {
			strDigest.append(byteToHexStr(aByteArray));
		}
		return strDigest.toString();
	}

	private static final char[] digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	private static String byteToHexStr(byte mByte) {
		char[] tempArr = new char[2];
		tempArr[0] = digit[(mByte >>> 4) & 0X0F];
		tempArr[1] = digit[mByte & 0X0F];
		return new String(tempArr);
	}

//	/**
//	 * 从微信中获取accessToken。2个小时过期。
//	 * 此代码移到data中。
//	 */
//	@Deprecated 
//	public void refreshAccessTokenFromWX() {
//		// 更新accessToken
//		String accessTokenResultJson = HttpClientUtil.getData(GET_token.replace("{appid}", getAppId()).replace("{secret}", getAppSecret()), null);
//		Map<String, Object> accessTokenResultMap = JsonUtil.jsonToObject(accessTokenResultJson, Map.class);
//		Assert.notNull(accessTokenResultMap, "accessTokenResultMap is null");
//		if (accessTokenResultMap.get("access_token") == null) {
//			// 再请求两次，第三次抛出异常
//			refreshAccessTokenFromWX();
//		}
//		String accessToken = (String) accessTokenResultMap.get("access_token");
//		if (StringUtil.isNotBlank(accessToken) ) { 
//			// 修改全局变量accessToken
//			setAccessToken(accessToken);
//			// accessToken和ticket插入数据库 
//			Map<String, Object> paramDataMap = new HashMap<String, Object>();
//			paramDataMap.put("appId", getAppId());
//			paramDataMap.put("accessToken", accessToken);
//			
//			String updateWXConfigResultJson = HttpClientUtil.getData(SystemProperties.getProperties("data_url").concat(ControllerVar.updateWXConfig), paramDataMap, null);
//			Map<String, Object> updateWXConfigResultMap = JsonUtil.jsonToObject(updateWXConfigResultJson, Map.class);
//			Assert.notNull(updateWXConfigResultMap, "updateWXConfigResultMap is null");
//			if ((Integer)updateWXConfigResultMap.get("error_code") != 0) {
//				throw new RuntimeException(SystemProperties.getProperties("database.error"));
//			}
//		}
//	}
	
	/**
	 * 从数据库中获取accessToken和ticket。
	 */
	public void refreshAccessTokenFromDB() {
		// 从数据库中去获取accessToken 
//		HttpRpcClient rpcClient = new HttpRpcClient();
//		Map<String, Object> paramData = new HashMap<String, Object>();
//		paramData.put("appId", getAppId());
//		// rpc调用微信配置接口
//		String wxConfigRpc = RpcUrl.Account_getWXConfig;
//		HttpHeader defaultHeader = HttpHeader.custom();// 请求头
//		RpcResponse<Object> configRes = rpcClient.post(wxConfigRpc, paramData, defaultHeader, Object.class);
//		if (configRes.getData() == null) {
//			refreshAccessTokenFromDB();
//		}
//		Map<String, Object> dataMap = (Map<String, Object>) configRes.getData();
//		String accessToken =  dataMap.get("accessToken").toString();
//		String appSecret =  dataMap.get("appSecret").toString();
//		String subscribeReply =  dataMap.get("subscribeReply").toString();
//		String subscribeReply2 =  dataMap.get("subscribeReply2").toString();
//		String ticket = dataMap.get("ticket").toString();
//		// 修改全局变量accessToken
//		synchronized (wxApiParam) {
//			wxApiParam.setAccessToken(accessToken);
//			wxApiParam.setAppKey(getAppId());
//			wxApiParam.setAppSecret(appSecret);
//			wxApiParam.setSubscribeReply(subscribeReply);
//			wxApiParam.setSubscribeReply2(subscribeReply2);
//			wxApiParam.setJsApiTicket(ticket);
//		}
//		Long leftTime=7200L;
//		try {
//			Date updateTime=DateUtil.formatStringTodate(dataMap.get("updateTime").toString(), null);
//			Long expiresIn= Long.parseLong(dataMap.get("expiresIn").toString());
//			Date nowDate=new Date();
//			leftTime=Math.abs(expiresIn-(nowDate.getTime()/1000-updateTime.getTime()/1000));
//		} catch (Exception e) {
//			log.warn("Can't get the wxConfig,leftTime default:7200");
//		}
//		final Long sleep=leftTime-5;
//		//等sleep秒去data获取新的
//		new Thread() {  
//            public void run() { 
//            	try {
//					Thread.sleep(sleep*1000);
//					refreshAccessTokenFromDB();
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//            }  
//        }.start();  
	}
//	/**
//	 * 从数据库中获取jsapi_ticket，如果未取到或者jsapi_ticket不可用则先更新再获取。
//	 */
//	public synchronized String getOrRefreshTicket() {
//		// 从数据库中去获取jsapi_ticket 
//		Map<String, Object> paramData = new HashMap<String, Object>();
//		paramData.put("appId", getAppId());
//		String wxConfigResultJson = HttpClientUtil.getData(SystemProperties.getProperties("data_url").concat(ControllerVar.getWXConfig), paramData, null);
//		
//		Map<String, Object> wxConfigResultMap = JsonUtil.jsonToObject(wxConfigResultJson, Map.class);
//		Assert.notNull(wxConfigResultMap, "wxConfigResultMap is null");
//		if ((Integer)wxConfigResultMap.get("error_code") != 0) {
//			throw new RuntimeException(SystemProperties.getProperties("database.error"));
//		}
//		
//		String ticket = "";
//		String updateTimeStr = "";
//		Map<String, Object> dataMap = (Map<String, Object>)wxConfigResultMap.get("data");
//		if (dataMap != null) {
//			ticket = (String) dataMap.get("ticket");
//			updateTimeStr = (String) dataMap.get("updateTime");
//			if(StringUtil.isNotBlank(ticket)) {
//				// ticket不为空，并且ticket未过期
//				Long now = new Date().getTime();
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//				Long updateTime;
//				try {
//					updateTime = sdf.parse(updateTimeStr).getTime();
//					if (now < updateTime + 7200 * 1000 - 5000) {
//						return ticket;
//					}
//				} catch (ParseException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		
//		// 如果从数据库中获取不到jsapi_ticket，或者jsapi_ticket过期,需要更新jsapi_ticket 
//		String resultJson = HttpClientUtil.getData(GET_token.replace("{appid}", getAppId()).replace("{secret}", getAppSecret()), null);
//		Map<String, Object> resultMap = JsonUtil.jsonToObject(resultJson, Map.class);
//		Assert.notNull(resultMap, "resultMap is null");
//		if (resultMap.get("access_token") == null) {
//			throw new RuntimeException(SystemProperties.getProperties("wxRequest.error"));
//		}
//		
//		// 请求成功
//		String accessToken = (String) resultMap.get("access_token");
//		if (StringUtil.isNotBlank(accessToken) ) {
//			// 微信全局类设置accessToken
//			setAccessToken(accessToken);
//		}else{
//			return null;
//		}
//		
//		// 更新jsapi_ticket,将jsapi_ticket存入数据库
//		if (enableJsApi) {
//			String ticketResultJson = HttpClientUtil.getData(GET_ticket.replace("{access_token}", accessToken), null);
//			Map<String, Object> ticketResultMap = JsonUtil.jsonToObject(ticketResultJson, Map.class);
//			Assert.notNull(ticketResultMap, "ticketResultMap is null");
//			// 请求成功
//			if (ticketResultMap.get("errcode") != null && 0 == Integer.parseInt(ticketResultMap.get("errcode").toString())) { 
//				// 微信全局类设置ticket
//				ticket = (String) ticketResultMap.get("ticket");
//				if (StringUtil.isNotBlank(ticket)) {
//					setJsApiTicket(ticket);
//					// accessToken和ticket插入数据库 
//					Map<String, Object> paramDataMap = new HashMap<String, Object>();
//					paramDataMap.put("appId", getAppId());
//					paramDataMap.put("accessToken", accessToken);
//					paramDataMap.put("ticket", ticket);
//					
//					String updateWXConfigResultJson = HttpClientUtil.getData(SystemProperties.getProperties("data_url").concat(ControllerVar.updateWXConfig), paramDataMap, null);
//					Map<String, Object> updateWXConfigResultMap = JsonUtil.jsonToObject(updateWXConfigResultJson, Map.class);
//					Assert.notNull(updateWXConfigResultMap, "jsonMap is null");
//					if ((Integer)updateWXConfigResultMap.get("error_code") != 0) {
//						throw new RuntimeException(SystemProperties.getProperties("database.error"));
//					}
//					
//					// 返回jsapi_ticket
//					return ticket;
//				}
//			}else {
//				throw new RuntimeException(SystemProperties.getProperties("wxRequest.error"));
//			}
//		}
//		
//		return null;
//	}

	/**
	 * 网页授权获取openIdWX
	 * 
	 * @param code 网页授权时，微信服务器发给本地服务器的code参数
	 * @return String openIdWX
	 */
	public synchronized String getOpenIdByAuthAccessToken(String code) {
		// 获取authAccessToken
		String firstJson = HttpClientUtil.getData(String.format(GET_AUTH_ACCESS_TOKEN, getAppId(), wxApiParam.getAppSecret(), code), null);
		Map<String, Object> firstJsonMap = SerializeUtil.serializeJsonToObject(firstJson, Map.class);
		Assert.notNull(firstJsonMap, "firstJsonMap is null");
		String authAccessToken = (String) firstJsonMap.get("access_token");
		if (authAccessToken == null) {
			throw new RuntimeException(SystemProperties.getProperties("wxRequest.error"));
		}
		return (String) firstJsonMap.get("openid");
	}
	
	/**
	 * 网页授权获取用户nickname和头像地址
	 * @param request
	 * */
	public synchronized Map<String, Object> getUserInfoByAuthAccessToken(HttpServletRequest request){
		String code = request.getParameter("code");
		// 获取authAccessToken
		String firstJson = HttpClientUtil.getData(String.format(GET_AUTH_ACCESS_TOKEN, getAppId(), wxApiParam.getAppSecret(), code), null);
		Map<String, Object> firstJsonMap = SerializeUtil.serializeJsonToObject(firstJson, Map.class);
		Assert.notNull(firstJsonMap, "firstJsonMap is null");
		String authAccessToken = (String) firstJsonMap.get("access_token");
		String openIdWX = firstJsonMap.get("openid").toString();
		if (authAccessToken == null) {
			throw new RuntimeException(SystemProperties.getProperties("wxRequest.error"));
		}
		String secondJson = HttpClientUtil.getData(WxApiConfig.GET_AUTH_USERINFO.replace("{access_token}", authAccessToken).replace("{openid}", openIdWX), null);
		Map<String, Object> secondJsonMap = SerializeUtil.serializeJsonToObject(secondJson, Map.class);
		Assert.notNull(firstJsonMap, "firstJsonMap is null");
		secondJsonMap.put("openIdWX", openIdWX);
		return secondJsonMap;
	}
	
	/**
	 * 获取菜单json
	 */
	public String getMenuJson() {
		// 一级菜单名字
		String name1 = "";// menuKey_1 孩子成绩
		String type1 = "";
		String url1 = "";
		String name2 = "";// menuKey_2 提分计划
		String type2 = "";
		String url2 = "";
		String name3 = "";// menuKey_3 我的极课
		
		// 二级菜单（第一个一级菜单的）
//		String type11 = "";// menuKey_4 学习情况
//		String name11 = "";
//		String url11 = "";
//		
//		String type12 = "";// menuKey_5 提分动态
//		String name12 = "";
//		String url12 = "";
		
//		String type13 = "";// menuKey_6 最近考试
//		String name13 = "";
//		String url13 = "";
		
		// 二级菜单（第二个一级菜单的）
//		String type21 = "";// menuKey_6最新消息
//		String name21 = "";
//		String url21 = "";
		
//		String type22 = "";// menuKey_8 给老师留言
//		String name22 = "";
//		String url22 = "";
//		
//		String type23 = "";// menuKey_9 老师联系方式
//		String name23 = "";
//		String url23 = "";
		
		// 二级菜单（第三个一级菜单的）
		String type31 = "";// menuKey_7绑定孩子信息
		String name31 = "";
		String url31 = "";
		
		String type32 = "";// menuKey_8 联系客服
		String name32 = "";
		String url32 = "";
		
		String type33 = "";// menuKey_9 使用指南
		String name33 = "";
		String url33 = "";
		
		String type34 = "";// menuKey_10 活动中心
		String name34 = "";
		String url34 = "";
		
		
//		String type35 = "";// menuKey_10 
//		String name35 = "";
//		String url35 = "";
		
//		// 从数据库查询微信自定义菜单信息
//		HttpRpcClient rpcClient = new HttpRpcClient();
//		// rpc调用微信菜单接口
//		String wxMunuRpc = RpcUrl.Account_getWXCustomMenu;
//		HttpHeader defaultHeader = HttpHeader.custom();// 请求头
//		RpcResponse<Object> menuRes = rpcClient.post(wxMunuRpc, null, defaultHeader, Object.class);
//		if (menuRes.getData() == null) {
//			throw new RuntimeException(SystemProperties.getProperties("database.error"));
//		}
//		List<Map<String, Object>> dataList = (List<Map<String, Object>>) menuRes.getData();
//		if (null != dataList && dataList.size() > 0){
//			for (int i = 0; i < dataList.size(); i ++) {
//				Map<String, Object> menuMap = (Map<String, Object>)dataList.get(i);
//				switch(i + 1){
//				case 1:
//					name1 = (String) menuMap.get("name");
//					type1 = (String) menuMap.get("menuType");
//					url1 = (String) menuMap.get("urlLink");
//					break;
//				case 2:
//					name2 = (String) menuMap.get("name");
//					type2 = (String) menuMap.get("menuType");
//					url2 = (String) menuMap.get("urlLink");
//					break;
//				case 3:
//					name3 = (String) menuMap.get("name");
//					break;
//				case 4:
//					name31 = (String) menuMap.get("name");
//					type31 = (String) menuMap.get("menuType");
//					url31 = (String) menuMap.get("urlLink");
//					break;
//				case 5:
//					name32 = (String) menuMap.get("name");
//					type32 = (String) menuMap.get("menuType");
//					url32 = (String) menuMap.get("urlLink");
//					break;
//				case 6:
//					name33 = (String) menuMap.get("name");
//					type33 = (String) menuMap.get("menuType");
//					url33 = (String) menuMap.get("urlLink");
//					break;
//				case 7:
//					name34 = (String) menuMap.get("name");
//					type34 = (String) menuMap.get("menuType");
//					url34 = (String) menuMap.get("urlLink");
//					break;
//				}
//			}
//		}
		
		// 用于保存菜单参数的Map
		Map<String, Object> menuMap = new HashMap<String, Object>();
		
		List<Map<String, Object>> button = new ArrayList<Map<String, Object>>();
		menuMap.put("button", button) ;
		
		// 第一个1级菜单 ===================
		Map<String, Object> map1 = new HashMap<String, Object>();
		map1.put("name", name1);
		map1.put("type", type1);
		map1.put("url", url1);
//		List<Map<String, Object>> sub_button1 = new ArrayList<Map<String, Object>>();
//		// 第一个2级菜单
//		Map<String, Object> map11 = new HashMap<String, Object>();
//		map11 .put("type", type11);
//		map11 .put("name", name11);
//		map11 .put("url", url11);
		
//		sub_button1.add(map11);
//		// 第二个2级菜单
//		Map<String, Object> map12 = new HashMap<String, Object>();
//		map12.put("type", type12);
//		map12.put("name", name12);
//		map12.put("url", url12);
//		
//		sub_button1.add(map12);
		
		//BEGIN 微信迭代2.0菜单修整
		// 第三个2级菜单
//		Map<String, Object> map13 = new HashMap<String, Object>();
//		map13.put("type", type13);
//		map13.put("name", name13);
//		map13.put("url", url13);
//		
//		sub_button1.add(map13);
		// end
		
//		map1.put("sub_button", sub_button1);
		
		button.add(map1);
		
		// 第二个1级菜单 ===================
		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("name", name2);
		map2.put("type", type2);
		map2.put("url", url2);
		button.add(map2);
		
		// 第三个1级菜单 ===================
		Map<String, Object> map3 = new HashMap<String, Object>();
		map3.put("name", name3);
		List<Map<String, Object>> sub_button3 = new ArrayList<Map<String, Object>>();
		// 第一个2级菜单
		Map<String, Object> map31 = new HashMap<String, Object>();
		map31.put("type", type31);
		map31.put("name", name31);
		map31.put("url", url31);
		sub_button3.add(map31);
		
		// 第二个2级菜单
		Map<String, Object> map32 = new HashMap<String, Object>();
		map32 .put("type", type32);
		map32 .put("name", name32);
		map32 .put("url", url32);
		sub_button3.add(map32);
		
		// 第三个2级菜单
		Map<String, Object> map33 = new HashMap<String, Object>();
		map33.put("type", type33);
		map33.put("name", name33);
		map33.put("url", url33);
		sub_button3.add(map33);
		
		// 第四个2级菜单
		Map<String, Object> map34 = new HashMap<String, Object>();
		map34.put("type", type34);
		map34.put("name", name34);
		map34.put("url", url34);
		sub_button3.add(map34);

		map3.put("sub_button", sub_button3);
		
		button.add(map3);
		
		// Map转json
		String menuJson = SerializeUtil.serializeToJson(menuMap);
		
		return menuJson;
	}
	/**
	 * 自定义菜单创建
	 * 
	 * @param jsonData 
	 */
	public void createMenu(String jsonData) {
		// 创建自定义菜单
		HttpClientUtil.post(CREATE_menu.replace("{access_token}", getWxApiParam().getAccessToken()), jsonData, null, new HttpClientUtil.ResponseCallback() {
			@Override
			public void onResponse(int resultCode, String resultJson) {
				if (resultCode != 200) {
					throw new RuntimeException(SystemProperties.getProperties("createmenu.error"));
				}
				System.out.println(WxReturnCode.get(resultCode));
				System.out.println(resultJson);
			}
		});
	}
	/**
	 * 自定义菜单查询
	 * 
	 * @return String 创建自定义菜单的json
	 */
	public String getMenu() {
		// 查询自定义菜单
		String json = HttpClientUtil.getData(GET_menu.replace("{access_token}", getWxApiParam().getAccessToken()), null, null);
		return json;
	}
	/**
	 * 自定义菜单删除
	 */
	public String deleteMenu() {
		// 创建自定义菜单
		String resultJson  = HttpClientUtil.getData(DELETE_menu.replace("{access_token}", getWxApiParam().getAccessToken()), null, null);
		return resultJson; 
	}
	/**
	 * 设置行业，获取模板id
	 * 
	 * @param templateIdShortJson 模板消息json      
	 * {
     *      "template_id_short":"TM00015"
     *  }
	 */
	public void getTemplateId(String templateIdShortJson ) {		
		HttpClientUtil.post(String.format(GET_TEMPLATE_ID, getWxApiParam().getAccessToken()), templateIdShortJson, null, new ResponseCallback() {
			@Override
			public void onResponse(int resultCode, String resultJson) {
				System.out.println(WxReturnCode.get(resultCode));
				System.out.println("resultJson:" + resultJson);
			}
		});
	}
	/**
	 * 调用微信接口api，发送模板消息
	 * 
	 * @param templateJson
	 * @return Object[] 发送成功：{true,"errcode:0"};发送失败：{false,"errcode:xx"}
	 */
	public Object[] sendTemplateMessage(String templateJson) throws Exception{
		final Object[] msg = {"",""};
		String accessToken = getWxApiParam().getAccessToken();
		String url = String.format(SEND_TEMPLATE, accessToken);
		HttpClientUtil.post(url, templateJson, null, new ResponseCallback() {
			@Override
			public void onResponse(int resultCode, String resultJson) {
				System.out.println("发送成功:" + resultJson);
				Map<String, Object> resultMap = SerializeUtil.serializeJsonToObject(resultJson, Map.class);
				Assert.notNull(resultMap, "resultMap is null");
				if (resultMap.get("errcode") != null && (Integer)resultMap.get("errcode") == 0) {
					msg[0] = true;
					msg[1] = "errcode:0";
				}else{
					msg[0] = false;
					msg[1] = "errcode:" + String.valueOf(resultMap.get("errcode"));
				}
			}
		});
		return msg;
	}
	/**
	 * 查询客户的会话状态
	 * 
	 * @param openIdWX
	 * @return 
	 */
	public String getCustomSessionStatus(String openIdWX) {
		String json = HttpClientUtil.getData(String.format(GET_CUSTOM_SESSION_STATUS, getWxApiParam().getAccessToken(), openIdWX), null, null);
		return json;
	}
	/**
	 * 获取在线客服接待信息
	 * 
	 * @return 
	 */
	public String getOnlineKfList() {
		String json = HttpClientUtil.getData(String.format(GET_ONLINE_KF_LIST, getWxApiParam().getAccessToken()), null, null);
		return json;
	}
	/**
	 * 获取客服基本信息
	 * 
	 * @return 
	 */
	public String getKfInfo() {
		String json = HttpClientUtil.getData(String.format(GET_KF_INFO, getWxApiParam().getAccessToken()), null, null);
		return json;
	}
	/**
	 * 关闭会话
	 * 
	 * @param kfAccount 客服账号
	 * @param openIdWX
	 * @param text
	 */
	public void closeKfSession(String kfAccount, String openIdWX, String text) {
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("kf_account", kfAccount);
		paramMap.put("openid", openIdWX);
		paramMap.put("text", text);
		String paramJson = SerializeUtil.serializeToJson(paramMap);
	    HttpClientUtil.post(String.format(CLOSE_KF_SESSION, getWxApiParam().getAccessToken()), paramJson, null, new ResponseCallback() {
			@Override
			public void onResponse(int resultCode, String resultJson) {
				System.out.println(resultCode);
				System.out.println(resultJson);
			}
			
		});
	}
	/**
	 * 获取未接入会话列表
	 * 
	 * @return
	 */
	public String getWaitCase() {
		String json = HttpClientUtil.getData(String.format(GET_WAIT_CASE, getWxApiParam().getAccessToken()), null, null);
		return json;
	}
	
	public class WxApiParam{
		private String appKey;
		private String appSecret;
		private String appToken;
		private boolean enableJsApi;
		private String jsApiTicket;
		private String accessToken;
		private String autoReply;
		private String subscribeReply;
		private String subscribeReply2;
		/**
		 * 网页授权
		 */
		private String authAccessToken;
		private String refreshToken;
		private Integer expires_in;// 过期时间，单位秒，可以通过此数据设置定时器，过期后自动抓去新的。
		public String getAppKey() {
			return appKey;
		}
		public void setAppKey(String appKey) {
			this.appKey = appKey;
		}
		public String getAppSecret() {
			return appSecret;
		}
		public void setAppSecret(String appSecret) {
			this.appSecret = appSecret;
		}
		public String getAppToken() {
			return appToken;
		}
		public void setAppToken(String appToken) {
			this.appToken = appToken;
		}
		public boolean isEnableJsApi() {
			return enableJsApi;
		}
		public void setEnableJsApi(boolean enableJsApi) {
			this.enableJsApi = enableJsApi;
		}
		public String getJsApiTicket() {
			return jsApiTicket;
		}
		public void setJsApiTicket(String jsApiTicket) {
			this.jsApiTicket = jsApiTicket;
		}
		public String getAccessToken() {
			return accessToken;
		}
		public void setAccessToken(String accessToken) {
			this.accessToken = accessToken;
		}
		public String getAutoReply() {
			return autoReply;
		}
		public void setAutoReply(String autoReply) {
			this.autoReply = autoReply;
		}
		public String getSubscribeReply() {
			return subscribeReply;
		}
		public void setSubscribeReply(String subscribeReply) {
			this.subscribeReply = subscribeReply;
		}
		public String getSubscribeReply2() {
			return subscribeReply2;
		}
		public void setSubscribeReply2(String subscribeReply2) {
			this.subscribeReply2 = subscribeReply2;
		}
		public String getAuthAccessToken() {
			return authAccessToken;
		}
		public void setAuthAccessToken(String authAccessToken) {
			this.authAccessToken = authAccessToken;
		}
		public String getRefreshToken() {
			return refreshToken;
		}
		public void setRefreshToken(String refreshToken) {
			this.refreshToken = refreshToken;
		}
		public Integer getExpires_in() {
			return expires_in;
		}
		public void setExpires_in(Integer expires_in) {
			this.expires_in = expires_in;
		}
		
	}
	
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public WxApiParam getWxApiParam() {
		refreshAccessTokenFromDB();//调用的时候，刷新
		return wxApiParam;
	}
	/**
	 * 微信JS SDK相关
	 * 
	 * author: ljs
	 * */
	public Map<String, String> sign(String jsapi_ticket, String url) {
        Map<String, String> ret = new HashMap<String, String>();
        String nonce_str = create_nonce_str();
        String timestamp = create_timestamp();
        String str;
        String signature = "";
 
        //注意这里参数名必须全部小写，且必须有序
        str = "jsapi_ticket=" + jsapi_ticket + "&noncestr=" + nonce_str + "&timestamp=" + timestamp + "&url=" + url;
 
        try
        {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(str.getBytes("UTF-8"));
            signature = byteToHex(crypt.digest());
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
 
        ret.put("url", url);
        ret.put("jsapi_ticket", jsapi_ticket);
        ret.put("nonceStr", nonce_str);
        ret.put("timestamp", timestamp);
        ret.put("signature", signature);
        ret.put("appId", getAppId());
        return ret;
    }
	
	/**
	 * 微信JS SDK相关
	 * 
	 * author: ljs
	 * */
	private static String create_nonce_str() {
        return UUID.randomUUID().toString();
    }
 
	/**
	 * 微信JS SDK相关
	 * 
	 * author: ljs
	 * */
    private static String create_timestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }
    
    /**
	 * 微信JS SDK相关
	 * 
	 * author: ljs
	 * */
    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
	
	 /**
     * 自定义分组创建
     * 
     * @param jsonData 
     */
    public String  createGroup(String jsonData) {
    	final String[] msg = {""};
        // 创建自定义菜单
        HttpClientUtil.post(CREATE_group.replace("{access_token}", getWxApiParam().getAccessToken()), jsonData, null, new HttpClientUtil.ResponseCallback() {
            @Override
            public void onResponse(int resultCode, String resultJson) {
                if (resultCode != 200) {
                    throw new RuntimeException(SystemProperties.getProperties("createGroup.error"));
                }
                System.out.println(WxReturnCode.get(resultCode));
                msg[0]=resultJson;
            }
        });
    	return msg[0];
    }
    /**
     * 获取所有分组
     * 
     * @param jsonData 
     */
    public String getAllGroup() {
        String json =HttpClientUtil.postData(GET_groups.replace("{access_token}", getWxApiParam().getAccessToken()), null, null);
        return json;
    }
    
    /**
     * 获取单个用户所在组
     * 
     * @param jsonData 
     */
    public String getGroupById(String openid) {
        Map<String,Object>param=new HashMap<String,Object>();
        param.put("openid", openid);
        
        // 获取单个用户所在组
        String json =HttpClientUtil.postData(GET_group_byid .replace("{access_token}", getWxApiParam().getAccessToken()), param,null);
        return json;
    }
    /**
     * 修改用户组名
     * 
     * @param jsonData 
     */
    public String updateGroup(String id,String name) {
        Map<String,Object>param=new HashMap<String,Object>();
        param.put("id", id);
        param.put("name", name);
        String json =HttpClientUtil.postData(UPDATE_group.replace("{access_token}", getWxApiParam().getAccessToken()), param,null);
        return json;
    }
    /**
     *删除用户组
     * 
     * @param jsonData 
     */
    public void deleteGroup(String jsonData) {
        // 创建自定义菜单
        HttpClientUtil.post(DELETE_group.replace("{access_token}", getWxApiParam().getAccessToken()), jsonData, null, new HttpClientUtil.ResponseCallback() {
            @Override
            public void onResponse(int resultCode, String resultJson) {
                if (resultCode != 200) {
                    throw new RuntimeException(SystemProperties.getProperties("deleteGroup.error"));
                }
                System.out.println(WxReturnCode.get(resultCode));
                System.out.println(resultJson);
            }
        });
    }
    /**
     * 移动用户到其他组
     * 
     * @param jsonData 
     */
    public void changeGroup(String openid,String to_groupid) {
        Map<String,Object>param=new HashMap<String,Object>();
        param.put("openid", openid);
        param.put("to_groupid", to_groupid);
        String jsonData = SerializeUtil.serializeToJson(param);
        // 创建自定义菜单
        HttpClientUtil.post(CHANGE_group.replace("{access_token}", getWxApiParam().getAccessToken()), jsonData, null, new HttpClientUtil.ResponseCallback() {
            @Override
            public void onResponse(int resultCode, String resultJson) {
                if (resultCode != 200) {
                    throw new RuntimeException(SystemProperties.getProperties("changeGroup.error"));
                }
                System.out.println(WxReturnCode.get(resultCode));
                System.out.println(resultJson);
            }
        });
    }
    /**
     * 批量移动用户到其他组
     * 
     * @param jsonData 
     */
    public void batchChangeGroup(String jsonData) {
        // 创建自定义菜单
        HttpClientUtil.post(BATCH_group.replace("{access_token}", getWxApiParam().getAccessToken()), jsonData, null, new HttpClientUtil.ResponseCallback() {
            @Override
            public void onResponse(int resultCode, String resultJson) {
                if (resultCode != 200) {
                    throw new RuntimeException(SystemProperties.getProperties("batchGroup.error"));
                }
                System.out.println(WxReturnCode.get(resultCode));
                System.out.println(resultJson);
            }
        });
    }
}
