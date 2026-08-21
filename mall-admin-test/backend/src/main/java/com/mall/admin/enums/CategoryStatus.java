package com.mall.admin.enums;

import com.mall.admin.common.ErrorCode;
import com.mall.admin.exception.BusinessException;
import lombok.Getter;

@Getter
public enum CategoryStatus {
    DISABLED(0),
    ENABLED(1);

    private final int value;

    CategoryStatus(int value) {
        this.value = value;
    }

    public static void requireValid(Integer status) {
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "分类状态只能是 0 或 1");
        }
    }
}
