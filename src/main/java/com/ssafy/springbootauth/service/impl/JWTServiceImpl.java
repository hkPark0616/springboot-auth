package com.ssafy.springbootauth.service.impl;

import com.ssafy.springbootauth.dto.JwtDto;
import com.ssafy.springbootauth.entity.RefreshTokenEntity;
import com.ssafy.springbootauth.exception.ExpiredRefreshException;
import com.ssafy.springbootauth.exception.InvalidJwtException;
import com.ssafy.springbootauth.repository.RefreshTokenRepository;
import com.ssafy.springbootauth.service.JWTService;
import com.ssafy.springbootauth.util.CookieUtil;
import com.ssafy.springbootauth.util.JWTUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class JWTServiceImpl implements JWTService {

  private final JWTUtil jwtUtil;
  private final CookieUtil cookieUtil;
  private final RefreshTokenRepository refreshTokenRepository;

  public JWTServiceImpl(
      JWTUtil jwtUtil,
      CookieUtil cookieUtil,
      RefreshTokenRepository refreshTokenRepository
  ) {

    this.jwtUtil = jwtUtil;
    this.cookieUtil = cookieUtil;
    this.refreshTokenRepository = refreshTokenRepository;
  }

  @Override
  public JwtDto reissueRefreshToken(String refreshToken) {

    checkRefreshTokenError(refreshToken);

    String userSeq = jwtUtil.getKey(refreshToken, "userSeq");

    if (userSeq != null) {
      String userEmail = jwtUtil.getKey(refreshToken, "userEmail");
      return setTokens(userSeq, userEmail, "user");
    }
    else {
      String adminSeq = jwtUtil.getKey(refreshToken, "adminSeq");
      String role = jwtUtil.getKey(refreshToken, "role");
      return setTokens(adminSeq, role, "admin");
    }
  }

  @Override
  public void saveRefreshTokenToRedis(String refreshToken, String userSeq) {

    refreshTokenRepository.save(new RefreshTokenEntity(refreshToken, userSeq));
  }

  @Override
  public void checkRefreshTokenError(String refreshToken) {

    if (refreshToken == null || refreshToken.equals("no_refresh_token")) {
      throw new InvalidJwtException(refreshToken);
    }

    if (!jwtUtil.getKey(refreshToken, "category").equals("refresh") ||
        !refreshTokenRepository.existsById(refreshToken)
    ) {
      throw new InvalidJwtException(refreshToken);
    }

    try {
      jwtUtil.isExpired(refreshToken);
    } catch (Exception e) {
      throw new ExpiredRefreshException();
    }
  }

  @Override
  public JwtDto setTokens(String userSeq, String userEmail, String type) {

    String accessToken = jwtUtil.createJWT("access", userSeq, userEmail, type, 5 * 60 * 1000L);
    String refreshToken = jwtUtil.createJWT("refresh", userSeq, userEmail, type, 24 * 60 * 60 * 1000L);
    Cookie refreshTokenCookie = cookieUtil.createCookie("refresh", refreshToken);

    saveRefreshTokenToRedis(refreshToken, userSeq);

    return JwtDto.builder()
        .accessToken(accessToken)
        .refreshToken(refreshTokenCookie)
        .build();
  }

  @Override
  public String getRefreshToken(HttpServletRequest request) {

    Cookie[] cookies = request.getCookies();
    for (Cookie cookie : cookies) {
      if (cookie.getName().equals("refresh")) return cookie.getValue();
    }
    return null;
  }
}
