package com.mall.admin.service;

import com.mall.admin.dto.CategoryCreateRequest;
import com.mall.admin.dto.CategoryUpdateRequest;
import com.mall.admin.vo.CategoryVO;

import java.util.List;

public interface CategoryService {

    List<CategoryVO> list(Integer status);

    CategoryVO getById(Long id);

    CategoryVO create(CategoryCreateRequest request);

    CategoryVO update(Long id, CategoryUpdateRequest request);

    void delete(Long id);
}
