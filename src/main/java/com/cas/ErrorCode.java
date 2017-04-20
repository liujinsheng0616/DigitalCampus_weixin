package com.cas;


/**
 * 系统错误码
 * 规则：
 * 		1开头的，表示通用错误；
 *		2开头的，表示帐号类错误；
 *		3开头的，表示组织机构信息错误，如学校，年级，班级，学科等；
 *		4开头的，表示试卷信息错误，如试卷，试题，知识点等；
 *		5开头的，表示统计类错误；
 *		6开头的，表示学生数据类错题，如错因信息，留言等
 
 *		9开头的，表示第三方对接错误
 * @author jason
 *
 */
public interface ErrorCode{
	
	public final static String Success = "0";

	//帐号 以20开头
	public final static String ACCESS_TOKEN_From_null = "20000"; //未提交from
	public final static String ACCESS_TOKEN_null = "20001"; //未提交token
	public final static String ACCESS_TOKEN_expire = "20002"; //token过期
	public final static String Account_NAME_OR_PASSWORD_wrong = "20003"; //帐号密码错误	
	public final static String Account_disable = "20004"; //帐号被禁用。
	public final static String Account_oldpwd_wrong = "20005"; //旧密码错误
	public final static String Account_info_wrong = "20006"; //用户信息有误
	public final static String OPENID_null = "20007"; //未提交openId
	public final static String Checkcode_wrong_or_timeouts = "20008"; //验证码错误或超时
	public final static String Update_Pwd_error = "20009"; //更新密码错误
	public final static String NICKNAME_EXISTS = "20010";	//昵称已存在
	public final static String PHONE_ALREADY_BIND = "20012";	//手机已经被绑定过
	public final static String QQ_ALREADY_BIND = "20014";	//QQ已经被绑定过
	public final static String QQ_NOT_BIND = "20024";	//QQ没有绑定过极课号
	public final static String Account_limit = "20019"; //帐号登录受限
	public final static String Account_logged= "20020"; //账号已登录过
	
	//TODO 要修改。
	public final static String QQ_token_null = "50001"; //绑定返回参数为空
	public final static String MAX_LIMIT = "20121";//超出最大范围
	public final static String Save_fail = "20113";		//保存失败
	public final static String Update_fail = "20114"; //更新失敗
	public final static String Delete_fail = "20115";//删除失败
	public final static String DUPLICATE = "20116"; //数据重复
	public final static String Download_fail = "20120"; //下載失敗
	public final static String Send_fail = "20124";	//发送失败
	public final static String Operate_fail = "20125";	//操作失败
	public final static String has_overdue="66668";//验证码过期失效或不对
	
	//预留
	public final static String Reserve = "99999";
	
	//空题干 TODO待修改
	public final static String NOPIC_IMAGE = "46669";
}
