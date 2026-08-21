package com.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.admin.common.AuthSupport;
import com.mall.admin.common.AuthUser;
import com.mall.admin.common.ErrorCode;
import com.mall.admin.dto.InventoryChangeRequest;
import com.mall.admin.entity.InventoryRecord;
import com.mall.admin.entity.Product;
import com.mall.admin.enums.InventoryOperationType;
import com.mall.admin.enums.ProductStatus;
import com.mall.admin.exception.BusinessException;
import com.mall.admin.mapper.InventoryRecordMapper;
import com.mall.admin.mapper.ProductMapper;
import com.mall.admin.service.InventoryService;
import com.mall.admin.vo.InventoryRecordVO;
import com.mall.admin.vo.InventoryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Inventory changes use conditional SQL updates for concurrency safety:
 * <pre>
 *   UPDATE products SET stock = stock - #{qty} WHERE id = ? AND stock >= #{qty}
 * </pre>
 * If two requests race on the same stock, only requests that still satisfy stock >= qty succeed.
 * This avoids negative stock without needing distributed locks.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final ProductMapper productMapper;
    private final InventoryRecordMapper inventoryRecordMapper;

    @Override
    public InventoryVO getByProductId(Long productId) {
        AuthSupport.requireLogin();
        return toInventoryVO(requireProduct(productId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InventoryVO increase(Long productId, InventoryChangeRequest request) {
        AuthUser operator = AuthSupport.requireAdmin();
        validateQuantity(request.getQuantity());

        Product product = requireProduct(productId);
        int quantity = request.getQuantity();

        int affected = productMapper.increaseStock(productId, quantity);
        if (affected != 1) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        Product latest = requireProduct(productId);
        int after = latest.getStock();
        int before = after - quantity;
        saveRecord(productId, before, quantity, after, InventoryOperationType.INCREASE,
                operator.getUserId(), request.getRemark());
        log.info("Inventory increased: productId={}, before={}, change={}, after={}",
                productId, before, quantity, after);
        return toInventoryVO(latest);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InventoryVO decrease(Long productId, InventoryChangeRequest request) {
        AuthUser operator = AuthSupport.requireAdmin();
        validateQuantity(request.getQuantity());

        requireProduct(productId);
        int quantity = request.getQuantity();

        // Conditional update prevents concurrent oversell / negative stock.
        int affected = productMapper.decreaseStock(productId, quantity);
        if (affected != 1) {
            Product latest = productMapper.selectById(productId);
            if (latest == null) {
                throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
            }
            throw new BusinessException(ErrorCode.INVENTORY_INSUFFICIENT);
        }

        Product latest = requireProduct(productId);
        int after = latest.getStock();
        int before = after + quantity;
        saveRecord(productId, before, -quantity, after, InventoryOperationType.DECREASE,
                operator.getUserId(), request.getRemark());
        log.info("Inventory decreased: productId={}, before={}, change={}, after={}",
                productId, before, -quantity, after);
        return toInventoryVO(latest);
    }

    @Override
    public List<InventoryRecordVO> listRecords(Long productId) {
        AuthSupport.requireLogin();
        requireProduct(productId);
        return inventoryRecordMapper.selectList(new LambdaQueryWrapper<InventoryRecord>()
                        .eq(InventoryRecord::getProductId, productId)
                        .orderByDesc(InventoryRecord::getId))
                .stream()
                .map(this::toRecordVO)
                .collect(Collectors.toList());
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new BusinessException(ErrorCode.INVENTORY_ADJUST_INVALID);
        }
    }

    private Product requireProduct(Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        return product;
    }

    private void saveRecord(Long productId, int before, int change, int after,
                            InventoryOperationType type, Long operatorId, String remark) {
        InventoryRecord record = new InventoryRecord();
        record.setProductId(productId);
        record.setBeforeStock(before);
        record.setChangeQuantity(change);
        record.setAfterStock(after);
        record.setOperationType(type.name());
        record.setOperatorId(operatorId);
        record.setRemark(remark);
        inventoryRecordMapper.insert(record);
    }

    private InventoryVO toInventoryVO(Product product) {
        ProductStatus status = ProductStatus.fromDb(product.getStatus());
        return InventoryVO.builder()
                .productId(product.getId())
                .productName(product.getName())
                .stock(product.getStock())
                .status(status == null ? null : status.name())
                .build();
    }

    private InventoryRecordVO toRecordVO(InventoryRecord record) {
        return InventoryRecordVO.builder()
                .id(record.getId())
                .productId(record.getProductId())
                .beforeStock(record.getBeforeStock())
                .changeQuantity(record.getChangeQuantity())
                .afterStock(record.getAfterStock())
                .operationType(record.getOperationType())
                .operatorId(record.getOperatorId())
                .remark(record.getRemark())
                .createdAt(record.getCreatedAt())
                .build();
    }
}
