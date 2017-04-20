package com.cas.entity;

/**
 * 转发到多客服的回复消息
 * 
 * @author zhuzhen
 *
 */
public class RespTransferCustomerMessage extends RespBaseMessage{
	// 回复消息的XML模板
	public static final String TEMPLATE =
			"<xml>\n" +
			"<ToUserName><![CDATA[${@msg.toUserName}]]></ToUserName>\n" +
			"<FromUserName><![CDATA[${@msg.fromUserName}]]></FromUserName>\n" +
			"<CreateTime>${@msg.createTime}</CreateTime>\n" +
			"<MsgType><![CDATA[${@msg.msgType}]]></MsgType>\n" +
			"</xml>";
	
	public RespTransferCustomerMessage(){
		super.setMsgType(WxApiConfig.MsgType_TRANSFER_CUSTOMER);
	}
}
