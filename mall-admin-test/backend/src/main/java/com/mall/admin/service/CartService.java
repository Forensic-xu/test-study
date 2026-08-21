package com.mall.admin.service;

import com.mall.admin.dto.CartAddRequest;
import com.mall.admin.dto.CartUpdateRequest;
import com.mall.admin.vo.CartItemVO;

import java.util.List;

public interface CartService {

    List<CartItemVO> listMyCart();

    CartItemVO add(CartAddRequest request);

    CartItemVO update(Long id, CartUpdateRequest request);

    void delete(Long id);

    void clear();
}
