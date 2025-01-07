package com.ssafy.springbootauth.controller;

import com.ssafy.springbootauth.dto.UserDto;
import com.ssafy.springbootauth.response.ResponseMessage;
import com.ssafy.springbootauth.response.UserResponseCode;
import com.ssafy.springbootauth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {

        this.userService = userService;
    }

    @Operation(
            summary = "회원가입",
            description = "UserDto를 받아 회원을 등록합니다."
    )
    @PostMapping("")
    public ResponseEntity<ResponseMessage.CustomMessage> addUser(
            @RequestBody
            UserDto userDto
    ) {

        userService.register(userDto);
        return ResponseMessage.responseBasicEntity(UserResponseCode.USER_CREATED);
    }

    @Operation(
            summary = "회원정보 삭제",
            description = "String 객체의 userSeq 받아서 해당하는 회원 삭제"
    )
    @DeleteMapping("/{userSeq}")
    public ResponseEntity<ResponseMessage.CustomMessage> deleteUser(
            @PathVariable("userSeq")
            String userSeq
    ) {

        userService.deleteUser(Long.parseLong(userSeq));
        return ResponseMessage.responseBasicEntity(UserResponseCode.USER_DELETED);
    }

}
