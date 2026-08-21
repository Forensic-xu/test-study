package com.mall.admin.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OperationLogVO {

    private Long id;
    private Long userId;
    private String username;
    private String module;
    private String action;
    private String method;
    private String requestUri;
    private String requestParams;
    private Integer httpStatus;
    private String ip;
    private String detail;
    private Integer status;
    private LocalDateTime createdAt;
}
