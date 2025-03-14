package com.swwx.charm.zookeeper.exception;

import com.swwx.charm.commons.lang.exception.BasicException;

/**
 * Created by hongliang.wang on 15/12/23.
 */
public class GetLockFailedException extends BasicException {

	private static final long serialVersionUID = 7380105538242431751L;

	public GetLockFailedException() {
		super();
	}

	public GetLockFailedException(String msg, Throwable th) {
		super(msg, th);
	}

	
}
