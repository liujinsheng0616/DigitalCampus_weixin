package com.cas.entity;

import java.util.Map;
public class ReqLinkMessage extends ReqBaseMessage {

	// 消息标题
	private String title;
	// 消息描述
	private String description;
	// 消息链接
	private String url;
	
	public ReqLinkMessage(Map<String, String> requestMap){
		this.bind(requestMap);
	}
	
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public void bind(Map<String, String> requestMap) {
		// 消息类型
		String msgType = requestMap.get("MsgType");
		// 消息id
		String msgId = requestMap.get("MsgId");
		// 发送方帐号（一个OpenID）
		String fromUserName = requestMap.get("FromUserName");
		// 开发者微信号（公众帐号）
		String toUserName = requestMap.get("ToUserName");
		//消息创建时间
		String createTime = requestMap.get("CreateTime");
		
		String title = requestMap.get("Title");
		String description = requestMap.get("Description");
		String url = requestMap.get("Url");
		
		setCreateTime(createTime);
		setFromUserName(fromUserName);
		setMsgId(msgId);
		setMsgType(msgType);
		setToUserName(toUserName);
		
		setTitle(title);
		setDescription(description);
		setUrl(url);
	}

}
