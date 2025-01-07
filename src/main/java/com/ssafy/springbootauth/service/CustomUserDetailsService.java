package com.ssafy.springbootauth.service;

import com.ssafy.springbootauth.dto.CustomUserDetails;
import com.ssafy.springbootauth.entity.UserEntity;
import com.ssafy.springbootauth.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	public CustomUserDetailsService(UserRepository userRepository) {

		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		Optional<UserEntity> userEntity = userRepository.getUserByUserId(username);
		if (userEntity == null) return null;
		return new CustomUserDetails(userEntity);
	}

}
