package com.mall.admin.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InventoryChangeRequest {

    @NotNull(message = "调整数量不能为空")
    @Min(value = 1, message = "调整数量必须大于0")
    private Integer quantity;

    private String remark;
}
