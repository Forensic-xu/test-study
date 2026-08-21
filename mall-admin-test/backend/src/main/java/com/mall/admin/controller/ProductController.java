package com.mall.admin.controller;

import com.mall.admin.common.OpLog;
import com.mall.admin.common.PageResult;
import com.mall.admin.common.Result;
import com.mall.admin.dto.ProductCreateRequest;
import com.mall.admin.dto.ProductUpdateRequest;
import com.mall.admin.service.ProductService;
import com.mall.admin.vo.ProductVO;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "商品管理", description = "商品查询与管理（写操作仅 ADMIN）")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "商品分页列表",
            description = "支持 name / categoryId / status(ON_SALE|OFF_SALE) 过滤")
    @GetMapping
    public Result<PageResult<ProductVO>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String status) {
        return Result.success(productService.page(page, size, name, categoryId, status));
    }

    @Operation(summary = "商品详情")
    @GetMapping("/{id}")
    public Result<ProductVO> getById(@PathVariable Long id) {
        return Result.success(productService.getById(id));
    }

    @Operation(summary = "创建商品",
            description = "ADMIN；price>0；stock>=0 且必填；分类必须存在；名称唯一")
    @OpLog(module = "PRODUCT", action = "CREATE")
    @PostMapping
    public Result<ProductVO> create(@Valid @RequestBody ProductCreateRequest request) {
        return Result.success(productService.create(request));
    }

    @Operation(summary = "修改商品", description = "ADMIN；库存建议走库存接口；勿依赖前端传金额")
    @OpLog(module = "PRODUCT", action = "UPDATE")
    @PutMapping("/{id}")
    public Result<ProductVO> update(@PathVariable Long id, @Valid @RequestBody ProductUpdateRequest request) {
        return Result.success(productService.update(id, request));
    }

    @Operation(summary = "删除商品", description = "ADMIN；被购物车/订单引用时 409/20009")
    @OpLog(module = "PRODUCT", action = "DELETE")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return Result.success();
    }
}
