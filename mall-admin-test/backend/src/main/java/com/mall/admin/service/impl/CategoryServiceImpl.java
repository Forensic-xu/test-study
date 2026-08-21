package com.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.admin.common.AuthSupport;
import com.mall.admin.common.ErrorCode;
import com.mall.admin.dto.CategoryCreateRequest;
import com.mall.admin.dto.CategoryUpdateRequest;
import com.mall.admin.entity.Category;
import com.mall.admin.entity.Product;
import com.mall.admin.exception.BusinessException;
import com.mall.admin.mapper.CategoryMapper;
import com.mall.admin.mapper.ProductMapper;
import com.mall.admin.service.CategoryService;
import com.mall.admin.vo.CategoryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;

    @Override
    public List<CategoryVO> list(Integer status) {
        AuthSupport.requireLogin();
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Category::getStatus, status);
        }
        wrapper.orderByAsc(Category::getId);
        return categoryMapper.selectList(wrapper).stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryVO getById(Long id) {
        AuthSupport.requireLogin();
        return toVO(requireCategory(id));
    }

    @Override
    public CategoryVO create(CategoryCreateRequest request) {
        AuthSupport.requireAdmin();
        ensureNameUnique(request.getName(), null);

        Category category = new Category();
        category.setName(request.getName().trim());
        category.setParentId(0L);
        category.setSortOrder(0);
        category.setStatus(request.getStatus() == null ? 1 : request.getStatus());
        categoryMapper.insert(category);
        log.info("Category created: id={}, name={}", category.getId(), category.getName());
        return toVO(category);
    }

    @Override
    public CategoryVO update(Long id, CategoryUpdateRequest request) {
        AuthSupport.requireAdmin();
        Category category = requireCategory(id);

        if (StringUtils.hasText(request.getName())) {
            ensureNameUnique(request.getName(), id);
            category.setName(request.getName().trim());
        }
        if (request.getStatus() != null) {
            category.setStatus(request.getStatus());
        }
        categoryMapper.updateById(category);
        log.info("Category updated: id={}", id);
        return toVO(requireCategory(id));
    }

    @Override
    public void delete(Long id) {
        AuthSupport.requireAdmin();
        requireCategory(id);

        Long productCount = productMapper.selectCount(new LambdaQueryWrapper<Product>()
                .eq(Product::getCategoryId, id));
        if (productCount != null && productCount > 0) {
            throw new BusinessException(ErrorCode.CATEGORY_HAS_PRODUCTS);
        }

        categoryMapper.deleteById(id);
        log.info("Category deleted: id={}", id);
    }

    private Category requireCategory(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        return category;
    }

    private void ensureNameUnique(String name, Long excludeId) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<Category>()
                .eq(Category::getName, name.trim());
        if (excludeId != null) {
            wrapper.ne(Category::getId, excludeId);
        }
        Long count = categoryMapper.selectCount(wrapper);
        if (count != null && count > 0) {
            throw new BusinessException(ErrorCode.CATEGORY_NAME_EXISTS);
        }
    }

    private CategoryVO toVO(Category category) {
        return CategoryVO.builder()
                .id(category.getId())
                .name(category.getName())
                .status(category.getStatus())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}
