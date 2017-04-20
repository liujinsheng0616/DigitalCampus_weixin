package com.cas.service.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.cas.dao.UserDao;
import com.cas.entity.Article;
import com.cas.entity.RespNewsMessage;
import com.cas.entity.RespTextMessage;
import com.cas.entity.RespTransferCustomerMessage;
import com.cas.entity.WxApiConfig;
import com.cas.entity.account.User;
import com.cas.framework.utils.SerializeUtil;
import com.cas.framework.utils.StringUtil;
import com.cas.framework.utils.SystemProperties;
import com.cas.service.WeixinService;
import com.cas.util.MD5Util;
import com.cas.util.RequestHandler;
import com.cas.util.RespXmlBuilder;
import com.cas.util.Sha1Util;
import com.cas.util.WxUtil;
import com.cas.util.XMLUtil;

/**
 * @Creat 2017年04月8日
 * @Author:kingson·liu
 * 微信主业务逻辑处理实现类
 */
@Service
public class WeixinServiceImpl implements WeixinService{

	private static Logger LOGGER = Logger.getLogger(WeixinServiceImpl.class);
	private static Integer Two_hours=2 * 60 * 60 * 1000;
	
	@Autowired
	private WxApiConfig wxApiConfig;
	
	@Autowired
	private UserDao userDaoImpl;
	
	@Override
	public String processMsg(Map<String, String> reqMap) {
		String msgType = reqMap.get("MsgType");
		String resultMsg = "";
		if (msgType.equals(WxApiConfig.MsgType_EVENT)) {
			String eventType = reqMap.get("Event");
			if (eventType.equals(WxApiConfig.EventType_SUBSCRIBE)) {
				resultMsg = processSubscribeMsg(reqMap);// 关注
			} else if (eventType.equals(WxApiConfig.EventType_UNSUBSCRIBE)) {
				processUnsubscribeMsg(reqMap);// 取消关注
			} else if (eventType.equals(WxApiConfig.EventType_CLICK)) {
//				resultMsg = processClickMsg(reqMap);
			} else if (eventType.equals(WxApiConfig.EventType_VIEW)) {
				// 菜单url点击事件，可以不用处理。
			} else if (eventType.equals(WxApiConfig.EventType_LOCATION)) {
				resultMsg = processLocationMsg(reqMap);
			} else if (eventType.equals(WxApiConfig.EventType_KF_CREATE_SESSION)) {
				resultMsg = processKfCreateSession(reqMap);
			} else if (eventType.equals(WxApiConfig.EventType_KF_CLOSE_SESSION)) {
				resultMsg = processKfCloseSession(reqMap);
			} else if (eventType.equals(WxApiConfig.EventType_KF_SWITCH_SESSION)) {
				resultMsg = processKfSwitchSession(reqMap);
			} else if (eventType.equals(WxApiConfig.MERCHANT_ORDER)) {
				// 用户支付后 获取订单信息
				//processWxShop(reqMap);
			}
		} else {// 接受普通消息，然后处理。
			if (msgType.equals(WxApiConfig.MsgType_TEXT)) {
				resultMsg = processTxtMsg(reqMap);
			} else if (msgType.equals(WxApiConfig.MsgType_IMAGE)) {
				resultMsg = "";
			} else if (msgType.equals(WxApiConfig.MsgType_VOICE)) {
				resultMsg = "";
			} else if (msgType.equals(WxApiConfig.MsgType_VIDEO)) {
				resultMsg = "";
			} else if (msgType.equals(WxApiConfig.MsgType_LOCATION)) {
				resultMsg = "";
			} else if (msgType.equals(WxApiConfig.MsgType_LINK)) {
				resultMsg = "";
			} 
		}
		return resultMsg;
	}
	
