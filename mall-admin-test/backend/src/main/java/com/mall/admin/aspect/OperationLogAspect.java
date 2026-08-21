package com.mall.admin.aspect;

import com.mall.admin.common.AuthUser;
import com.mall.admin.common.OpLog;
import com.mall.admin.common.UserContext;
import com.mall.admin.entity.OperationLog;
import com.mall.admin.mapper.OperationLogMapper;
import com.mall.admin.util.SensitiveMasker;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogMapper operationLogMapper;

    @Around("@annotation(opLog)")
    public Object around(ProceedingJoinPoint pjp, OpLog opLog) throws Throwable {
        long start = System.currentTimeMillis();
        HttpServletRequest request = currentRequest();
        String params = buildParams(pjp);
        try {
            Object result = pjp.proceed();
            saveLog(opLog, request, params, 200, 1, "OK (" + (System.currentTimeMillis() - start) + "ms)");
            return result;
        } catch (Throwable ex) {
            Integer httpStatus = resolveHttpStatus(ex);
            saveLog(opLog, request, params, httpStatus, 0, ex.getMessage());
            throw ex;
        }
    }

    private void saveLog(OpLog opLog, HttpServletRequest request, String params,
                         Integer httpStatus, int success, String detail) {
        try {
            OperationLog row = new OperationLog();
            AuthUser user = UserContext.get();
            if (user != null) {
                row.setUserId(user.getUserId());
                row.setUsername(user.getUsername());
            } else if (request != null && params != null && params.contains("username")) {
                // login may have no UserContext yet; keep username out of sensitive mask already
                row.setUsername(extractUsernameHint(params));
            }
            row.setModule(opLog.module());
            row.setAction(opLog.action());
            if (request != null) {
                row.setMethod(request.getMethod());
                row.setRequestUri(request.getRequestURI());
                row.setIp(resolveIp(request));
            }
            row.setRequestParams(trim(params, 1900));
            row.setHttpStatus(httpStatus);
            row.setDetail(trim(detail, 900));
            row.setStatus(success);
            operationLogMapper.insert(row);
        } catch (Exception ex) {
            log.warn("Failed to persist operation log: {}", ex.getMessage());
        }
    }

    private String buildParams(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        String[] names = signature.getParameterNames();
        Object[] args = pjp.getArgs();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg == null || arg instanceof HttpServletRequest) {
                continue;
            }
            String name = names != null && i < names.length ? names[i] : ("arg" + i);
            String value = SensitiveMasker.maskObject(arg);
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(name).append('=').append(value);
        }
        return sb.toString();
    }

    private Integer resolveHttpStatus(Throwable ex) {
        if (ex instanceof com.mall.admin.exception.BusinessException be) {
            return be.getErrorCode().getHttpStatus().value();
        }
        return 500;
    }

    private String extractUsernameHint(String params) {
        int idx = params.indexOf("\"username\":");
        if (idx < 0) {
            return null;
        }
        int start = params.indexOf('"', idx + 11);
        int end = params.indexOf('"', start + 1);
        if (start >= 0 && end > start) {
            return params.substring(start + 1, end);
        }
        return null;
    }

    private HttpServletRequest currentRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs == null ? null : attrs.getRequest();
    }

    private String resolveIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String trim(String value, int max) {
        if (value == null) {
            return null;
        }
        return value.length() <= max ? value : value.substring(0, max);
    }
}
