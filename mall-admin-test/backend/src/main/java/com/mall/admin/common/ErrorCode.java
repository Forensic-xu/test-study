package com.mall.admin.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Stable business error codes for API automation assertions.
 * Do not change existing codes casually — see docs/error-codes.md.
 */
@Getter
public enum ErrorCode {

    SUCCESS(200, "success", HttpStatus.OK),

    // ===== Auth / User 1xxxx =====
    USER_NOT_FOUND(10001, "用户不存在", HttpStatus.NOT_FOUND),
    PASSWORD_ERROR(10002, "密码错误", HttpStatus.UNAUTHORIZED),
    USER_DISABLED(10003, "用户被禁用", HttpStatus.FORBIDDEN),
    TOKEN_INVALID(10004, "Token 无效", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(10005, "Token 已过期", HttpStatus.UNAUTHORIZED),
    TOKEN_MISSING(10006, "未登录或缺少 Token", HttpStatus.UNAUTHORIZED),
    USERNAME_EXISTS(10007, "用户名已存在", HttpStatus.CONFLICT),
    FORBIDDEN(10008, "无权限访问", HttpStatus.FORBIDDEN),
    USER_CREATE_FAILED(10009, "创建用户失败", HttpStatus.BAD_REQUEST),

    // ===== Product 2xxxx =====
    PRODUCT_NOT_FOUND(20001, "商品不存在", HttpStatus.NOT_FOUND),
    PRODUCT_OFF_SHELF(20002, "商品已下架", HttpStatus.CONFLICT),
    PRODUCT_STOCK_INSUFFICIENT(20003, "商品库存不足", HttpStatus.CONFLICT),
    PRODUCT_PARAM_INVALID(20004, "商品参数非法", HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_FOUND(20005, "商品分类不存在", HttpStatus.NOT_FOUND),
    CATEGORY_NAME_EXISTS(20006, "商品分类名称已存在", HttpStatus.CONFLICT),
    CATEGORY_HAS_PRODUCTS(20007, "分类下存在商品，不允许删除", HttpStatus.CONFLICT),
    PRODUCT_NAME_EXISTS(20008, "商品名称已存在", HttpStatus.CONFLICT),
    PRODUCT_IN_USE(20009, "商品已被购物车或订单引用，不允许删除", HttpStatus.CONFLICT),

    // ===== Cart 3xxxx =====
    CART_ITEM_NOT_FOUND(30001, "购物车商品不存在", HttpStatus.NOT_FOUND),
    CART_QUANTITY_INVALID(30002, "购物车数量非法", HttpStatus.BAD_REQUEST),
    CART_FORBIDDEN(30004, "无权操作该购物车项", HttpStatus.FORBIDDEN),

    // ===== Order 4xxxx =====
    ORDER_NOT_FOUND(40001, "订单不存在", HttpStatus.NOT_FOUND),
    ORDER_STATUS_INVALID(40002, "订单状态不允许该操作", HttpStatus.CONFLICT),
    ORDER_CREATE_FAILED(40003, "订单创建失败", HttpStatus.BAD_REQUEST),
    ORDER_ITEMS_EMPTY(40004, "订单商品列表不能为空", HttpStatus.BAD_REQUEST),
    ORDER_CANCEL_NOT_ALLOWED(40005, "订单状态不允许取消", HttpStatus.CONFLICT),
    ORDER_PAY_NOT_ALLOWED(40006, "订单状态不允许支付", HttpStatus.CONFLICT),
    ORDER_SHIP_NOT_ALLOWED(40007, "订单状态不允许发货", HttpStatus.CONFLICT),
    ORDER_COMPLETE_NOT_ALLOWED(40008, "订单状态不允许完成", HttpStatus.CONFLICT),
    ORDER_FORBIDDEN(40009, "无权操作该订单", HttpStatus.FORBIDDEN),
    ORDER_MODE_INVALID(40010, "订单创建参数无效，请提供 cartItemIds 或 items", HttpStatus.BAD_REQUEST),

    // ===== Inventory 5xxxx =====
    INVENTORY_ADJUST_INVALID(50001, "库存调整数量非法", HttpStatus.BAD_REQUEST),
    INVENTORY_INSUFFICIENT(50002, "库存不足，无法减少", HttpStatus.CONFLICT),

    // ===== Common =====
    PARAM_INVALID(90001, "参数校验失败", HttpStatus.BAD_REQUEST),
    SYSTEM_ERROR(90002, "系统异常", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
