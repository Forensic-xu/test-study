package com.mall.admin.common;

import com.mall.admin.enums.UserRole;
import com.mall.admin.exception.BusinessException;

public final class AuthSupport {

    private AuthSupport() {
    }

    public static AuthUser requireLogin() {
        AuthUser authUser = UserContext.get();
        if (authUser == null) {
            throw new BusinessException(ErrorCode.TOKEN_MISSING);
        }
        return authUser;
    }

    public static AuthUser requireAdmin() {
        AuthUser authUser = requireLogin();
        if (!UserRole.ADMIN.name().equals(authUser.getRole())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        return authUser;
    }

    public static boolean isAdmin(AuthUser authUser) {
        return authUser != null && UserRole.ADMIN.name().equals(authUser.getRole());
    }
}
