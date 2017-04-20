package com.cas.controller.interceptor;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.cas.ErrorCode;
import com.cas.framework.utils.SystemProperties;

/**
 * 项目拦截器
 * @author kingson·liu
 * @date 2017年04月10日
 * */
public class ControlHandlerInterceptor extends HandlerInterceptorAdapter {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private static final String ENCODING_DEFAULT = "UTF-8";
	private static final String ContentType_json = "application/json;charset=" + ENCODING_DEFAULT;
	//private static final String ContentType_html = "text/html;charset=" + ENCODING_DEFAULT;
	@Autowired
	private MessageSource messageSource;
	
//	private HttpRpcClient rpcClient=new HttpRpcClient();
	/**
	 * 支持跨域的域名
	 */
	private List<String> accessAllowedFrom;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String local = request.getParameter("locale");
//		String code = ReportErrorCode.Parameter_required;
		if ("zh".equals(local))
			LocaleContextHolder.setLocale(Locale.CHINESE, true);
		else if ("en".equals(local))
			LocaleContextHolder.setLocale(Locale.ENGLISH, true);
		else {
			LocaleContextHolder.setLocale(request.getLocale(), true);
		}
		request.setAttribute("site_title",messageSource.getMessage("site_title", null, "", LocaleContextHolder.getLocale()));
		response.setContentType(ContentType_json);
		response.setCharacterEncoding(ENCODING_DEFAULT);
		request.setCharacterEncoding(ENCODING_DEFAULT);
		String url = getRequestUrl(request);

		if("options".equals(request.getMethod().toLowerCase())){//说明是跨域请求
			return true;
		}
		// 不需要拦截的url在system.properties中设置ignore_url,微信请求只跟openID有关，跟accessToken无关
		if (SystemProperties.getIgnoreUrl().indexOf(url) > 0) {
			return true;
		}
		try {
//			//下面的代码为示例写法
//			String openIdWX = request.getParameter("openIdWX");
//			if (StringUtil.isNotEmpty(openIdWX)){
//				String rpcUrl = RpcUrl.Account_getAccountByOpenIdWX;
//				Map<String,Object> param=new HashMap<String,Object>();
//				param.put("openIdWX", openIdWX);
//				param.put("jike-client-from", RemoveHeaderUtil.getHttpHeader(request));
//				HttpHeader defaultHeader = HttpHeader.custom();
//				//RpcResponse<String> res=rpcClient.get(rpcUrl, defaultHeader, String.class);
//				String res=rpcClient.post(rpcUrl, param, defaultHeader);
//				String jsonResult = res;
//				if (StringUtil.isNotEmpty(jsonResult)) {
//					request.setAttribute("SESSION_ACCOUNT", jsonResult);
//					return true;
//				}
//			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		response.setContentType(ContentType_json);
		response.getWriter()
				.write("{\"error_code\":" + ErrorCode.ACCESS_TOKEN_null + ",\"error_msg\":\""
						+ messageSource.getMessage(ErrorCode.ACCESS_TOKEN_null, null, ErrorCode.ACCESS_TOKEN_null, LocaleContextHolder.getLocale()) + "\",\"data\":"
						+ null + "}");
		return false;

	}
	
	public List<String> getAccessAllowedFrom() {
		return accessAllowedFrom;
	}

	public void setAccessAllowedFrom(List<String> accessAllowedFrom) {
		this.accessAllowedFrom = accessAllowedFrom;
	}

	/**获取req里面的相对url路径
	 * like "/action!method.action"
	 */
	private String getRequestUrl(HttpServletRequest request) {
		String url = request.getRequestURI();
		String path = request.getContextPath();
		if (StringUtils.isNotEmpty(path)) {
			return url.substring(path.length());
		}
		return url;
	}
	 
}
