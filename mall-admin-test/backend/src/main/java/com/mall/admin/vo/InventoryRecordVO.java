package com.mall.admin.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class InventoryRecordVO {

    private Long id;
    private Long productId;
    private Integer beforeStock;
    private Integer changeQuantity;
    private Integer afterStock;
    private String operationType;
    private Long operatorId;
    private String remark;
    private LocalDateTime createdAt;
}
