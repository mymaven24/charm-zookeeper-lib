package com.swwx.charm.zookeeper.exception;

import com.swwx.charm.commons.lang.exception.BasicException;

/**
 * Created by hongliang.wang on 15/12/23.
 */
public class ReleaseLockFailedException extends BasicException {

	private static final long serialVersionUID = 3159367891722226255L;

	public ReleaseLockFailedException() {
		super();
	}

	public ReleaseLockFailedException(String msg, Throwable th) {
		super(msg, th);
	}
	
	

}
