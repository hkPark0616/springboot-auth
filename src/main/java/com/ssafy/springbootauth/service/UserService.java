package com.ssafy.springbootauth.service;

import com.ssafy.springbootauth.dto.UserDto;

public interface UserService {

  void register(UserDto userDto);
  void deleteUser(long userSeq);

}
