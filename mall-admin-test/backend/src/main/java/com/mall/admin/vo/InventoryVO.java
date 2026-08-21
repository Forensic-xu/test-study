package com.mall.admin.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InventoryVO {

    private Long productId;
    private String productName;
    private Integer stock;
    private String status;
}
