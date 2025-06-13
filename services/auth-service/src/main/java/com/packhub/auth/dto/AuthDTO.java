package com.packhub.auth.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AuthDTO {

    private Integer userCode;
    private String password;
    private Long id;
    private String token;
}