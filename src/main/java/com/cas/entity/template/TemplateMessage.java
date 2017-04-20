package com.cas.entity.template;

/**
 * 模板消息
 * 
 * @author kingson·liu
 *
 */
public abstract class TemplateMessage {
	// 接收方OpenId
	private String touser;
	
	// 模板Id
	private String template_id;
	
	private String url;
	
	private String topcolor = "";

	public String getTouser() {
		return touser;
	}

	public void setTouser(String touser) {
		this.touser = touser;
	}

	public String getTemplate_id() {
		return template_id;
	}

	public void setTemplate_id(String template_id) {
		this.template_id = template_id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTopcolor() {
		return topcolor;
	}

	public void setTopcolor(String topcolor) {
		this.topcolor = topcolor;
	}
}
