package org.telegram.plugins.xuser.ex;

/**
 * 停止运行的异常。
 * @author ThinkPad
 *
 */
public class StopRuningException extends RuntimeException {
	private static final long serialVersionUID = 1L;
 
	public StopRuningException(String msg) {
		super(msg);
	}


}
