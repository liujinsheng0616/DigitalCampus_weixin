package com.cas.entity;

import java.util.List;

public class RespNewsMessage extends RespBaseMessage {
	// 回复的图文消息的XML模板
	public static final String TEMPLATE =
			"<xml>\n" +
			"<ToUserName><![CDATA[${@msg.toUserName}]]></ToUserName>\n" +
			"<FromUserName><![CDATA[${@msg.fromUserName}]]></FromUserName>\n" +
			"<CreateTime>${@msg.createTime}</CreateTime>\n" +
			"<MsgType><![CDATA[${@msg.msgType}]]></MsgType>\n" +
				"<ArticleCount>${@msg.getArticleCount()}</ArticleCount>\n" +
				"<Articles>\n" +
					"<#list @msg.getArticles() as x>\n"+
						"<item>\n" +
							"<Title><![CDATA[${(x.title)!}]]></Title>\n" + 
							"<Description><![CDATA[${(x.description)!}]]></Description>\n" +
							"<PicUrl><![CDATA[${(x.picUrl)!}]]></PicUrl>\n" +
							"<Url><![CDATA[${(x.url)!}]]></Url>\n" +
						"</item>\n" +
					"</#list>\n" +
				"</Articles>\n" +
			"</xml>";
	
	// 图文消息个数，限制为10条以内
	private int ArticleCount;
	// 多条图文消息信息，默认第一个item为大图
	private List<Article> Articles;

	public RespNewsMessage(){
		super.setMsgType(WxApiConfig.MsgType_NEWS);
	}


	public int getArticleCount() {
		return ArticleCount;
	}

	public void setArticleCount(int articleCount) {
		ArticleCount = articleCount;
	}

	public List<Article> getArticles() {
		return Articles;
	}

	public void setArticles(List<Article> articles) {
		Articles = articles;
	}
}