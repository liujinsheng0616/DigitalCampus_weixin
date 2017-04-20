package com.cas.entity;

import java.util.Map;

public class ReqImageMessage extends ReqBaseMessage {
	// 图片链接
	private String picUrl;
	// 图片消息媒体id，可以调用多媒体文件下载接口拉取数据。
	private String mediaId;
	
	
	public ReqImageMessage(Map<String, String> requestMap){
		this.bind(requestMap);
	}
	
	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
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
		String picUrl = requestMap.get("PicUrl");
		String mediaId = requestMap.get("MediaId");
		
		setCreateTime(createTime);
		setFromUserName(fromUserName);
		setMsgId(msgId);
		setMsgType(msgType);
		setToUserName(toUserName);
		
		setMediaId(mediaId);
		setPicUrl(picUrl);
	}
}
