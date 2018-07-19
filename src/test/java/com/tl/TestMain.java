package com.tl;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class TestMain {

	public static String getEncoding(String str) {
		String encode;

		encode = "UTF-16";
		try {
			if (str.equals(new String(str.getBytes(), encode))) {
				return encode;
			}
		} catch (Exception ex) {
		}

		encode = "ASCII";
		try {
			if (str.equals(new String(str.getBytes(), encode))) {
				return "字符串<< " + str + " >>中仅由数字和英文字母组成，无法识别其编码格式";
			}
		} catch (Exception ex) {
		}

		encode = "ISO-8859-1";
		try {
			if (str.equals(new String(str.getBytes(), encode))) {
				return encode;
			}
		} catch (Exception ex) {
		}

		encode = "GB2312";
		try {
			if (str.equals(new String(str.getBytes(), encode))) {
				return encode;
			}
		} catch (Exception ex) {
		}

		encode = "UTF-8";
		try {
			if (str.equals(new String(str.getBytes(), encode))) {
				return encode;
			}
		} catch (Exception ex) {
		}

		/*
		 * ......待完善
		 */

		return "未识别编码格式";
	}

	public static void main(String[] args) {
		// 获取系统默认编码
		System.out.println("系统默认编码：" + System.getProperty("file.encoding")); // 查询结果GBK
		// 系统默认字符编码
		System.out.println("系统默认字符编码：" + Charset.defaultCharset()); // 查询结果GBK
		// 操作系统用户使用的语言
		System.out.println("系统默认语言：" + System.getProperty("user.language")); // 查询结果zh

		System.out.println();

		String s1 = "hi, nice to meet you!";
		String s2 = "hi, 我来了！";
		String s3 = "Bitkeep 瀹樻柟涓枃绀惧尯";

		System.out.println(getEncoding(s1));
		System.out.println(getEncoding(s2));
		System.out.println(getEncoding(s3));

		try {
			String name = "Bitkeep 瀹樻柟涓枃绀惧尯";
			// 用新的字符编码生成字符串ASCII,ISO-8859-1
			testCharset(name, "UTF-8", "UTF-8");
			testCharset(name, "UTF-8", "ASCII");
			testCharset(name, "UTF-8", "ISO-8859-1");
			testCharset(name, "UTF-8", "GB2312");
			
			testCharset(name, "GB2312", "UTF-8");
			testCharset(name, "GB2312", "ASCII");
			testCharset(name, "GB2312", "ISO-8859-1");
			
			testCharset(name, "ISO-8859-1", "UTF-8");
			testCharset(name, "ISO-8859-1", "ASCII");
			testCharset(name, "ISO-8859-1", "GB2312");
			
			testCharset(name, "ASCII", "UTF-8");
			testCharset(name, "ASCII", "ISO-8859-1");
			testCharset(name, "ASCII", "GB2312");
			
//			testCharset(name, "UTF-8", "UTF-16");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void testCharset(String name, String c1, String c2)
			throws UnsupportedEncodingException {
		byte[] bs = name.getBytes(c1);
		String str = new String(bs, c2); // 用新的字符编码生成字符串ASCII,ISO-8859-1
		System.out.println(str);
	}
}
