package com.ssafy.springbootauth.exception;

public class SQLInsertException extends RuntimeException {
	public SQLInsertException(Exception e) {
		super(e);
	}
}
