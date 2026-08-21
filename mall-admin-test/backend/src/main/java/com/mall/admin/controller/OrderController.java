package com.mall.admin.controller;

import com.mall.admin.common.OpLog;
import com.mall.admin.common.PageResult;
import com.mall.admin.common.Result;
import com.mall.admin.dto.OrderCreateRequest;
import com.mall.admin.service.OrderService;
import com.mall.admin.vo.OrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "订单管理", description = "创建/查询/取消/状态流转；金额由服务端按商品现价计算")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "创建订单",
            description = "支持 cartItemIds / checkoutAll / items；忽略客户端金额；事务内扣库存写流水")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "参数错误"),
            @ApiResponse(responseCode = "404", description = "商品不存在"),
            @ApiResponse(responseCode = "409", description = "库存不足/下架")
    })
    @OpLog(module = "ORDER", action = "CREATE")
    @PostMapping
    public Result<OrderVO> create(@Valid @RequestBody OrderCreateRequest request) {
        return Result.success(orderService.create(request));
    }

    @Operation(summary = "订单分页列表", description = "USER 仅自己的；ADMIN 全部。可选 status 过滤")
    @GetMapping
    public Result<PageResult<OrderVO>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") long page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") long size,
            @Parameter(description = "状态 PENDING/PAID/...") @RequestParam(required = false) String status) {
        return Result.success(orderService.page(page, size, status));
    }

    @Operation(summary = "订单详情")
    @GetMapping("/{id}")
    public Result<OrderVO> getById(@Parameter(description = "订单ID") @PathVariable Long id) {
        return Result.success(orderService.getById(id));
    }

    @Operation(summary = "取消订单", description = "仅 PENDING→CANCELLED；恢复库存；重复取消失败（幂等）")
    @OpLog(module = "ORDER", action = "CANCEL")
    @PutMapping("/{id}/cancel")
    public Result<OrderVO> cancel(@PathVariable Long id) {
        return Result.success(orderService.cancel(id));
    }

    @Operation(summary = "支付订单", description = "ADMIN；PENDING→PAID")
    @OpLog(module = "ORDER", action = "PAY")
    @PutMapping("/{id}/pay")
    public Result<OrderVO> pay(@PathVariable Long id) {
        return Result.success(orderService.pay(id));
    }

    @Operation(summary = "发货", description = "ADMIN；PAID→SHIPPED")
    @OpLog(module = "ORDER", action = "SHIP")
    @PutMapping("/{id}/ship")
    public Result<OrderVO> ship(@PathVariable Long id) {
        return Result.success(orderService.ship(id));
    }

    @Operation(summary = "完成订单", description = "ADMIN；SHIPPED→COMPLETED")
    @OpLog(module = "ORDER", action = "COMPLETE")
    @PutMapping("/{id}/complete")
    public Result<OrderVO> complete(@PathVariable Long id) {
        return Result.success(orderService.complete(id));
    }
}
