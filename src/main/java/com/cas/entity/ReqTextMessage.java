package com.cas.entity;

import java.util.Map;

public class ReqTextMessage extends ReqBaseMessage {
	// 文本消息内容
	private String content;
	
	
	public ReqTextMessage(Map<String, String> requestMap){
		this.bind(requestMap);
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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
		// 文本内容
		String content = requestMap.get("Content");
		
		setContent(content);
		setCreateTime(createTime);
		setFromUserName(fromUserName);
		setMsgId(msgId);
		setMsgType(msgType);
		setToUserName(toUserName);
	}
	
}
