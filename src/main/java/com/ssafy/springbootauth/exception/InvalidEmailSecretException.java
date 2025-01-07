package com.ssafy.springbootauth.exception;

public class InvalidEmailSecretException extends RuntimeException {
	public InvalidEmailSecretException(String message) {
		super(message);
	}
}
