package com.mall.admin.controller;

import com.mall.admin.common.OpLog;
import com.mall.admin.common.Result;
import com.mall.admin.dto.InventoryChangeRequest;
import com.mall.admin.service.InventoryService;
import com.mall.admin.vo.InventoryRecordVO;
import com.mall.admin.vo.InventoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "库存管理", description = "库存查询与增减（增减仅 ADMIN）；使用条件更新防止负库存")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @Operation(summary = "查询商品库存")
    @GetMapping("/{productId}")
    public Result<InventoryVO> getByProductId(@PathVariable Long productId) {
        return Result.success(inventoryService.getByProductId(productId));
    }

    @Operation(summary = "增加库存", description = "ADMIN；quantity 必须 > 0；写入 INCREASE 流水")
    @OpLog(module = "INVENTORY", action = "INCREASE")
    @PutMapping("/{productId}/increase")
    public Result<InventoryVO> increase(@PathVariable Long productId,
                                        @Valid @RequestBody InventoryChangeRequest request) {
        return Result.success(inventoryService.increase(productId, request));
    }

    @Operation(summary = "减少库存",
            description = "ADMIN；quantity 必须 > 0；库存不足返回 409/50002；写入 DECREASE 流水")
    @OpLog(module = "INVENTORY", action = "DECREASE")
    @PutMapping("/{productId}/decrease")
    public Result<InventoryVO> decrease(@PathVariable Long productId,
                                        @Valid @RequestBody InventoryChangeRequest request) {
        return Result.success(inventoryService.decrease(productId, request));
    }

    @Operation(summary = "库存流水列表")
    @GetMapping("/{productId}/records")
    public Result<List<InventoryRecordVO>> listRecords(@PathVariable Long productId) {
        return Result.success(inventoryService.listRecords(productId));
    }
}
