package com.cas.controller;

import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.cas.entity.WxApiConfig;
import com.cas.framework.base.BaseController;
import com.cas.framework.utils.SerializeUtil;

public class CommonController extends BaseController {
	public final static String Success = "0"; //操作成功
	public final static String System_api_unsuport = "10006"; //缺少参数
	public final static String GoodsInfo_error = "goodsInfoerror"; //缺少参数
	public final static String Failure = "-1";//操作失败
	public static final Logger log = LoggerFactory.getLogger(CommonController.class);
	@Autowired
	public WxApiConfig wxApiConfig;
	
	/**
	 * Render data to json for i18n
	 * @param errorCode - error code
	 * @return json
	 */
	public ResponseEntity<String> renderData(String errorCode) {
		return renderData(errorCode, null);
	}
	/**
	 * Render data to json for i18n
	 * @param errorCode - error code
	 * @param obj - object
	 * @return json
	 */
	public ResponseEntity<String> renderData(String errorCode, Object obj) {
		String errorMsg = messageSource.getMessage(errorCode, null, errorCode, LocaleContextHolder.getLocale());
		return renderData(errorCode, errorMsg, obj);
	}
	/**
	 * Render data to json
	 * @param errorCode - error code
	 * @param error - error message
	 * @param obj - object
	 * @return json
	 */
	public ResponseEntity<String> renderData(String errorCode, String error, Object obj) {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("\"error_code\":" + errorCode + ",");
		sb.append("\"error_msg\":\"" + error + "\",");
		if (obj != null) {
			sb.append("\"data\":" + SerializeUtil.serializeToJson(obj));
		} else {
			sb.append("\"data\":" + null + "");
		}
		sb.append("}");
		ResponseEntity<String> responseEntity = new ResponseEntity<String>(sb.toString(), initHttpHeadersJson(errorCode),
				HttpStatus.OK);
		return responseEntity;
	}
	
	/**
	 * 初始化HTTP头.
	 * @return HttpHeaders
	 */
	protected HttpHeaders initHttpHeadersJson() {
		HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = new MediaType("application", "json", Charset.forName("utf-8"));
		headers.setContentType(mediaType);
		return headers;
	}
	/**
	 * 只有成功才记录业务操作日志
	 * @param errorCode
	 * @return
	 */
	private HttpHeaders initHttpHeadersJson(String errorCode) {
		HttpHeaders headers = initHttpHeadersJson();
		if(errorCode.equals(Success)) {
			headers.add("needRecortBusinessLog", "1");
		}
		return headers;
	}
	

}
