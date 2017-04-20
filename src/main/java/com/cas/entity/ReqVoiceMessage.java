package com.cas.entity;

import java.util.Map;

public class ReqVoiceMessage extends ReqBaseMessage {

	//语音消息媒体id，可以调用多媒体文件下载接口拉取数据。
	private String mediaId;
	//语音格式，如amr，speex等
	private String format;
	
	public ReqVoiceMessage(Map<String, String> requestMap){
		this.bind(requestMap);
	}
	
	public String getMediaId() {
		return mediaId;
	}
	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
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
		
		String mediaId = requestMap.get("MediaId");
		String format = requestMap.get("Format");
		
		setCreateTime(createTime);
		setFromUserName(fromUserName);
		setMsgId(msgId);
		setMsgType(msgType);
		setToUserName(toUserName);		
		
		setMediaId(mediaId);
		setFormat(format);
	}
	
}
