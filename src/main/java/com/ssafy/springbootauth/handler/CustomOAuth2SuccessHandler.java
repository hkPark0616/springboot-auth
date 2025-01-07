package com.ssafy.springbootauth.handler;

import com.ssafy.springbootauth.dto.CustomOAuth2User;
import com.ssafy.springbootauth.dto.JwtDto;
import com.ssafy.springbootauth.service.JWTService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

	private final JWTService jwtService;

	public CustomOAuth2SuccessHandler(JWTService jwtService) {

		this.jwtService = jwtService;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

		OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

		onAuthenticationUserSuccess(response, (CustomOAuth2User) oAuth2User);

	}

	private void onAuthenticationUserSuccess(HttpServletResponse response, CustomOAuth2User customOAuth2User) throws IOException, ServletException {

		String userSeq = String.valueOf(customOAuth2User.getSeq());
		String userEmail = customOAuth2User.getEmail();
		JwtDto jwtDto = jwtService.setTokens(userSeq, userEmail, "user");

		response.addCookie(jwtDto.getRefreshToken());
		response.sendRedirect("http://localhost:3000" + "?token=" + jwtDto.getAccessToken());
	}

}