	/**
	 * 处理用户关注事件
	 * 
	 * @param reqMap
	 * 
	 */
	private String processSubscribeMsg(final Map<String, String> reqMap) {
		String openIdWX = reqMap.get("FromUserName");
		// 如果该用户是再次关注（即在此之前为取消关注的状态，即在数据库中当前关注flg为2）
		System.out.println("wxApiConfig.getWxApiParam()==="+wxApiConfig.getWxApiParam());
		Map<String, Object> getUserInfo = wxApiConfig.getUserInfo(wxApiConfig.getWxApiParam().getAccessToken(), openIdWX);
		String nickname = (String) getUserInfo.get("nickname");
		Map<String, Object> paramData = new HashMap<String, Object>();
		paramData.put("openIdWX", openIdWX);
		paramData.put("loginPwd", WxUtil.encrypt(openIdWX, "md5"));
		// 过滤微信昵称中的表情字符
		if (StringUtil.isBlank(WxUtil.filterEmoji(nickname))) {
			// 全部为表情字符
			paramData.put("nickname", "[昵称]");
		}else{
			paramData.put("nickname", WxUtil.filterSpecilString(WxUtil.filterEmoji(nickname)));
		}
		String sex = getUserInfo.get("sex").toString();
		// 用户的性别，值为1时是男性，值为2时是女性，值为0时是未知
		if (sex != null && "1".equals(sex)) {
			sex = SystemProperties.getProperties("sex.male");
		}else if (sex != null && "2".equals(sex)) {
			sex = SystemProperties.getProperties("sex.female");
		}else if (sex != null && "0".equals(sex)) {
			sex = "";
		}
		paramData.put("sex", sex);
		paramData.put("city",getUserInfo.get("city"));
		paramData.put("country", getUserInfo.get("country"));
		paramData.put("province", getUserInfo.get("province"));
		paramData.put("language", getUserInfo.get("language"));
		paramData.put("headimgurl", getUserInfo.get("headimgurl"));
		paramData.put("subscribe", getUserInfo.get("subscribe"));
		// 从数据库查询微信自定义菜单信息
		// rpc调用微信菜单接口
//		HttpRpcClient rpcClient = new HttpRpcClient();
//		String subscribeRpc = RpcUrl.Account_Subscribe;
//		HttpHeader defaultHeader = HttpHeader.custom();// 请求头
//		RpcResponse<Object> subscribeRes = rpcClient.post(subscribeRpc, paramData,defaultHeader, Object.class);
//		if (subscribeRes.getData() == null) {
//			throw new RuntimeException(SystemProperties.getProperties("database.error"));
//		}
//		Map<String, Object> account = (Map<String, Object>) subscribeRes.getData();
//		String returnContent="";
//		String subscribe = String.valueOf(account.get("subscribe"));
//		if ("2".equals(subscribe)) {
//			// 再关注
//			// 由于根据account获取的属性是关注之前的属性，所以再次关注时，subscribe修正为"2"(在再次关注之前，为取消状态)
//			returnContent = wxApiConfig.getWxApiParam().getSubscribeReply2();
//		} else {
//			// 首次关注
//			returnContent = wxApiConfig.getWxApiParam().getSubscribeReply();
//		}
		// 回复关注成功的文本消息
		RespNewsMessage respNewsMessage = new RespNewsMessage();
		respNewsMessage.setCreateTime(new Date().getTime());
		respNewsMessage.setFromUserName(reqMap.get("ToUserName"));
		respNewsMessage.setToUserName(openIdWX);
		respNewsMessage.setMsgType(WxApiConfig.MsgType_NEWS);
		respNewsMessage.setFuncFlag(0);
		List<Article> articleList = new ArrayList<Article>();
		
		Article article1 = new Article();
		article1.setTitle("");
        article1.setDescription("");
        // 修改素材地址
        article1.setPicUrl("https://mmbiz.qlogo.cn/mmbiz_png/cU89F2F6hIwU0myibWeGRiaSHu0xy9loPUObEJc8LLibeYqbOgb7StHFQxtIxZpbtiaGCsr6b2FP04n8WfdzXic6GNw/0?wx_fmt=png");
        String redictUrl = SystemProperties.getProperties("family_path") + "/app/main.html#/childrenManage?openIdWX="+openIdWX;
        article1.setUrl(redictUrl);
        
        Article article2 = new Article();
        article2.setTitle("下载极课同学，精准提分告别题海");
        article2.setDescription("");
        // 修改素材地址
        article2.setPicUrl("https://mmbiz.qlogo.cn/mmbiz_png/cU89F2F6hIwU0myibWeGRiaSHu0xy9loPU6czgyzebibzHn9fjnuFrhVuRMUh7pRroVnJw6nFpYUJps4rVwmlA2Ow/0?wx_fmt=png");
        article2.setUrl("http://a.app.qq.com/o/simple.jsp?pkgname=com.fclassroom.appstudentclient");
        
        articleList.add(article1);
        articleList.add(article2);
        
        respNewsMessage.setArticleCount(articleList.size());
        respNewsMessage.setArticles(articleList);
		return RespXmlBuilder.build(respNewsMessage);
	}
	
