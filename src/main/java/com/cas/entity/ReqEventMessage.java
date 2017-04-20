package com.cas.entity;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
public class ReqEventMessage extends ReqBaseMessage {
	// 事件类型
	private String event;
	private String eventKey;
	private String ticket;

	// 地理位置纬度
	private String latitude;
	// 地理位置经度
	private String longitude;
	// 地理位置精度
	private String precision;
	
	public ReqEventMessage(Map<String, String> requestMap){
		this.bind(requestMap);
	}
	
	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getEventKey() {
		return eventKey;
	}

	public void setEventKey(String eventKey) {
		this.eventKey = eventKey;
	}

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getPrecision() {
		return precision;
	}

	public void setPrecision(String precision) {
		this.precision = precision;
	}

	@Override
	public void bind(Map<String, String> requestMap) {
		// 消息类型
		String msgType = requestMap.get("MsgType");
		// 发送方帐号（一个OpenID）
		String fromUserName = requestMap.get("FromUserName");
		// 开发者微信号（公众帐号）
		String toUserName = requestMap.get("ToUserName");
		//消息创建时间
		String createTime = requestMap.get("CreateTime");
		String event = requestMap.get("Event");
		String eventKey = StringUtils.isBlank(requestMap.get("EventKey"))?"":requestMap.get("EventKey");
		String ticket = StringUtils.isBlank(requestMap.get("Ticket"))?"":requestMap.get("Ticket");
		String latitude = StringUtils.isBlank(requestMap.get("Latitude"))?"":requestMap.get("Latitude");
		String longitude = StringUtils.isBlank(requestMap.get("Longitude"))?"":requestMap.get("Longitude");
		String precision = StringUtils.isBlank(requestMap.get("Precision"))?"":requestMap.get("Precision");
		setCreateTime(createTime);
		setFromUserName(fromUserName);
		setMsgType(msgType);
		setToUserName(toUserName);		
		setEvent(event);
		setEventKey(eventKey);
		setTicket(ticket);
		setLatitude(latitude);
		setLongitude(longitude);
		setPrecision(precision);
	}

}
