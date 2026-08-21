package com.mall.admin.service;

import com.mall.admin.dto.UserCreateRequest;
import com.mall.admin.dto.UserUpdateRequest;
import com.mall.admin.vo.UserVO;

import java.util.List;

public interface UserService {

    UserVO getById(Long id);

    List<UserVO> listUsers(String username, String role, Integer status);

    UserVO create(UserCreateRequest request);

    UserVO update(Long id, UserUpdateRequest request);

    void delete(Long id);
}
