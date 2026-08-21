package com.mall.admin.enums;

import lombok.Getter;

@Getter
public enum UserStatus {
    DISABLED(0),
    ENABLED(1);

    private final int value;

    UserStatus(int value) {
        this.value = value;
    }
}
