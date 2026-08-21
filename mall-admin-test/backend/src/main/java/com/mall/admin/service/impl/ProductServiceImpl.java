package com.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.admin.common.AuthSupport;
import com.mall.admin.common.ErrorCode;
import com.mall.admin.common.PageResult;
import com.mall.admin.dto.ProductCreateRequest;
import com.mall.admin.dto.ProductUpdateRequest;
import com.mall.admin.entity.Category;
import com.mall.admin.entity.InventoryRecord;
import com.mall.admin.entity.Product;
import com.mall.admin.enums.InventoryOperationType;
import com.mall.admin.enums.ProductStatus;
import com.mall.admin.exception.BusinessException;
import com.mall.admin.mapper.CategoryMapper;
import com.mall.admin.mapper.InventoryRecordMapper;
import com.mall.admin.mapper.ProductMapper;
import com.mall.admin.mapper.ProductRelationChecker;
import com.mall.admin.service.ProductService;
import com.mall.admin.vo.ProductVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;
    private final InventoryRecordMapper inventoryRecordMapper;
    private final ProductRelationChecker productRelationChecker;

    @Override
    public PageResult<ProductVO> page(long page, long size, String name, Long categoryId, String status) {
        AuthSupport.requireLogin();
        long pageNo = page <= 0 ? 1 : page;
        long pageSize = size <= 0 ? 10 : Math.min(size, 100);

        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(name)) {
            wrapper.like(Product::getName, name.trim());
        }
        if (categoryId != null) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }
        if (StringUtils.hasText(status)) {
            ProductStatus productStatus = ProductStatus.fromName(status);
            wrapper.eq(Product::getStatus, productStatus.getDbValue());
        }
        wrapper.orderByDesc(Product::getId);

        Page<Product> result = productMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);
        Map<Long, String> categoryNameMap = loadCategoryNames(result.getRecords());

        List<ProductVO> records = result.getRecords().stream()
                .map(p -> toVO(p, categoryNameMap.get(p.getCategoryId())))
                .collect(Collectors.toList());

        return PageResult.<ProductVO>builder()
                .page(result.getCurrent())
                .size(result.getSize())
                .total(result.getTotal())
                .pages(result.getPages())
                .records(records)
                .build();
    }

    @Override
    public ProductVO getById(Long id) {
        AuthSupport.requireLogin();
        Product product = requireProduct(id);
        Category category = categoryMapper.selectById(product.getCategoryId());
        return toVO(product, category == null ? null : category.getName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductVO create(ProductCreateRequest request) {
        AuthSupport.requireAdmin();
        requireCategoryExists(request.getCategoryId());
        ensureNameUnique(request.getName(), null);

        ProductStatus status = ProductStatus.fromName(request.getStatus());
        Product product = new Product();
        product.setName(request.getName().trim());
        product.setCategoryId(request.getCategoryId());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setStatus(status.getDbValue());
        product.setDescription(request.getDescription());
        productMapper.insert(product);

        if (request.getStock() > 0) {
            saveInventoryRecord(product.getId(), 0, request.getStock(), request.getStock(),
                    InventoryOperationType.INCREASE, "商品创建初始化库存");
        }

        log.info("Product created: id={}, name={}, stock={}", product.getId(), product.getName(), product.getStock());
        return getById(product.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductVO update(Long id, ProductUpdateRequest request) {
        AuthSupport.requireAdmin();
        Product product = requireProduct(id);

        if (StringUtils.hasText(request.getName())) {
            ensureNameUnique(request.getName(), id);
            product.setName(request.getName().trim());
        }
        if (request.getCategoryId() != null) {
            requireCategoryExists(request.getCategoryId());
            product.setCategoryId(request.getCategoryId());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (StringUtils.hasText(request.getStatus())) {
            product.setStatus(ProductStatus.fromName(request.getStatus()).getDbValue());
        }

        // Stock changes on product update are applied via conditional SQL + inventory record.
        if (request.getStock() != null && !Objects.equals(request.getStock(), product.getStock())) {
            int before = product.getStock();
            int target = request.getStock();
            int delta = target - before;
            int affected;
            if (delta > 0) {
                affected = productMapper.increaseStock(id, delta);
                if (affected != 1) {
                    throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
                }
                saveInventoryRecord(id, before, delta, target, InventoryOperationType.INCREASE, "商品编辑调整库存");
            } else {
                affected = productMapper.decreaseStock(id, Math.abs(delta));
                if (affected != 1) {
                    throw new BusinessException(ErrorCode.INVENTORY_INSUFFICIENT);
                }
                saveInventoryRecord(id, before, delta, target, InventoryOperationType.DECREASE, "商品编辑调整库存");
            }
            product.setStock(null);
        }

        productMapper.updateById(product);
        log.info("Product updated: id={}", id);
        return getById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        AuthSupport.requireAdmin();
        requireProduct(id);

        if (productRelationChecker.countCartByProductId(id) > 0
                || productRelationChecker.countOrderItemsByProductId(id) > 0) {
            throw new BusinessException(ErrorCode.PRODUCT_IN_USE);
        }

        inventoryRecordMapper.delete(new LambdaQueryWrapper<InventoryRecord>()
                .eq(InventoryRecord::getProductId, id));
        productMapper.deleteById(id);
        log.info("Product deleted: id={}", id);
    }

    private Product requireProduct(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        return product;
    }

    private void requireCategoryExists(Long categoryId) {
        Category category = categoryMapper.selectById(categoryId);
        if (category == null) {
            throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
        }
    }

    private void ensureNameUnique(String name, Long excludeId) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getName, name.trim());
        if (excludeId != null) {
            wrapper.ne(Product::getId, excludeId);
        }
        Long count = productMapper.selectCount(wrapper);
        if (count != null && count > 0) {
            throw new BusinessException(ErrorCode.PRODUCT_NAME_EXISTS);
        }
    }

    private void saveInventoryRecord(Long productId, int before, int change, int after,
                                     InventoryOperationType type, String remark) {
        InventoryRecord record = new InventoryRecord();
        record.setProductId(productId);
        record.setBeforeStock(before);
        record.setChangeQuantity(change);
        record.setAfterStock(after);
        record.setOperationType(type.name());
        record.setOperatorId(AuthSupport.requireLogin().getUserId());
        record.setRemark(remark);
        inventoryRecordMapper.insert(record);
    }

    private Map<Long, String> loadCategoryNames(List<Product> products) {
        Set<Long> categoryIds = products.stream()
                .map(Product::getCategoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (categoryIds.isEmpty()) {
            return Map.of();
        }
        return categoryMapper.selectBatchIds(categoryIds).stream()
                .collect(Collectors.toMap(Category::getId, Category::getName, (a, b) -> a));
    }

    private ProductVO toVO(Product product, String categoryName) {
        ProductStatus status = ProductStatus.fromDb(product.getStatus());
        return ProductVO.builder()
                .id(product.getId())
                .name(product.getName())
                .categoryId(product.getCategoryId())
                .categoryName(categoryName)
                .price(product.getPrice())
                .stock(product.getStock())
                .status(status == null ? null : status.name())
                .description(product.getDescription())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
