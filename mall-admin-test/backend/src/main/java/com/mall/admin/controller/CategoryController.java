package com.mall.admin.controller;

import com.mall.admin.common.OpLog;
import com.mall.admin.common.Result;
import com.mall.admin.dto.CategoryCreateRequest;
import com.mall.admin.dto.CategoryUpdateRequest;
import com.mall.admin.service.CategoryService;
import com.mall.admin.vo.CategoryVO;
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

import java.util.List;

@Tag(name = "商品分类", description = "分类查询与管理（写操作仅 ADMIN）")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "分类列表", description = "可选按 status 过滤：1启用 / 0禁用")
    @GetMapping
    public Result<List<CategoryVO>> list(@RequestParam(required = false) Integer status) {
        return Result.success(categoryService.list(status));
    }

    @Operation(summary = "分类详情")
    @GetMapping("/{id}")
    public Result<CategoryVO> getById(@PathVariable Long id) {
        return Result.success(categoryService.getById(id));
    }

    @Operation(summary = "创建分类", description = "ADMIN；名称唯一；空名称返回 400/90001")
    @OpLog(module = "CATEGORY", action = "CREATE")
    @PostMapping
    public Result<CategoryVO> create(@Valid @RequestBody CategoryCreateRequest request) {
        return Result.success(categoryService.create(request));
    }

    @Operation(summary = "修改分类", description = "ADMIN；分类不存在 404/20005；名称重复 409/20006")
    @OpLog(module = "CATEGORY", action = "UPDATE")
    @PutMapping("/{id}")
    public Result<CategoryVO> update(@PathVariable Long id, @Valid @RequestBody CategoryUpdateRequest request) {
        return Result.success(categoryService.update(id, request));
    }

    @Operation(summary = "删除分类", description = "ADMIN；存在商品时 409/20007")
    @OpLog(module = "CATEGORY", action = "DELETE")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.success();
    }
}
