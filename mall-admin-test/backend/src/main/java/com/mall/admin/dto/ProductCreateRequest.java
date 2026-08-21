package com.mall.admin.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductCreateRequest {

    @NotBlank(message = "商品名称不能为空")
    @Size(max = 100, message = "商品名称长度不能超过100")
    private String name;

    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    @NotNull(message = "商品价格不能为空")
    @DecimalMin(value = "0.01", inclusive = true, message = "商品价格必须大于0")
    private BigDecimal price;

    /**
     * stock must be provided and >= 0. null is rejected by @NotNull.
     */
    @NotNull(message = "商品库存不能为空")
    @Min(value = 0, message = "商品库存不能小于0")
    private Integer stock;

    @NotBlank(message = "商品状态不能为空")
    @Pattern(regexp = "ON_SALE|OFF_SALE", message = "商品状态只能是 ON_SALE 或 OFF_SALE")
    private String status = "ON_SALE";

    @Size(max = 1000, message = "商品描述长度不能超过1000")
    private String description;
}
