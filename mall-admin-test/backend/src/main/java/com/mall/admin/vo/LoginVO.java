package com.mall.admin.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginVO {

    private String token;
    private String tokenType;
    private Long expiresInHours;
    private Long userId;
    private String username;
    private String nickname;
    private String role;
}
