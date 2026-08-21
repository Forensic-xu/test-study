package com.mall.admin.controller;

import com.mall.admin.common.OpLog;
import com.mall.admin.common.Result;
import com.mall.admin.dto.UserCreateRequest;
import com.mall.admin.dto.UserUpdateRequest;
import com.mall.admin.service.UserService;
import com.mall.admin.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

@Tag(name = "用户管理", description = "用户查询与管理；写操作仅 ADMIN")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "用户详情", description = "ADMIN 可查任意用户；USER 仅可查自己")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "403", description = "越权 code=10008"),
            @ApiResponse(responseCode = "404", description = "用户不存在 code=10001")
    })
    @GetMapping("/{id}")
    public Result<UserVO> getById(@Parameter(description = "用户ID") @PathVariable Long id) {
        return Result.success(userService.getById(id));
    }

    @Operation(summary = "用户列表", description = "仅 ADMIN")
    @GetMapping
    public Result<List<UserVO>> list(
            @Parameter(description = "用户名模糊") @RequestParam(required = false) String username,
            @Parameter(description = "角色 ADMIN/USER") @RequestParam(required = false) String role,
            @Parameter(description = "状态 1启用 0禁用") @RequestParam(required = false) Integer status) {
        return Result.success(userService.listUsers(username, role, status));
    }

    @Operation(summary = "创建用户")
    @OpLog(module = "USER", action = "CREATE")
    @PostMapping
    public Result<UserVO> create(@Valid @RequestBody UserCreateRequest request) {
        return Result.success(userService.create(request));
    }

    @Operation(summary = "更新用户")
    @OpLog(module = "USER", action = "UPDATE")
    @PutMapping("/{id}")
    public Result<UserVO> update(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        return Result.success(userService.update(id, request));
    }

    @Operation(summary = "删除用户")
    @OpLog(module = "USER", action = "DELETE")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.success();
    }
}
