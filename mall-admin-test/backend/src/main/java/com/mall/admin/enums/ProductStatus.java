package com.mall.admin.enums;

import com.mall.admin.common.ErrorCode;
import com.mall.admin.exception.BusinessException;
import lombok.Getter;

@Getter
public enum ProductStatus {
    ON_SALE(1),
    OFF_SALE(0);

    private final int dbValue;

    ProductStatus(int dbValue) {
        this.dbValue = dbValue;
    }

    public static ProductStatus fromDb(Integer status) {
        if (status == null) {
            return null;
        }
        for (ProductStatus item : values()) {
            if (item.dbValue == status) {
                return item;
            }
        }
        throw new BusinessException(ErrorCode.PRODUCT_PARAM_INVALID, "非法商品状态: " + status);
    }

    public static ProductStatus fromName(String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessException(ErrorCode.PRODUCT_PARAM_INVALID, "商品状态不能为空");
        }
        try {
            return ProductStatus.valueOf(name.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BusinessException(ErrorCode.PRODUCT_PARAM_INVALID, "商品状态只能是 ON_SALE 或 OFF_SALE");
        }
    }
}
