package com.cas.entity;


public class RespMusicMessage extends RespBaseMessage {
	// 回复的音乐消息的XML模板
	public static final String TEMPLATE =
			"<xml>\n" +
				"<ToUserName><![CDATA[${@msg.toUserName}]]></ToUserName>\n" +
				"<FromUserName><![CDATA[${@msg.fromUserName}]]></FromUserName>\n" +
				"<CreateTime>${@msg.createTime}</CreateTime>\n" +
				"<MsgType><![CDATA[${@msg.msgType}]]></MsgType>\n" +
					"<Music>\n" +
						"<Title><![CDATA[${(@msg.title)!}]]></Title>\n" +
						"<Description><![CDATA[${(@msg.description)!}]]></Description>\n" +
						"<MusicUrl><![CDATA[${(@msg.musicUrl)!}]]></MusicUrl>\n" +
						"<HQMusicUrl><![CDATA[${(@msg.hqMusicUrl)!}]]></HQMusicUrl>\n" +
						// 官司方文档错误，无此标记: "<ThumbMediaId><![CDATA[${@msg.thumbMediaId}]]></ThumbMediaId>\n" +
						"<FuncFlag>${@msg.funcFlag}</FuncFlag>\n" +
					"</Music>\n" +
			"</xml>";
	
	// 音乐
	private Music Music;

	public RespMusicMessage(){
		super.setMsgType(WxApiConfig.MsgType_MUSIC);
	}

	public Music getMusic() {
		return Music;
	}

	public void setMusic(Music music) {
		Music = music;
	}
}