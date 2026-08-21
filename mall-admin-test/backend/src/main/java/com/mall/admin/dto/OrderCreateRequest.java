package com.mall.admin.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * Create order by cart settlement and/or direct items.
 * Provide either cartItemIds (or checkoutAll=true) or items. Both modes share the same order structure.
 * Client-sent totalAmount (if any) is ignored — server recalculates from DB prices.
 */
@Data
public class OrderCreateRequest {

    /** Cart item IDs owned by current user. */
    private List<Long> cartItemIds;

    /** If true, settle all cart items for current user. */
    private Boolean checkoutAll;

    /** Direct buy items. */
    @Valid
    private List<OrderItemRequest> items;

    private String remark;

    @Data
    public static class OrderItemRequest {

        @NotNull(message = "商品ID不能为空")
        private Long productId;

        @NotNull(message = "数量不能为空")
        @Min(value = 1, message = "数量必须大于0")
        private Integer quantity;
    }
}
