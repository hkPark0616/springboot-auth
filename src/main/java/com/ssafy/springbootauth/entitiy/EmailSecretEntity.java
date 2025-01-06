package com.ssafy.springbootauth.entitiy;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
public class EmailSecretEntity {

    @Id
    private String email;
    private String secret;
}
