package com.ssafy.springbootauth.service;

import com.ssafy.springbootauth.dto.CustomOAuth2User;
import com.ssafy.springbootauth.dto.oauth2response.*;
import com.ssafy.springbootauth.entity.UserEntity;
import com.ssafy.springbootauth.exception.SQLInsertException;
import com.ssafy.springbootauth.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
	
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	
	public CustomOAuth2UserService(
			UserRepository userRepository,
			BCryptPasswordEncoder passwordEncoder
	) {
		
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

		OAuth2User oAuth2User = super.loadUser(userRequest);

		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		OAuth2Response oAuth2Response = switch (registrationId) {
			case "google" -> new GoogleResponse(oAuth2User.getAttributes());
			case "naver" -> new NaverResponse(oAuth2User.getAttributes());
			case "kakao" -> new KakaoResponse(oAuth2User.getAttributes());
			default -> null;
		};

		if (oAuth2Response == null) return null;

		else {
			return handleUser(oAuth2Response);
		}
	}

	private OAuth2User handleUser(OAuth2Response oAuth2Response) {
		String userId = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();

		// 기존 사용자 조회
		Optional<UserEntity> user = userRepository.getUserByUserId(userId);
		Optional<UserEntity> test = userRepository.getUserByUserId("string");
		UserEntity existUser = new UserEntity();
		// user가 존재하지 않으면 예외를 던짐
//		UserEntity existUser = user.orElseThrow(() -> new IllegalArgumentException("User not found for userId: " + oAuth2Response.getName()));

		if (!user.isPresent()) {
			// 신규 사용자 생성
			UserEntity userEntity = UserEntity.builder()
					.userId(userId)
					.userName(oAuth2Response.getName())
					.userEmail(oAuth2Response.getEmail())
					.socialPlatform(oAuth2Response.getProvider())
					.build();

			try {
				// 새로운 사용자 DB에 저장
				userRepository.save(userEntity);
//				existUser = userEntity;

			} catch (Exception e) {
				// 예외 처리 시 로그 추가
				throw new SQLInsertException(e);
			}
			existUser = userEntity;
		} else {
			// 기존 사용자 이메일 업데이트 및 필요시 다른 필드 업데이트
			existUser.setUserEmail(oAuth2Response.getEmail());

			userRepository.updateUserEmail(userId, oAuth2Response.getEmail());
//			userRepository.save(existUser);
		}

//		UserEntity userEntity = UserEntity.builder()
//				.userId(existUser.getUserId())
//				.userEmail(existUser.getUserEmail())
//				.userName(existUser.getUserName())
//				.socialType(existUser.getSocialType())
//				.socialPlatform(existUser.getSocialPlatform())
//				.createdAt(existUser.getCreatedAt() != null ? existUser.getCreatedAt() : LocalDateTime.now()) // null일 경우 현재 시간 설정
//				.build();

		return new CustomOAuth2User(existUser);
	}


}
