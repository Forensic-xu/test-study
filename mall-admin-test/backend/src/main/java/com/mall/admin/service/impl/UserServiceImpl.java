package com.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.admin.common.AuthUser;
import com.mall.admin.common.ErrorCode;
import com.mall.admin.common.UserContext;
import com.mall.admin.dto.UserCreateRequest;
import com.mall.admin.dto.UserUpdateRequest;
import com.mall.admin.entity.User;
import com.mall.admin.enums.UserRole;
import com.mall.admin.enums.UserStatus;
import com.mall.admin.exception.BusinessException;
import com.mall.admin.mapper.UserMapper;
import com.mall.admin.service.UserService;
import com.mall.admin.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserVO getById(Long id) {
        AuthUser current = requireLogin();
        User user = requireUser(id);

        // ADMIN can view any user; USER can only view self
        if (!isAdmin(current) && !Objects.equals(current.getUserId(), id)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        return toVO(user);
    }

    @Override
    public List<UserVO> listUsers(String username, String role, Integer status) {
        requireAdmin();
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(username)) {
            wrapper.like(User::getUsername, username);
        }
        if (StringUtils.hasText(role)) {
            wrapper.eq(User::getRole, role);
        }
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        wrapper.orderByAsc(User::getId);
        return userMapper.selectList(wrapper).stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public UserVO create(UserCreateRequest request) {
        requireAdmin();
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));
        if (count != null && count > 0) {
            throw new BusinessException(ErrorCode.USERNAME_EXISTS);
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        user.setStatus(UserStatus.ENABLED.getValue());

        int rows = userMapper.insert(user);
        if (rows != 1) {
            throw new BusinessException(ErrorCode.USER_CREATE_FAILED);
        }
        log.info("User created: id={}, username={}, role={}", user.getId(), user.getUsername(), user.getRole());
        return toVO(user);
    }

    @Override
    public UserVO update(Long id, UserUpdateRequest request) {
        requireAdmin();
        User user = requireUser(id);

        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (StringUtils.hasText(request.getRole())) {
            user.setRole(request.getRole());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }

        userMapper.updateById(user);
        log.info("User updated: id={}", id);
        return toVO(requireUser(id));
    }

    @Override
    public void delete(Long id) {
        requireAdmin();
        AuthUser current = requireLogin();
        if (Objects.equals(current.getUserId(), id)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "不能删除当前登录账号");
        }
        requireUser(id);
        userMapper.deleteById(id);
        log.info("User deleted: id={}", id);
    }

    private User requireUser(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return user;
    }

    private AuthUser requireLogin() {
        AuthUser authUser = UserContext.get();
        if (authUser == null) {
            throw new BusinessException(ErrorCode.TOKEN_MISSING);
        }
        return authUser;
    }

    private void requireAdmin() {
        AuthUser authUser = requireLogin();
        if (!isAdmin(authUser)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
    }

    private boolean isAdmin(AuthUser authUser) {
        return authUser != null && UserRole.ADMIN.name().equals(authUser.getRole());
    }

    private UserVO toVO(User user) {
        return UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
