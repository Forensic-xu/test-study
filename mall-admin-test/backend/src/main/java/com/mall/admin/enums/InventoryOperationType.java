package com.mall.admin.enums;

import lombok.Getter;

/**
 * Inventory flow types.
 * ORDER_DEDUCT / ORDER_CANCEL_RESTORE are reserved for Phase 3 order flows.
 */
@Getter
public enum InventoryOperationType {
    INCREASE,
    DECREASE,
    ORDER_DEDUCT,
    ORDER_CANCEL_RESTORE
}
