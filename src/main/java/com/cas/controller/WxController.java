
 package com.cas.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cas.ErrorCode;
import com.cas.entity.WxApiConfig;
import com.cas.framework.utils.StringUtil;
import com.cas.framework.utils.SystemProperties;
import com.cas.service.WeixinService;

/**
 * 微信业务入口，微信公众号第三方平台
 * @author kingson·liu
 * @date 2017年04月10日
 * */

@Controller("wxController")
@RequestMapping(value = "/wx")
public class WxController extends CommonController {
	
	@Autowired
	private WeixinService wxServiceImpl;
	/**
	 * 微信配置接口,处理配置服务器时候的响应
	 * @throws Exception
	 */
	@RequestMapping(value = "/reply",method = RequestMethod.GET)
	@ResponseBody
	public String signature(Model model, HttpServletRequest request) throws Exception {
		String signature = request.getParameter("signature");
	    String timestamp = request.getParameter("timestamp");
	    String nonce = request.getParameter("nonce");
		if (!WxApiConfig.checkSignature(wxApiConfig.getWxApiParam().getAccessToken(),signature, timestamp, nonce)) {
			return request.getParameter("echostr");
        }
		return "";
	}
	
	/**
	 * 接收微信用户的消息，并回复消息
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/reply", method = RequestMethod.POST)
	public void reply(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			// 处理微信用户的消息
			Map<String, String> reqMap = WxApiConfig.parseXml(request);
			// 回复信息
			log.debug("接受微信用户内容：",reqMap);
			String returnStr = wxServiceImpl.processMsg(reqMap);
			log.debug("返回微信用户内容：",returnStr);		
			if (!StringUtil.isBlank(returnStr)) {
				// 回复消息 
				response.setCharacterEncoding("UTF-8");
				// 以XML格式返回消息
				response.setContentType("text/xml");
				response.getWriter().print(returnStr);
			}
		} catch (Exception e) {
			log.warn(this.getClass() + " " + e.getMessage());
		}
	}
	/**
	 * 菜单创建
	 * 
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/createMenu")
	public void createMenu(HttpServletResponse response) throws Exception {
		// 创建菜单
		wxServiceImpl.createMenu();
		renderText(response, SystemProperties.getProperties("createmenu.success"));
	}
	/**
	 * 菜单删除
	 * 
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/deleteMenu")
	public void deleteMenu(HttpServletResponse response) throws Exception {
		// 删除菜单,并显示结果
		renderText(response, wxServiceImpl.deleteMenu());
	}
	/**
	 * 菜单查询
	 * 
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/getMenu")
	public void getMenu(HttpServletResponse response) throws Exception {
		// 显示查询的菜单
		renderText(response, wxServiceImpl.getMenu());
	}
	
	/**
	 * [对应微信菜单：学习情况] 跳转到到微网站成绩单页面
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "/toLearnSituation")
	public String toLearnSituation(HttpServletRequest request) throws Exception {
		String openIdWX = request.getParameter("openIdWX");
		if (StringUtil.isBlank(openIdWX)) {
			// 根据网页授权机制获取openIdWX
			openIdWX = wxServiceImpl.getOpenIdByAuthAccessToken(request); 
		}
		Assert.notNull(openIdWX, "openIdWX is null");
		
		String redirectUrl = "/app/main.html#/childrenManage?openIdWX="+openIdWX;
		return "redirect:" + redirectUrl;
	}
	
	/**
	 * 模板消息1：微信家长绑定孩子成功。发送绑定成功的模板消息。
	 * @param request
	 * @param redirectAttributes
	 */
	@RequestMapping(value = "/bindSuccess")
	public String sendBindTempMsg(HttpServletRequest request) {
		String openIdWX = request.getParameter("openIdWX");
		String studentIdStr = request.getParameter("studentId");
		
		Assert.notNull(openIdWX, "openIdWX is null");
		Assert.notNull(studentIdStr, "studentIdStr is null");
		
		// 发生模板消息
		try {
//			wxServiceImpl.sendBindTemplateMessage(request);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Long studentId = Long.parseLong(studentIdStr);
		String url = "/app/main.html#/index?openIdWX="+openIdWX+"&studentId="+studentId;
		// 微信内嵌网站首页
		return "redirect:" + url;
	}
	
	/**
	 * 发送微信JS-SDK权限验证的参数到页面
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/sendJssdkParameters")
	public ResponseEntity<String> sendJssdkParameters(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap)
	{
		String jsapi_ticket = wxApiConfig.getWxApiParam().getJsApiTicket();
		//获取完整的URL地址
		String url = request.getParameter("url");
		Map<String, String> data = wxApiConfig.sign(jsapi_ticket, url);
		modelMap.put("timestamp", data.get("timestamp"));
		modelMap.put("nonceStr", data.get("nonceStr"));
		modelMap.put("signature", data.get("signature"));
		modelMap.put("appId", data.get("appId"));
		return renderData(Success, modelMap);
	}

	/**
     * [创建分组] 
     * POST数据例子：{"group":{"name":"test"}}
     * @throws Exception
     */
    @RequestMapping(value = "/createGroup")
    public ResponseEntity<String> createGroup(HttpServletRequest request, String jsonData) throws Exception {
        try{
            return renderData(Success,  wxApiConfig.createGroup(jsonData));
        } catch(Exception e){
            e.printStackTrace();
            return renderData(Failure, "system error");
        }
    }
 
    /**
     * [获取所有分组] 
     * 
     * @throws Exception
     */
    @RequestMapping(value = "/getAllGroup")
    public ResponseEntity<String>  getAllGroup(HttpServletRequest request) throws Exception {
    	return renderData(Success, wxApiConfig.getAllGroup());

    }
    /**
     * [获取用户所在组] 
     * 
     * @throws Exception
     */
    @RequestMapping(value = "getGroupById")
    public ResponseEntity<String>  getGroupById(HttpServletRequest request) throws Exception {
        Map<String,Object> paramMap = wxApiConfig.getUserInfoByAuthAccessToken(request);
        String openIdWX = paramMap.get("openIdWX").toString();
        return renderData(Success,wxApiConfig.getGroupById(openIdWX));
    }
    
    /**
     * [修改用户所在组] 
     * 
     * @throws Exception
     */
    @RequestMapping(value = "updateGroup")
    public ResponseEntity<String> updateGroup(HttpServletRequest request,String id,String name) throws Exception {
    	return renderData(Success,wxApiConfig.updateGroup(id, name));
    }
    
    /**
     * [移动用户到其他组] 
     * 
     * @throws Exception
     */
    @RequestMapping(value = "changeGroup")
    public ResponseEntity<String> changeGroup(HttpServletRequest request,String togroupid) throws Exception {
        Map<String,Object> paramMap = wxApiConfig.getUserInfoByAuthAccessToken(request);
        String openIdWX = paramMap.get("openIdWX").toString();
        try{
            wxApiConfig.changeGroup(openIdWX, togroupid);
            return renderData(Success, true);
        } catch(Exception e){
            e.printStackTrace();
            return renderData(Failure, "system error");
        }
    }
    
    /**
     * [批量移动用户到其他组] 
     * jsonData:POST数据例子：{"openid_list":["oDF3iYx0ro3_7jD4HFRDfrjdCM58","oDF3iY9FGSSRHom3B-0w5j4jlEyY"],"to_groupid":108}
     * @throws Exception
     */
    @RequestMapping(value = "batchChangeGroup")
    public ResponseEntity<String> batchChangeGroup(HttpServletRequest request,String jsonData) throws Exception {
        try{
            wxApiConfig.batchChangeGroup(jsonData);
            return renderData(Success, true);
        } catch(Exception e){
            e.printStackTrace();
            return renderData(Failure, "system error");
        }
    }
    
    /**
     * [删除分组] 
     * jsonData:POST数据例子：POST数据例子：{"group":{"id":108}}
     * @throws Exception
     */
    @RequestMapping(value = "deleteGroup")
    public ResponseEntity<String> deleteGroup(HttpServletRequest request,String jsonData) throws Exception {
        try{
            wxApiConfig.deleteGroup(jsonData);
            return renderData(Success, true);
        } catch(Exception e){
            e.printStackTrace();
            return renderData(Failure, "system error");
        }
    }
    
    @RequestMapping(value = "getUserInfo")
    public ResponseEntity<String> getUserInfo(HttpServletRequest request){
    	return renderData(ErrorCode.Success, wxServiceImpl.getUserInfo());
    }
}