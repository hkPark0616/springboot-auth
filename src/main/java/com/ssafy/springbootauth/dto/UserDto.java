package com.ssafy.springbootauth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long userSeq;
    private String userId;
    private String userPw;
    private String userEmail;
    private String userName;
    private String socialType;
    private String socialPlatform;
    private LocalDateTime createdAt;

}
