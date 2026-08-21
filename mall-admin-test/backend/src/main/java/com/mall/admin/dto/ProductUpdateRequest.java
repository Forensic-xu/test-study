package com.mall.admin.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductUpdateRequest {

    @Size(max = 100, message = "商品名称长度不能超过100")
    private String name;

    private Long categoryId;

    @DecimalMin(value = "0.01", inclusive = true, message = "商品价格必须大于0")
    private BigDecimal price;

    /**
     * Stock on product update is optional. Prefer inventory APIs for stock changes.
     * If provided, must be >= 0.
     */
    @Min(value = 0, message = "商品库存不能小于0")
    private Integer stock;

    @Pattern(regexp = "ON_SALE|OFF_SALE", message = "商品状态只能是 ON_SALE 或 OFF_SALE")
    private String status;

    @Size(max = 1000, message = "商品描述长度不能超过1000")
    private String description;
}
