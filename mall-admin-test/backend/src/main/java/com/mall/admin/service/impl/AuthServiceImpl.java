package com.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.admin.common.ErrorCode;
import com.mall.admin.dto.LoginRequest;
import com.mall.admin.entity.User;
import com.mall.admin.enums.UserStatus;
import com.mall.admin.exception.BusinessException;
import com.mall.admin.mapper.UserMapper;
import com.mall.admin.service.AuthService;
import com.mall.admin.util.JwtUtil;
import com.mall.admin.vo.LoginVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${mall.jwt.expire-hours}")
    private long expireHours;

    @Override
    public LoginVO login(LoginRequest request) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));

        if (user == null) {
            log.warn("User login failed: username={}, reason=USER_NOT_FOUND", request.getUsername());
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        if (user.getStatus() == null || user.getStatus() == UserStatus.DISABLED.getValue()) {
            log.warn("User login failed: username={}, reason=USER_DISABLED", request.getUsername());
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("User login failed: username={}, reason=PASSWORD_ERROR", request.getUsername());
            throw new BusinessException(ErrorCode.PASSWORD_ERROR);
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        // Never log password or token
        log.info("User login success: userId={}, username={}, role={}",
                user.getId(), user.getUsername(), user.getRole());

        return LoginVO.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresInHours(expireHours)
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .role(user.getRole())
                .build();
    }
}
