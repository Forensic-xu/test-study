package com.mall.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.admin.common.AuthSupport;
import com.mall.admin.common.PageResult;
import com.mall.admin.common.Result;
import com.mall.admin.entity.OperationLog;
import com.mall.admin.mapper.OperationLogMapper;
import com.mall.admin.vo.OperationLogVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@Tag(name = "操作日志", description = "ADMIN 查询操作审计日志（参数已脱敏，不含密码/Token）")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/operation-logs")
@RequiredArgsConstructor
public class OperationLogController {

    private final OperationLogMapper operationLogMapper;

    @Operation(summary = "操作日志分页")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "401", description = "未登录", content = @Content(schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "403", description = "无权限", content = @Content(schema = @Schema(implementation = Result.class)))
    })
    @GetMapping
    public Result<PageResult<OperationLogVO>> page(
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") long page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") long size,
            @Parameter(description = "用户名模糊查询") @RequestParam(required = false) String username) {
        AuthSupport.requireAdmin();
        long pageNo = page <= 0 ? 1 : page;
        long pageSize = size <= 0 ? 10 : Math.min(size, 100);

        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(username)) {
            wrapper.like(OperationLog::getUsername, username.trim());
        }
        wrapper.orderByDesc(OperationLog::getId);

        Page<OperationLog> result = operationLogMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);
        return Result.success(PageResult.<OperationLogVO>builder()
                .page(result.getCurrent())
                .size(result.getSize())
                .total(result.getTotal())
                .pages(result.getPages())
                .records(result.getRecords().stream().map(this::toVO).collect(Collectors.toList()))
                .build());
    }

    private OperationLogVO toVO(OperationLog log) {
        return OperationLogVO.builder()
                .id(log.getId())
                .userId(log.getUserId())
                .username(log.getUsername())
                .module(log.getModule())
                .action(log.getAction())
                .method(log.getMethod())
                .requestUri(log.getRequestUri())
                .requestParams(log.getRequestParams())
                .httpStatus(log.getHttpStatus())
                .ip(log.getIp())
                .detail(log.getDetail())
                .status(log.getStatus())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
