package org.telegram.plugins.xuser.ex;

public class ForbiddenGroupException extends RuntimeException {

	public ForbiddenGroupException(String msg) {
		super(msg);
	}
}
