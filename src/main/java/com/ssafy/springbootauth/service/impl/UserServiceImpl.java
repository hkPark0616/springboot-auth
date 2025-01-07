package com.ssafy.springbootauth.service.impl;

import com.ssafy.springbootauth.dto.UserDto;
import com.ssafy.springbootauth.entity.UserEntity;
import com.ssafy.springbootauth.repository.UserRepository;
import com.ssafy.springbootauth.service.UserService;
import com.ssafy.springbootauth.util.SecretUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

  private final BCryptPasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final SecretUtil secretUtil;

  public UserServiceImpl(
      BCryptPasswordEncoder passwordEncoder,
      UserRepository userRepository,
      SecretUtil secretUtil
  ) {

    this.passwordEncoder = passwordEncoder;
    this.userRepository = userRepository;
    this.secretUtil = secretUtil;

  }


  @Override
  public void register(UserDto userDto) {

    // UserDto 데이터를 UserEntity로 매핑
    UserEntity userEntity = UserEntity.builder()
            .userId(userDto.getUserId())
            .userPw(passwordEncoder.encode(userDto.getUserPw()))
            .userEmail(userDto.getUserEmail())
            .userName(userDto.getUserName())
            .socialType(userDto.getSocialType())
            .socialPlatform(userDto.getSocialPlatform())
            .createdAt(userDto.getCreatedAt() != null ? userDto.getCreatedAt() : LocalDateTime.now()) // null일 경우 현재 시간 설정
            .build();

    userRepository.save(userEntity);
  }

  @Override
  public void deleteUser(long userSeq) {

    userRepository.deleteById(userSeq);
  }

}
