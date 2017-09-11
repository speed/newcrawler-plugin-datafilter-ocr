package com.newcrawler.plugin.common.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class MD5Util {
	private static Log logger = LogFactory.getLog(MD5Util.class);
	
	public static void main(String[] args){
		String str=null;
		System.out.println(getMD5Str(str));
	}
	public static String getMD5Str(String str){
		String md5Digest=null;
		try {
			md5Digest = getMD5Str(str, null);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		
		return md5Digest;
	}
	public static String getMD5Str16bit(String str){
		String md5Digest=null;
		try {
			md5Digest = getMD5Str(str, 16);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return md5Digest;
	}
	
	public static String getMD5Str(byte[] bytes){
		String md5Digest=null;
		try {
			md5Digest = getMD5Str(bytes, null);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		
		return md5Digest;
	}
	public static String getMD5Str16bit(byte[] bytes){
		String md5Digest=null;
		try {
			md5Digest = getMD5Str(bytes, 16);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return md5Digest;
	}
	
	private static String getMD5Str(String str, Integer bit) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		return getMD5Str(str.getBytes("UTF-8"), bit);
	}

	private static String getMD5Str(byte[] bytes, Integer bit) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest messageDigest = null;
		messageDigest = MessageDigest.getInstance("MD5");
		messageDigest.reset();
		messageDigest.update(bytes);
		byte[] byteArray = messageDigest.digest();
		StringBuffer md5StrBuff = new StringBuffer();
		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(
						Integer.toHexString(0xFF & byteArray[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
		}
		if(bit!=null && bit==16){
			return md5StrBuff.toString().substring(8,24);
		}
		return md5StrBuff.toString();
	}
}
