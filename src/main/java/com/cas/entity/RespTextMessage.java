package com.cas.entity;

import com.cas.framework.utils.SystemProperties;
import com.cas.util.RespXmlBuilder;

public class RespTextMessage extends RespBaseMessage {
	// 回复的文本消息的XML模板
	public static final String TEMPLATE =
			"<xml>\n" +
			"<ToUserName><![CDATA[${@msg.toUserName}]]></ToUserName>\n" +
			"<FromUserName><![CDATA[${@msg.fromUserName}]]></FromUserName>\n" +
			"<CreateTime>${@msg.createTime}</CreateTime>\n" +
			"<MsgType><![CDATA[${@msg.msgType}]]></MsgType>\n" +
			"<Content><![CDATA[${@msg.content}]]></Content>\n" +
			"</xml>";
	
	// 回复的消息内容
	private String Content;

	public RespTextMessage(){
		super.setMsgType(WxApiConfig.MsgType_TEXT);
	}


	public String getContent() {
		return Content;
	}

	public void setContent(String content) {
		Content = content;
	}
	
	public static void main(String[] args) {
		RespTextMessage respTextMessage = new RespTextMessage();
		respTextMessage.setContent(SystemProperties.getProperties("duokefu.noregist"));
		respTextMessage.setCreateTime(System.currentTimeMillis());
		respTextMessage.setFromUserName("s");
		respTextMessage.setToUserName("ss");
		String respXMLMessage = RespXmlBuilder.build(respTextMessage);
		System.out.println(respXMLMessage);
	}
}