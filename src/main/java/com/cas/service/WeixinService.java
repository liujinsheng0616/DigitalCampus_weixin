package com.cas.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cas.entity.account.User;

/**
 * @Creat 2017年04月8日
 * @Author:kingson·liu
 * 微信主业务逻辑处理
 */
public interface WeixinService {
	/**
	 * 微信信息处理入口
	 * @param reqMap
	 * @return 返回处理后的响应内容给微信用户。
	 */
	public String processMsg(Map<String, String> reqMap);
	
	/**
	 * 菜单创建
	 * @throws Exception
	 */
	public abstract void createMenu();
	
	/**
	 * 菜单删除
	 * @return 结果json
	 * @throws Exception
	 */
	public abstract String deleteMenu();
	
	/**
	 * 菜单查询
	 * 
	 * @return 菜单json
	 * @throws Exception
	 */
	public abstract String getMenu();
	
	/**
	 * 网页授权机制获取openIdWX
	 * @prama request 
	 * @return openIdWX
	 */
	public abstract String getOpenIdByAuthAccessToken(HttpServletRequest request);
	
	/**
	 * */
	public List<User> getUserInfo();
	
	/**
	 * 微信支付
	 * */
	public abstract Map<String, Object> wxPayMeth(HttpServletRequest request, HttpServletResponse response,String openIdWX, String totleFee, String goodsIds, Long studentId, String jikeNum, Long schoolId, Long gradeId, Long clzssId, String studentName, Long consigneeId)throws Exception;
}
