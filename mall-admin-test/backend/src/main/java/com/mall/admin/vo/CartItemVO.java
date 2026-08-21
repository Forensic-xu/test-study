package com.mall.admin.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CartItemVO {

    private Long id;
    private Long userId;
    private Long productId;
    private String productName;
    private BigDecimal productPrice;
    private Integer productStock;
    private String productStatus;
    private Integer quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
