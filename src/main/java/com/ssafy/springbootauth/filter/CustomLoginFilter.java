package com.ssafy.springbootauth.filter;

import com.ssafy.springbootauth.dto.JwtDto;
import com.ssafy.springbootauth.entity.UserEntity;
import com.ssafy.springbootauth.repository.UserRepository;
import com.ssafy.springbootauth.response.AuthResponseCode;
import com.ssafy.springbootauth.response.ResponseMessage;
import com.ssafy.springbootauth.service.JWTService;
import com.ssafy.springbootauth.util.CookieUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Optional;

public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authenticationManager;
	private final JWTService jwtService;
	private final UserRepository userRepository;
	private final CookieUtil cookieUtil;

	public CustomLoginFilter(
			AuthenticationManager authenticationManager,
			JWTService jwtService,
			UserRepository userRepository,
			CookieUtil cookieUtil
	) {

		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
		this.userRepository = userRepository;
		this.cookieUtil = cookieUtil;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

		String userId = obtainUsername(request);
		String password = obtainPassword(request);

		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
				new UsernamePasswordAuthenticationToken(userId, password);

		return authenticationManager.authenticate(usernamePasswordAuthenticationToken);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

		String userId = authResult.getName();
		Optional<UserEntity> userEntity = userRepository.getUserByUserSeq(Long.valueOf(userId));
		String userSeq = String.valueOf(userEntity.get().getUserSeq());
		String userEmail = userEntity.get().getUserEmail();
		JwtDto jwtDto = jwtService.setTokens(userSeq, userEmail, "user");

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwtDto.getAccessToken());
		headers.add(HttpHeaders.SET_COOKIE, cookieUtil.convertToString(jwtDto.getRefreshToken()));

		ResponseMessage.setHeadersResponse(response, AuthResponseCode.LOGIN_SUCCESS, headers);
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {

		ResponseMessage.setBasicResponse(response, AuthResponseCode.FAIL_TO_LOGIN);
	}
}