	/**
	 * 处理用户取消关注,把该用户account相关数据冻结 
	 * @param reqMap
	 */
	private void processUnsubscribeMsg(Map<String, String> reqMap) {
		String openIdWX = reqMap.get("FromUserName");
		Map<String, Object> paramData = new HashMap<String, Object>();
		paramData.put("openIdWX", openIdWX);
		paramData.put("subscribe", "2");
		// rpc调用微信菜单接口
//		HttpRpcClient rpcClient = new HttpRpcClient();
//		String updateParentInfoRpc = RpcUrl.Account_UpdateParentInfoByOpenIdWX;
//		HttpHeader defaultHeader = HttpHeader.custom();// 请求头
//		RpcResponse<Object> parentRes = rpcClient.post(updateParentInfoRpc, paramData, defaultHeader, Object.class);
//		if (parentRes.getData() == null) {
//			throw new RuntimeException(SystemProperties.getProperties("database.error"));
//		}
	}
	
	/**
	 * 处理上传用户地理位置的事件
	 * @param reqMap
	 */
	private String processLocationMsg(Map<String, String> reqMap) {
		String fromUserName = reqMap.get("FromUserName");
		String latitude = "";// 地理位置纬度
		String longitude = "";// 地理位置经度
		String precision = "";// 地理位置精度
		String scale = "";// 地图缩放大小
		String label = "";// 地理位置信息
		if (reqMap.get("Latitude") != null) {
			// 如果是自动上报，抓去此数据。
			latitude = reqMap.get("Latitude");// 地理位置纬度
			longitude = reqMap.get("Longitude");// 地理位置经度
			precision = reqMap.get("Precision");// 地理位置精度
		}else {
			// 如果是微信用户主动发送，会有此数据
			latitude = reqMap.get("Location_X");// 地理位置纬度
			longitude = reqMap.get("Location_Y");// 地理位置经度
			scale = reqMap.get("Scale");// 地图缩放大小
			label = reqMap.get("Label");// 地理位置信息
		}
		
		// 记录地理位置  
		Map<String, Object> paramData = new HashMap<String, Object>();
		paramData.put("openIdWX", fromUserName);
		paramData.put("longitude", longitude);
		paramData.put("latitude", latitude);
		paramData.put("precision", precision);
		paramData.put("scale", scale);
		paramData.put("label", label);
		// rpc调用更新家长信息接口
//		HttpRpcClient rpcClient = new HttpRpcClient();
//		String updateParentInfoRpc = RpcUrl.Account_UpdateParentInfoByOpenIdWX;
//		HttpHeader defaultHeader = HttpHeader.custom();// 请求头
//		RpcResponse<Object> parentRes = rpcClient.post(updateParentInfoRpc,paramData, defaultHeader, Object.class);
//		if (parentRes.getData() == null) {
//			throw new RuntimeException(SystemProperties.getProperties("database.error"));
//		}
		return "";
	}
	
