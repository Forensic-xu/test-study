package com.mall.admin.controller;

import com.mall.admin.common.OpLog;
import com.mall.admin.common.Result;
import com.mall.admin.dto.LoginRequest;
import com.mall.admin.service.AuthService;
import com.mall.admin.vo.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "认证", description = "登录获取 JWT（无需 Token）")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "用户登录", description = "成功返回 JWT；失败区分用户不存在/密码错误/禁用")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "登录成功"),
            @ApiResponse(responseCode = "400", description = "参数校验失败 code=90001", content = @Content(schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "401", description = "密码错误 code=10002", content = @Content(schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "403", description = "用户禁用 code=10003", content = @Content(schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "404", description = "用户不存在 code=10001", content = @Content(schema = @Schema(implementation = Result.class)))
    })
    @OpLog(module = "AUTH", action = "LOGIN")
    @PostMapping("/api/auth/login")
    public Result<LoginVO> login(
            @RequestBody(description = "登录账号密码", required = true,
                    content = @Content(schema = @Schema(implementation = LoginRequest.class)))
            @Valid @org.springframework.web.bind.annotation.RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }
}
