/**
 * 
 */
package org.telegram.plugins.xuser;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ThinkPad
 *
 */
public class XUtils {
	protected static Logger logger = LoggerFactory.getLogger(XUtils.class);
	/**
	 * @param name
	 * @return
	 */
	public static String transChartset(String name) {
		return transChartset(name, "GBK", "UTF-8");
	}

	public static String transChartset(String name, String before, String after) {
		String str = name;
		try {
			byte[] bs = name.getBytes(before);
			str = new String(bs, after);
		} catch (UnsupportedEncodingException e) {
			logger.warn("{}字符转换异常", name);
		}
		return str;
	}
}
