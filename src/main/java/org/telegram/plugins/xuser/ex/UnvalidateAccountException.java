package org.telegram.plugins.xuser.ex;

/**
 * 无效的账号。
 * @author ThinkPad
 *
 */
public class UnvalidateAccountException extends RuntimeException {
	private static final long serialVersionUID = 1L;
 
	public UnvalidateAccountException(String msg) {
		super(msg);
	}


}
