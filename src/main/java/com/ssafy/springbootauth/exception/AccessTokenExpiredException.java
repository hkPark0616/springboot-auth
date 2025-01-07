package com.ssafy.springbootauth.exception;

public class AccessTokenExpiredException extends RuntimeException {
  public AccessTokenExpiredException(String message) {
    super(message);
  }
}
