package com.mall.admin.service;

import com.mall.admin.dto.LoginRequest;
import com.mall.admin.vo.LoginVO;

public interface AuthService {

    LoginVO login(LoginRequest request);
}