	/**
	 * 接入会话
	 * 
	 * @param reqMap
	 */
	private String processKfCreateSession(Map<String, String> reqMap) {
		String openIdWX = reqMap.get("FromUserName");
		String fromUserName = reqMap.get("ToUserName");
		String kfAccount = reqMap.get("KfAccount");
		
		// 获取客服的基本信息
		String json = wxApiConfig.getKfInfo();
		@SuppressWarnings("unchecked")
		Map<String, Object> jsonMap = SerializeUtil.serializeJsonToObject(json, Map.class);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> kfList = (List<Map<String, Object>>) jsonMap.get("kf_list");
		if (kfList != null && kfList.size() > 0) {
			for (Map<String, Object> kfMap : kfList) {
				String kf_account = (String) kfMap.get("kf_account");
				Assert.notNull(kf_account, "kf_account is null");
				if (kf_account.equals(kfAccount)) {
					String kfNickName = (String) kfMap.get("kf_nick");
					Assert.notNull(kfNickName, "kfNickName is null");
					RespTextMessage respTextMessage = new RespTextMessage();
					respTextMessage.setContent(SystemProperties.getProperties("duokefu.getinsession.text"));
					respTextMessage.setCreateTime(System.currentTimeMillis());
					respTextMessage.setFromUserName(fromUserName);
					respTextMessage.setToUserName(openIdWX);
					String respXMLMessage = RespXmlBuilder.build(respTextMessage);
					return respXMLMessage;
				}
			}
		}
		return "";
	}
	
	/**
	 * 关闭会话
	 * @param reqMap
	 */
	private String processKfCloseSession(Map<String, String> reqMap) {
		String openIdWX = reqMap.get("FromUserName");
		String fromUserName = reqMap.get("ToUserName");
		// 发送会话关闭提醒
		RespTextMessage respTextMessage = new RespTextMessage();
		respTextMessage.setContent(SystemProperties.getProperties("duokefu.closesession.text"));
		respTextMessage.setCreateTime(System.currentTimeMillis());
		respTextMessage.setFromUserName(fromUserName);
		respTextMessage.setToUserName(openIdWX);
		String respXMLMessage = RespXmlBuilder.build(respTextMessage);
		return respXMLMessage;
	}
	/**
	 * 转接会话
	 * 
	 * @param reqMap
	 */
	private String processKfSwitchSession(Map<String, String> reqMap) {
		String fromUserName = reqMap.get("FromUserName");
		String openIdWX = reqMap.get("ToUserName");
		// 发送转接会话提醒
		RespTextMessage respTextMessage = new RespTextMessage();
		respTextMessage.setContent(SystemProperties.getProperties("duokefu.switch_kf"));
		respTextMessage.setCreateTime(System.currentTimeMillis());
		respTextMessage.setFromUserName(openIdWX);
		respTextMessage.setToUserName(fromUserName);
		String respXMLMessage = RespXmlBuilder.build(respTextMessage);
		return respXMLMessage;
	}
	
