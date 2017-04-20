package com.cas.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cas.framework.utils.StringUtil;

/**
 * 微信日常处理工具类
 * @author kingson·liu
 * @date 2017年04月10日
 * */
public class WxUtil {
	/**
	 * 算法加密
	 * 
	 * @param str 加密前字符串
	 * @param type 加密法则
	 * @return 加密后的字符串
	 */
	public static String encrypt(String str, String type) {
        try {
            MessageDigest crypt = MessageDigest.getInstance(type);
            crypt.reset();
            crypt.update(str.getBytes("UTF-8"));
            str = byteToHex(crypt.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
	}
	
    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
    
    /**
	 * 过滤emoji 或者 其他非文字类型的字符
	 * @param source
	 * @return
	 */
	public static String filterEmoji(String source)
	{
		int len = source.length();
		StringBuilder buf = new StringBuilder(len);
		for (int i = 0; i < len; i++)
		{
			char codePoint = source.charAt(i);
			if (isNotEmojiCharacter(codePoint))
			{
				buf.append(codePoint);
			}
		}
		return buf.toString();
	}
	
	private static boolean isNotEmojiCharacter(char codePoint)
	{
		return (codePoint == 0x0) ||
			(codePoint == 0x9) ||
			(codePoint == 0xA) ||
			(codePoint == 0xD) ||
			((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
			((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||
			((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
	}
	
	/**
	 * 过滤nickName的特殊字符
	 * */
	public static String filterSpecilString(String str){
		if(StringUtil.isNotBlank(str)){
			// 清除掉所有特殊字符
			String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
			Pattern p = Pattern.compile(regEx);
			Matcher m = p.matcher(str);
			return m.replaceAll("").trim();
		}
		return str;
	}
	
	/**
	 * 微信支付元转换成分
	 * 
	 * @param money
	 * @return
	 */
	public static String getMoney(String amount) {
		if (amount == null) {
			return "";
		}
		// 金额转化为分为单位
		String currency = amount.replaceAll("\\$|\\￥|\\,", ""); // 处理包含, ￥
		int index = currency.indexOf(".");
		int length = currency.length();
		Long amLong = 0l;
		if (index == -1) {
			amLong = Long.valueOf(currency + "00");
		} else if (length - index >= 3) {
			amLong = Long.valueOf((currency.substring(0, index + 3)).replace(
					".", ""));
		} else if (length - index == 2) {
			amLong = Long.valueOf((currency.substring(0, index + 2)).replace(
					".", "") + 0);
		} else {
			amLong = Long.valueOf((currency.substring(0, index + 1)).replace(
					".", "") + "00");
		}
		return amLong.toString();
	}
}
