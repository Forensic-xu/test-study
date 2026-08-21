package com.mall.admin.controller;

import com.mall.admin.common.OpLog;
import com.mall.admin.common.Result;
import com.mall.admin.dto.CartAddRequest;
import com.mall.admin.dto.CartUpdateRequest;
import com.mall.admin.service.CartService;
import com.mall.admin.vo.CartItemVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "购物车", description = "仅操作当前登录用户自己的购物车；支持数据隔离测试")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @Operation(summary = "查看我的购物车")
    @GetMapping
    public Result<List<CartItemVO>> list() {
        return Result.success(cartService.listMyCart());
    }

    @Operation(summary = "加入购物车",
            description = "商品须存在且 ON_SALE；quantity>0 且不超过库存；同商品合并数量")
    @OpLog(module = "CART", action = "ADD")
    @PostMapping
    public Result<CartItemVO> add(@Valid @RequestBody CartAddRequest request) {
        return Result.success(cartService.add(request));
    }

    @Operation(summary = "修改购物车数量")
    @OpLog(module = "CART", action = "UPDATE")
    @PutMapping("/{id}")
    public Result<CartItemVO> update(@PathVariable Long id, @Valid @RequestBody CartUpdateRequest request) {
        return Result.success(cartService.update(id, request));
    }

    @Operation(summary = "清空我的购物车", description = "必须放在 /{id} 之前，避免被路径变量误匹配")
    @OpLog(module = "CART", action = "CLEAR")
    @DeleteMapping("/clear")
    public Result<Void> clear() {
        cartService.clear();
        return Result.success();
    }

    @Operation(summary = "删除购物车项", description = "只能删除自己的；越权返回 403/30004")
    @OpLog(module = "CART", action = "DELETE")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        cartService.delete(id);
        return Result.success();
    }
}