	/**
	 * 处理家长发送文本消息事件,将家长消息交给多客服系统处理
	 * @param reqMap
	 */
	@SuppressWarnings("unchecked")
	public String processTxtMsg(Map<String, String> reqMap) {
		String openIdWX = reqMap.get("FromUserName");
		String toUserName = reqMap.get("ToUserName");
		
		// 多客服 
		// 查询客户的会话状态
		String json = wxApiConfig.getCustomSessionStatus(openIdWX);
		Map<String, Object> jsonMap = SerializeUtil.serializeJsonToObject(json, Map.class);
		Assert.notNull(jsonMap, "jsonMap is null");
		Integer errcode = (Integer) jsonMap.get("errcode");
		String errmsg = (String) jsonMap.get("errmsg");
		String kfAccount = (String) jsonMap.get("kf_account");
		System.out.println(json);
		if (errcode != null && errcode == 0 && !SystemProperties.getProperties("duokefu.sessionnotexist").equals(errmsg)) {
			// 有会话,已经接入
			// 查看家长与客服会话的时间
			Integer createTime = (Integer) jsonMap.get("createtime");
			// 会话超过两小时
			if ((System.currentTimeMillis()-createTime*1000L) >Two_hours  && StringUtil.isNotBlank(kfAccount)) {
				LOGGER.warn(System.currentTimeMillis()+" "+createTime+"关闭会话，给客服发送超时和会话关闭提醒");
				// 关闭会话，给客服发送超时和会话关闭提醒
				LOGGER.warn("关闭会话，给客服发送超时和会话关闭提醒");
				wxApiConfig.closeKfSession(kfAccount, openIdWX, SystemProperties.getProperties("duokefu.timeout") + SystemProperties.getProperties("duokefu.closesession.text"));
				// 给用户发送超时提醒
				RespTextMessage respTextMessage = new RespTextMessage();
				respTextMessage.setContent(SystemProperties.getProperties("duokefu.timeout"));
				respTextMessage.setCreateTime(System.currentTimeMillis());
				respTextMessage.setFromUserName(toUserName);
				respTextMessage.setToUserName(openIdWX);
				String respXMLMessage = RespXmlBuilder.build(respTextMessage);
				return respXMLMessage;
			}
		}else{
			// 查询当前是否有在线客服
			json = wxApiConfig.getOnlineKfList();
			jsonMap = SerializeUtil.serializeJsonToObject(json, Map.class);
			Assert.notNull(jsonMap, "jsonMap is null");
			List<Map<String, Object>> kfOnlineList = (List<Map<String, Object>>) jsonMap.get("kf_online_list");
			if (kfOnlineList != null && kfOnlineList.size() > 0) {
				// 有在线客服
				// 发转接多客服系统消息
				LOGGER.warn("有在线客服 发转接多客服系统消息");
				RespTransferCustomerMessage respTransferCustomerMessage = new RespTransferCustomerMessage();
				respTransferCustomerMessage.setCreateTime(System
						.currentTimeMillis());
				respTransferCustomerMessage.setFromUserName(toUserName);
				respTransferCustomerMessage.setToUserName(openIdWX);
				String respXMLMessage = RespXmlBuilder
						.build(respTransferCustomerMessage);
				return respXMLMessage;
			} else {
				// 没有在线客服
				// 提示客服不在线
				LOGGER.warn("提示客服不在线");
				RespTextMessage respTextMessage = new RespTextMessage();
				respTextMessage.setContent(SystemProperties
						.getProperties("duokefu.nokfonline"));
				respTextMessage.setCreateTime(System.currentTimeMillis());
				respTextMessage.setFromUserName(toUserName);
				respTextMessage.setToUserName(openIdWX);
				String respXMLMessage = RespXmlBuilder.build(respTextMessage);
				return respXMLMessage;
			}
		}
		return null;
	}

	@Override
	public void createMenu() {
		// 获取菜单json
		String menuJson = wxApiConfig.getMenuJson();
		wxApiConfig.createMenu(menuJson);
	}

	@Override
	public String deleteMenu() {
		return wxApiConfig.deleteMenu();
	}

	@Override
	public String getMenu() {
		return wxApiConfig.getMenu();
	}

	@Override
	public String getOpenIdByAuthAccessToken(HttpServletRequest request) {
		// 本地测试代码
		//		String openIdWX = "oNdMruMfn4Y7dzuJ5Nq16NoWG9Lc"; 
		// 获取code
		String code = request.getParameter("code");
		Assert.notNull(code, "code is null");
		// 获取openIdWX
		String openIdWX = StringUtils.trimToNull(wxApiConfig.getOpenIdByAuthAccessToken(code));
		Assert.notNull(openIdWX, "openIdWX is null");
		return openIdWX;
	}

