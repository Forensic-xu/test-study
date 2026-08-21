package com.mall.admin.service;

import com.mall.admin.common.PageResult;
import com.mall.admin.dto.OrderCreateRequest;
import com.mall.admin.vo.OrderVO;

public interface OrderService {

    OrderVO create(OrderCreateRequest request);

    PageResult<OrderVO> page(long page, long size, String status);

    OrderVO getById(Long id);

    OrderVO cancel(Long id);

    OrderVO pay(Long id);

    OrderVO ship(Long id);

    OrderVO complete(Long id);
}
