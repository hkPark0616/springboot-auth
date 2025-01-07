package com.ssafy.springbootauth.response;

import org.springframework.http.HttpStatus;

public interface ResponseCode {

	int getCode();
	String getMessage();
	HttpStatus getHttpStatus();
}