	@Override
	public Map<String, Object> wxPayMeth(HttpServletRequest request,
			HttpServletResponse response, String openIdWX, String totleFee,
			String goodsIds, Long studentId, String jikeNum, Long schoolId,
			Long gradeId, Long clzssId, String studentName, Long consigneeId)
			throws Exception {
		String[] goodsArray = goodsIds.split(",");
		// 生成随机字符串
		String noncestr = Sha1Util.getNonceStr();
		// 生成1970年到现在的秒数.
		String timestamp = Sha1Util.getTimeStamp();
		// 订单号，自定义生成规则，只要全局唯一就OK
		String out_trade_no = "WXTFJH" + System.currentTimeMillis();
		// 订单金额，应该是根据state（商品id）从数据库中查询出来
		String order_price = WxUtil.getMoney(totleFee);
		// 商品描述，应该是根据state（商品id）从数据库中查询出来
		String body = "招生缴费";

		/**
		 * 第三步：统一下单接口
		 * 需要第二步生成的openid，参考：https://pay.weixin.qq.com/wiki/doc/api/jsapi
		 * .php?chapter=9_1
		 */
		RequestHandler reqHandler = new RequestHandler(request, response);
		// 初始化 RequestHandler 类可以在微信的文档中找到.还有相关工具类
		reqHandler.init();

		// 微信分配的公众账号ID（企业号corpid即为此appId）
		reqHandler.setParameter("appid", wxApiConfig.getWxApiParam().getAppKey());
		// 微信支付分配的商户号
		reqHandler.setParameter("mch_id", SystemProperties.getProperties("PARTNERID"));
		// 终端设备号(门店号或收银设备ID)，注意：PC网页或公众号内支付请传"WEB"
		reqHandler.setParameter("device_info", "WEB");
		// 随机字符串，不长于32位。推荐随机数生成算法
		reqHandler.setParameter("nonce_str", noncestr);
		// 商品描述
		reqHandler.setParameter("body", body);
		// 商家订单号
		reqHandler.setParameter("out_trade_no", out_trade_no);
		// 商品金额,以分为单位
		reqHandler.setParameter("total_fee", order_price);
		// APP和网页支付提交用户端ip，Native支付填调用微信支付API的机器IP。
		reqHandler.setParameter("spbill_create_ip", "139.196.172.72");
		// 下面的notify_url是用户支付成功后为微信调用的action 异步回调.
		reqHandler.setParameter("notify_url", SystemProperties.getProperties("NOTIFY_URL"));
		// 交易类型,取值如下：JSAPI，NATIVE，APP，详细说明见参数规定
		reqHandler.setParameter("trade_type", "JSAPI");
		// ------------需要进行用户授权获取用户openid-------------
		reqHandler.setParameter("openid", openIdWX); // 这个必填.

		// 生成签名，并且转为xml
		String requestXml = reqHandler.getRequestXml();
		System.out.println("requestXml:" + requestXml);

		// 得到的预支付id
		String prepay_id = unifiedorder(requestXml);
		// 返回数据Map
		SortedMap<String, Object> returnMap = new TreeMap<String, Object>();
		if (StringUtil.isNotEmpty(prepay_id)){
			Map<String, Object> paramData = new HashMap<String, Object>();
			paramData.put("studentId", studentId);
			paramData.put("prepayId", out_trade_no);
			paramData.put("totleFee", totleFee);
			paramData.put("amount", goodsArray.length);
			paramData.put("openIdWX", openIdWX);
			paramData.put("goodsIds", goodsIds);
			paramData.put("jikeNum", jikeNum);
			paramData.put("schoolId", schoolId);
			paramData.put("clzssId", clzssId);
			paramData.put("gradeId", gradeId);
			paramData.put("studentName", studentName);
			paramData.put("consigneeId", consigneeId);
//			HttpRpcClient rpcClient = new HttpRpcClient();
//			String rpcUrl = RpcUrl.Family_addOrderInfo;
//			HttpHeader defaultHeader = HttpHeader.custom();
//			RpcResponse<Object> res = rpcClient.post(rpcUrl, paramData, defaultHeader, Object.class);
//			if (!res.getErrorCode().equals("0")){
//				// 查询数据库失败
//				throw new RuntimeException(SystemProperties.getProperties("database.error"));
//			}
//			Map<String, Object> dataMap = (Map<String, Object>) res.getData();
//			if (dataMap != null && dataMap.size() > 0){
//				returnMap.put("orderId", dataMap.get("orderId"));
//				returnMap.put("orderNum", dataMap.get("orderNum"));
//			} else {
//				// 查询数据库失败
//				throw new RuntimeException(SystemProperties.getProperties("database.error"));
//			}
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("appId", wxApiConfig.getWxApiParam().getAppKey());
		params.put("timeStamp", timestamp);
		params.put("nonceStr", noncestr);
		params.put("package", "prepay_id=" + prepay_id);
		params.put("signType", "MD5");

		// 生成支付签名,这个签名 给 微信支付的调用使用
		SortedMap<String, Object> signMap = new TreeMap<String, Object>();
		signMap.put("appId", wxApiConfig.getWxApiParam().getAppKey());
		signMap.put("timeStamp", timestamp);
		signMap.put("nonceStr", noncestr);
		signMap.put("package", "prepay_id=" + prepay_id);
		signMap.put("signType", "MD5");
		// 微信支付签名
		String paySign = MD5Util.createSign(signMap, SystemProperties.getProperties("PARTNERID_KEY"));

		returnMap.put("appId", wxApiConfig.getWxApiParam().getAppKey());
		returnMap.put("timeStamp", timestamp);
		returnMap.put("nonceStr", noncestr);
		returnMap.put("package", "prepay_id=" + prepay_id);
		returnMap.put("signType", "MD5");
		returnMap.put("paySign", paySign);

		return returnMap;
	}
	
	/**
	 * 统一下单接口 参考文档：https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_1
	 * @return
	 * @throws Exception
	 */
	private static String unifiedorder(String requestXml) throws Exception {
		// 统一下单接口提交 xml格式
		URL orderUrl = new URL("https://api.mch.weixin.qq.com/pay/unifiedorder");
		HttpURLConnection conn = (HttpURLConnection) orderUrl.openConnection();
//		conn.setConnectTimeout(30000); // 设置连接主机超时（单位：毫秒)
//		conn.setReadTimeout(30000); // 设置从主机读取数据超时（单位：毫秒)
		conn.setDoOutput(true); // post请求参数要放在http正文内，顾设置成true，默认是false
		conn.setDoInput(true); // 设置是否从httpUrlConnection读入，默认情况下是true
		conn.setUseCaches(false); // Post 请求不能使用缓存
		// 设定传送的内容类型是可序列化的java对象(如果不设此项,在传送序列化对象时,当WEB服务默认的不是这种类型时可能抛java.io.EOFException)
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestMethod("POST");// 设定请求的方法为"POST"，默认是GET
		conn.connect();
		OutputStream outputStream = conn.getOutputStream();
		// 注意编码格式	
		outputStream.write(requestXml.getBytes("UTF-8"));
        outputStream.close(); 
        InputStream inputStream = conn.getInputStream();  
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");  
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);  
        String str = null;  
        StringBuffer buffer = new StringBuffer();  
        while ((str = bufferedReader.readLine()) != null) {  
            buffer.append(str);  
        }  
        // 释放资源  
        bufferedReader.close();  
        inputStreamReader.close();  
        inputStream.close();  
        inputStream = null;  
        conn.disconnect();
		System.out.println("result=========返回的xml=============" + buffer.toString());
		@SuppressWarnings("unchecked")
		Map<String, String> orderMap = XMLUtil.doXMLParse(buffer.toString());
		System.out.println("orderMap===========================" + orderMap);
		// 获取
		String prepay_id = orderMap.get("prepay_id");
		return prepay_id;
	}

	@Override
	public List<User> getUserInfo() {
		return userDaoImpl.getUser();
	}
}
