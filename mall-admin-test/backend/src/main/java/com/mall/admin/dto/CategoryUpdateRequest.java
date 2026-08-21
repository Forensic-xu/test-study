package com.mall.admin.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryUpdateRequest {

    @Size(max = 50, message = "分类名称长度不能超过50")
    private String name;

    @Min(value = 0, message = "分类状态只能是0或1")
    @Max(value = 1, message = "分类状态只能是0或1")
    private Integer status;
}
