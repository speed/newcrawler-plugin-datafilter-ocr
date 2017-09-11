package com.newcrawler.plugin.common.utils;

import org.apache.commons.codec.binary.Base64;

/**
 * http://www.javatips.net/blog/2011/08/how-to-encode-and-decode-in-base64-using-java
 * @author speed
 *
 */
public class Base64Utils {

	public static byte[] encode(byte[] originBytes) throws Exception {
		byte[] encodedBytes = Base64.encodeBase64(originBytes);
		return encodedBytes;
	}

	public static byte[] decode(byte[] encodedBytes) throws Exception {
		byte[] decodedBytes = Base64.decodeBase64(encodedBytes);
		return decodedBytes;
	}

	public static void main(String[] args) throws Exception {
		String test = "realhowto";
		byte res1[] = Base64Utils.encode(test.getBytes());
		System.out.println(test + " base64 -> " + java.util.Arrays.toString(res1));
		System.out.println(new String(res1));
		
		byte res2[] = Base64Utils.decode(res1);
		System.out.println("");
		System.out.println(java.util.Arrays.toString(res1) + " string --> " + new String(res2));

		/*
		 * output realhowto base64 -> [99, 109, 86, 104, 98, 71, 104, 118, 100, 51, 82, 118] 
		 * cmVhbGhvd3Rv 
		 * 
		 * [99, 109, 86, 104, 98, 71, 104, 118, 100, 51, 82, 118] string --> realhowto
		 */
	}
}
