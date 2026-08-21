package com.mall.admin.service;

import com.mall.admin.common.PageResult;
import com.mall.admin.dto.ProductCreateRequest;
import com.mall.admin.dto.ProductUpdateRequest;
import com.mall.admin.vo.ProductVO;

public interface ProductService {

    PageResult<ProductVO> page(long page, long size, String name, Long categoryId, String status);

    ProductVO getById(Long id);

    ProductVO create(ProductCreateRequest request);

    ProductVO update(Long id, ProductUpdateRequest request);

    void delete(Long id);
}
