package com.cas.util;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.cas.entity.Article;
import com.cas.entity.RespBaseMessage;
import com.cas.entity.RespMusicMessage;
import com.cas.entity.RespNewsMessage;
import com.cas.entity.RespTextMessage;
import com.cas.entity.RespTransferCustomerMessage;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

/**
 * 将回复消息转化成相应的XML，以便微信服务器接收
 * 
 * @author kingson·liu
 *
 */
public class RespXmlBuilder {

	private static String encoding = "utf-8";
	private static Configuration config = initConfig();

	private RespXmlBuilder() {
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String build(RespBaseMessage respMessage) {
		if (respMessage == null)
			throw new IllegalArgumentException("参数RespBaseMessage不能为 null");

		Map root = new HashMap();
		root.put("@msg", respMessage);

		try {
			Template template = config.getTemplate(respMessage.getClass().getSimpleName(), encoding);
			StringWriter sw = new StringWriter();
			template.process(root, sw);
			return sw.toString();
		} catch (freemarker.core.InvalidReferenceException e) {
			throw new RuntimeException("可能是 " + respMessage.getClass().getSimpleName() + " 对象中的某些属性未赋值，请仔细检查", e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static Configuration initConfig() {
		Configuration config = new Configuration();
		StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
		initStringTemplateLoader(stringTemplateLoader);
		config.setTemplateLoader(stringTemplateLoader);

		config.setTemplateUpdateDelay(999999);
		config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		config.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);
		config.setDefaultEncoding(encoding);
		config.setOutputEncoding(encoding);
		config.setLocale(Locale.getDefault());
		config.setLocalizedLookup(false);

		config.setNumberFormat("#0.#####");
		config.setDateFormat("yyyy-MM-dd");
		config.setTimeFormat("HH:mm:ss");
		config.setDateTimeFormat("yyyy-MM-dd HH:mm:ss");
		return config;
	}

	private static void initStringTemplateLoader(StringTemplateLoader loader) {
		loader.putTemplate(RespTextMessage.class.getSimpleName(), RespTextMessage.TEMPLATE);
		loader.putTemplate(RespNewsMessage.class.getSimpleName(), RespNewsMessage.TEMPLATE);
		loader.putTemplate(RespMusicMessage.class.getSimpleName(), RespMusicMessage.TEMPLATE);
		loader.putTemplate(RespTransferCustomerMessage.class.getSimpleName(), RespTransferCustomerMessage.TEMPLATE);
	}

	public static void setEncoding(String encoding) {
		RespXmlBuilder.encoding = encoding;
	}

	public static String getEncoding() {
		return encoding;
	}

	public static void main(String[] args) {
//		// 文本消息回复xml
//		RespTextMessage msg = new RespTextMessage();
//		msg.setToUserName("ToUserName");
//		msg.setFromUserName("FromUserName");
//		msg.setCreateTime(20140101L);
//		msg.setContent("RespMessage消息内容");
//		String xml = RespXmlBuilder.build(msg);
//		System.out.println(xml);
		
//		// 图文消息回复xml
//		RespNewsMessage respNewsMessage = new RespNewsMessage();
//		respNewsMessage.setArticleCount(2);
//		
//		// item项
//		List<Article> articles = new ArrayList<Article>();
//		Article article1 = new Article();
//		// item1
//		article1.setDescription("description1");
//		article1.setPicUrl("picUrl1");
//		article1.setUrl("url1");
//		article1.setTitle("title1");
//		articles.add(article1);
//		// item2
//		Article article2 = new Article();
//		article2.setDescription("description2");
//		article2.setPicUrl("picUrl2");
//		article2.setUrl("url2");
//		article2.setTitle("title2");
//		articles.add(article2);
//		respNewsMessage.setArticles(articles);
//		
//		respNewsMessage.setCreateTime(20140303L);
//		respNewsMessage.setFromUserName("fromUserName");
//		respNewsMessage.setToUserName("toUserName");
//		String xml = RespXmlBuilder.build(respNewsMessage);
//		System.out.println(xml);
		
		// 图文消息回复xml
		RespNewsMessage respNewsMessage = new RespNewsMessage();
		respNewsMessage.setArticleCount(1);
		
		// item项
		List<Article> articles = new ArrayList<Article>();
		Article article1 = new Article();
		// item1
		article1.setDescription("description1");
		article1.setPicUrl("picUrl1");
		article1.setUrl("url1");
		article1.setTitle("title1");
		articles.add(article1);

		respNewsMessage.setArticles(articles);
		
		respNewsMessage.setCreateTime(20140303L);
		respNewsMessage.setFromUserName("fromUserName");
		respNewsMessage.setToUserName("toUserName");
		String xml = RespXmlBuilder.build(respNewsMessage);
		System.out.println(xml);
	}
}
