package com.ssafy.springbootauth.exception;

public class InvalidJwtException extends RuntimeException {
  public InvalidJwtException(String message) {
    super(message);
  }
}
