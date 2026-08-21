package com.mall.admin.exception;

import com.mall.admin.common.ErrorCode;
import com.mall.admin.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        ErrorCode errorCode = ex.getErrorCode();
        log.warn("[{}] {} {} business code={} message={}",
                MDC.get("traceId"), request.getMethod(), request.getRequestURI(),
                errorCode.getCode(), ex.getMessage());
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(Result.fail(errorCode, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                     HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining("; "));
        log.warn("[{}] {} {} validation failed: {}",
                MDC.get("traceId"), request.getMethod(), request.getRequestURI(), message);
        return ResponseEntity
                .status(ErrorCode.PARAM_INVALID.getHttpStatus())
                .body(Result.fail(ErrorCode.PARAM_INVALID, message));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Result<Void>> handleBindException(BindException ex, HttpServletRequest request) {
        String message = ex.getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining("; "));
        log.warn("[{}] {} {} bind failed: {}",
                MDC.get("traceId"), request.getMethod(), request.getRequestURI(), message);
        return ResponseEntity
                .status(ErrorCode.PARAM_INVALID.getHttpStatus())
                .body(Result.fail(ErrorCode.PARAM_INVALID, message));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Result<Void>> handleConstraintViolation(ConstraintViolationException ex,
                                                                  HttpServletRequest request) {
        String message = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        log.warn("[{}] {} {} constraint: {}",
                MDC.get("traceId"), request.getMethod(), request.getRequestURI(), message);
        return ResponseEntity
                .status(ErrorCode.PARAM_INVALID.getHttpStatus())
                .body(Result.fail(ErrorCode.PARAM_INVALID, message));
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            HttpRequestMethodNotSupportedException.class
    })
    public ResponseEntity<Result<Void>> handleBadRequest(Exception ex, HttpServletRequest request) {
        log.warn("[{}] {} {} bad request: {}",
                MDC.get("traceId"), request.getMethod(), request.getRequestURI(), ex.getMessage());
        return ResponseEntity
                .status(ErrorCode.PARAM_INVALID.getHttpStatus())
                .body(Result.fail(ErrorCode.PARAM_INVALID, "请求参数格式错误"));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Result<Void>> handleNotFound(NoHandlerFoundException ex, HttpServletRequest request) {
        log.warn("[{}] {} {} not found", MDC.get("traceId"), request.getMethod(), request.getRequestURI());
        return ResponseEntity
                .status(ErrorCode.PARAM_INVALID.getHttpStatus())
                .body(Result.fail(ErrorCode.PARAM_INVALID, "接口不存在"));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Result<Void>> handleDataAccess(DataAccessException ex, HttpServletRequest request) {
        log.error("[{}] {} {} database error",
                MDC.get("traceId"), request.getMethod(), request.getRequestURI(), ex);
        return ResponseEntity
                .status(ErrorCode.SYSTEM_ERROR.getHttpStatus())
                .body(Result.fail(ErrorCode.SYSTEM_ERROR, "数据库异常"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception ex, HttpServletRequest request) {
        log.error("[{}] {} {} unhandled exception",
                MDC.get("traceId"), request.getMethod(), request.getRequestURI(), ex);
        return ResponseEntity
                .status(ErrorCode.SYSTEM_ERROR.getHttpStatus())
                .body(Result.fail(ErrorCode.SYSTEM_ERROR));
    }

    private String formatFieldError(FieldError fieldError) {
        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
    }
}
