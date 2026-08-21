package com.mall.admin.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING,
    PAID,
    SHIPPED,
    COMPLETED,
    CANCELLED
}
